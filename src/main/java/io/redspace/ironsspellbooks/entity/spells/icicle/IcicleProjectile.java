package io.redspace.ironsspellbooks.entity.spells.icicle;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class IcicleProjectile extends AbstractMagicProjectile {
   public IcicleProjectile(EntityType<? extends IcicleProjectile> entityType, Level level) {
      super(entityType, level);
      this.m_20242_(true);
      this.setInfinitePiercing();
   }

   public IcicleProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends IcicleProjectile>)EntityRegistry.ICICLE_PROJECTILE.get(), levelIn);
      this.m_5602_(shooter);
   }

   protected void m_8060_(BlockHitResult blockHitResult) {
      super.m_8060_(blockHitResult);
      this.m_146870_();
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      super.m_5790_(entityHitResult);
      DamageSources.applyDamage(
         entityHitResult.m_82443_(), this.getDamage(), ((AbstractSpell)SpellRegistry.ICICLE_SPELL.get()).getDamageSource(this, this.m_19749_())
      );
      this.pierceOrDiscard();
   }

   @Override
   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         double speed = 0.05;
         double dx = Utils.random.m_188500_() * 2.0 * speed - speed;
         double dy = Utils.random.m_188500_() * 2.0 * speed - speed;
         double dz = Utils.random.m_188500_() * 2.0 * speed - speed;
         this.f_19853_
            .m_7106_(
               (ParticleOptions)(Utils.random.m_188500_() < 0.3 ? ParticleHelper.SNOWFLAKE : ParticleTypes.f_175821_),
               this.m_20185_() + dx,
               this.m_20186_() + dy,
               this.m_20189_() + dz,
               dx,
               dy,
               dz
            );
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.SNOWFLAKE, x, y, z, 15, 0.1, 0.1, 0.1, 0.1, true);
   }

   @Override
   public float getSpeed() {
      return 1.4F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ICE_IMPACT);
   }
}
