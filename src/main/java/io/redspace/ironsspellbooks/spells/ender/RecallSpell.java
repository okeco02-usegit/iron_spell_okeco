package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.goals.HomeOwner;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalTeleporter;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RecallSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "recall");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(300.0)
      .build();

   public RecallSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 80;
      this.baseManaCost = 100;
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.castTime;
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer serverPlayer) {
         if (entity.m_21231_().f_19281_) {
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
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.RECALL_PREPARE.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.f_11852_);
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.playSound(this.getCastFinishSound(), entity);
      if (entity instanceof ServerPlayer serverPlayer) {
         ServerLevel respawnLevel = ((ServerLevel)world).m_7654_().m_129880_(serverPlayer.m_8963_());
         respawnLevel = respawnLevel == null ? world.m_7654_().m_129783_() : respawnLevel;
         Optional<Vec3> spawnLocation = findSpawnPosition(respawnLevel, serverPlayer);
         if (spawnLocation.isPresent()) {
            Vec3 vec3 = spawnLocation.get();
            if (serverPlayer.f_19853_.m_46472_() != respawnLevel.m_46472_()) {
               serverPlayer.changeDimension(respawnLevel, new PortalTeleporter(vec3));
            } else {
               serverPlayer.m_6021_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
            }
         } else {
            respawnLevel = world.m_7654_().m_129783_();
            if (serverPlayer.f_19853_.m_46472_() != respawnLevel.m_46472_()) {
               serverPlayer.changeDimension(respawnLevel, new PortalTeleporter(Vec3.f_82478_));
            }

            serverPlayer.f_8906_.m_9829_(new ClientboundGameEventPacket(ClientboundGameEventPacket.f_132153_, 0.0F));
            BlockPos pos = respawnLevel.m_220360_();
            serverPlayer.m_6021_(pos.m_123341_(), pos.m_123342_(), pos.m_123343_());
         }
      } else if (entity instanceof HomeOwner homeOwner && homeOwner.getHome() != null) {
         BlockPos pos = homeOwner.getHome();
         entity.m_6021_(pos.m_123341_(), pos.m_123342_() + 0.15, pos.m_123343_());
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public static Optional<Vec3> findSpawnPosition(ServerLevel level, ServerPlayer player) {
      BlockPos spawnBlockpos = player.m_8961_();
      if (spawnBlockpos == null) {
         return Optional.empty();
      } else {
         BlockState blockstate = level.m_8055_(spawnBlockpos);
         Block block = blockstate.m_60734_();
         if (block instanceof RespawnAnchorBlock && (Integer)blockstate.m_61143_(RespawnAnchorBlock.f_55833_) > 0 && RespawnAnchorBlock.m_55850_(level)) {
            return RespawnAnchorBlock.m_55839_(EntityType.f_20532_, level, spawnBlockpos);
         } else {
            return block instanceof BedBlock && BedBlock.m_49488_(level)
               ? BedBlock.m_260958_(EntityType.f_20532_, level, spawnBlockpos, player.m_6350_(), player.m_146908_())
               : Optional.empty();
         }
      }
   }

   public static void ambientParticles(LivingEntity entity, SyncedSpellData spellData) {
      float f = entity.f_19797_ * 0.125F;
      Vec3 trail1 = new Vec3(Mth.m_14089_(f), Mth.m_14031_(f * 2.0F), Mth.m_14031_(f)).m_82541_();
      Vec3 trail2 = new Vec3(Mth.m_14031_(f), Mth.m_14089_(f * 2.0F), Mth.m_14089_(f)).m_82541_();
      Vec3 trail3 = trail1.m_82559_(trail2).m_82541_().m_82490_(1.0F + (Mth.m_14031_(f) + Mth.m_14089_(f)) * 0.5F);
      Vec3 pos = entity.m_20191_().m_82399_();
      entity.f_19853_
         .m_7106_(ParticleHelper.UNSTABLE_ENDER, pos.f_82479_ + trail1.f_82479_, pos.f_82480_ + trail1.f_82480_, pos.f_82481_ + trail1.f_82481_, 0.0, 0.0, 0.0);
      entity.f_19853_
         .m_7106_(ParticleHelper.UNSTABLE_ENDER, pos.f_82479_ + trail2.f_82479_, pos.f_82480_ + trail2.f_82480_, pos.f_82481_ + trail2.f_82481_, 0.0, 0.0, 0.0);
      entity.f_19853_
         .m_7106_(ParticleHelper.UNSTABLE_ENDER, pos.f_82479_ + trail3.f_82479_, pos.f_82480_ + trail3.f_82480_, pos.f_82481_ + trail3.f_82481_, 0.0, 0.0, 0.0);
   }

   @Override
   public void playSound(Optional<SoundEvent> sound, Entity entity) {
      sound.ifPresent(soundEvent -> entity.m_5496_(soundEvent, 2.0F, 1.0F));
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.none();
   }

   @Override
   public boolean stopSoundOnCancel() {
      return true;
   }
}
