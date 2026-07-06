package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncManaPacket implements CustomPacketPayload {
   private int playerMana = 0;
   private MagicData playerMagicData = null;

   public SyncManaPacket(MagicData playerMagicData) {
      this.playerMagicData = playerMagicData;
   }

   public SyncManaPacket(FriendlyByteBuf buf) {
      this.playerMana = buf.readInt();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeInt((int)this.playerMagicData.getMana());
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.setMana(this.playerMana));
      return true;
   }
}
