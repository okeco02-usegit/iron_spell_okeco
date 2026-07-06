package io.redspace.ironsspellbooks.entity.spells.creeper_head;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.spells.evocation.ChainCreeperSpell;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CreeperHeadProjectile extends AbstractMagicProjectile {
   protected boolean chainOnKill;
   protected int chainCount;
   protected float speed;

   public CreeperHeadProjectile(EntityType<? extends CreeperHeadProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.chainOnKill = false;
   }

   public CreeperHeadProjectile(LivingEntity shooter, Level level, float speed, float damage) {
      super((EntityType<? extends Projectile>)EntityRegistry.CREEPER_HEAD_PROJECTILE.get(), level);
      this.m_5602_(shooter);
      this.speed = speed;
      this.damage = damage;
      this.explosionRadius = 5.0F;
      this.shoot(shooter.m_20154_());
   }

   public CreeperHeadProjectile(LivingEntity shooter, Level level, Vec3 speed, float damage) {
      super((EntityType<? extends Projectile>)EntityRegistry.CREEPER_HEAD_PROJECTILE.get(), level);
      this.m_5602_(shooter);
      this.damage = damage;
      this.explosionRadius = 5.0F;
      this.speed = (float)speed.m_82553_();
      this.shoot(speed);
   }

   public void setChainOnKill(boolean chain) {
      this.chainOnKill = chain;
   }

   public void setChainCount(int count) {
      this.chainCount = count;
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
            double distance = entity.m_20182_().m_82554_(hitResult.m_82450_());
            if (distance < this.explosionRadius && this.m_5603_(entity)) {
               if (entity instanceof LivingEntity livingEntity && livingEntity.m_21224_()) {
                  break;
               }

               float damage = (float)(this.damage * (1.0 - Math.pow(distance / this.explosionRadius, 2.0)));
               DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.LOB_CREEPER_SPELL.get()).getDamageSource(this, this.m_19749_()));
               if (this.chainOnKill && entity instanceof LivingEntity livingEntity && livingEntity.m_21224_()) {
                  ChainCreeperSpell.summonCreeperRing(
                     this.m_9236_(),
                     this.m_19749_() instanceof LivingEntity livingOwner ? livingOwner : null,
                     livingEntity.m_146892_(),
                     this.damage * 0.85F,
                     this.chainCount
                  );
               }
            }
         }

         double x = this.m_20185_();
         double y = this.m_20186_();
         double z = this.m_20189_();
         MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123813_, x, y, z, 3, 0.1, 0.1, 0.1, 0.3, true);
         MagicManager.spawnParticles(
            this.f_19853_, new BlastwaveParticleOptions(1.0F, 1.0F, 1.0F, this.explosionRadius * 1.2F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0, true
         );
         this.m_5496_(SoundEvents.f_11913_, 3.0F, Utils.random.m_188501_() * 0.2F + 0.9F);
         this.discardHelper(hitResult);
      }
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
   }
}
