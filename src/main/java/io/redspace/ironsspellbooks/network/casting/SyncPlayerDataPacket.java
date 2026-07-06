package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncPlayerDataPacket implements CustomPacketPayload {
   SyncedSpellData syncedSpellData;

   public SyncPlayerDataPacket(SyncedSpellData playerSyncedData) {
      this.syncedSpellData = playerSyncedData;
   }

   public SyncPlayerDataPacket(FriendlyByteBuf buf) {
      this.syncedSpellData = SyncedSpellData.read(buf);
   }

   public void toBytes(FriendlyByteBuf buf) {
      SyncedSpellData.write(buf, this.syncedSpellData);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.handlePlayerSyncedData(this.syncedSpellData));
      return true;
   }
}
