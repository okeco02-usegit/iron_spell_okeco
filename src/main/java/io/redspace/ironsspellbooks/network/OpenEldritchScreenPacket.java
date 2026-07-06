package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenEldritchScreenPacket implements CustomPacketPayload {
   private final InteractionHand hand;

   public OpenEldritchScreenPacket(InteractionHand pHand) {
      this.hand = pHand;
   }

   public OpenEldritchScreenPacket(FriendlyByteBuf buf) {
      this.hand = (InteractionHand)buf.m_130066_(InteractionHand.class);
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130068_(this.hand);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientSpellCastHelper.openEldritchResearchScreen(this.hand));
      return true;
   }
}
