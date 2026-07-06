package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncRecastsPacket implements CustomPacketPayload {
   private final Map<String, RecastInstance> recastLookup;

   public SyncRecastsPacket(Map<String, RecastInstance> recastLookup) {
      this.recastLookup = recastLookup;
   }

   public SyncRecastsPacket(FriendlyByteBuf buf) {
      this.recastLookup = buf.m_236847_(SyncRecastsPacket::readSpellID, SyncRecastsPacket::readRecastInstance);
   }

   public static String readSpellID(FriendlyByteBuf buffer) {
      return buffer.m_130277_();
   }

   public static RecastInstance readRecastInstance(FriendlyByteBuf buffer) {
      RecastInstance tmp = new RecastInstance();
      tmp.readFromBuffer(buffer);
      return tmp;
   }

   public static void writeSpellId(FriendlyByteBuf buf, String spellId) {
      buf.m_130070_(spellId);
   }

   public static void writeRecastInstance(FriendlyByteBuf buf, RecastInstance recastInstance) {
      recastInstance.writeToBuffer(buf);
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_236831_(this.recastLookup, SyncRecastsPacket::writeSpellId, SyncRecastsPacket::writeRecastInstance);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.setRecasts(new PlayerRecasts(this.recastLookup)));
      return true;
   }
}
