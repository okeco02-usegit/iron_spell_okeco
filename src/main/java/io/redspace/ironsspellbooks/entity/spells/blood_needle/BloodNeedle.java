package io.redspace.ironsspellbooks.entity.spells.blood_needle;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BloodNeedle extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Float> DATA_Z_ROT = SynchedEntityData.m_135353_(BloodNeedle.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.m_135353_(BloodNeedle.class, EntityDataSerializers.f_135029_);
   private static int soundTimestamp;

   public BloodNeedle(EntityType<? extends BloodNeedle> entityType, Level level) {
      super(entityType, level);
      this.m_20242_(true);
   }

   public BloodNeedle(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)EntityRegistry.BLOOD_NEEDLE.get(), levelIn);
      this.m_5602_(shooter);
   }

   public void setZRot(float zRot) {
      if (!this.f_19853_.f_46443_) {
         this.f_19804_.m_135381_(DATA_Z_ROT, zRot);
      }
   }

   public void setScale(float scale) {
      if (!this.f_19853_.f_46443_) {
         this.f_19804_.m_135381_(DATA_SCALE, scale);
      }
   }

   @Override
   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_Z_ROT, 0.0F);
      this.f_19804_.m_135372_(DATA_SCALE, 1.0F);
      super.m_8097_();
   }

   public float getZRot() {
      return (Float)this.f_19804_.m_135370_(DATA_Z_ROT);
   }

   public float getScale() {
      return (Float)this.f_19804_.m_135370_(DATA_SCALE);
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128350_("zRot", this.getZRot());
      if (this.getScale() != 1.0F) {
         tag.m_128350_("Scale", this.getScale());
      }
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.setZRot(tag.m_128457_("zRot"));
      if (tag.m_128441_("Scale")) {
         this.setScale(tag.m_128457_("Scale"));
      }
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      super.m_5790_(entityHitResult);
      DamageSources.applyDamage(
         entityHitResult.m_82443_(), this.getDamage(), ((AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get()).getDamageSource(this, this.m_19749_())
      );
   }

   @Override
   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      this.discardHelper(hitresult);
   }

   @Override
   protected void doImpactSound(Supplier<SoundEvent> sound) {
      if (soundTimestamp != this.f_19797_) {
         super.doImpactSound(sound);
         soundTimestamp = this.f_19797_;
      }
   }

   @Override
   public void trailParticles() {
      for (int i = 0; i < 2; i++) {
         double speed = 0.05;
         double dx = Utils.random.m_188500_() * 2.0 * speed - speed;
         double dy = Utils.random.m_188500_() * 2.0 * speed - speed;
         double dz = Utils.random.m_188500_() * 2.0 * speed - speed;
         this.f_19853_.m_7106_(ParticleHelper.BLOOD, this.m_20185_() + dx, this.m_20186_() + dy, this.m_20189_() + dz, dx, dy, dz);
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.BLOOD, x, y, z, 15, 0.1, 0.1, 0.1, 0.18, true);
   }

   @Override
   public float getSpeed() {
      return 2.5F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.BLOOD_NEEDLE_IMPACT);
   }
}
