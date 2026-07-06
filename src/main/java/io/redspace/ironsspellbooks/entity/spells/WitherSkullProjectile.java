package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WitherSkullProjectile extends AbstractMagicProjectile {
   float speed = 1.0F;

   public WitherSkullProjectile(EntityType<? extends AbstractMagicProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public WitherSkullProjectile(LivingEntity shooter, Level level, float speed, float damage) {
      super((EntityType<? extends Projectile>)EntityRegistry.WITHER_SKULL_PROJECTILE.get(), level);
      this.m_5602_(shooter);
      this.speed = speed;
      this.damage = damage;
      this.explosionRadius = 2.0F;
      this.shoot(shooter.m_20154_());
      this.m_20242_(true);
   }

   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20191_().m_82399_();
      this.f_19853_.m_7106_(ParticleTypes.f_123762_, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 0.0, 0.0, 0.0);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return this.speed;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   @Override
   protected void m_6532_(HitResult hitResult) {
      if (!this.m_9236_().f_46443_) {
         for (Entity entity : this.m_9236_().m_45933_(this, this.m_20191_().m_82400_(this.explosionRadius))) {
            double distance = entity.m_20238_(hitResult.m_82450_());
            if (distance < this.explosionRadius * this.explosionRadius && this.m_5603_(entity)) {
               float damage = (float)(this.damage * (1.0 - distance / (this.explosionRadius * this.explosionRadius)));
               AbstractSpell spell = (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get();
               DamageSources.applyDamage(entity, damage, spell.getDamageSource(this, this.m_19749_()));
            }
         }

         this.f_19853_.m_255391_(this, this.m_20185_(), this.m_20186_(), this.m_20189_(), 0.0F, false, ExplosionInteraction.NONE);
         this.discardHelper(hitResult);
      }
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
   }
}
