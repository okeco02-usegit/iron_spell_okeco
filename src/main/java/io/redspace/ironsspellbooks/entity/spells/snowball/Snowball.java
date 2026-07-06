package io.redspace.ironsspellbooks.entity.spells.snowball;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Snowball extends AbstractMagicProjectile {
   public Snowball(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public Snowball(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.SNOWBALL.get(), level);
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
               ParticleHelper.SNOW_DUST,
               d0 - random.f_82479_,
               d1 + 0.5 - random.f_82480_,
               d2 - random.f_82481_,
               random.f_82479_ * 0.5,
               random.f_82480_ * 0.5,
               random.f_82481_ * 0.5
            );
      }

      Vec3 random = Utils.getRandomVec3(0.2);
      this.f_19853_
         .m_7106_(
            ParticleHelper.SNOWFLAKE,
            d0 - random.f_82479_,
            d1 + 0.5 - random.f_82480_,
            d2 - random.f_82481_,
            random.f_82479_ * 0.5,
            random.f_82480_ * 0.5,
            random.f_82481_ * 0.5
         );
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.SNOW_DUST, x, y, z, 50, 0.5, 0.5, 0.5, 0.2, true);
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.SNOWFLAKE, x, y, z, 50, 0.5, 0.5, 0.5, 0.2, false);
   }

   @Override
   public float getSpeed() {
      return 1.0F;
   }

   @Override
   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      this.createFrostField(Utils.moveToRelativeGroundLevel(this.f_19853_, hitresult.m_82450_(), 2));
      float explosionRadius = this.getExplosionRadius();

      for (Entity entity : this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius))) {
         double distance = entity.m_20238_(hitresult.m_82450_());
         if (entity instanceof LivingEntity livingEntity
            && distance < explosionRadius * explosionRadius
            && this.m_5603_(entity)
            && !DamageSources.isFriendlyFireBetween(this.m_19749_(), entity)
            && Utils.hasLineOfSight(this.f_19853_, hitresult.m_82450_(), entity.m_20182_().m_82520_(0.0, entity.m_20192_() * 0.5F, 0.0), true)) {
            livingEntity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.CHILLED.get(), (int)this.getDamage()));
         }
      }

      this.discardHelper(hitresult);
   }

   public void createFrostField(Vec3 location) {
      if (!this.f_19853_.f_46443_) {
         FrostField fire = new FrostField(this.f_19853_);
         fire.m_5602_(this.m_19749_());
         fire.setDuration((int)this.getDamage());
         fire.setRadius(this.getExplosionRadius());
         fire.setCircular();
         fire.m_20219_(location);
         this.f_19853_.m_7967_(fire);
      }
   }

   @Override
   protected void doImpactSound(Supplier<SoundEvent> sound) {
      this.f_19853_
         .m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), sound.get(), SoundSource.NEUTRAL, 2.0F, 0.7F + Utils.random.m_188501_() * 0.2F);
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ICE_SPIKE_EMERGE);
   }
}
