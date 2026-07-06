package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncCooldownPacket implements CustomPacketPayload {
   private final String spellId;
   private final int duration;

   public SyncCooldownPacket(String spellId, int duration) {
      this.spellId = spellId;
      this.duration = duration;
   }

   public SyncCooldownPacket(FriendlyByteBuf buf) {
      this.spellId = buf.m_130277_();
      this.duration = buf.readInt();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130070_(this.spellId);
      buf.writeInt(this.duration);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.getCooldowns().addCooldown(this.spellId, this.duration));
      return true;
   }
}
