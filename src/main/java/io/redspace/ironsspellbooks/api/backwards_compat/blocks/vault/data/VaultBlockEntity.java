package io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.VaultBlock;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class VaultBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VaultServerData serverData = new VaultServerData();
   private final VaultSharedData sharedData = new VaultSharedData();
   private final VaultClientData clientData = new VaultClientData();
   private VaultConfig config = VaultConfig.DEFAULT;

   public VaultBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)BlockRegistry.VAULT_BLOCK_ENTITY.get(), pos, state);
   }

   @Nullable
   public Packet<ClientGamePacketListener> m_58483_() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public CompoundTag m_5995_() {
      return (CompoundTag)Util.m_137469_(new CompoundTag(), p_330145_ -> p_330145_.m_128365_("shared_data", encode(VaultSharedData.CODEC, this.sharedData)));
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128365_("config", encode(VaultConfig.CODEC, this.config));
      tag.m_128365_("shared_data", encode(VaultSharedData.CODEC, this.sharedData));
      tag.m_128365_("server_data", encode(VaultServerData.CODEC, this.serverData));
   }

   private static <T> Tag encode(Codec<T> codec, T value) {
      return (Tag)codec.encodeStart(NbtOps.f_128958_, value).getOrThrow(false, IronsSpellbooks.LOGGER::error);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      DynamicOps<Tag> dynamicops = NbtOps.f_128958_;
      if (tag.m_128441_("server_data")) {
         VaultServerData.CODEC.parse(dynamicops, tag.m_128423_("server_data")).resultOrPartial(LOGGER::error).ifPresent(this.serverData::set);
      }

      if (tag.m_128441_("config")) {
         VaultConfig.CODEC.parse(dynamicops, tag.m_128423_("config")).resultOrPartial(LOGGER::error).ifPresent(p_324546_ -> this.config = p_324546_);
      }

      if (tag.m_128441_("shared_data")) {
         VaultSharedData.CODEC.parse(dynamicops, tag.m_128423_("shared_data")).resultOrPartial(LOGGER::error).ifPresent(this.sharedData::set);
      }
   }

   @Nullable
   public VaultServerData getServerData() {
      return this.f_58857_ != null && !this.f_58857_.f_46443_ ? this.serverData : null;
   }

   public VaultSharedData getSharedData() {
      return this.sharedData;
   }

   public VaultClientData getClientData() {
      return this.clientData;
   }

   public VaultConfig getConfig() {
      return this.config;
   }

   @VisibleForTesting
   public void setConfig(VaultConfig config) {
      this.config = config;
   }

   public static final class Client {
      private static final int PARTICLE_TICK_RATE = 20;
      private static final float IDLE_PARTICLE_CHANCE = 0.5F;
      private static final float AMBIENT_SOUND_CHANCE = 0.02F;
      private static final int ACTIVATION_PARTICLE_COUNT = 20;
      private static final int DEACTIVATION_PARTICLE_COUNT = 20;

      public static void tick(Level level, BlockPos pos, BlockState state, VaultClientData clientData, VaultSharedData sharedData) {
         clientData.updateDisplayItemSpin();
         if (level.m_46467_() % 20L == 0L) {
            emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
         }

         emitIdleParticles(level, pos, sharedData, state.m_61143_(VaultBlock.OMINOUS) ? ParticleTypes.f_123745_ : ParticleTypes.f_175834_);
         playIdleSounds(level, pos, sharedData);
      }

      public static void emitActivationParticles(Level level, BlockPos pos, BlockState state, VaultSharedData sharedData, ParticleOptions particle) {
         emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
         RandomSource randomsource = level.f_46441_;

         for (int i = 0; i < 20; i++) {
            Vec3 vec3 = randomPosInsideCage(pos, randomsource);
            level.m_7106_(ParticleTypes.f_123762_, vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_(), 0.0, 0.0, 0.0);
            level.m_7106_(particle, vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_(), 0.0, 0.0, 0.0);
         }
      }

      public static void emitDeactivationParticles(Level level, BlockPos pos, ParticleOptions particle) {
         RandomSource randomsource = level.f_46441_;

         for (int i = 0; i < 20; i++) {
            Vec3 vec3 = randomPosCenterOfCage(pos, randomsource);
            Vec3 vec31 = new Vec3(randomsource.m_188583_() * 0.02, randomsource.m_188583_() * 0.02, randomsource.m_188583_() * 0.02);
            level.m_7106_(particle, vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_(), vec31.m_7096_(), vec31.m_7098_(), vec31.m_7094_());
         }
      }

      private static void emitIdleParticles(Level level, BlockPos pos, VaultSharedData sharedData, ParticleOptions particle) {
         RandomSource randomsource = level.m_213780_();
         if (randomsource.m_188501_() <= 0.5F) {
            Vec3 vec3 = randomPosInsideCage(pos, randomsource);
            level.m_7106_(ParticleTypes.f_123762_, vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_(), 0.0, 0.0, 0.0);
            if (shouldDisplayActiveEffects(sharedData)) {
               level.m_7106_(particle, vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_(), 0.0, 0.0, 0.0);
            }
         }
      }

      private static void emitConnectionParticlesForPlayer(Level level, Vec3 pos, Player player) {
         RandomSource randomsource = level.f_46441_;
         Vec3 vec3 = pos.m_82505_(player.m_20182_().m_82520_(0.0, player.m_20206_() / 2.0F, 0.0));
         int i = Mth.m_216271_(randomsource, 2, 5);

         for (int j = 0; j < i; j++) {
            Vec3 var7 = vec3.m_272010_(randomsource, 1.0F);
         }
      }

      private static void emitConnectionParticlesForNearbyPlayers(Level level, BlockPos pos, BlockState state, VaultSharedData sharedData) {
         Set<UUID> set = sharedData.getConnectedPlayers();
         if (!set.isEmpty()) {
            Vec3 vec3 = keyholePos(pos, (Direction)state.m_61143_(VaultBlock.FACING));

            for (UUID uuid : set) {
               Player player = level.m_46003_(uuid);
               if (player != null && isWithinConnectionRange(pos, sharedData, player)) {
                  emitConnectionParticlesForPlayer(level, vec3, player);
               }
            }
         }
      }

      private static boolean isWithinConnectionRange(BlockPos pos, VaultSharedData sharedData, Player player) {
         return player.m_20183_().m_123331_(pos) <= Mth.m_144952_(sharedData.connectedParticlesRange());
      }

      private static void playIdleSounds(Level level, BlockPos pos, VaultSharedData sharedData) {
         if (shouldDisplayActiveEffects(sharedData)) {
            RandomSource randomsource = level.m_213780_();
            if (randomsource.m_188501_() <= 0.02F) {
               level.m_245747_(
                  pos,
                  (SoundEvent)SoundRegistry.VAULT_AMBIENT.get(),
                  SoundSource.BLOCKS,
                  randomsource.m_188501_() * 0.25F + 0.75F,
                  randomsource.m_188501_() + 0.5F,
                  false
               );
            }
         }
      }

      public static boolean shouldDisplayActiveEffects(VaultSharedData sharedData) {
         return sharedData.hasDisplayItem();
      }

      private static Vec3 randomPosCenterOfCage(BlockPos pos, RandomSource random) {
         return Vec3.m_82528_(pos).m_82520_(Mth.m_216263_(random, 0.4, 0.6), Mth.m_216263_(random, 0.4, 0.6), Mth.m_216263_(random, 0.4, 0.6));
      }

      private static Vec3 randomPosInsideCage(BlockPos pos, RandomSource random) {
         return Vec3.m_82528_(pos).m_82520_(Mth.m_216263_(random, 0.1, 0.9), Mth.m_216263_(random, 0.25, 0.75), Mth.m_216263_(random, 0.1, 0.9));
      }

      private static Vec3 keyholePos(BlockPos pos, Direction facing) {
         return Vec3.m_82539_(pos).m_82520_(facing.m_122429_() * 0.5, 1.75, facing.m_122431_() * 0.5);
      }
   }

   public static final class Server {
      private static final int UNLOCKING_DELAY_TICKS = 14;
      private static final int DISPLAY_CYCLE_TICK_RATE = 20;
      private static final int INSERT_FAIL_SOUND_BUFFER_TICKS = 15;

      public static void tick(ServerLevel level, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
         VaultState vaultstate = (VaultState)state.m_61143_(VaultBlock.STATE);
         if (shouldCycleDisplayItem(level.m_46467_(), vaultstate)) {
            cycleDisplayItemFromLootTable(level, vaultstate, config, sharedData, pos);
         }

         BlockState blockstate = state;
         if (level.m_46467_() >= serverData.stateUpdatingResumesAt()) {
            blockstate = (BlockState)state.m_61124_(VaultBlock.STATE, vaultstate.tickAndGetNext(level, pos, config, serverData, sharedData));
            if (!state.equals(blockstate)) {
               setVaultState(level, pos, state, blockstate, config, sharedData);
            }
         }

         if (serverData.isDirty || sharedData.isDirty) {
            VaultBlockEntity.m_155232_(level, pos, state);
            if (sharedData.isDirty) {
               level.m_7260_(pos, state, blockstate, 2);
            }

            serverData.isDirty = false;
            sharedData.isDirty = false;
         }
      }

      public static void tryInsertKey(
         ServerLevel level,
         BlockPos pos,
         BlockState state,
         VaultConfig config,
         VaultServerData serverData,
         VaultSharedData sharedData,
         Player player,
         ItemStack stack
      ) {
         VaultState vaultstate = (VaultState)state.m_61143_(VaultBlock.STATE);
         if (canEjectReward(config, vaultstate)) {
            if (!isValidToInsert(config, stack)) {
               playInsertFailSound(level, serverData, pos, (SoundEvent)SoundRegistry.VAULT_INSERT_ITEM_FAIL.get());
            } else if (serverData.hasRewardedPlayer(player)) {
               playInsertFailSound(level, serverData, pos, (SoundEvent)SoundRegistry.VAULT_REJECT_REWARDED_PLAYER.get());
            } else {
               List<ItemStack> list = resolveItemsToEject(level, config, pos, player);
               if (!list.isEmpty()) {
                  player.m_36246_(Stats.f_12982_.m_12902_(stack.m_41720_()));
                  if (!player.m_150110_().f_35937_) {
                     stack.m_41774_(config.keyItem().m_41613_());
                  }

                  unlock(level, state, pos, config, serverData, sharedData, list);
                  serverData.addToRewardedPlayers(player);
                  sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, config.deactivationRange());
               }
            }
         }
      }

      static void setVaultState(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState, VaultConfig config, VaultSharedData sharedData) {
         VaultState vaultstate = (VaultState)oldState.m_61143_(VaultBlock.STATE);
         VaultState vaultstate1 = (VaultState)newState.m_61143_(VaultBlock.STATE);
         level.m_7731_(pos, newState, 3);
         vaultstate.onTransition(level, pos, vaultstate1, config, sharedData, (Boolean)newState.m_61143_(VaultBlock.OMINOUS));
      }

      static void cycleDisplayItemFromLootTable(ServerLevel level, VaultState state, VaultConfig config, VaultSharedData sharedData, BlockPos pos) {
         if (!canEjectReward(config, state)) {
            sharedData.setDisplayItem(ItemStack.f_41583_);
         } else {
            ItemStack itemstack = getRandomDisplayItemFromLootTable(level, pos, config.overrideLootTableToDisplay().orElse(config.lootTable()));
            sharedData.setDisplayItem(itemstack);
         }
      }

      private static ItemStack getRandomDisplayItemFromLootTable(ServerLevel level, BlockPos pos, ResourceLocation lootTable) {
         LootTable loottable = level.m_7654_().m_278653_().m_278676_(lootTable);
         LootParams lootparams = new Builder(level).m_287286_(LootContextParams.f_81460_, Vec3.m_82512_(pos)).m_287235_(LootContextParamSets.f_81411_);
         List<ItemStack> list = loottable.m_287195_(lootparams);
         return list.isEmpty() ? ItemStack.f_41583_ : (ItemStack)Util.m_214621_(list, level.m_213780_());
      }

      private static void unlock(
         ServerLevel level,
         BlockState state,
         BlockPos pos,
         VaultConfig config,
         VaultServerData serverData,
         VaultSharedData sharedData,
         List<ItemStack> itemsToEject
      ) {
         serverData.setItemsToEject(itemsToEject);
         sharedData.setDisplayItem(serverData.getNextItemToEject());
         serverData.pauseStateUpdatingUntil(level.m_46467_() + 14L);
         setVaultState(level, pos, state, (BlockState)state.m_61124_(VaultBlock.STATE, VaultState.UNLOCKING), config, sharedData);
      }

      private static List<ItemStack> resolveItemsToEject(ServerLevel level, VaultConfig config, BlockPos pos, Player player) {
         LootTable loottable = level.m_7654_().m_278653_().m_278676_(config.lootTable());
         LootParams lootparams = new Builder(level)
            .m_287286_(LootContextParams.f_81460_, Vec3.m_82512_(pos))
            .m_287239_(player.m_36336_())
            .m_287286_(LootContextParams.f_81455_, player)
            .m_287235_(LootContextParamSets.f_81411_);
         return loottable.m_287195_(lootparams);
      }

      private static boolean canEjectReward(VaultConfig config, VaultState state) {
         return config.lootTable() != BuiltInLootTables.f_78712_ && !config.keyItem().m_41619_() && state != VaultState.INACTIVE;
      }

      private static boolean isValidToInsert(VaultConfig config, ItemStack stack) {
         return ItemStack.m_150942_(stack, config.keyItem()) && stack.m_41613_() >= config.keyItem().m_41613_();
      }

      private static boolean shouldCycleDisplayItem(long gameTime, VaultState state) {
         return gameTime % 20L == 0L && state == VaultState.ACTIVE;
      }

      private static void playInsertFailSound(ServerLevel level, VaultServerData serverData, BlockPos pos, SoundEvent sound) {
         if (level.m_46467_() >= serverData.getLastInsertFailTimestamp() + 15L) {
            level.m_247517_(null, pos, sound, SoundSource.BLOCKS);
            serverData.setLastInsertFailTimestamp(level.m_46467_());
         }
      }
   }
}
