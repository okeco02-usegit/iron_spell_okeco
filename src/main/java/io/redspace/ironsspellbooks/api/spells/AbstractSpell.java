package io.redspace.ironsspellbooks.api.spells;

import com.google.common.util.concurrent.AtomicDouble;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.OnCastFinishedPacket;
import io.redspace.ironsspellbooks.network.casting.OnCastStartedPacket;
import io.redspace.ironsspellbooks.network.casting.OnClientCastPacket;
import io.redspace.ironsspellbooks.network.casting.UpdateCastingStatePacket;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;

public abstract class AbstractSpell {
   public static final Style ELDRITCH_OBFUSCATED_STYLE = Style.f_131099_.m_178524_(true).m_131150_(ResourceLocation.withDefaultNamespace("alt"));
   private String spellID = null;
   private String deathMessageId = null;
   private String spellName = null;
   protected int baseManaCost;
   protected int manaCostPerLevel;
   protected int baseSpellPower;
   protected int spellPowerPerLevel;
   protected int castTime;
   private volatile List<Double> rarityWeights;
   private final int maxRarity = SpellRarity.LEGENDARY.getValue();

   public final String getSpellName() {
      if (this.spellName == null) {
         ResourceLocation resourceLocation = Objects.requireNonNull(this.getSpellResource());
         this.spellName = resourceLocation.m_135815_().intern();
      }

      return this.spellName;
   }

   public final String getSpellId() {
      if (this.spellID == null) {
         ResourceLocation resourceLocation = Objects.requireNonNull(this.getSpellResource());
         this.spellID = resourceLocation.toString().intern();
      }

      return this.spellID;
   }

   public final ResourceLocation getSpellIconResource() {
      return ResourceLocation.fromNamespaceAndPath(this.getSpellResource().m_135827_(), "textures/gui/spell_icons/" + this.getSpellName() + ".png");
   }

   @Deprecated(forRemoval = true)
   public int getMinRarity() {
      return SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.MIN_RARITY).getValue();
   }

   public int getMaxLevel() {
      return SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.MAX_LEVEL);
   }

   public int getMinLevel() {
      return 1;
   }

   public MutableComponent getDisplayName(Player player) {
      boolean obfuscateName = player != null && this.obfuscateStats(player);
      return Component.m_237115_(this.getComponentId()).m_130948_(obfuscateName ? ELDRITCH_OBFUSCATED_STYLE : Style.f_131099_);
   }

   public String getComponentId() {
      return String.format("spell.%s.%s", this.getSpellResource().m_135827_(), this.getSpellName());
   }

   public Component getLockedMessage() {
      return Component.m_237115_("ui.irons_spellbooks.unlearned_error");
   }

   public abstract ResourceLocation getSpellResource();

   public abstract DefaultConfig getDefaultConfig();

   public abstract CastType getCastType();

   public SchoolType getSchoolType() {
      return SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.SCHOOL);
   }

   public Vector3f getTargetingColor() {
      return this.getSchoolType().getTargetingColor();
   }

   public final int getLevelFor(int level, @Nullable LivingEntity caster) {
      int addition = 0;
      if (caster != null) {
         addition = CuriosApi.getCuriosInventory(caster)
            .map(
               inv -> inv.findCurios(AffinityData::hasAffinityData)
                  .stream()
                  .mapToInt(slot -> AffinityData.getAffinityData(slot.stack()).getBonusFor(this))
                  .sum()
            )
            .orElse(0);
      }

      ModifySpellLevelEvent levelEvent = new ModifySpellLevelEvent(this, caster, level, level + addition);
      MinecraftForge.EVENT_BUS.post(levelEvent);
      return levelEvent.getLevel();
   }

   public int getManaCost(int level) {
      return (int)(
         (this.baseManaCost + this.manaCostPerLevel * (level - 1)) * SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.MANA_MULTIPLIER)
      );
   }

   public int getSpellCooldown() {
      return (int)(SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.COOLDOWN_IN_SECONDS) * 20.0);
   }

   public int getCastTime(int spellLevel) {
      return this.getCastType() == CastType.INSTANT ? 0 : this.castTime;
   }

   public ICastDataSerializable getEmptyCastData() {
      return null;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(this.defaultCastSound());
   }

   public AnimationHolder getCastStartAnimation() {
      return switch (this.getCastType()) {
         case INSTANT -> SpellAnimations.ANIMATION_INSTANT_CAST;
         case CONTINUOUS -> SpellAnimations.ANIMATION_CONTINUOUS_CAST;
         case LONG -> SpellAnimations.ANIMATION_LONG_CAST;
         default -> AnimationHolder.none();
      };
   }

   public AnimationHolder getCastFinishAnimation() {
      return switch (this.getCastType()) {
         case INSTANT -> AnimationHolder.pass();
         case LONG -> SpellAnimations.ANIMATION_LONG_CAST_FINISH;
         default -> AnimationHolder.none();
      };
   }

   public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {
      double entitySpellPowerModifier = 1.0;
      double entitySchoolPowerModifier = 1.0;
      float configPowerModifier = SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.POWER_MULTIPLIER).floatValue();
      if (sourceEntity instanceof LivingEntity livingEntity) {
         entitySpellPowerModifier = (float)livingEntity.m_21133_((Attribute)AttributeRegistry.SPELL_POWER.get());
         entitySchoolPowerModifier = this.getSchoolType().getPowerFor(livingEntity);
      }

      return (float)(
         (this.baseSpellPower + this.spellPowerPerLevel * (spellLevel - 1)) * entitySpellPowerModifier * entitySchoolPowerModifier * configPowerModifier
      );
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 0;
   }

   public float getEntityPowerMultiplier(@Nullable LivingEntity entity) {
      float base = SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.POWER_MULTIPLIER).floatValue();
      if (entity == null) {
         return base;
      }

      float entitySpellPowerModifier = (float)entity.m_21133_((Attribute)AttributeRegistry.SPELL_POWER.get());
      double entitySchoolPowerModifier = this.getSchoolType().getPowerFor(entity);
      return (float)(base * entitySpellPowerModifier * entitySchoolPowerModifier);
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      double entityCastTimeModifier = 1.0;
      if (entity != null) {
         if (this.getCastType() != CastType.CONTINUOUS) {
            entityCastTimeModifier = 2.0 - Utils.softCapFormula(entity.m_21133_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get()));
         } else {
            entityCastTimeModifier = entity.m_21133_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get());
         }
      }

      return Math.round(this.getCastTime(spellLevel) * (float)entityCastTimeModifier);
   }

   public boolean attemptInitiateCast(
      ItemStack stack, int spellLevel, Level level, Player player, CastSource castSource, boolean triggerCooldown, String castingEquipmentSlot
   ) {
      if (level.f_46443_) {
         return false;
      }

      ServerPlayer serverPlayer = (ServerPlayer)player;
      MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
      if (!playerMagicData.isCasting()) {
         CastResult castResult = this.canBeCastedBy(spellLevel, castSource, playerMagicData, serverPlayer);
         if (castResult.message != null) {
            serverPlayer.f_8906_.m_9829_(new ClientboundSetActionBarTextPacket(castResult.message));
         }

         if (castResult.isSuccess()
            && this.checkPreCastConditions(level, spellLevel, serverPlayer, playerMagicData)
            && !MinecraftForge.EVENT_BUS.post(new SpellPreCastEvent(player, this.getSpellId(), spellLevel, this.getSchoolType(), castSource))) {
            if (serverPlayer.m_6117_()) {
               serverPlayer.m_5810_();
            }

            int effectiveCastTime = this.getEffectiveCastTime(spellLevel, player);
            playerMagicData.initiateCast(this, spellLevel, effectiveCastTime, castSource, castingEquipmentSlot);
            playerMagicData.setPlayerCastingItem(stack);
            this.onServerPreCast(player.f_19853_, spellLevel, player, playerMagicData);
            PacketDistributor.sendToPlayer(
               serverPlayer, new UpdateCastingStatePacket(this.getSpellId(), spellLevel, effectiveCastTime, castSource, castingEquipmentSlot)
            );
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new OnCastStartedPacket(serverPlayer.m_20148_(), this.getSpellId(), spellLevel));
            return true;
         } else {
            return false;
         }
      } else {
         Utils.serverSideCancelCast(serverPlayer);
         return false;
      }
   }

   public void castSpell(Level world, int spellLevel, ServerPlayer serverPlayer, CastSource castSource, boolean triggerCooldown) {
      MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
      PlayerRecasts playerRecasts = magicData.getPlayerRecasts();
      boolean playerAlreadyHasRecast = playerRecasts.hasRecastForSpell(this.getSpellId());
      SpellOnCastEvent event = new SpellOnCastEvent(serverPlayer, this.getSpellId(), spellLevel, this.getManaCost(spellLevel), this.getSchoolType(), castSource);
      MinecraftForge.EVENT_BUS.post(event);
      if (castSource.consumesMana() && !playerAlreadyHasRecast && (!serverPlayer.m_7500_() || (Boolean)ServerConfigs.CREATIVE_MANA_COST.get())) {
         float newMana = Math.max(magicData.getMana() - event.getManaCost(), 0.0F);
         magicData.setMana(newMana);
         PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData));
      }

      this.onCast(world, event.getSpellLevel(), serverPlayer, castSource, magicData);
      boolean playerHasRecastsLeft = playerRecasts.hasRecastForSpell(this.getSpellId());
      if (playerAlreadyHasRecast && playerHasRecastsLeft) {
         playerRecasts.decrementRecastCount(this.getSpellId());
      } else if (!playerHasRecastsLeft && triggerCooldown && (!serverPlayer.m_7500_() || (Boolean)ServerConfigs.CREATIVE_COOLDOWN.get())) {
         MagicHelper.MAGIC_MANAGER.addCooldown(serverPlayer, this, castSource);
      }

      PacketDistributor.sendToPlayer(serverPlayer, new OnClientCastPacket(this.getSpellId(), spellLevel, castSource, magicData.getAdditionalCastData()));
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      MagicHelper.MAGIC_MANAGER.addCooldown(serverPlayer, this, recastInstance.getCastSource());
   }

   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      this.playSound(this.getCastFinishSound(), entity);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.playSound(this.getCastFinishSound(), entity);
   }

   public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
      if ((Boolean)ServerConfigs.DISABLE_ADVENTURE_MODE_CASTING.get()
         && player instanceof ServerPlayer serverPlayer
         && serverPlayer.f_8941_.m_9290_() == GameType.ADVENTURE) {
         return new CastResult(CastResult.Type.FAILURE, Component.m_237115_("ui.irons_spellbooks.cast_error_adventure").m_130940_(ChatFormatting.RED));
      } else {
         float playerMana = playerMagicData.getMana();
         boolean hasEnoughMana = playerMana - this.getManaCost(spellLevel) >= 0.0F;
         boolean isSpellOnCooldown = playerMagicData.getPlayerCooldowns().isOnCooldown(this);
         boolean hasRecastForSpell = playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId());
         if (this.requiresLearning() && !this.isLearned(player)) {
            return new CastResult(CastResult.Type.FAILURE, Component.m_237115_("ui.irons_spellbooks.cast_error_unlearned").m_130940_(ChatFormatting.RED));
         } else if (castSource == CastSource.SCROLL && this.getRecastCount(spellLevel, player) > 0) {
            return new CastResult(
               CastResult.Type.FAILURE,
               Component.m_237110_("ui.irons_spellbooks.cast_error_scroll", new Object[]{this.getDisplayName(player)}).m_130940_(ChatFormatting.RED)
            );
         } else if (castSource != CastSource.SPELLBOOK && castSource != CastSource.SWORD
            || !isSpellOnCooldown
            || player.m_7500_() && !(Boolean)ServerConfigs.CREATIVE_COOLDOWN.get()) {
            return hasRecastForSpell || !castSource.consumesMana() || hasEnoughMana || player.m_7500_() && !ServerConfigs.CREATIVE_MANA_COST.get()
               ? new CastResult(CastResult.Type.SUCCESS)
               : new CastResult(
                  CastResult.Type.FAILURE,
                  Component.m_237110_("ui.irons_spellbooks.cast_error_mana", new Object[]{this.getDisplayName(player)}).m_130940_(ChatFormatting.RED)
               );
         } else {
            return new CastResult(
               CastResult.Type.FAILURE,
               Component.m_237110_("ui.irons_spellbooks.cast_error_cooldown", new Object[]{this.getDisplayName(player)}).m_130940_(ChatFormatting.RED)
            );
         }
      }
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return true;
   }

   public void playSound(Optional<SoundEvent> sound, Entity entity) {
      sound.ifPresent(soundEvent -> entity.m_5496_(soundEvent, 2.0F, 0.9F + Utils.random.m_188501_() * 0.2F));
   }

   private SoundEvent defaultCastSound() {
      return this.getSchoolType().getCastSound();
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      playerMagicData.resetCastingState();
      if (entity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new OnCastFinishedPacket(serverPlayer.m_20148_(), this.getSpellId(), cancelled));
      }
   }

   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      if (this.getCastType().immediatelySuppressRightClicks() && ClientInputEvents.isUseKeyDown) {
         ClientSpellCastHelper.setSuppressRightClicks(true);
      }

      this.playSound(this.getCastStartSound(), entity);
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      this.playSound(this.getCastStartSound(), entity);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
   }

   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return false;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof AbstractSpell other ? this.getSpellResource().equals(other.getSpellResource()) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.getSpellResource().hashCode();
   }

   public void resetRarityWeights() {
      this.rarityWeights = null;
   }

   private void initializeRarityWeights() {
      synchronized (SpellRegistry.none()) {
         if (this.rarityWeights == null) {
            int minRarity = this.getMinRarity();
            int maxRarity = this.getMaxRarity();
            List<Double> rarityRawConfig = SpellRarity.getRawRarityConfig();
            List<Double> rarityConfig = SpellRarity.getRarityConfig();
            if (minRarity != 0) {
               List<Double> subList = rarityRawConfig.subList(minRarity, maxRarity + 1);
               double subtotal = subList.stream().reduce(0.0, Double::sum);
               List<Double> rarityRawWeights = subList.stream().map(item -> item / subtotal * (1.0 - subtotal) + item).toList();
               AtomicDouble counter = new AtomicDouble();
               this.rarityWeights = new ArrayList<>();
               rarityRawWeights.forEach(item -> this.rarityWeights.add(counter.addAndGet(item)));
            } else {
               this.rarityWeights = rarityConfig;
            }
         }
      }
   }

   public SpellRarity getRarity(int level) {
      if (this.rarityWeights == null) {
         this.initializeRarityWeights();
      }

      int maxLevel = this.getMaxLevel();
      int maxRarity = this.getMaxRarity();
      if (maxLevel == 1) {
         return SpellRarity.values()[this.getMinRarity()];
      }

      if (level >= maxLevel) {
         return SpellRarity.LEGENDARY;
      }

      double percentOfMaxLevel = (double)level / maxLevel;
      int lookupOffset = maxRarity + 1 - this.rarityWeights.size();

      for (int i = 0; i < this.rarityWeights.size(); i++) {
         if (percentOfMaxLevel <= this.rarityWeights.get(i)) {
            return SpellRarity.values()[i + lookupOffset];
         }
      }

      return SpellRarity.COMMON;
   }

   public String getDeathMessageId() {
      if (this.deathMessageId == null) {
         this.deathMessageId = this.getSpellId().replace(':', '.');
      }

      return this.deathMessageId;
   }

   public final SpellDamageSource getDamageSource(Entity attacker) {
      return this.getDamageSource(attacker, attacker);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return SpellDamageSource.source(projectile, attacker, this);
   }

   public boolean isEnabled() {
      return SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.ENABLED);
   }

   public int getMaxRarity() {
      return this.maxRarity;
   }

   public int getMinLevelForRarity(SpellRarity rarity) {
      if (this.rarityWeights == null) {
         this.initializeRarityWeights();
      }

      int minRarity = this.getMinRarity();
      int maxLevel = this.getMaxLevel();
      if (rarity.getValue() < minRarity) {
         return 0;
      } else {
         return rarity.getValue() == minRarity ? 1 : (int)(this.rarityWeights.get(rarity.getValue() - (1 + minRarity)) * maxLevel) + 1;
      }
   }

   public boolean allowLooting() {
      return this.getSchoolType().allowLooting;
   }

   public boolean canBeCraftedBy(Player player) {
      return !this.requiresLearning() || this.isLearned(player);
   }

   public boolean allowCrafting() {
      return SpellConfigManager.getSpellConfigValue(this, SpellConfigParameter.ALLOW_CRAFTING);
   }

   public boolean obfuscateStats(@Nullable Player player) {
      return this.requiresLearning() && !this.isLearned(player);
   }

   public boolean isLearned(@Nullable Player player) {
      if (player == null) {
         return false;
      } else {
         return player.f_19853_.f_46443_
            ? ClientMagicData.getSyncedSpellData(player).isSpellLearned(this)
            : MagicData.getPlayerMagicData(player).getSyncedData().isSpellLearned(this);
      }
   }

   public boolean requiresLearning() {
      return this.getSchoolType().requiresLearning;
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return this.getCastType() == CastType.LONG && !((CurioBaseItem)ItemRegistry.CONCENTRATION_AMULET.get()).isEquippedBy(player);
   }

   public boolean stopSoundOnCancel() {
      return false;
   }
}
