package io.redspace.ironsspellbooks.network.spells;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

public class GuidingBoltManagerStopTrackingPacket implements CustomPacketPayload {
   private final UUID entity;

   public GuidingBoltManagerStopTrackingPacket(Entity entity) {
      this.entity = entity.m_20148_();
   }

   public GuidingBoltManagerStopTrackingPacket(FriendlyByteBuf buf) {
      this.entity = buf.m_130259_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130077_(this.entity);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> GuidingBoltManager.handleClientboundStopTracking(this.entity));
      return true;
   }
}
