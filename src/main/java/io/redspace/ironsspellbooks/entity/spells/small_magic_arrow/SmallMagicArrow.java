package io.redspace.ironsspellbooks.entity.spells.small_magic_arrow;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SmallMagicArrow extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.m_135353_(SmallMagicArrow.class, EntityDataSerializers.f_135035_);
   public int shakeTime;
   protected boolean inGround;

   public SmallMagicArrow(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SmallMagicArrow(Level levelIn, Entity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.SMALL_MAGIC_ARROW.get(), levelIn);
      this.m_5602_(shooter);
   }

   @Override
   public void shoot(Vec3 rotation) {
      this.m_20256_(rotation);
   }

   @Override
   public void m_8119_() {
      if (this.shakeTime > 0) {
         this.shakeTime--;
      }

      if (!this.inGround) {
         super.m_8119_();
      } else {
         this.deltaMovementOld = this.m_20184_();
         if (this.f_19797_ > 300) {
            this.m_146870_();
            return;
         }

         if (this.shouldFall()) {
            this.inGround = false;
            this.m_20256_(this.m_20184_().m_82541_().m_82490_(0.05F));
         }
      }
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(IN_GROUND, false);
   }

   private boolean shouldFall() {
      return this.inGround && this.f_19853_.m_45772_(new AABB(this.m_20182_(), this.m_20182_()).m_82400_(0.06));
   }

   protected void m_8060_(BlockHitResult pResult) {
      super.m_8060_(pResult);
      Vec3 vec3 = pResult.m_82450_().m_82492_(this.m_20185_(), this.m_20186_(), this.m_20189_());
      this.m_20256_(vec3);
      Vec3 vec31 = vec3.m_82541_().m_82490_(0.05F);
      this.m_20343_(this.m_20185_() - vec31.f_82479_, this.m_20186_() - vec31.f_82480_, this.m_20189_() - vec31.f_82481_);
      this.m_5496_(SoundEvents.f_11685_, 1.0F, 1.2F / (this.f_19796_.m_188501_() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      if (!this.f_19853_.f_46443_) {
         Entity entity = entityHitResult.m_82443_();
         boolean hit = DamageSources.applyDamage(
            entity, this.getDamage(), ((AbstractSpell)SpellRegistry.ARROW_VOLLEY_SPELL.get()).getDamageSource(this, this.m_19749_())
         );
         boolean ignore = entity.m_6095_() == EntityType.f_20566_;
         if (hit) {
            this.pierceOrDiscard();
         } else {
            this.m_20256_(this.m_20184_().m_82490_(-0.1));
            this.m_146922_(this.m_146908_() + 180.0F);
            this.f_19859_ += 180.0F;
         }
      }
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128379_("inGround", this.inGround);
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.inGround = tag.m_128471_("inGround");
   }

   @Override
   public void trailParticles() {
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return 2.0F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }
}
