package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.ClientHelper;
import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenHeldBookPacket implements CustomPacketPayload {
   public final byte hand;

   public OpenHeldBookPacket(InteractionHand hand) {
      this.hand = (byte)hand.ordinal();
   }

   public OpenHeldBookPacket(FriendlyByteBuf pBuffer) {
      this.hand = pBuffer.readByte();
   }

   public void toBytes(FriendlyByteBuf pBuffer) {
      pBuffer.writeByte(this.hand);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientHelper.handleOpenBookPacket(this));
      return true;
   }
}
