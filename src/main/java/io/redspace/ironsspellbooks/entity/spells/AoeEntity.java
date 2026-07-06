package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

public abstract class AoeEntity extends Projectile implements NoKnockbackProjectile {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.m_135353_(AoeEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> DATA_CIRCULAR = SynchedEntityData.m_135353_(AoeEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> DATA_DELAY = SynchedEntityData.m_135353_(AoeEntity.class, EntityDataSerializers.f_135028_);
   protected float damage;
   protected int duration = 600;
   protected int reapplicationDelay = 10;
   protected int durationOnUse;
   protected float radiusOnUse;
   protected float radiusPerTick;
   protected int effectDuration;

   public int getReapplicationDelay() {
      return this.reapplicationDelay;
   }

   public int getDurationOnUse() {
      return this.durationOnUse;
   }

   public float getRadiusOnUse() {
      return this.radiusOnUse;
   }

   public float getRadiusPerTick() {
      return this.radiusPerTick;
   }

   public void setReapplicationDelay(int reapplicationDelay) {
      this.reapplicationDelay = reapplicationDelay;
   }

   public void setDurationOnUse(int durationOnUse) {
      this.durationOnUse = durationOnUse;
   }

   public void setRadiusOnUse(float radiusOnUse) {
      this.radiusOnUse = radiusOnUse;
   }

   public void setRadiusPerTick(float radiusPerTick) {
      this.radiusPerTick = radiusPerTick;
   }

   public AoeEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19794_ = true;
      this.f_19850_ = false;
   }

   protected float particleYOffset() {
      return 0.0F;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   public void setEffectDuration(int effectDuration) {
      this.effectDuration = effectDuration;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ > this.getDelay()) {
         if (!this.f_19853_.f_46443_) {
            if (this.f_19797_ > this.duration + this.getDelay()) {
               this.m_146870_();
               return;
            }

            if (this.f_19797_ % this.reapplicationDelay == 0) {
               this.checkHits();
            }

            if (this.f_19797_ % 5 == 0) {
               this.setRadius(this.getRadius() + this.radiusPerTick);
            }
         } else {
            this.ambientParticles();
         }
      }

      this.m_146884_(this.m_20182_().m_82549_(this.m_20184_()));
   }

   protected void checkHits() {
      if (!this.f_19853_.f_46443_) {
         List<LivingEntity> targets = this.f_19853_
            .m_45976_(LivingEntity.class, this.m_20191_().m_82377_(this.getInflation().f_82479_, this.getInflation().f_82480_, this.getInflation().f_82481_));
         boolean hit = false;
         float radiusSqr = this.getRadius();
         radiusSqr *= radiusSqr;

         for (LivingEntity target : targets) {
            if (this.m_5603_(target) && (!this.isCircular() || target.m_20280_(this) < radiusSqr) && this.canHitTargetForGroundContext(target)) {
               this.applyEffect(target);
               hit = true;
            }
         }

         if (hit) {
            this.setRadius(this.getRadius() + this.radiusOnUse);
            this.duration = this.duration + this.durationOnUse;
            this.onPostHit();
         }
      }
   }

   protected Vec3 getInflation() {
      return Vec3.f_82478_;
   }

   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return target.m_20096_() || target.m_20186_() - this.m_20186_() < 0.5;
   }

   protected boolean m_5603_(Entity pTarget) {
      return (this.m_19749_() == null || pTarget != this.m_19749_() && !this.m_19749_().m_7307_(pTarget)) && super.m_5603_(pTarget);
   }

   public void onPostHit() {
   }

   public abstract void applyEffect(LivingEntity var1);

   public void ambientParticles(ParticleOptions particle) {
      float f = this.getParticleCount();
      f = Mth.m_14036_(f * this.getRadius(), f / 4.0F, f * 10.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.f_19796_.m_188501_() > f - i) {
            return;
         }

         float r = this.getRadius();
         Vec3 pos;
         if (this.isCircular()) {
            float distance = r * (1.0F - this.f_19796_.m_188501_() * this.f_19796_.m_188501_());
            float theta = this.f_19796_.m_188501_() * 6.282F;
            pos = new Vec3(distance * Mth.m_14089_(theta), 0.2F, distance * Mth.m_14031_(theta));
         } else {
            pos = new Vec3(Utils.getRandomScaled(r * 0.85F), 0.2F, Utils.getRandomScaled(r * 0.85F));
         }

         Vec3 motion = new Vec3(Utils.getRandomScaled(0.03F), this.f_19796_.m_188500_() * 0.01F, Utils.getRandomScaled(0.03F))
            .m_82490_(this.getParticleSpeedModifier());
         Vec3 vec3 = new Vec3(this.m_20185_() + pos.f_82479_, this.m_20186_() + pos.f_82480_ + 1.0, this.m_20189_() + pos.f_82481_);
         vec3 = Utils.moveToRelativeGroundLevel(this.f_19853_, vec3, 1, 2);
         this.f_19853_
            .m_7106_(particle, vec3.f_82479_, vec3.f_82480_ + this.particleYOffset(), vec3.f_82481_, motion.f_82479_, motion.f_82480_, motion.f_82481_);
      }
   }

   public void ambientParticles() {
      if (this.f_19853_.f_46443_) {
         this.getParticle().ifPresent(this::ambientParticles);
      }
   }

   protected float getParticleSpeedModifier() {
      return 1.0F;
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   public boolean m_6060_() {
      return false;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_RADIUS, 2.0F);
      this.f_19804_.m_135372_(DATA_CIRCULAR, false);
      this.f_19804_.m_135372_(DATA_DELAY, 0);
   }

   public void m_7350_(EntityDataAccessor<?> pKey) {
      if (DATA_RADIUS.equals(pKey)) {
         this.m_6210_();
         if (this.getRadius() < 0.1F) {
            this.m_146870_();
         }
      }

      super.m_7350_(pKey);
   }

   public void setRadius(float pRadius) {
      if (!this.f_19853_.f_46443_) {
         this.m_20088_().m_135381_(DATA_RADIUS, Mth.m_14036_(pRadius, 0.0F, 32.0F));
      }
   }

   public void setDuration(int duration) {
      if (!this.f_19853_.f_46443_) {
         this.duration = duration;
      }
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDelay(int delay) {
      if (!this.f_19853_.f_46443_) {
         this.f_19804_.m_135381_(DATA_DELAY, delay);
      }
   }

   public int getDelay() {
      return (Integer)this.f_19804_.m_135370_(DATA_DELAY);
   }

   public void m_6210_() {
      double d0 = this.m_20185_();
      double d1 = this.m_20186_();
      double d2 = this.m_20189_();
      super.m_6210_();
      this.m_6034_(d0, d1, d2);
   }

   public float getRadius() {
      return (Float)this.m_20088_().m_135370_(DATA_RADIUS);
   }

   public Boolean isCircular() {
      return (Boolean)this.m_20088_().m_135370_(DATA_CIRCULAR);
   }

   public void setCircular() {
      this.m_20088_().m_135381_(DATA_CIRCULAR, true);
   }

   public abstract float getParticleCount();

   public abstract Optional<ParticleOptions> getParticle();

   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, 1.2F);
   }

   protected void m_7380_(CompoundTag pCompound) {
      pCompound.m_128405_("Age", this.f_19797_);
      pCompound.m_128405_("Duration", this.duration);
      pCompound.m_128405_("ReapplicationDelay", this.reapplicationDelay);
      pCompound.m_128405_("DurationOnUse", this.durationOnUse);
      pCompound.m_128350_("RadiusOnUse", this.radiusOnUse);
      pCompound.m_128350_("RadiusPerTick", this.radiusPerTick);
      pCompound.m_128350_("Radius", this.getRadius());
      pCompound.m_128350_("Damage", this.getDamage());
      pCompound.m_128379_("Circular", this.isCircular());
      pCompound.m_128405_("EffectDuration", this.effectDuration);
      pCompound.m_128405_("Delay", this.getDelay());
      super.m_7380_(pCompound);
   }

   protected void m_7378_(CompoundTag pCompound) {
      this.f_19797_ = pCompound.m_128451_("Age");
      if (pCompound.m_128451_("Duration") > 0) {
         this.duration = pCompound.m_128451_("Duration");
      }

      if (pCompound.m_128451_("ReapplicationDelay") > 0) {
         this.reapplicationDelay = pCompound.m_128451_("ReapplicationDelay");
      }

      if (pCompound.m_128451_("Radius") > 0) {
         this.setRadius(pCompound.m_128457_("Radius"));
      }

      if (pCompound.m_128451_("DurationOnUse") > 0) {
         this.durationOnUse = pCompound.m_128451_("DurationOnUse");
      }

      if (pCompound.m_128451_("RadiusOnUse") > 0) {
         this.radiusOnUse = pCompound.m_128457_("RadiusOnUse");
      }

      if (pCompound.m_128451_("RadiusPerTick") > 0) {
         this.radiusPerTick = pCompound.m_128457_("RadiusPerTick");
      }

      if (pCompound.m_128451_("EffectDuration") > 0) {
         this.effectDuration = pCompound.m_128451_("EffectDuration");
      }

      if (pCompound.m_128451_("Delay") > 0) {
         this.setDelay(pCompound.m_128451_("Delay"));
      }

      this.setDamage(pCompound.m_128457_("Damage"));
      if (pCompound.m_128471_("Circular")) {
         this.setCircular();
      }

      super.m_7378_(pCompound);
   }
}
