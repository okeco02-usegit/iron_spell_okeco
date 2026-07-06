package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SerializedTargetData implements ICastDataSerializable {
   protected UUID targetUUID;

   public SerializedTargetData(Entity target) {
      this.targetUUID = target.m_20148_();
   }

   public SerializedTargetData() {
      this.targetUUID = null;
   }

   @Override
   public void reset() {
   }

   @Nullable
   public Entity getTarget(ServerLevel level) {
      return level.m_8791_(this.targetUUID);
   }

   public UUID getTargetUUID() {
      return this.targetUUID;
   }

   @Nullable
   public Vec3 getTargetPosition(ServerLevel level) {
      Entity target = this.getTarget(level);
      return target == null ? null : target.m_20182_();
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.m_130077_(this.targetUUID);
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.targetUUID = buffer.m_130259_();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.m_128362_("target", this.targetUUID);
      return tag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.targetUUID = nbt.m_128342_("target");
   }
}
