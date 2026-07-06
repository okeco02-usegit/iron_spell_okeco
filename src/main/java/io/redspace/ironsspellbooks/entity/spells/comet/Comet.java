package io.redspace.ironsspellbooks.entity.spells.comet;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Comet extends AbstractMagicProjectile {
   public Comet(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
   }

   public Comet(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.COMET.get(), pLevel);
      this.m_5602_(pShooter);
   }

   public void shoot(Vec3 rotation, float innaccuracy) {
      Vec3 offset = Utils.getRandomVec3(1.0).m_82541_().m_82490_(innaccuracy);
      super.shoot(rotation.m_82549_(offset));
   }

   @Override
   public void trailParticles() {
      Vec3 vec = this.m_20184_();
      double length = vec.m_82553_();
      int count = (int)Math.min(20L, Math.round(length) * 4L) + 1;
      float f = (float)length / count;

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.04);
         Vec3 p = vec.m_82490_(f * i);
         this.f_19853_
            .m_7106_(
               ParticleHelper.UNSTABLE_ENDER,
               this.m_20185_() + random.f_82479_ + p.f_82479_,
               this.m_20186_() + random.f_82480_ + p.f_82480_,
               this.m_20189_() + random.f_82481_ + p.f_82481_,
               random.f_82479_,
               random.f_82480_,
               random.f_82481_
            );
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(
         this.f_19853_,
         new BlastwaveParticleOptions(((AbstractSpell)SpellRegistry.STARFALL_SPELL.get()).getSchoolType().getTargetingColor(), 1.25F),
         x,
         y,
         z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
   }

   @Override
   public float getSpeed() {
      return 1.85F;
   }

   @Override
   protected void doImpactSound(Supplier<SoundEvent> sound) {
      this.f_19853_
         .m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), sound.get(), SoundSource.NEUTRAL, 0.8F, 1.35F + Utils.random.m_188501_() * 0.3F);
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(() -> SoundEvents.f_11913_);
   }

   @Override
   protected void m_6532_(HitResult hitResult) {
      if (!this.f_19853_.f_46443_) {
         this.impactParticles(this.f_19790_, this.f_19791_, this.f_19792_);
         this.getImpactSound().ifPresent(this::doImpactSound);
         float explosionRadius = this.getExplosionRadius();

         for (Entity entity : this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius))) {
            double distance = entity.m_20238_(hitResult.m_82450_());
            if (distance < explosionRadius * explosionRadius && this.m_5603_(entity)) {
               DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.STARFALL_SPELL.get()).getDamageSource(this, this.m_19749_()));
            }
         }

         this.discardHelper(hitResult);
      }
   }
}
