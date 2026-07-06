package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class ShockwaveParticlesPacket implements CustomPacketPayload {
   Vec3 pos;
   float radius;
   String particleName;

   public ShockwaveParticlesPacket(Vec3 pos, float radius, ParticleType particleType) {
      this.pos = pos;
      this.radius = radius;
      this.particleName = Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(particleType)).toString();
   }

   public ShockwaveParticlesPacket(FriendlyByteBuf buf) {
      this.pos = this.readVec3(buf);
      this.radius = buf.readFloat();
      this.particleName = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      this.writeVec3(this.pos, buf);
      buf.writeFloat(this.radius);
      buf.m_130070_(this.particleName);
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
      ctx.enqueueWork(() -> {
         try {
            ParticleType<?> type = (ParticleType<?>)ForgeRegistries.PARTICLE_TYPES.getValue(ResourceLocation.parse(this.particleName));
            ClientSpellCastHelper.handleClientboundShockwaveParticle(this.pos, this.radius, type);
         } catch (Exception var2x) {
         }
      });
      return true;
   }
}
