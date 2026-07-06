package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class OnClientCastPacket implements CustomPacketPayload {
   String spellId;
   int level;
   CastSource castSource;
   ICastData castData;

   public OnClientCastPacket(String spellId, int level, CastSource castSource, ICastData castData) {
      this.spellId = spellId;
      this.level = level;
      this.castSource = castSource;
      this.castData = castData;
   }

   public OnClientCastPacket(FriendlyByteBuf buf) {
      this.spellId = buf.m_130277_();
      this.level = buf.readInt();
      this.castSource = (CastSource)buf.m_130066_(CastSource.class);
      if (buf.readBoolean()) {
         ICastDataSerializable tmp = SpellRegistry.getSpell(this.spellId).getEmptyCastData();
         tmp.readFromBuffer(buf);
         this.castData = tmp;
      }
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130070_(this.spellId);
      buf.writeInt(this.level);
      buf.m_130068_(this.castSource);
      if (this.castData instanceof ICastDataSerializable castDataSerializable) {
         buf.writeBoolean(true);
         castDataSerializable.writeToBuffer(buf);
      } else {
         buf.writeBoolean(false);
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(
         () -> DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT, () -> () -> ClientSpellCastHelper.handleClientboundOnClientCast(this.spellId, this.level, this.castSource, this.castData)
         )
      );
      return true;
   }
}
