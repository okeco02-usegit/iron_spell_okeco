package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class CastErrorPacket implements CustomPacketPayload {
   public final CastErrorPacket.ErrorType errorType;
   public final String spellId;

   public CastErrorPacket(CastErrorPacket.ErrorType errorType, AbstractSpell spell) {
      this.spellId = spell.getSpellId();
      this.errorType = errorType;
   }

   public CastErrorPacket(FriendlyByteBuf buf) {
      this.errorType = (CastErrorPacket.ErrorType)buf.m_130066_(CastErrorPacket.ErrorType.class);
      this.spellId = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130068_(this.errorType);
      buf.m_130070_(this.spellId);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientSpellCastHelper.handleCastErrorMessage(this));
      return true;
   }

   public enum ErrorType {
      COOLDOWN,
      MANA;
   }
}
