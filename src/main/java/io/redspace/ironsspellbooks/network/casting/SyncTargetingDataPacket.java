package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.ClientSpellTargetingData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncTargetingDataPacket implements CustomPacketPayload {
   private final List<UUID> targetUUIDs = new ArrayList<>();
   private final String spellId;

   public SyncTargetingDataPacket(LivingEntity entity, AbstractSpell spell) {
      this.targetUUIDs.add(entity.m_20148_());
      this.spellId = spell.getSpellId();
   }

   public SyncTargetingDataPacket(AbstractSpell spell, List<UUID> uuids) {
      this.targetUUIDs.addAll(uuids);
      this.spellId = spell.getSpellId();
   }

   public SyncTargetingDataPacket(FriendlyByteBuf buf) {
      this.spellId = buf.m_130277_();
      int i = buf.readInt();

      for (int j = 0; j < i; j++) {
         this.targetUUIDs.add(buf.m_130259_());
      }
   }

   public void toBytes(FriendlyByteBuf buf) {
      IronsSpellbooks.LOGGER.debug("ClientboundSyncTargetingData.toBytes: {} {}: {}", new Object[]{this.spellId, this.targetUUIDs.size(), this.targetUUIDs});
      buf.m_130070_(this.spellId);
      buf.writeInt(this.targetUUIDs.size());
      this.targetUUIDs.forEach(buf::m_130077_);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.setTargetingData(new ClientSpellTargetingData(this.spellId, this.targetUUIDs)));
      return true;
   }
}
