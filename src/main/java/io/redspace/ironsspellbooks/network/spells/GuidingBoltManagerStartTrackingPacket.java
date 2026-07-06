package io.redspace.ironsspellbooks.network.spells;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.network.NetworkEvent.Context;

public class GuidingBoltManagerStartTrackingPacket implements CustomPacketPayload {
   private final UUID entity;
   private final List<Integer> projectileIds;

   public GuidingBoltManagerStartTrackingPacket(Entity entity, List<Projectile> projectiles) {
      this.entity = entity.m_20148_();
      this.projectileIds = projectiles.stream().<Integer>map(Entity::m_19879_).toList();
   }

   public GuidingBoltManagerStartTrackingPacket(FriendlyByteBuf buf) {
      this.projectileIds = new ArrayList<>();
      this.entity = buf.m_130259_();
      int i = buf.readInt();

      for (int j = 0; j < i; j++) {
         this.projectileIds.add(buf.readInt());
      }
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.m_130077_(this.entity);
      buf.writeInt(this.projectileIds.size());

      for (Integer projectileId : this.projectileIds) {
         buf.writeInt(projectileId);
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> GuidingBoltManager.handleClientboundStartTracking(this.entity, this.projectileIds));
      return true;
   }
}
