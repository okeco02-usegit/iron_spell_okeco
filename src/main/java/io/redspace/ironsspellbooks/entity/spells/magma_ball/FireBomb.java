package io.redspace.ironsspellbooks.entity.spells.magma_ball;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

public class FireBomb extends AbstractMagicProjectile {
   float aoeDamage;

   public FireBomb(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public FireBomb(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.FIRE_BOMB.get(), level);
      this.m_5602_(shooter);
   }

   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20184_();
      double d0 = this.m_20185_() - vec3.f_82479_;
      double d1 = this.m_20186_() - vec3.f_82480_;
      double d2 = this.m_20189_() - vec3.f_82481_;

      for (int i = 0; i < 4; i++) {
         Vec3 random = Utils.getRandomVec3(0.2);
         this.f_19853_
            .m_7106_(
               ParticleTypes.f_123762_,
               d0 - random.f_82479_,
               d1 + 0.5 - random.f_82480_,
               d2 - random.f_82481_,
               random.f_82479_ * 0.5,
               random.f_82480_ * 0.5,
               random.f_82481_ * 0.5
            );
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123756_, x, y, z, 30, 1.5, 0.1, 1.5, 1.0, false);
   }

   @Override
   public float getSpeed() {
      return 0.65F;
   }

   @Override
   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      this.createFireField(hitresult.m_82450_());
      float explosionRadius = this.getExplosionRadius();

      for (Entity entity : this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius))) {
         double distance = entity.m_20238_(hitresult.m_82450_());
         if (distance < explosionRadius * explosionRadius
            && this.m_5603_(entity)
            && Utils.hasLineOfSight(this.f_19853_, hitresult.m_82450_(), entity.m_20182_().m_82520_(0.0, entity.m_20192_() * 0.5F, 0.0), true)) {
            double p = 1.0 - Math.pow(Math.sqrt(distance) / explosionRadius, 3.0);
            float damage = (float)(this.damage * p);
            DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.MAGMA_BOMB_SPELL.get()).getDamageSource(this, this.m_19749_()));
         }
      }

      this.discardHelper(hitresult);
   }

   public void createFireField(Vec3 location) {
      if (!this.f_19853_.f_46443_) {
         FireField fire = new FireField(this.f_19853_);
         fire.m_5602_(this.m_19749_());
         fire.setDuration(200);
         fire.setDamage(this.aoeDamage);
         fire.setRadius(this.getExplosionRadius());
         fire.setCircular();
         fire.m_20219_(location);
         this.f_19853_.m_7967_(fire);
      }
   }

   public void setAoeDamage(float damage) {
      this.aoeDamage = damage;
   }

   public float getAoeDamage() {
      return this.aoeDamage;
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128350_("AoeDamage", this.aoeDamage);
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.aoeDamage = tag.m_128457_("AoeDamage");
   }

   @Override
   protected void doImpactSound(Supplier<SoundEvent> sound) {
      this.f_19853_
         .m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), sound.get(), SoundSource.NEUTRAL, 2.0F, 1.2F + Utils.random.m_188501_() * 0.2F);
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(() -> SoundEvents.f_11913_);
   }
}
