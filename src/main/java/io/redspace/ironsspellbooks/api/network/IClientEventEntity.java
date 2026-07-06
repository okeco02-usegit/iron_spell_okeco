package io.redspace.ironsspellbooks.api.network;

import io.redspace.ironsspellbooks.network.EntityEventPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.world.entity.Entity;

public interface IClientEventEntity {
   void handleClientEvent(byte var1);

   default <T extends Entity & IClientEventEntity> void serverTriggerEvent(byte eventId) {
      PacketDistributor.sendToPlayersTrackingEntity((Entity)this, new EntityEventPacket((Entity)this, eventId));
   }
}
