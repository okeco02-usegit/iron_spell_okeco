package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;

public class EchoingStrikeEntity extends AoeEntity {
   private Entity toTrack;
   public final int waitTime = 20;

   public EchoingStrikeEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setCircular();
   }

   public EchoingStrikeEntity(Level level, LivingEntity owner, float damage, float radius) {
      this((EntityType<? extends Projectile>)EntityRegistry.ECHOING_STRIKE.get(), level);
      this.m_5602_(owner);
      this.setRadius(radius);
      this.setDamage(damage);
   }

   @Override
   public void applyEffect(LivingEntity target) {
   }

   public void setTracking(Entity entity) {
      this.toTrack = entity;
   }

   @Override
   public void m_8119_() {
      if (this.toTrack != null && this.f_19797_ < 10) {
         this.m_146884_(this.toTrack.m_20182_());
      } else if (this.f_19797_ == 20) {
         this.m_5496_((SoundEvent)SoundRegistry.ECHOING_STRIKE.get(), 1.0F, Utils.random.m_216332_(8, 12) * 0.1F);
         if (!this.f_19853_.f_46443_) {
            Vec3 center = this.m_20191_().m_82399_();
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.UNSTABLE_ENDER, center.f_82479_, center.f_82480_, center.f_82481_, 25, 0.0, 0.0, 0.0, 0.18, false
            );
            MagicManager.spawnParticles(
               this.f_19853_,
               new BlastwaveParticleOptions(
                  ((AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get()).getSchoolType().getTargetingColor(), this.getRadius() * 0.9F
               ),
               center.f_82479_,
               center.f_82480_,
               center.f_82481_,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            float explosionRadius = this.getRadius();
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius));
            Vec3 losCenter = Utils.moveToRelativeGroundLevel(this.f_19853_, center, 2);
            losCenter = Utils.raycastForBlock(this.f_19853_, losCenter, losCenter.m_82520_(0.0, 3.0, 0.0), Fluid.NONE)
               .m_82450_()
               .m_82549_(losCenter)
               .m_82490_(0.5);

            for (Entity entity : entities) {
               double distanceSqr = entity.m_20238_(center);
               if (distanceSqr < explosionRadiusSqr
                  && this.m_5603_(entity)
                  && Utils.hasLineOfSight(this.f_19853_, losCenter, entity.m_20191_().m_82399_(), true)) {
                  double p = Mth.m_14008_(1.0 - distanceSqr / explosionRadiusSqr + 0.4F, 0.0, 1.0);
                  float damage = (float)(this.damage * p);
                  DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get()).getDamageSource(this, this.m_19749_()));
               }
            }
         }
      } else if (this.f_19797_ > 20) {
         this.m_146870_();
      }

      if (this.f_19853_.f_46443_ && this.f_19797_ < 10) {
         Vec3 position = this.m_20191_().m_82399_();

         for (int i = 0; i < 3; i++) {
            Vec3 vec3 = Utils.getRandomVec3(1.0);
            vec3 = vec3.m_82559_(vec3)
               .m_82542_(Mth.m_14205_(vec3.f_82479_), Mth.m_14205_(vec3.f_82480_), Mth.m_14205_(vec3.f_82481_))
               .m_82490_(this.getRadius())
               .m_82549_(position);
            Vec3 motion = position.m_82546_(vec3).m_82490_(0.125);
            this.f_19853_
               .m_7106_(ParticleHelper.UNSTABLE_ENDER, vec3.f_82479_, vec3.f_82480_ - 0.5, vec3.f_82481_, motion.f_82479_, motion.f_82480_, motion.f_82481_);
         }
      }
   }

   @Override
   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   @Override
   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }
}
