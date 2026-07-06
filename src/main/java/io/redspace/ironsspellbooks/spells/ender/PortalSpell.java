package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlock;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class PortalSpell extends AbstractSpell {
   public static final int PORTAL_RECAST_COUNT = 2;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "portal");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(180.0)
      .build();

   public PortalSpell() {
      this.baseSpellPower = 300;
      this.spellPowerPerLevel = 120;
      this.baseManaCost = 200;
      this.manaCostPerLevel = 10;
      this.castTime = 0;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public ICastDataSerializable getEmptyCastData() {
      return new PortalData();
   }

   @Override
   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public static BlockHitResult getTargetBlock(Level level, LivingEntity entity, Fluid clipContext, double reach) {
      Vec3 rotation = entity.m_20154_().m_82541_().m_82490_(reach);
      Vec3 pos = entity.m_146892_();
      Vec3 dest = rotation.m_82549_(pos);
      return level.m_45547_(new ClipContext(pos, dest, Block.OUTLINE, clipContext, entity));
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (level.m_46472_().equals(PocketDimensionManager.POCKET_DIMENSION)) {
         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_dimension").m_130940_(ChatFormatting.RED)));
         }

         return false;
      } else {
         RecastInstance recast = playerMagicData.getPlayerRecasts().getRecastInstance(this.getSpellId());
         if (recast != null && recast.getCastData() instanceof PortalData portalData && portalData.isBlock) {
            BlockHitResult blockHitResult = getTargetBlock(level, entity, Fluid.NONE, this.getCastDistance(spellLevel, entity));
            if (blockHitResult.m_6662_() == Type.MISS
               || !(level.m_7702_(blockHitResult.m_82425_()) instanceof PortalFrameBlockEntity portalFrame && !portalFrame.isPortalConnected())) {
               if (entity instanceof ServerPlayer serverPlayer) {
                  serverPlayer.f_8906_
                     .m_9829_(
                        new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.portal_target_failure").m_130940_(ChatFormatting.RED))
                     );
               }

               return false;
            }
         }

         return super.checkPreCastConditions(level, spellLevel, entity, playerMagicData);
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
         RecastInstance recastInstance = playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())
            ? playerMagicData.getPlayerRecasts().getRecastInstance(this.getSpellId())
            : null;
         BlockHitResult blockHitResult = getTargetBlock(level, entity, Fluid.NONE, this.getCastDistance(spellLevel, entity));
         boolean canHitBlock = recastInstance == null || ((PortalData)recastInstance.getCastData()).isBlock;
         if (canHitBlock
            && blockHitResult.m_6662_() != Type.MISS
            && level.m_7702_(blockHitResult.m_82425_()) instanceof PortalFrameBlockEntity portalFrame
            && !portalFrame.isPortalConnected()) {
            this.handleBlockPortal(recastInstance, spellLevel, castSource, playerMagicData, player, blockHitResult, portalFrame);
         } else {
            this.handleEntityPortal(recastInstance, level, spellLevel, entity, castSource, playerMagicData, player, serverLevel, blockHitResult);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void handleBlockPortal(
      @Nullable RecastInstance recastInstance,
      int spellLevel,
      CastSource castSource,
      MagicData playerMagicData,
      Player player,
      BlockHitResult blockHitResult,
      PortalFrameBlockEntity portalFrame
   ) {
      Vec3 portalLocation = portalFrame.getPortalLocation();
      float portalRotation = ((Direction)portalFrame.m_58900_().m_61143_(PortalFrameBlock.FACING)).m_122435_();
      if (recastInstance != null) {
         PortalData portalData = (PortalData)recastInstance.getCastData();
         if (portalData.globalPos1 != null & portalData.portalEntityId1 != null) {
            portalData.globalPos2 = PortalPos.of(player.f_19853_.m_46472_(), portalLocation, portalRotation);
            portalData.portalEntityId2 = portalFrame.getUUID();
            PortalManager.INSTANCE.addPortalData(portalData.portalEntityId1, portalData);
            PortalManager.INSTANCE.addPortalData(portalData.portalEntityId2, portalData);
            portalFrame.m_6596_();
         }
      } else {
         PortalData portalData = new PortalData();
         portalData.isBlock = true;
         portalData.globalPos1 = PortalPos.of(player.f_19853_.m_46472_(), portalLocation, portalRotation);
         portalData.portalEntityId1 = portalFrame.getUUID();
         PortalManager.INSTANCE.addPortalData(portalData.portalEntityId1, portalData);
         portalFrame.m_6596_();
         playerMagicData.getPlayerRecasts()
            .addRecast(
               new RecastInstance(this.getSpellId(), spellLevel, 2, this.getRecastDuration(spellLevel, player), castSource, portalData), playerMagicData
            );
      }
   }

   private void handleEntityPortal(
      @Nullable RecastInstance recastInstance,
      Level level,
      int spellLevel,
      LivingEntity entity,
      CastSource castSource,
      MagicData playerMagicData,
      Player player,
      ServerLevel serverLevel,
      BlockHitResult blockHitResult
   ) {
      Vec3 hitResultPos = blockHitResult.m_82450_().m_82546_(entity.m_20156_().m_82541_().m_82542_(0.25, 0.0, 0.25));
      Vec3 portalLocation = level.m_45547_(
            new ClipContext(hitResultPos, hitResultPos.m_82520_(0.0, -entity.m_20206_() - 1.0F, 0.0), Block.COLLIDER, Fluid.NONE, entity)
         )
         .m_82450_()
         .m_82520_(0.0, 0.076, 0.0);
      float portalRotation = 90.0F
         + Utils.getAngle(portalLocation.f_82479_, portalLocation.f_82481_, entity.m_20185_(), entity.m_20189_()) * (180.0F / (float)Math.PI);
      if (recastInstance != null) {
         PortalData portalData = (PortalData)recastInstance.getCastData();
         if (portalData.globalPos1 != null & portalData.portalEntityId1 != null) {
            portalData.globalPos2 = PortalPos.of(player.f_19853_.m_46472_(), portalLocation, portalRotation);
            portalData.setPortalDuration(this.getPortalDuration(spellLevel, player));
            PortalEntity secondPortalEntity = this.setupPortalEntity(serverLevel, portalData, player, portalLocation, portalRotation);
            secondPortalEntity.setPortalConnected();
            portalData.portalEntityId2 = secondPortalEntity.m_20148_();
            PortalManager.INSTANCE.addPortalData(portalData.portalEntityId1, portalData);
            PortalManager.INSTANCE.addPortalData(portalData.portalEntityId2, portalData);
            ServerLevel firstPortalLevel = serverLevel.m_7654_().m_129880_(portalData.globalPos1.dimension());
            if (firstPortalLevel != null) {
               PortalEntity firstPortalEntity = (PortalEntity)firstPortalLevel.m_8791_(portalData.portalEntityId1);
               if (firstPortalEntity != null) {
                  firstPortalEntity.setPortalConnected();
                  firstPortalEntity.setTicksToLive(portalData.ticksToLive);
               }
            }
         }
      } else {
         PortalData portalData = new PortalData();
         portalData.setPortalDuration(this.getRecastDuration(spellLevel, player) + 10);
         PortalEntity portalEntity = this.setupPortalEntity(level, portalData, player, portalLocation, portalRotation);
         portalData.globalPos1 = PortalPos.of(player.f_19853_.m_46472_(), portalLocation, portalRotation);
         portalData.portalEntityId1 = portalEntity.m_20148_();
         playerMagicData.getPlayerRecasts()
            .addRecast(
               new RecastInstance(this.getSpellId(), spellLevel, 2, this.getRecastDuration(spellLevel, player), castSource, portalData), playerMagicData
            );
      }
   }

   private PortalEntity setupPortalEntity(Level level, PortalData portalData, Player owner, Vec3 spawnPos, float rotation) {
      PortalEntity portalEntity = new PortalEntity(level, portalData);
      portalEntity.setOwnerUUID(owner.m_20148_());
      portalEntity.m_20219_(spawnPos);
      portalEntity.m_146922_(rotation);
      level.m_7967_(portalEntity);
      return portalEntity;
   }

   @Override
   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (recastResult != RecastResult.USED_ALL_RECASTS
         && castDataSerializable instanceof PortalData portalData
         && portalData.portalEntityId1 != null
         && portalData.globalPos1 != null) {
         MinecraftServer server = serverPlayer.m_20194_();
         if (server != null) {
            ServerLevel level = server.m_129880_(portalData.globalPos1.dimension());
            if (level != null) {
               if (portalData.isBlock) {
                  BlockPos block = BlockPos.m_274446_(portalData.globalPos1.pos());
                  if (level.m_46749_(block)) {
                     level.m_141902_(block, (BlockEntityType)BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get()).ifPresent(PortalFrameBlockEntity::m_6596_);
                  }
               } else {
                  Entity portal1 = level.m_8791_(portalData.portalEntityId1);
                  if (portal1 != null) {
                     portal1.m_146870_();
                  }
               }

               PortalManager.INSTANCE.removePortalData(portalData.portalEntityId1);
            }
         }
      }

      super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
   }

   public int getRecastDuration(int spellLevel, LivingEntity caster) {
      return 2400;
   }

   public int getPortalDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }

   private float getCastDistance(int spellLevel, LivingEntity sourceEntity) {
      return 48.0F;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.cast_range", new Object[]{Utils.stringTruncation(this.getCastDistance(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.portal_duration", new Object[]{Utils.timeFromTicks(this.getPortalDuration(spellLevel, caster), 2)})
      );
   }
}
