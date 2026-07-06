package io.redspace.ironsspellbooks.entity.spells.lightning_lance;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningLanceProjectile extends AbstractMagicProjectile {
   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20182_().m_82546_(this.m_20184_());
      this.f_19853_.m_7106_(ParticleHelper.ELECTRICITY, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 0.0, 0.0, 0.0);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ELECTRICITY, x, y, z, 75, 0.1, 0.1, 0.1, 2.0, true);
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ELECTRICITY, x, y, z, 75, 0.1, 0.1, 0.1, 0.5, false);
   }

   @Override
   public float getSpeed() {
      return 3.0F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public LightningLanceProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(false);
   }

   public LightningLanceProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.LIGHTNING_LANCE_PROJECTILE.get(), levelIn);
      this.m_5602_(shooter);
   }

   protected void m_8060_(BlockHitResult pResult) {
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      DamageSources.applyDamage(
         entityHitResult.m_82443_(), this.damage, ((AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get()).getDamageSource(this, this.m_19749_())
      );
   }

   @Override
   protected void m_6532_(HitResult pResult) {
      if (!this.f_19853_.f_46443_) {
         this.m_5496_(SoundEvents.f_12521_, 6.0F, 0.65F);
      }

      super.m_6532_(pResult);
      this.discardHelper(pResult);
   }

   public int getAge() {
      return this.f_19797_;
   }
}
