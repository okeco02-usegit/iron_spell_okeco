package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MultiTargetEntityCastData implements ICastDataSerializable {
   private List<UUID> targetUUIDs = new ArrayList<>();

   public MultiTargetEntityCastData(Entity... targets) {
      Arrays.stream(targets).forEach(target -> this.targetUUIDs.add(target.m_20148_()));
   }

   @Deprecated(forRemoval = true)
   public MultiTargetEntityCastData(LivingEntity... targets) {
      this((Entity[])targets);
   }

   @Override
   public void reset() {
      this.targetUUIDs.clear();
   }

   public List<UUID> getTargets() {
      return this.targetUUIDs;
   }

   public void addTarget(Entity entity) {
      this.targetUUIDs.add(entity.m_20148_());
   }

   public void addTarget(UUID uuid) {
      this.targetUUIDs.add(uuid);
   }

   public boolean isTargeted(Entity entity) {
      return this.targetUUIDs.contains(entity.m_20148_());
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeInt(this.targetUUIDs.size());
      this.targetUUIDs.forEach(buffer::m_130077_);
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.targetUUIDs = new ArrayList<>();
      int i = buffer.readInt();

      for (int j = 0; j < i; j++) {
         this.targetUUIDs.add(buffer.m_130259_());
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      ListTag uuids = new ListTag();
      this.targetUUIDs.stream().map(NbtUtils::m_129226_).forEach(uuids::add);
      tag.m_128365_("targets", uuids);
      return tag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.targetUUIDs = new ArrayList<>();
      ListTag listTag = nbt.m_128437_("targets", 11);
      listTag.stream().map(NbtUtils::m_129233_).forEach(this.targetUUIDs::add);
   }
}
