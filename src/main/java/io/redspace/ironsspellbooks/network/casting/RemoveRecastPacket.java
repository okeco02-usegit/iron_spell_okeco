package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class RemoveRecastPacket implements CustomPacketPayload {
   private final String spellId;

   public RemoveRecastPacket(String spellId) {
      this.spellId = spellId;
   }

   public RemoveRecastPacket(FriendlyByteBuf buf) {
      this.spellId = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130070_(this.spellId);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ClientMagicData.getRecasts().removeRecast(this.spellId);
         ClientMagicData.cacheClientSummons();
      });
      return true;
   }
}
