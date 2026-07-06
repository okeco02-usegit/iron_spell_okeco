package io.redspace.ironsspellbooks.entity.spells.portal;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

public class PortalTeleporter implements ITeleporter {
   private final Vec3 destinationPosition;
   private final Optional<Float> rotation;

   public PortalTeleporter(Vec3 destinationPosition) {
      this.destinationPosition = destinationPosition;
      this.rotation = Optional.empty();
   }

   public PortalTeleporter(Vec3 destinationPosition, float rotation) {
      this.destinationPosition = destinationPosition;
      this.rotation = Optional.of(rotation);
   }

   public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
      entity.f_19789_ = 0.0F;
      return repositionEntity.apply(false);
   }

   @Nullable
   public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
      return new PortalInfo(this.destinationPosition, Vec3.f_82478_, this.rotation.orElse(entity.m_146908_()), entity.m_146909_());
   }

   public boolean isVanilla() {
      return false;
   }

   public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
      return false;
   }
}
