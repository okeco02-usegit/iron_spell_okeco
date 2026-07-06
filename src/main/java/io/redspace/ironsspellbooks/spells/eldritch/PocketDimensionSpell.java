package io.redspace.ironsspellbooks.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.capabilities.magic.SerializedTargetData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalTeleporter;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PocketDimensionSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "pocket_dimension");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(60.0)
      .build();

   public PocketDimensionSpell() {
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
      this.castTime = 40;
      this.baseManaCost = 300;
   }

   @Override
   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.castTime;
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer serverPlayer) {
         if (level.m_46472_().equals(PocketDimensionManager.POCKET_DIMENSION)) {
            serverPlayer.f_8906_
               .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_dimension").m_130940_(ChatFormatting.RED)));
            return false;
         } else if (entity.m_21231_().f_19281_) {
            serverPlayer.m_5661_(Component.m_237115_("ui.irons_spellbooks.cast_error_combat").m_130940_(ChatFormatting.RED), true);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.POCKET_DIMENSION_TRAVEL.get());
   }

   @Override
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.ELDRITCH_PREPARE.get());
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public ICastDataSerializable getEmptyCastData() {
      return new SerializedTargetData();
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      if (entity instanceof ServerPlayer serverPlayer) {
         PortalData portalData = new PortalData();
         portalData.setPortalDuration(1200);
         portalData.firstPortal(serverPlayer.m_20148_(), PortalPos.of(serverPlayer.f_19853_.m_46472_(), serverPlayer.m_20182_(), serverPlayer.m_146908_()));
         PocketDimensionManager.INSTANCE.maybeGeneratePocketRoom(serverPlayer);
         BlockPos portalPos = PocketDimensionManager.INSTANCE
            .findPortalForStructure(serverPlayer.m_284548_(), PocketDimensionManager.INSTANCE.structurePosForPlayer(serverPlayer));
         ServerLevel pocketLevel = serverPlayer.m_20194_().m_129880_(PocketDimensionManager.POCKET_DIMENSION);
         if (pocketLevel.m_7702_(portalPos) instanceof PortalFrameBlockEntity portalFrameBlockEntity) {
            Vec3 particlePos = serverPlayer.m_20191_().m_82399_();
            MagicManager.spawnParticles(
               level, ParticleTypes.f_123762_, particlePos.f_82479_, particlePos.f_82480_, particlePos.f_82481_, 100, 0.1, 0.2, 0.1, 0.1, false
            );
            UUID uuid = portalFrameBlockEntity.getUUID();
            portalData.secondPortal(uuid, PortalPos.of(PocketDimensionManager.POCKET_DIMENSION, Vec3.m_82539_(portalPos), 180.0F));
            PortalManager.INSTANCE.addPortalData(uuid, portalData);
            portalFrameBlockEntity.m_6596_();
            PortalManager.INSTANCE.addDirectPortalCooldown(serverPlayer, uuid);
            Scroll.attemptRemoveScrollAfterCast(serverPlayer);
            serverPlayer.m_8127_();
            serverPlayer.changeDimension(pocketLevel, new PortalTeleporter(portalData.globalPos2.pos(), portalData.globalPos2.rotation()));
         }
      }
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }
}
