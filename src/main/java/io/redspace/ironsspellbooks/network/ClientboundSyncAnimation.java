package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

@Deprecated(forRemoval = true)
public class ClientboundSyncAnimation<T extends Entity & IAnimatedAttacker> extends SyncAnimationPacket<T> {
   public ClientboundSyncAnimation(String animationId, T entity) {
      super(animationId, entity);
   }

   public ClientboundSyncAnimation(FriendlyByteBuf buf) {
      super(buf);
   }
}
