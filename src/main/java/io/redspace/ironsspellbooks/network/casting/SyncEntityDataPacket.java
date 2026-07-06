package io.redspace.ironsspellbooks.network.casting;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncEntityDataPacket implements CustomPacketPayload {
   SyncedSpellData syncedSpellData;
   int entityId;

   public SyncEntityDataPacket(SyncedSpellData syncedSpellData, IMagicEntity entity) {
      this.syncedSpellData = syncedSpellData;
      if (entity instanceof PathfinderMob m) {
         this.entityId = m.m_19879_();
      } else {
         throw new IllegalStateException("Unable to add " + this.getClass().getSimpleName() + "to entity, must extend PathfinderMob.");
      }
   }

   public SyncEntityDataPacket(FriendlyByteBuf buf) {
      this.entityId = buf.readInt();
      this.syncedSpellData = SyncedSpellData.read(buf);
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeInt(this.entityId);
      SyncedSpellData.write(buf, this.syncedSpellData);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> ClientMagicData.handleAbstractCastingMobSyncedData(this.entityId, this.syncedSpellData));
      return true;
   }
}
