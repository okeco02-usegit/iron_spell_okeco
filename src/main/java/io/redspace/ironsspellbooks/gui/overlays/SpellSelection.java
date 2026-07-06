package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.api.network.ISerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;

public class SpellSelection implements ISerializable, INBTSerializable<CompoundTag> {
   public String equipmentSlot;
   public int index;
   public String lastEquipmentSlot;
   public int lastIndex;

   public SpellSelection() {
      this.equipmentSlot = "";
      this.index = -1;
      this.lastEquipmentSlot = "";
      this.lastIndex = -1;
   }

   public SpellSelection(String equipmentSlot, int index) {
      this.equipmentSlot = equipmentSlot;
      this.index = index;
      this.lastEquipmentSlot = "";
      this.lastIndex = -1;
   }

   public SpellSelection(String equipmentSlot, int index, String lastEquipmentSlot, int lastIndex) {
      this.equipmentSlot = equipmentSlot;
      this.index = index;
      this.lastEquipmentSlot = lastEquipmentSlot;
      this.lastIndex = lastIndex;
   }

   public boolean isEmpty() {
      return this.index < 0;
   }

   public void makeSelection(String equipmentSlot, int index) {
      if (equipmentSlot != null && index >= 0) {
         this.lastEquipmentSlot = this.equipmentSlot;
         this.lastIndex = this.index;
         this.equipmentSlot = equipmentSlot;
         this.index = index;
      }
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.m_130070_(this.equipmentSlot);
      buffer.writeInt(this.index);
      buffer.m_130070_(this.lastEquipmentSlot);
      buffer.writeInt(this.lastIndex);
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.equipmentSlot = buffer.m_130277_();
      this.index = buffer.readInt();
      this.lastEquipmentSlot = buffer.m_130277_();
      this.lastIndex = buffer.readInt();
   }

   public CompoundTag serializeNBT() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.m_128359_("slot", this.equipmentSlot);
      compoundTag.m_128405_("index", this.index);
      compoundTag.m_128359_("lastSlot", this.lastEquipmentSlot);
      compoundTag.m_128405_("lastIndex", this.lastIndex);
      return compoundTag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      this.equipmentSlot = compoundTag.m_128461_("slot");
      this.index = compoundTag.m_128451_("index");
      this.lastEquipmentSlot = compoundTag.m_128461_("lastSlot");
      this.lastIndex = compoundTag.m_128451_("lastIndex");
   }

   @Override
   public String toString() {
      return String.format(
         "equipmentSlot:%s, index:%d, lastEquipmentSlot:%s, lastIndex:%d", this.equipmentSlot, this.index, this.lastEquipmentSlot, this.lastIndex
      );
   }
}
