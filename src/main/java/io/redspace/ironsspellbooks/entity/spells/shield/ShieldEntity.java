package io.redspace.ironsspellbooks.entity.spells.shield;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public class ShieldEntity extends AbstractShieldEntity {
   protected ShieldPart[] subEntities;
   protected final Vec3[] subPositions;
   protected final int LIFETIME;
   protected int width = 5;
   protected int height = 5;
   protected int age;

   public ShieldEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.subEntities = new ShieldPart[this.width * this.height];
      this.subPositions = new Vec3[this.width * this.height];
      this.setHealth(10.0F);
      this.LIFETIME = 400;
      this.createShield();
   }

   public ShieldEntity(Level level, float health) {
      this((EntityType<?>)EntityRegistry.SHIELD_ENTITY.get(), level);
      this.setHealth(health);
   }

   @Override
   protected void createShield() {
      for (int x = 0; x < this.width; x++) {
         for (int y = 0; y < this.height; y++) {
            int i = x * this.height + y;
            this.subEntities[i] = new ShieldPart(this, "part" + (i + 1), 0.5F, 0.5F, true);
            this.subPositions[i] = new Vec3((x - this.width / 2.0F) * 0.5F + 0.25F, (y - this.height / 2.0F) * 0.5F, 0.0);
         }
      }
   }

   public void setRotation(float x, float y) {
      this.m_146926_(x);
      this.f_19860_ = x;
      this.m_146922_(y);
      this.f_19859_ = y;
   }

   @Override
   public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {
      if (!this.m_6673_(source)) {
         this.setHealth(this.getHealth() - amount);
         if (!this.m_9236_().f_46443_ && location != null) {
            MagicManager.spawnParticles(
               this.m_9236_(), ParticleTypes.f_175830_, location.f_82479_, location.f_82480_, location.f_82481_, 30, 0.1, 0.1, 0.1, 0.5, false
            );
            this.m_9236_()
               .m_6263_(
                  null, location.f_82479_, location.f_82480_, location.f_82481_, (SoundEvent)SoundRegistry.FORCE_IMPACT.get(), SoundSource.NEUTRAL, 0.8F, 1.0F
               );
         }
      }
   }

   @Override
   public void m_8119_() {
      this.hurtThisTick = false;
      if (this.getHealth() <= 0.0F) {
         this.destroy();
      } else if (++this.age > this.LIFETIME) {
         if (!this.m_9236_().f_46443_) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_12326_, SoundSource.NEUTRAL, 1.0F, 1.4F);
         }

         this.m_146870_();
      } else {
         for (int i = 0; i < this.subEntities.length; i++) {
            ShieldPart subEntity = this.subEntities[i];
            Vec3 pos = this.subPositions[i]
               .m_82496_((float) (Math.PI / 180.0) * -this.m_146909_())
               .m_82524_((float) (Math.PI / 180.0) * -this.m_146908_())
               .m_82549_(this.m_20182_());
            subEntity.m_146884_(pos);
            subEntity.f_19854_ = pos.f_82479_;
            subEntity.f_19855_ = pos.f_82480_;
            subEntity.f_19856_ = pos.f_82481_;
            subEntity.f_19790_ = pos.f_82479_;
            subEntity.f_19791_ = pos.f_82480_;
            subEntity.f_19792_ = pos.f_82481_;
         }
      }
   }

   @Override
   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   @Override
   protected void destroy() {
      if (!this.m_9236_().f_46443_) {
         this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11983_, SoundSource.NEUTRAL, 2.0F, 0.65F);
      }

      super.destroy();
   }
}
