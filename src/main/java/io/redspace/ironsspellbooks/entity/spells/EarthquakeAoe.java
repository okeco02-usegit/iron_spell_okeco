package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EarthquakeAoe extends AoeEntity implements AntiMagicSusceptible {
   public static Map<UUID, EarthquakeAoe> clientEarthquakeOrigins = new HashMap<>();
   private CameraShakeData cameraShakeData;
   private int slownessAmplifier;
   int waveAnim = -1;

   public EarthquakeAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.reapplicationDelay = 25;
      this.setCircular();
   }

   public EarthquakeAoe(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.EARTHQUAKE_AOE.get(), level);
   }

   @Override
   public void applyEffect(LivingEntity target) {
      SpellDamageSource damageSource = ((AbstractSpell)SpellRegistry.EARTHQUAKE_SPELL.get()).getDamageSource(this, this.m_19749_());
      DamageSources.ignoreNextKnockback(target);
      if (target.m_6469_(damageSource, this.getDamage())) {
         target.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 120, this.slownessAmplifier));
         target.m_20256_(target.m_20184_().m_82520_(0.0, 0.5, 0.0));
         target.f_19864_ = true;
      }
   }

   public int getSlownessAmplifier() {
      return this.slownessAmplifier;
   }

   public void setSlownessAmplifier(int slownessAmplifier) {
      this.slownessAmplifier = slownessAmplifier;
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ == 1) {
         this.createScreenShake();
      }

      if (this.f_19797_ % 20 == 1) {
         this.m_5496_((SoundEvent)SoundRegistry.EARTHQUAKE_LOOP.get(), 2.0F, 0.9F + this.f_19796_.m_188501_() * 0.15F);
      }

      if (this.f_19797_ % this.reapplicationDelay == 1) {
         this.waveAnim = 0;
         this.m_5496_((SoundEvent)SoundRegistry.EARTHQUAKE_IMPACT.get(), 1.5F, 0.9F + this.f_19796_.m_188501_() * 0.2F);
      }

      if (!this.f_19853_.f_46443_) {
         float radius = this.getRadius();
         Level level = this.f_19853_;
         int intensity = Math.min((int)(radius * radius * 0.09F), 15);

         for (int i = 0; i < intensity; i++) {
            Vec3 vec3 = this.m_20182_().m_82549_(this.uniformlyDistributedPointInRadius(radius));
            BlockPos blockPos = BlockPos.m_274446_(Utils.moveToRelativeGroundLevel(level, vec3, 4)).m_7495_();
            Utils.createTremorBlock(level, blockPos, 0.1F + this.f_19796_.m_188501_() * 0.2F);
         }

         if (this.waveAnim >= 0) {
            float circumference = this.waveAnim * 2 * 3.14F;
            int blocks = Mth.m_14045_((int)circumference, 0, 250);
            float anglePerBlock = 360.0F / blocks;

            for (int i = 0; i < blocks; i++) {
               Vec3 vec3 = new Vec3(this.waveAnim * Mth.m_14089_(anglePerBlock * i), 0.0, this.waveAnim * Mth.m_14031_(anglePerBlock * i));
               BlockPos blockPos = BlockPos.m_274446_(Utils.moveToRelativeGroundLevel(level, this.m_20182_().m_82549_(vec3), 4)).m_7495_();
               Utils.createTremorBlock(level, blockPos, 0.1F + this.f_19796_.m_188501_() * 0.2F);
            }

            if (this.waveAnim++ >= radius) {
               this.waveAnim = -1;
               if (this.f_19797_ + this.reapplicationDelay >= this.duration) {
                  this.m_146870_();
               }
            }
         }
      }
   }

   @Override
   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   @Override
   protected Vec3 getInflation() {
      return new Vec3(0.0, 5.0, 0.0);
   }

   protected void createScreenShake() {
      if (!this.f_19853_.f_46443_ && !this.m_213877_()) {
         this.cameraShakeData = new CameraShakeData(this.f_19853_, this.duration - this.f_19797_, this.m_20182_(), 15.0F);
         CameraShakeManager.addCameraShake(this.cameraShakeData);
      }
   }

   protected Vec3 uniformlyDistributedPointInRadius(float r) {
      float distance = r * (1.0F - this.f_19796_.m_188501_() * this.f_19796_.m_188501_());
      float theta = this.f_19796_.m_188501_() * 6.282F;
      return new Vec3(distance * Mth.m_14089_(theta), 0.2F, distance * Mth.m_14031_(theta));
   }

   public void m_142687_(RemovalReason pReason) {
      super.m_142687_(pReason);
      if (!this.f_19853_.f_46443_) {
         CameraShakeManager.removeCameraShake(this.cameraShakeData);
      }
   }

   @Override
   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, 3.0F);
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   @Override
   public void onAntiMagic(MagicData magicData) {
      this.m_146870_();
   }

   @Override
   protected void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("Slowness", this.slownessAmplifier);
   }

   @Override
   protected void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.slownessAmplifier = pCompound.m_128451_("Slowness");
      this.createScreenShake();
   }
}
