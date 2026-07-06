package io.redspace.ironsspellbooks.entity.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.entity.PartEntity;

public class ConePart extends PartEntity<AbstractConeProjectile> {
   public final AbstractConeProjectile parentEntity;
   public final String name;
   private final EntityDimensions size;

   public ConePart(AbstractConeProjectile coneProjectile, String name, float scaleX, float scaleY) {
      super(coneProjectile);
      this.size = EntityDimensions.m_20395_(scaleX, scaleY);
      this.m_6210_();
      this.parentEntity = coneProjectile;
      this.name = name;
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
