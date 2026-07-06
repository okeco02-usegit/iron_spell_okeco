package io.redspace.ironsspellbooks.entity.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.entity.PartEntity;

public class ShieldPart extends PartEntity<AbstractShieldEntity> {
   public final AbstractShieldEntity parentEntity;
   public final String name;
   private final EntityDimensions size;
   private final boolean hasCollision;

   public ShieldPart(AbstractShieldEntity shieldEntity, String name, float scaleX, float scaleY, boolean hasCollision) {
      super(shieldEntity);
      this.size = EntityDimensions.m_20395_(scaleX, scaleY);
      this.m_6210_();
      this.parentEntity = shieldEntity;
      this.name = name;
      this.hasCollision = hasCollision;
   }

   public boolean m_5829_() {
      return this.hasCollision;
   }

   public boolean m_6087_() {
      return !this.m_213877_();
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (!this.f_19853_.f_46443_ && !this.parentEntity.hurtThisTick) {
         this.parentEntity.takeDamage(pSource, pAmount, this.m_20191_().m_82399_());
         this.parentEntity.hurtThisTick = true;
      }

      return false;
   }

   protected void m_8097_() {
   }

   protected void m_7378_(CompoundTag compoundTag) {
   }

   protected void m_7380_(CompoundTag compoundTag) {
   }

   public boolean m_7306_(Entity entity) {
      return this == entity || this.parentEntity == entity;
   }

   public EntityDimensions m_6972_(Pose pose) {
      return this.size;
   }

   public boolean m_142391_() {
      return false;
   }
}
