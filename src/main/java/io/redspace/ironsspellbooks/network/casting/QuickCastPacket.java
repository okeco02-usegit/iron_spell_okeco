package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuickCastPacket implements CustomPacketPayload {
   private int slot;

   public QuickCastPacket(int slot) {
      this.slot = slot;
   }

   public QuickCastPacket(FriendlyByteBuf buf) {
      this.slot = buf.readInt();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeInt(this.slot);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer serverPlayer = ctx.getSender();
         Utils.serverSideInitiateQuickCast(serverPlayer, this.slot);
      });
      return true;
   }
}
