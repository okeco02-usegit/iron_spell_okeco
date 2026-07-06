package io.redspace.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.TierSortingRegistry;

public class TouchDigSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "touch_dig");
   private static final int distance = 8;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(0.5)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_(
            "ui.irons_spellbooks.harvest_level", new Object[]{Component.m_237115_(this.getHarvestLevel(this.getSpellPower(spellLevel, caster)).descriptionId)}
         ),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{8})
      );
   }

   public TouchDigSpell() {
      this.baseManaCost = 15;
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 3;
      this.castTime = 0;
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.TOUCH_DIG_CAST.get());
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   private TouchDigSpell.HarvestData getHarvestLevel(double spellPower) {
      if (spellPower >= 15.0) {
         return TouchDigSpell.HarvestData.NETHERITE;
      } else {
         return spellPower >= 13.0 ? TouchDigSpell.HarvestData.DIAMOND : TouchDigSpell.HarvestData.IRON;
      }
   }

   private boolean canBreak(Level level, BlockPos blockPos, double spellPower) {
      BlockState blockState = level.m_8055_(blockPos);
      return blockState.m_60800_(level, blockPos) >= 0.0F
         && TierSortingRegistry.isCorrectTierForDrops(this.getHarvestLevel(spellPower).harvestTier(), blockState);
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer serverPlayer && serverPlayer.f_8941_.m_9290_() == GameType.ADVENTURE) {
         serverPlayer.f_8906_
            .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_adventure").m_130940_(ChatFormatting.RED)));
         return false;
      } else {
         BlockHitResult blockHitResult = Utils.getTargetBlock(level, entity, Fluid.NONE, 8.0);
         if (blockHitResult.m_6662_() != Type.BLOCK) {
            if (entity instanceof ServerPlayer serverPlayer) {
               serverPlayer.f_8906_
                  .m_9829_(
                     new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_target_block").m_130940_(ChatFormatting.RED))
                  );
            }

            return false;
         } else if (!this.canBreak(level, blockHitResult.m_82425_(), this.getSpellPower(spellLevel, entity))) {
            if (entity instanceof ServerPlayer serverPlayer) {
               serverPlayer.f_8906_
                  .m_9829_(
                     new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_harvest_level").m_130940_(ChatFormatting.RED))
                  );
            }

            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      BlockHitResult blockhit = Utils.getTargetBlock(world, entity, Fluid.NONE, 8.0);
      Vec3 vec = blockhit.m_82450_();
      Vec3 particle = entity.m_146892_().m_82492_(0.0, 0.1, 0.0);
      int count = (int)vec.m_82554_(particle) * 2;

      for (int i = 0; i < count; i++) {
         Vec3 pos = vec.m_82549_(particle.m_82546_(vec).m_82490_((double)i / count));
         MagicManager.spawnParticles(world, ParticleTypes.f_123797_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, false);
      }

      MagicManager.spawnParticles(world, ParticleTypes.f_123797_, vec.f_82479_, vec.f_82480_, vec.f_82481_, 25, 0.0, 0.0, 0.0, 0.2, false);
      if (this.canBreak(world, blockhit.m_82425_(), this.getSpellPower(spellLevel, entity))
         && !(
            entity instanceof ServerPlayer serverPlayer
               && ForgeHooks.onBlockBreakEvent(world, serverPlayer.f_8941_.m_9290_(), serverPlayer, blockhit.m_82425_()) == -1
         )) {
         this.doDestroyBlock(world, blockhit.m_82425_(), entity);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private void doDestroyBlock(Level level, BlockPos pos, LivingEntity livingEntity) {
      BlockState blockstate = level.m_8055_(pos);
      if (!blockstate.m_60795_()) {
         FluidState fluidstate = level.m_6425_(pos);
         if (!(blockstate.m_60734_() instanceof BaseFireBlock)) {
            level.m_46796_(2001, pos, Block.m_49956_(blockstate));
         }

         BlockEntity blockentity = blockstate.m_155947_() ? level.m_7702_(pos) : null;
         Block.m_49881_(blockstate, level, pos, blockentity, livingEntity, livingEntity.m_21205_());
         if (level.m_7731_(pos, fluidstate.m_76188_(), 3)) {
            level.m_220407_(GameEvent.f_157794_, pos, Context.m_223719_(livingEntity, blockstate));
         }
      }
   }

   record HarvestData(Tier harvestTier, String descriptionId) {
      static TouchDigSpell.HarvestData NETHERITE = new TouchDigSpell.HarvestData(Tiers.NETHERITE, "ui.irons_spellbooks.harvest_level.netherite");
      static TouchDigSpell.HarvestData DIAMOND = new TouchDigSpell.HarvestData(Tiers.DIAMOND, "ui.irons_spellbooks.harvest_level.diamond");
      static TouchDigSpell.HarvestData IRON = new TouchDigSpell.HarvestData(Tiers.IRON, "ui.irons_spellbooks.harvest_level.iron");
   }
}
