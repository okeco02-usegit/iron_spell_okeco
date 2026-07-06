package io.redspace.ironsspellbooks.entity.spells.portal;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.util.NBT;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class PortalData implements ICastDataSerializable {
   public PortalPos globalPos1;
   public UUID portalEntityId1;
   public PortalPos globalPos2;
   public UUID portalEntityId2;
   public int ticksToLive;
   public boolean isBlock;

   public void setPortalDuration(int ticksToLive) {
      this.ticksToLive = ticksToLive;
   }

   public void firstPortal(UUID uuid, PortalPos pos) {
      this.portalEntityId1 = uuid;
      this.globalPos1 = pos;
   }

   public void secondPortal(UUID uuid, PortalPos pos) {
      this.portalEntityId2 = uuid;
      this.globalPos2 = pos;
   }

   public Optional<PortalPos> getConnectedPortalPos(UUID portalId) {
      if (this.portalEntityId1.equals(portalId)) {
         return Optional.of(this.globalPos2);
      } else {
         return this.portalEntityId2.equals(portalId) ? Optional.of(this.globalPos1) : Optional.empty();
      }
   }

   public UUID getConnectedPortalUUID(UUID portalId) {
      if (this.portalEntityId1.equals(portalId)) {
         return this.portalEntityId2;
      } else {
         return this.portalEntityId2.equals(portalId) ? this.portalEntityId1 : null;
      }
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeInt(this.ticksToLive);
      if (this.globalPos1 != null && this.portalEntityId1 != null) {
         buffer.writeBoolean(true);
         this.writePortalPosToBuffer(buffer, this.globalPos1);
         buffer.m_130077_(this.portalEntityId1);
         if (this.globalPos2 != null && this.portalEntityId2 != null) {
            buffer.writeBoolean(true);
            this.writePortalPosToBuffer(buffer, this.globalPos2);
            buffer.m_130077_(this.portalEntityId2);
         } else {
            buffer.writeBoolean(false);
         }
      } else {
         buffer.writeBoolean(false);
      }

      buffer.writeBoolean(this.isBlock);
   }

   private void writePortalPosToBuffer(FriendlyByteBuf buffer, PortalPos pos) {
      buffer.m_236858_(pos.dimension());
      Vec3 vec3 = pos.pos();
      buffer.writeInt((int)(vec3.f_82479_ * 10.0));
      buffer.writeInt((int)(vec3.f_82480_ * 10.0));
      buffer.writeInt((int)(vec3.f_82481_ * 10.0));
      buffer.writeFloat(pos.rotation());
   }

   private PortalPos readPortalPosFromBuffer(FriendlyByteBuf buffer) {
      return PortalPos.of(
         buffer.m_236801_(Registries.f_256858_), new Vec3(buffer.readInt() / 10.0, buffer.readInt() / 10.0, buffer.readInt() / 10.0), buffer.readFloat()
      );
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.ticksToLive = buffer.readInt();
      if (buffer.readBoolean()) {
         this.globalPos1 = this.readPortalPosFromBuffer(buffer);
         this.portalEntityId1 = buffer.m_130259_();
         if (buffer.readBoolean()) {
            this.globalPos2 = this.readPortalPosFromBuffer(buffer);
            this.portalEntityId2 = buffer.m_130259_();
         }
      }

      this.isBlock = buffer.readBoolean();
   }

   @Override
   public void reset() {
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.m_128405_("ticksToLive", this.ticksToLive);
      if (this.globalPos1 != null) {
         tag.m_128365_("gp1", NBT.writePortalPos(this.globalPos1));
         tag.m_128362_("pe1", this.portalEntityId1);
         if (this.globalPos2 != null) {
            tag.m_128365_("gp2", NBT.writePortalPos(this.globalPos2));
            tag.m_128362_("pe2", this.portalEntityId2);
         }
      }

      tag.m_128379_("isBlock", this.isBlock);
      return tag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      this.ticksToLive = compoundTag.m_128451_("ticksToLive");
      if (compoundTag.m_128441_("gp1") && compoundTag.m_128441_("pe1")) {
         this.globalPos1 = NBT.readPortalPos(compoundTag.m_128469_("gp1"));
         this.portalEntityId1 = compoundTag.m_128342_("pe1");
         if (compoundTag.m_128441_("gp2") && compoundTag.m_128441_("pe2")) {
            this.globalPos2 = NBT.readPortalPos(compoundTag.m_128469_("gp2"));
            this.portalEntityId2 = compoundTag.m_128342_("pe2");
         }
      }

      this.isBlock = compoundTag.m_128471_("isBlock");
   }

   @Override
   public String toString() {
      return String.format("PortalData[pos1:%s pos2:%s id1:%s id2:%s]", this.globalPos1, this.globalPos2, this.portalEntityId1, this.portalEntityId2);
   }
}
