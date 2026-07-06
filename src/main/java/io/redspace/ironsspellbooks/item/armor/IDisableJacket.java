package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.entity.EquipmentSlot;

public interface IDisableJacket {
   default boolean disableForSlot(EquipmentSlot slot) {
      return true;
   }
}
