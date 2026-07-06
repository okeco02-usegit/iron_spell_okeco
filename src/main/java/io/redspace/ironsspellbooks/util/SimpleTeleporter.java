package io.redspace.ironsspellbooks.util;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public class SimpleTeleporter implements ITeleporter {
   private final Vec3 destinationPosition;

   SimpleTeleporter(Vec3 destinationPosition) {
      this.destinationPosition = destinationPosition;
   }

   public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
      entity.f_19789_ = 0.0F;
      return repositionEntity.apply(false);
   }

   @Nullable
   public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
      return new PortalInfo(this.destinationPosition, Vec3.f_82478_, entity.m_146908_(), entity.m_146909_());
   }

   public boolean isVanilla() {
      return false;
   }

   public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
      return false;
   }
}
