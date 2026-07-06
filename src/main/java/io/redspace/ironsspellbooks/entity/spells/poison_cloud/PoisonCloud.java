package io.redspace.ironsspellbooks.entity.spells.poison_cloud;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class PoisonCloud extends AoeEntity {
   private DamageSource damageSource;

   public PoisonCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public PoisonCloud(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.POISON_CLOUD.get(), level);
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (this.damageSource == null) {
         this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.POISON_CLOUD), this, this.m_19749_());
      }

      DamageSources.ignoreNextKnockback(target);
      target.m_6469_(this.damageSource, this.getDamage());
      target.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 120, (int)this.getDamage()));
   }

   @Override
   public float getParticleCount() {
      return 0.15F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.of(ParticleHelper.POISON_CLOUD);
   }
}
