package io.redspace.ironsspellbooks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractVec3Packet {
   protected Vec3 pos;

   public AbstractVec3Packet(Vec3 pos) {
      this.pos = pos;
   }

   public AbstractVec3Packet(FriendlyByteBuf buf) {
      this.pos = this.readVec3(buf);
   }

   public void toBytes(FriendlyByteBuf buf) {
      this.writeVec3(this.pos, buf);
   }

   public Vec3 readVec3(FriendlyByteBuf buf) {
      double x = buf.readDouble();
      double y = buf.readDouble();
      double z = buf.readDouble();
      return new Vec3(x, y, z);
   }

   public void writeVec3(Vec3 vec3, FriendlyByteBuf buf) {
      buf.writeDouble(vec3.f_82479_);
      buf.writeDouble(vec3.f_82480_);
      buf.writeDouble(vec3.f_82481_);
   }
}
