package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ImpulseCastData implements ICastDataSerializable {
   public float x;
   public float y;
   public float z;
   public boolean hasImpulse;

   public ImpulseCastData() {
   }

   public ImpulseCastData(float x, float y, float z, boolean hasImpulse) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.hasImpulse = hasImpulse;
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeFloat(this.x);
      buffer.writeFloat(this.y);
      buffer.writeFloat(this.z);
      buffer.writeBoolean(this.hasImpulse);
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.x = buffer.readFloat();
      this.y = buffer.readFloat();
      this.z = buffer.readFloat();
      this.hasImpulse = buffer.readBoolean();
   }

   @Override
   public void reset() {
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.m_128350_("x", this.x);
      tag.m_128350_("y", this.y);
      tag.m_128350_("z", this.z);
      tag.m_128379_("i", this.hasImpulse);
      return tag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      this.x = compoundTag.m_128457_("x");
      this.y = compoundTag.m_128457_("y");
      this.z = compoundTag.m_128457_("z");
      this.hasImpulse = compoundTag.m_128471_("i");
   }
}
