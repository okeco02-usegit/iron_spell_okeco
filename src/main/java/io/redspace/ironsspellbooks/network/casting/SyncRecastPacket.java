package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncRecastPacket implements CustomPacketPayload {
   private final RecastInstance recastInstance;

   public SyncRecastPacket(RecastInstance recastInstance) {
      this.recastInstance = recastInstance;
   }

   public SyncRecastPacket(FriendlyByteBuf buf) {
      this.recastInstance = new RecastInstance();
      this.recastInstance.readFromBuffer(buf);
   }

   public void toBytes(FriendlyByteBuf buf) {
      if (this.recastInstance != null) {
         this.recastInstance.writeToBuffer(buf);
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ClientMagicData.getRecasts().forceAddRecast(this.recastInstance);
         ClientMagicData.cacheClientSummons();
      });
      return true;
   }
}
