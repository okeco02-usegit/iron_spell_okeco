package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.SimpleWeightedRandomList.Builder;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TrialSpawnerData {
   public static final String TAG_SPAWN_DATA = "spawn_data";
   private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
   private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
   private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18000;
   public static final Codec<Set<UUID>> CODEC_SET = Codec.list(UUIDUtil.f_235867_).xmap(Sets::newHashSet, Lists::newArrayList);
   public static MapCodec<TrialSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec(
      p_312830_ -> p_312830_.group(
            CODEC_SET.optionalFieldOf("registered_players", Sets.newHashSet()).forGetter(p_312495_ -> p_312495_.detectedPlayers),
            CODEC_SET.optionalFieldOf("current_mobs", Sets.newHashSet()).forGetter(p_312798_ -> p_312798_.currentMobs),
            Codec.LONG.optionalFieldOf("cooldown_ends_at", 0L).forGetter(p_312792_ -> p_312792_.cooldownEndsAt),
            Codec.LONG.optionalFieldOf("next_mob_spawns_at", 0L).forGetter(p_311772_ -> p_311772_.nextMobSpawnsAt),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_mobs_spawned", 0).forGetter(p_312862_ -> p_312862_.totalMobsSpawned),
            SpawnData.f_186559_.optionalFieldOf("spawn_data").forGetter(p_312634_ -> p_312634_.nextSpawnData),
            ResourceLocation.f_135803_.optionalFieldOf("ejecting_loot_table").forGetter(p_312388_ -> p_312388_.ejectingLootTable)
         )
         .apply(p_312830_, TrialSpawnerData::new)
   );
   protected final Set<UUID> detectedPlayers = new HashSet<>();
   protected final Set<UUID> currentMobs = new HashSet<>();
   protected long cooldownEndsAt;
   protected long nextMobSpawnsAt;
   protected int totalMobsSpawned;
   protected Optional<SpawnData> nextSpawnData;
   protected Optional<ResourceLocation> ejectingLootTable;
   @Nullable
   protected Entity displayEntity;
   @Nullable
   private SimpleWeightedRandomList<ItemStack> dispensing;
   protected double spin;
   protected double oSpin;

   public TrialSpawnerData() {
      this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
   }

   public TrialSpawnerData(
      Set<UUID> detectedPlayers,
      Set<UUID> currentMobs,
      long cooldownEndsAt,
      long nextMobSpawnsAt,
      int totalMobsSpawned,
      Optional<SpawnData> nextSpawnData,
      Optional<ResourceLocation> ejectingLootTable
   ) {
      this.detectedPlayers.addAll(detectedPlayers);
      this.currentMobs.addAll(currentMobs);
      this.cooldownEndsAt = cooldownEndsAt;
      this.nextMobSpawnsAt = nextMobSpawnsAt;
      this.totalMobsSpawned = totalMobsSpawned;
      this.nextSpawnData = nextSpawnData;
      this.ejectingLootTable = ejectingLootTable;
   }

   public void reset() {
      this.detectedPlayers.clear();
      this.totalMobsSpawned = 0;
      this.nextMobSpawnsAt = 0L;
      this.cooldownEndsAt = 0L;
      this.currentMobs.clear();
      this.nextSpawnData = Optional.empty();
   }

   public boolean hasMobToSpawn(TrialSpawner trialSpawner, RandomSource random) {
      boolean flag = this.getOrCreateNextSpawnData(trialSpawner, random).m_186567_().m_128425_("id", 8);
      return flag || !trialSpawner.getConfig().spawnPotentialsDefinition().m_146337_();
   }

   public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int players) {
      return this.totalMobsSpawned >= config.calculateTargetTotalMobs(players);
   }

   public boolean haveAllCurrentMobsDied() {
      return this.currentMobs.isEmpty();
   }

   public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int players) {
      return level.m_46467_() >= this.nextMobSpawnsAt && this.currentMobs.size() < config.calculateTargetSimultaneousMobs(players);
   }

   public int countAdditionalPlayers(BlockPos pos) {
      if (this.detectedPlayers.isEmpty()) {
         Util.m_143785_("Trial Spawner at " + pos + " has no detected players");
      }

      return Math.max(0, this.detectedPlayers.size() - 1);
   }

   public void tryDetectPlayers(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
      boolean flag = (pos.m_121878_() + level.m_46467_()) % 20L != 0L;
      if (!flag && (!spawner.getState().equals(TrialSpawnerState.COOLDOWN) || !spawner.isOminous())) {
         List<UUID> list = spawner.getPlayerDetector().detect(level, spawner.getEntitySelector(), pos, spawner.getRequiredPlayerRange(), true);
         boolean flag1;
         if (!spawner.isOminous() && !list.isEmpty()) {
            Optional<Pair<Player, MobEffect>> optional = findPlayerWithOminousEffect(level, list);
            optional.ifPresent(p_350233_ -> {
               Player player = (Player)p_350233_.getFirst();
               if (p_350233_.getSecond() == MobEffects.f_19594_) {
                  transformBadOmenIntoTrialOmen(player);
               }

               level.m_46796_(3020, BlockPos.m_274446_(player.m_146892_()), 0);
               spawner.applyOminous(level, pos);
            });
            flag1 = optional.isPresent();
         } else {
            flag1 = false;
         }

         if (!spawner.getState().equals(TrialSpawnerState.COOLDOWN) || flag1) {
            boolean flag2 = spawner.getData().detectedPlayers.isEmpty();
            List<UUID> list1 = flag2
               ? list
               : spawner.getPlayerDetector().detect(level, spawner.getEntitySelector(), pos, spawner.getRequiredPlayerRange(), false);
            if (this.detectedPlayers.addAll(list1)) {
               this.nextMobSpawnsAt = Math.max(level.m_46467_() + 40L, this.nextMobSpawnsAt);
               if (!flag1) {
                  level.m_247517_(null, pos, (SoundEvent)SoundRegistry.TRIAL_SPAWNER_DETECT_PLAYER.get(), SoundSource.BLOCKS);
               }
            }
         }
      }
   }

   private static Optional<Pair<Player, MobEffect>> findPlayerWithOminousEffect(ServerLevel level, List<UUID> players) {
      return Optional.empty();
   }

   public void resetAfterBecomingOminous(TrialSpawner spawner, ServerLevel level) {
      this.currentMobs.stream().<Entity>map(level::m_8791_).forEach(p_351984_ -> {
         if (p_351984_ != null) {
            level.m_46796_(3012, p_351984_.m_20183_(), TrialSpawner.FlameParticle.NORMAL.encode());
            if (p_351984_ instanceof Mob var2x) {
               ;
            }

            p_351984_.m_142687_(RemovalReason.DISCARDED);
         }
      });
      if (!spawner.getOminousConfig().spawnPotentialsDefinition().m_146337_()) {
         this.nextSpawnData = Optional.empty();
      }

      this.totalMobsSpawned = 0;
      this.currentMobs.clear();
      this.nextMobSpawnsAt = level.m_46467_() + spawner.getOminousConfig().ticksBetweenSpawn();
      spawner.markUpdated();
      this.cooldownEndsAt = level.m_46467_() + spawner.getOminousConfig().ticksBetweenItemSpawners();
   }

   private static void transformBadOmenIntoTrialOmen(Player player) {
   }

   public boolean isReadyToOpenShutter(ServerLevel level, float delay, int targetCooldownLength) {
      long i = this.cooldownEndsAt - targetCooldownLength;
      return (float)level.m_46467_() >= (float)i + delay;
   }

   public boolean isReadyToEjectItems(ServerLevel level, float delay, int targetCooldownLength) {
      long i = this.cooldownEndsAt - targetCooldownLength;
      return (float)(level.m_46467_() - i) % delay == 0.0F;
   }

   public boolean isCooldownFinished(ServerLevel level) {
      return level.m_46467_() >= this.cooldownEndsAt;
   }

   public void setEntityId(TrialSpawner spawner, RandomSource random, EntityType<?> entityType) {
      this.getOrCreateNextSpawnData(spawner, random).m_186567_().m_128359_("id", BuiltInRegistries.f_256780_.m_7981_(entityType).toString());
   }

   protected SpawnData getOrCreateNextSpawnData(TrialSpawner spawner, RandomSource random) {
      if (this.nextSpawnData.isPresent()) {
         return this.nextSpawnData.get();
      }

      SimpleWeightedRandomList<SpawnData> simpleweightedrandomlist = spawner.getConfig().spawnPotentialsDefinition();
      Optional<SpawnData> optional = simpleweightedrandomlist.m_146337_()
         ? this.nextSpawnData
         : simpleweightedrandomlist.m_216829_(random).map(Wrapper::m_146310_);
      this.nextSpawnData = Optional.of(optional.orElseGet(SpawnData::new));
      spawner.markUpdated();
      return this.nextSpawnData.get();
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(TrialSpawner spawner, Level level, TrialSpawnerState spawnerState) {
      if (!spawnerState.hasSpinningMob()) {
         return null;
      }

      if (this.displayEntity == null) {
         CompoundTag compoundtag = this.getOrCreateNextSpawnData(spawner, level.m_213780_()).m_186567_();
         if (compoundtag.m_128425_("id", 8)) {
            this.displayEntity = EntityType.m_20645_(compoundtag, level, Function.identity());
         }
      }

      return this.displayEntity;
   }

   public CompoundTag getUpdateTag(TrialSpawnerState spawnerState) {
      CompoundTag compoundtag = new CompoundTag();
      if (spawnerState == TrialSpawnerState.ACTIVE) {
         compoundtag.m_128356_("next_mob_spawns_at", this.nextMobSpawnsAt);
      }

      this.nextSpawnData
         .ifPresent(
            p_338045_ -> compoundtag.m_128365_(
               "spawn_data",
               (Tag)SpawnData.f_186559_.encodeStart(NbtOps.f_128958_, p_338045_).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
            )
         );
      return compoundtag;
   }

   public double getSpin() {
      return this.spin;
   }

   public double getOSpin() {
      return this.oSpin;
   }

   SimpleWeightedRandomList<ItemStack> getDispensingItems(ServerLevel level, TrialSpawnerConfig config, BlockPos pos) {
      if (this.dispensing != null) {
         return this.dispensing;
      }

      LootTable loottable = level.m_7654_().m_278653_().m_278676_(config.itemsToDropWhenOminous());
      LootParams lootparams = new net.minecraft.world.level.storage.loot.LootParams.Builder(level).m_287235_(LootContextParamSets.f_81410_);
      long i = lowResolutionPosition(level, pos);
      ObjectArrayList<ItemStack> objectarraylist = loottable.m_287214_(lootparams, i);
      if (objectarraylist.isEmpty()) {
         return SimpleWeightedRandomList.m_185864_();
      }

      Builder<ItemStack> builder = new Builder();
      ObjectListIterator var10 = objectarraylist.iterator();

      while (var10.hasNext()) {
         ItemStack itemstack = (ItemStack)var10.next();
         builder.m_146271_(itemstack.m_255036_(1), itemstack.m_41613_());
      }

      this.dispensing = builder.m_146270_();
      return this.dispensing;
   }

   private static long lowResolutionPosition(ServerLevel level, BlockPos pos) {
      BlockPos blockpos = new BlockPos(Mth.m_14143_(pos.m_123341_() / 30.0F), Mth.m_14143_(pos.m_123342_() / 20.0F), Mth.m_14143_(pos.m_123343_() / 30.0F));
      return level.m_7328_() + blockpos.m_121878_();
   }
}
