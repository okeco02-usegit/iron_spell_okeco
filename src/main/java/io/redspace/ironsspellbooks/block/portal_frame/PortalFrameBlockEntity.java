package io.redspace.ironsspellbooks.block.portal_frame;

import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalTeleporter;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PortalFrameBlockEntity extends BlockEntity {
   private PortalFrameBlockEntity.PortalId portalId;
   private int color = -1;
   @Nullable
   private UUID ownerUUID = null;
   boolean clientIsConnected;
   private boolean active;
   private int activeCooldown;

   public PortalFrameBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      this((BlockEntityType<?>)BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
   }

   public PortalFrameBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
      super(pType, pPos, pBlockState);
      if (isPrimary(pBlockState)) {
         this.portalId = new PortalFrameBlockEntity.PortalId(Optional.of(UUID.randomUUID()));
      } else {
         this.portalId = new PortalFrameBlockEntity.PortalId(Optional.empty());
      }
   }

   public static boolean isPrimary(BlockState blockState) {
      return ((DoubleBlockHalf)blockState.m_61143_(PortalFrameBlock.HALF)).equals(DoubleBlockHalf.LOWER);
   }

   public static Direction directionToOther(DoubleBlockHalf half) {
      return half == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP;
   }

   public static DoubleBlockHalf otherHalf(DoubleBlockHalf half) {
      return half == DoubleBlockHalf.UPPER ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
   }

   private void ifNeighborPresent(Consumer<PortalFrameBlockEntity> consumer) {
      if (this.f_58857_ != null
         && this.f_58857_.m_7702_(this.m_58899_().m_121945_(directionToOther((DoubleBlockHalf)this.m_58900_().m_61143_(PortalFrameBlock.HALF)))) instanceof PortalFrameBlockEntity portalFrameBlockEntity
         )
       {
         consumer.accept(portalFrameBlockEntity);
      }
   }

   private <T> T ifNeighborPresentExecute(Function<PortalFrameBlockEntity, T> function, T defaultValue) {
      return this.f_58857_ != null
            && this.f_58857_.m_7702_(this.m_58899_().m_121945_(directionToOther((DoubleBlockHalf)this.m_58900_().m_61143_(PortalFrameBlock.HALF)))) instanceof PortalFrameBlockEntity portalFrameBlockEntity
         ? function.apply(portalFrameBlockEntity)
         : defaultValue;
   }

   private void ifOtherPortalFramePresent(Consumer<PortalFrameBlockEntity> consumer) {
      PortalData portalData = this.getPortalData();
      if (portalData != null && this.f_58857_ instanceof ServerLevel serverLevel) {
         MinecraftServer server = serverLevel.m_7654_();
         boolean primary = this.getUUID().equals(portalData.portalEntityId1);
         PortalPos otherPos = primary ? portalData.globalPos2 : portalData.globalPos1;
         ServerLevel dimension = server.m_129880_(otherPos.dimension());
         BlockPos otherBlockPos = BlockPos.m_274446_(otherPos.pos());
         if (dimension != null && dimension.m_7702_(otherBlockPos) instanceof PortalFrameBlockEntity portalFrame) {
            consumer.accept(portalFrame);
         }
      }
   }

   public boolean isPortalConnected() {
      return this.getPortalData() != null;
   }

   public void breakPortalConnection() {
      this.setColor(-1);
      PortalData portalData = this.getPortalData();
      if (portalData != null) {
         PortalManager.INSTANCE.removePortalData(portalData.portalEntityId1);
         PortalManager.INSTANCE.removePortalData(portalData.portalEntityId2);
         this.ifOtherPortalFramePresent(PortalFrameBlockEntity::m_6596_);
         this.m_6596_();
      }
   }

   @Nullable
   private PortalData getPortalData() {
      return PortalManager.INSTANCE.getPortalData(this.portalId.uuid(this));
   }

   public Vec3 getPortalLocation() {
      return isPrimary(this.m_58900_()) ? Vec3.m_82539_(this.m_58899_()) : Vec3.m_82539_(this.m_58899_()).m_82492_(0.0, 1.0, 0.0);
   }

   @Nullable
   public UUID getOwnerUUID() {
      return isPrimary(this.m_58900_()) ? this.ownerUUID : this.ifNeighborPresentExecute(PortalFrameBlockEntity::getOwnerUUID, null);
   }

   public void setOwnerUUID(UUID ownerUUID) {
      if (isPrimary(this.m_58900_())) {
         this.ownerUUID = ownerUUID;
      } else {
         this.ifNeighborPresent(tile -> tile.ownerUUID = ownerUUID);
      }
   }

   public void teleport(Entity entity) {
      if (entity.f_19853_ instanceof ServerLevel serverLevel) {
         UUID uuid = this.getUUID();
         PortalManager.INSTANCE.processDelayCooldown(uuid, entity.m_20148_(), 1);
         if (PortalManager.INSTANCE.canUsePortal(uuid, entity)) {
            PortalData portalData = PortalManager.INSTANCE.getPortalData(uuid);
            PortalManager.INSTANCE.addPortalCooldown(entity, uuid);
            portalData.getConnectedPortalPos(uuid)
               .ifPresent(
                  portalPos -> {
                     Vec3 destination = portalPos.pos();
                     serverLevel.m_5594_(null, this.m_58899_(), SoundEvents.f_11852_, SoundSource.BLOCKS, 1.0F, 1.0F);
                     if (serverLevel.m_46472_().equals(portalPos.dimension())) {
                        entity.m_264318_(
                           serverLevel,
                           destination.f_82479_,
                           destination.f_82480_,
                           destination.f_82481_,
                           RelativeMovement.f_263774_,
                           portalPos.rotation(),
                           entity.m_146909_()
                        );
                     } else {
                        MinecraftServer server = serverLevel.m_7654_();
                        ServerLevel dim = server.m_129880_(portalPos.dimension());
                        if (dim != null) {
                           entity.changeDimension(dim, new PortalTeleporter(destination, portalPos.rotation()));
                           dim.m_6263_(
                              null, destination.f_82479_, destination.f_82480_, destination.f_82481_, SoundEvents.f_11852_, SoundSource.BLOCKS, 1.0F, 1.0F
                           );
                        }
                     }
                  }
               );
         }
      }
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      if (tag.m_128441_("uuid")) {
         UUID uuid = tag.m_128342_("uuid");
         this.portalId = new PortalFrameBlockEntity.PortalId(Optional.of(uuid));
      }

      if (tag.m_128441_("owner")) {
         this.ownerUUID = tag.m_128342_("owner");
      }

      if (tag.m_128441_("color")) {
         this.color = tag.m_128451_("color");
      }
   }

   public void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      if (isPrimary(this.m_58900_())) {
         tag.m_128405_("color", this.color);
         UUID uuid = this.getUUID();
         if (uuid != null) {
            tag.m_128362_("uuid", uuid);
         }

         if (this.ownerUUID != null) {
            tag.m_128362_("owner", this.ownerUUID);
         }
      }
   }

   public UUID getUUID() {
      return this.portalId.uuid(this);
   }

   public CompoundTag m_5995_() {
      CompoundTag tag = super.m_5995_();
      tag.m_128379_("connected", this.isPortalConnected());
      tag.m_128405_("color", this.color);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.handleUpdateTag(pkt.m_131708_());
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
      }
   }

   public void handleUpdateTag(CompoundTag tag) {
      super.handleUpdateTag(tag);
      this.clientIsConnected = tag.m_128471_("connected");
      this.color = tag.m_128451_("color");
   }

   public void m_6596_() {
      super.m_6596_();
      if (isPrimary(this.m_58900_())) {
         if (this.f_58857_ != null) {
            this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 2);
         }
      } else {
         this.ifNeighborPresent(PortalFrameBlockEntity::m_6596_);
      }
   }

   public static void serverTick(Level level, BlockPos pos, BlockState blockState, PortalFrameBlockEntity portalFrameBlockEntity) {
      if (level.m_46467_() % 5L == 0L) {
         PortalManager.INSTANCE.processCooldownTick(portalFrameBlockEntity.getUUID(), -5);
      }

      if (portalFrameBlockEntity.active) {
         portalFrameBlockEntity.active = --portalFrameBlockEntity.activeCooldown > 0;
         level.m_6249_((Entity)null, blockState.m_60808_(level, pos).m_83215_().m_82338_(pos), ((PortalFrameBlock)blockState.m_60734_())::canTeleport)
            .forEach(portalFrameBlockEntity::teleport);
      }
   }

   public void setActive() {
      this.active = true;
      this.activeCooldown = 10;
   }

   public int getColor() {
      return isPrimary(this.m_58900_()) ? this.color : this.ifNeighborPresentExecute(PortalFrameBlockEntity::getColor, -1);
   }

   private void setColor(int color, boolean updateOther) {
      if (color != this.getColor() && this.isPortalConnected()) {
         if (isPrimary(this.m_58900_())) {
            this.color = color;
            this.m_6596_();
            if (updateOther) {
               this.ifOtherPortalFramePresent(frame -> frame.setColor(color, false));
            }
         } else {
            this.ifNeighborPresent(tile -> tile.setColor(color));
         }
      }
   }

   public void setColor(int color) {
      this.setColor(color, true);
   }

   record PortalId(Optional<UUID> _uuid) {
      UUID uuid(PortalFrameBlockEntity portalFrameBlockEntity) {
         return this._uuid
            .orElse(
               portalFrameBlockEntity.f_58857_.m_46749_(portalFrameBlockEntity.m_58899_().m_7495_())
                     && portalFrameBlockEntity.f_58857_.m_7702_(portalFrameBlockEntity.m_58899_().m_7495_()) instanceof PortalFrameBlockEntity be
                  ? be.getUUID()
                  : null
            );
      }
   }
}
