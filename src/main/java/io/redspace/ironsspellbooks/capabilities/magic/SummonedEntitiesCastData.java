package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SummonedEntitiesCastData implements ICastDataSerializable {
   protected Set<UUID> summons = new HashSet<>();
   protected float maxHealthPool;

   public void add(Entity entity) {
      this.summons.add(entity.m_20148_());
      if (entity instanceof LivingEntity livingEntity) {
         this.maxHealthPool = this.maxHealthPool + livingEntity.m_21233_();
      }
   }

   public void handleRemove(UUID uuid, MagicData ownerData, RecastInstance recastInstance) {
      this.summons.remove(uuid);
      if (this.summons.isEmpty()) {
         ownerData.getPlayerRecasts().removeRecast(recastInstance, RecastResult.USED_ALL_RECASTS);
      }
   }

   @Override
   public void reset() {
   }

   public float getMaxHealthPool() {
      return this.maxHealthPool;
   }

   public Set<UUID> getSummons() {
      return this.summons;
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeInt(this.summons.size());

      for (UUID uuid : this.summons) {
         buffer.m_130077_(uuid);
      }

      buffer.writeFloat(this.maxHealthPool);
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      int i = buffer.readInt();

      for (int j = 0; j < i; j++) {
         this.summons.add(buffer.m_130259_());
      }

      this.maxHealthPool = buffer.readFloat();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      ListTag list = new ListTag();
      this.summons.forEach(uuid -> list.add(NbtUtils.m_129226_(uuid)));
      tag.m_128365_("summons", list);
      tag.m_128350_("maxHealthPool", this.maxHealthPool);
      return tag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag list = nbt.m_128437_("summons", 11);
      list.forEach(tag -> this.summons.add(NbtUtils.m_129233_(tag)));
      this.maxHealthPool = nbt.m_128457_("maxHealthPool");
   }
}
