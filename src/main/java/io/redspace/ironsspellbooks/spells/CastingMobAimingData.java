package io.redspace.ironsspellbooks.spells;

import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;

public class CastingMobAimingData implements ICastDataSerializable {
   private Vec3 aimPosition = Vec3.f_82478_;
   private Vec3 lastAimPosition = Vec3.f_82478_;

   public void updateAim(Entity target, float strength) {
      Vec3 wanted = target.m_20191_().m_82399_();
      if (this.aimPosition.equals(Vec3.f_82478_)) {
         this.aimPosition = wanted;
         this.lastAimPosition = wanted;
      } else {
         this.lastAimPosition = this.aimPosition;
         this.aimPosition = this.aimPosition.m_82549_(wanted.m_82546_(this.aimPosition).m_82490_(strength));
      }
   }

   public Vec3 getAimPosition() {
      return this.aimPosition;
   }

   public Vec3 getAimPosition(float partialTick) {
      return this.lastAimPosition.m_82549_(this.aimPosition.m_82546_(this.lastAimPosition).m_82490_(partialTick));
   }

   public Vec3 getForward(Entity host) {
      return this.aimPosition.m_82546_(host.m_146892_()).m_82541_();
   }

   @Override
   public void reset() {
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.writeInt((int)(this.aimPosition.f_82479_ * 10.0));
      buffer.writeInt((int)(this.aimPosition.f_82480_ * 10.0));
      buffer.writeInt((int)(this.aimPosition.f_82481_ * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.f_82479_ * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.f_82480_ * 10.0));
      buffer.writeInt((int)(this.lastAimPosition.f_82481_ * 10.0));
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.aimPosition = new Vec3(buffer.readInt() / 10.0, buffer.readInt() / 10.0, buffer.readInt() / 10.0);
      this.lastAimPosition = new Vec3(buffer.readInt() / 10.0, buffer.readInt() / 10.0, buffer.readInt() / 10.0);
   }

   public @UnknownNullability CompoundTag serializeNBT() {
      return new CompoundTag();
   }

   public void deserializeNBT(CompoundTag nbt) {
   }
}
