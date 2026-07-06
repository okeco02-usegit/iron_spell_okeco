package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AcidOrb extends AbstractMagicProjectile {
   int rendLevel;
   int rendDuration;

   public AcidOrb(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public AcidOrb(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.ACID_ORB.get(), level);
      this.m_5602_(shooter);
   }

   public int getRendLevel() {
      return this.rendLevel;
   }

   public void setRendLevel(int rendLevel) {
      this.rendLevel = rendLevel;
   }

   public int getRendDuration() {
      return this.rendDuration;
   }

   public void setRendDuration(int rendDuration) {
      this.rendDuration = rendDuration;
   }

   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20182_().m_82546_(this.m_20184_().m_82490_(2.0));
      this.f_19853_.m_7106_(ParticleHelper.ACID, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 0.0, 0.0, 0.0);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ACID, x, y, z, 55, 0.08, 0.08, 0.08, 0.3, true);
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ACID_BUBBLE, x, y, z, 25, 0.08, 0.08, 0.08, 0.3, false);
   }

   @Override
   public float getSpeed() {
      return 1.0F;
   }

   @Override
   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      if (!this.f_19853_.f_46443_) {
         float explosionRadius = 3.5F;

         for (Entity entity : this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius))) {
            double distance = entity.m_20182_().m_82554_(hitresult.m_82450_());
            if (distance < explosionRadius
               && Utils.hasLineOfSight(this.f_19853_, hitresult.m_82450_(), entity.m_146892_(), true)
               && entity instanceof LivingEntity livingEntity
               && livingEntity != this.m_19749_()) {
               livingEntity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.REND.get(), this.getRendDuration(), this.getRendLevel()));
            }
         }

         this.discardHelper(hitresult);
      }
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ACID_ORB_IMPACT);
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("RendLevel", this.rendLevel);
      tag.m_128405_("RendDuration", this.rendDuration);
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.rendLevel = tag.m_128451_("RendLevel");
      this.rendDuration = tag.m_128451_("RendDuration");
   }
}
