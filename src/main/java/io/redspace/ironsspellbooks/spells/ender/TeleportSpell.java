package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.particles.TeleportParticlesPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TeleportSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "teleport");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(3.0)
      .build();

   public TeleportSpell() {
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.baseManaCost = 20;
      this.manaCostPerLevel = 5;
      this.castTime = 0;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.f_11852_);
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      TeleportSpell.TeleportData teleportData = (TeleportSpell.TeleportData)playerMagicData.getAdditionalCastData();
      Vec3 dest = null;
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         if (potentialTarget != null) {
            dest = potentialTarget;
         }
      }

      if (dest == null) {
         dest = findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
      }

      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new TeleportParticlesPacket(entity.m_20182_(), dest));
      if (entity.m_20159_()) {
         entity.m_8127_();
      }

      Utils.handleSpellTeleport(this, entity, dest);
      entity.m_183634_();
      playerMagicData.resetAdditionalCastData();
      entity.m_5496_(this.getCastFinishSound().get(), 2.0F, 1.0F);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static Vec3 findTeleportLocation(Level level, LivingEntity entity, float maxDistance) {
      BlockHitResult blockHitResult = Utils.getTargetBlock(level, entity, Fluid.NONE, maxDistance);
      return solveTeleportDestination(level, entity, blockHitResult.m_82425_(), blockHitResult.m_82450_());
   }

   public static Vec3 solveTeleportDestination(Level level, LivingEntity entity, BlockPos blockPos, Vec3 vec3) {
      BlockPos pos = blockPos;
      Vec3 bbOffset = entity.m_20156_().m_82541_().m_82542_(entity.m_20205_() / 3.0F, 0.0, entity.m_20206_() / 3.0F);
      Vec3 bbImpact = vec3.m_82546_(bbOffset);
      double ledgeY = level.m_45547_(new ClipContext(Vec3.m_82539_(pos).m_82520_(0.0, 3.0, 0.0), Vec3.m_82539_(pos), Block.COLLIDER, Fluid.NONE, null))
         .m_82450_()
         .f_82480_;
      boolean isAir = level.m_8055_(new BlockPos(new Vec3i(pos.m_123341_(), (int)ledgeY, pos.m_123343_())).m_7494_()).m_60795_();
      boolean los = level.m_45547_(new ClipContext(bbImpact, bbImpact.m_82520_(0.0, ledgeY - pos.m_123342_(), 0.0), Block.COLLIDER, Fluid.NONE, entity))
            .m_6662_()
         == Type.MISS;
      return isAir && los && Math.abs(ledgeY - pos.m_123342_()) <= 3.0
         ? new Vec3(pos.m_123341_() + 0.5, ledgeY + 0.001, pos.m_123343_() + 0.5)
         : level.m_45547_(new ClipContext(bbImpact, bbImpact.m_82520_(0.0, -entity.m_20206_(), 0.0), Block.COLLIDER, Fluid.NONE, entity))
            .m_82450_()
            .m_82520_(0.0, 0.001, 0.0);
   }

   public static void particleCloud(Level level, Vec3 pos) {
      if (level.f_46443_) {
         double width = 0.5;
         float height = 1.0F;

         for (int i = 0; i < 55; i++) {
            double x = pos.f_82479_ + Utils.random.m_188500_() * width * 2.0 - width;
            double y = pos.f_82480_ + height + Utils.random.m_188500_() * height * 1.2 * 2.0 - height * 1.2;
            double z = pos.f_82481_ + Utils.random.m_188500_() * width * 2.0 - width;
            double dx = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            double dy = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            double dz = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            level.m_6493_(ParticleTypes.f_123760_, true, x, y, z, dx, dy, dz);
         }
      }
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * this.getSpellPower(spellLevel, null));
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }

   public static class TeleportData implements ICastData {
      private Vec3 teleportTargetPosition;

      public TeleportData(Vec3 teleportTargetPosition) {
         this.teleportTargetPosition = teleportTargetPosition;
      }

      public void setTeleportTargetPosition(Vec3 targetPosition) {
         this.teleportTargetPosition = targetPosition;
      }

      public Vec3 getTeleportTargetPosition() {
         return this.teleportTargetPosition;
      }

      @Override
      public void reset() {
      }
   }
}
