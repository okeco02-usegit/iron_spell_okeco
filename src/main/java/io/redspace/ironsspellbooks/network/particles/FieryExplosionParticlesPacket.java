package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public class FieryExplosionParticlesPacket implements CustomPacketPayload {
   private final Vec3 pos1;
   private final float radius;

   public FieryExplosionParticlesPacket(Vec3 pos1, float radius) {
      this.pos1 = pos1;
      this.radius = radius;
   }

   public FieryExplosionParticlesPacket(FriendlyByteBuf buf) {
      this.pos1 = this.readVec3(buf);
      this.radius = buf.readFloat();
   }

   public void toBytes(FriendlyByteBuf buf) {
      this.writeVec3(this.pos1, buf);
      buf.writeFloat(this.radius);
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

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientSpellCastHelper.handleClientboundFieryExplosion(this.pos1, this.radius));
      return true;
   }
}
