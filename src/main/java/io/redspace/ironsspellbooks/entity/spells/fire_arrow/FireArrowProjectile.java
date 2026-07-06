package io.redspace.ironsspellbooks.entity.spells.fire_arrow;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireArrowProjectile extends AbstractMagicProjectile {
   boolean suspendGravity;

   public FireArrowProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      super.m_20242_(true);
   }

   public FireArrowProjectile(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.FIRE_ARROW_PROJECTILE.get(), pLevel);
      this.m_5602_(pShooter);
   }

   public void m_20242_(boolean pNoGravity) {
      this.suspendGravity = pNoGravity;
      super.m_20242_(pNoGravity);
   }

   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20184_();
      double d0 = this.m_20185_() - vec3.f_82479_;
      double d1 = this.m_20186_() - vec3.f_82480_;
      double d2 = this.m_20189_() - vec3.f_82481_;
      int count = Mth.m_14045_((int)(vec3.m_82556_() * 4.0), 1, 4);

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(1.0).m_82549_(vec3.m_82541_()).m_82490_(0.25);
         float f = (float)i / count;
         double x = Mth.m_14139_(f, d0, this.m_20185_() + vec3.f_82479_);
         double y = Mth.m_14139_(f, d1, this.m_20186_() + vec3.f_82480_) - 0.4;
         double z = Mth.m_14139_(f, d2, this.m_20189_() + vec3.f_82481_);
         this.f_19853_
            .m_6493_(
               ParticleHelper.FIRE,
               true,
               x - random.f_82479_,
               y + 0.5 - random.f_82480_,
               z - random.f_82481_,
               random.f_82479_ * 0.5,
               random.f_82480_ * 0.5,
               random.f_82481_ * 0.5
            );
      }
   }

   @Override
   public void m_8119_() {
      if (this.f_19797_ == 10 && !this.suspendGravity) {
         this.m_20242_(false);
      }

      super.m_8119_();
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return 2.0F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(() -> SoundEvents.f_11913_);
   }

   @Override
   protected void m_6532_(HitResult hitResult) {
      if (!this.f_19853_.f_46443_) {
         float directDamage = this.damage;
         float explosionDamage = directDamage * 0.5F;
         UUID ignore = null;
         if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity directHit = entityHitResult.m_82443_();
            DamageSources.applyDamage(directHit, directDamage, ((AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get()).getDamageSource(this, this.m_19749_()));
            ignore = directHit.m_20148_();
         }

         float explosionRadius = this.getExplosionRadius();
         float explosionRadiusSqr = explosionRadius * explosionRadius;
         List<Entity> entities = this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius));
         Vec3 losPoint = Utils.raycastForBlock(this.f_19853_, this.m_20182_(), this.m_20182_().m_82520_(0.0, 2.0, 0.0), Fluid.NONE).m_82450_();

         for (Entity entity : entities) {
            double distanceSqr = entity.m_20238_(hitResult.m_82450_());
            if (ignore != entity.m_20148_()
               && distanceSqr < explosionRadiusSqr
               && this.m_5603_(entity)
               && Utils.hasLineOfSight(this.f_19853_, losPoint, entity.m_20191_().m_82399_(), true)) {
               double p = 1.0 - distanceSqr / explosionRadiusSqr;
               float damage = (float)(explosionDamage * p);
               DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get()).getDamageSource(this, this.m_19749_()));
            }
         }

         if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
         }

         PacketDistributor.sendToPlayersTrackingEntity(
            this, new FieryExplosionParticlesPacket(hitResult.m_82450_().m_82546_(this.m_20184_().m_82490_(0.25)), this.getExplosionRadius() * 0.7F)
         );
         this.m_5496_(SoundEvents.f_11913_, 4.0F, (1.0F + (this.f_19853_.f_46441_.m_188501_() - this.f_19853_.f_46441_.m_188501_()) * 0.2F) * 0.7F);
         this.discardHelper(hitResult);
      }
   }
}
