package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbsorptionParticlesPacket implements CustomPacketPayload {
   private Vec3 pos;

   public AbsorptionParticlesPacket(Vec3 pos) {
      this.pos = pos;
   }

   public AbsorptionParticlesPacket(FriendlyByteBuf buf) {
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

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientSpellCastHelper.handleClientsideAbsorptionParticles(this.pos));
      return true;
   }
}
