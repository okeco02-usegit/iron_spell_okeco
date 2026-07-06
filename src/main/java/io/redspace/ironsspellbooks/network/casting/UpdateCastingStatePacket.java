package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateCastingStatePacket implements CustomPacketPayload {
   private final String spellId;
   private final int spellLevel;
   private final int castTime;
   private final CastSource castSource;
   private final String castingEquipmentSlot;

   public UpdateCastingStatePacket(String spellId, int spellLevel, int castTime, CastSource castSource, String castingEquipmentSlot) {
      this.spellId = spellId;
      this.spellLevel = spellLevel;
      this.castTime = castTime;
      this.castSource = castSource;
      this.castingEquipmentSlot = castingEquipmentSlot;
   }

   public UpdateCastingStatePacket(FriendlyByteBuf buf) {
      this.spellId = buf.m_130277_();
      this.spellLevel = buf.readInt();
      this.castTime = buf.readInt();
      this.castSource = (CastSource)buf.m_130066_(CastSource.class);
      this.castingEquipmentSlot = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130070_(this.spellId);
      buf.writeInt(this.spellLevel);
      buf.writeInt(this.castTime);
      buf.m_130068_(this.castSource);
      buf.m_130070_(this.castingEquipmentSlot);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.setClientCastState(this.spellId, this.spellLevel, this.castTime, this.castSource, this.castingEquipmentSlot));
      return true;
   }
}
