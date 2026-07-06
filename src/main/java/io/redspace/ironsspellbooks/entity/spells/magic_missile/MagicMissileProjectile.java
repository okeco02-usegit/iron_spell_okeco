package io.redspace.ironsspellbooks.entity.spells.magic_missile;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicMissileProjectile extends AbstractMagicProjectile {
   public MagicMissileProjectile(EntityType<? extends MagicMissileProjectile> entityType, Level level) {
      super(entityType, level);
      this.m_20242_(true);
   }

   public MagicMissileProjectile(EntityType<? extends MagicMissileProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.m_5602_(shooter);
   }

   public MagicMissileProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends MagicMissileProjectile>)EntityRegistry.MAGIC_MISSILE_PROJECTILE.get(), levelIn, shooter);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.UNSTABLE_ENDER, x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   @Override
   public float getSpeed() {
      return 2.5F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   protected void m_8060_(BlockHitResult blockHitResult) {
      super.m_8060_(blockHitResult);
      this.m_146870_();
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      super.m_5790_(entityHitResult);
      DamageSources.applyDamage(
         entityHitResult.m_82443_(), this.damage, ((AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get()).getDamageSource(this, this.m_19749_())
      );
      this.pierceOrDiscard();
   }

   @Override
   public void trailParticles() {
      Vec3 vec = this.m_20184_();
      double length = vec.m_82553_();
      int count = (int)Math.min(20L, Math.round(length) * 3L) + 1;
      float f = (float)length / count;

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.02);
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
}
