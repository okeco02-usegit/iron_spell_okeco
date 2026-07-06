package io.redspace.ironsspellbooks.entity.spells.snowball;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class FrostField extends AoeEntity {
   public FrostField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.reapplicationDelay = 1;
   }

   public FrostField(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.FROST_FIELD.get(), level);
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (!DamageSources.isFriendlyFireBetween(this.m_19749_(), target)) {
         Utils.addFreezeTicks(target, 10);
      }
   }

   @Override
   public float getParticleCount() {
      return 0.2F * this.getRadius();
   }

   @Override
   protected float particleYOffset() {
      return 0.25F;
   }

   @Override
   protected float getParticleSpeedModifier() {
      return 1.4F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   @Override
   public void ambientParticles() {
      if (this.f_19853_.f_46443_) {
         this.ambientParticles(ParticleHelper.SNOWFLAKE);
         this.ambientParticles(ParticleHelper.SNOW_DUST);
      }
   }
}
