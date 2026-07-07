package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.item.CastingImplementData;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.block.BloodCauldronBlock;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.data.IronsDataStorage;
import io.redspace.ironsspellbooks.datagen.DamageTypeTagGenerator;
import io.redspace.ironsspellbooks.effect.AbyssalShroudEffect;
import io.redspace.ironsspellbooks.effect.EvasionEffect;
import io.redspace.ironsspellbooks.effect.IMobEffectEndCallback;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.ImmolateEffect;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.ice_spider.ICritablePartEntity;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.network.EquipmentChangedPacket;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent.TeleportCommand;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.LevelEvent.Load;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@EventBusSubscriber
public class ServerPlayerEvents {
   @SubscribeEvent
   public static void onUseItem(RightClickItem event) {
      Player player = event.getEntity();
      if (player.f_19853_.f_46443_) {
         MinecraftInstanceHelper.ifPlayerPresent(localPlayer -> {
            if (ClientMagicData.isCasting() && player.m_20148_().equals(localPlayer.m_20148_())) {
               event.setCanceled(true);
            }
         });
      } else {
         MagicData magicData = MagicData.getPlayerMagicData(player);
         if (magicData.isCasting() && event.getItemStack() != magicData.getPlayerCastingItem()) {
            event.setCanceled(true);
         }
      }

      if (!event.isCanceled()) {
         Level level = player.f_19853_;
         InteractionHand hand = event.getHand();
         ItemStack itemStack = player.m_21120_(hand);
         if (CastingImplementData.has(itemStack) && CastingImplementData.get(itemStack)) {
            SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
            SpellSelectionManager.SelectionOption selectionOption = spellSelectionManager.getSelection();
            if (selectionOption == null || selectionOption.spellData.equals(SpellData.EMPTY)) {
               return;
            }

            SpellData spellData = selectionOption.spellData;
            int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);
            if (level.m_5776_()) {
               if (ClientMagicData.isCasting()) {
                  event.setCancellationResult(InteractionResult.CONSUME);
               } else {
                  if (ClientMagicData.getPlayerMana() < spellData.getSpell().getManaCost(spellLevel)
                     || ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                     || !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell())) {
                     return;
                  }

                  event.setCancellationResult(InteractionResult.CONSUME);
               }
            }

            String castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;
            if (spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, castingSlot)) {
               event.setCancellationResult(InteractionResult.CONSUME);
            } else {
               event.setCancellationResult(InteractionResult.FAIL);
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerDropItem(ItemTossEvent event) {
      ItemStack itemStack = event.getEntity().m_32055_();
      if (itemStack.m_41720_() instanceof Scroll) {
         MagicData magicData = MagicData.getPlayerMagicData(event.getPlayer());
         if (magicData.isCasting() && magicData.getCastSource() == CastSource.SCROLL && magicData.getCastType() == CastType.CONTINUOUS) {
            itemStack.m_41774_(1);
         }
      }
   }

   @SubscribeEvent
   public static void onLevelLoaded(Load event) {
      if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.m_46472_() == Level.f_46428_) {
         IronsDataStorage.init(serverLevel.m_8895_());
      }
   }

   @SubscribeEvent
   public static void onServerStoppedEvent(ServerStoppedEvent event) {
      IronsSpellbooks.MCS = null;
      IronsSpellbooks.OVERWORLD = null;
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      IronsSpellbooks.MCS = event.getServer();
      IronsSpellbooks.OVERWORLD = IronsSpellbooks.MCS.m_129783_();
   }

   @SubscribeEvent
   public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
         if (playerMagicData.isCasting() && (event.getFrom().m_41720_() instanceof CastingItem || event.getTo().m_41720_() instanceof CastingItem)) {
            Utils.serverSideCancelCast(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer, new EquipmentChangedPacket());
            return;
         }

         boolean isFromSpellContainer = ISpellContainer.isSpellContainer(event.getFrom());
         if (isFromSpellContainer
            && ISpellContainer.get(event.getFrom()).getIndexForSpell(playerMagicData.getCastingSpell().getSpell()) >= 0
            && !Utils.isSameItemSameComponentsIgnoreDurability(event.getFrom(), event.getTo())) {
            if (playerMagicData.isCasting()) {
               Utils.serverSideCancelCast(serverPlayer);
            }

            PacketDistributor.sendToPlayer(serverPlayer, new EquipmentChangedPacket());
         } else if (isFromSpellContainer || ISpellContainer.isSpellContainer(event.getTo())) {
            PacketDistributor.sendToPlayer(serverPlayer, new EquipmentChangedPacket());
         }
      }
   }

   @SubscribeEvent
   public static void onCurioChangeEvent(CurioChangeEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer
         && (ISpellContainer.isSpellContainer(event.getFrom()) || ISpellContainer.isSpellContainer(event.getTo()))) {
         PacketDistributor.sendToPlayer(serverPlayer, new EquipmentChangedPacket());
      }
   }

   @SubscribeEvent
   public static void onPlayerLogOut(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         Utils.serverSideCancelCast(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onPlayerOpenContainer(Open event) {
      if (!event.getEntity().f_19853_.f_46443_) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting()) {
               Utils.serverSideCancelCast(serverPlayer);
            }
         }
      }
   }

   @SubscribeEvent
   public static void handleUpgradeModifiers(ItemAttributeModifierEvent event) {
      UpgradeData upgradeData = UpgradeData.getUpgradeData(event.getItemStack());
      if (upgradeData != UpgradeData.NONE && upgradeData.getUpgradedSlot().equals(event.getSlotType().m_20751_())) {
         UpgradeUtils.handleAttributeEvent(event.getModifiers(), upgradeData, event::addModifier, event::removeModifier, Optional.empty());
      }
   }

   @SubscribeEvent
   public static void handleCurioUpgradeModifiers(CurioAttributeModifierEvent event) {
      UpgradeData upgradeData = UpgradeData.getUpgradeData(event.getItemStack());
      if (upgradeData != UpgradeData.NONE && upgradeData.getUpgradedSlot().equals(event.getSlotContext().identifier())) {
         UpgradeUtils.handleAttributeEvent(event.getModifiers(), upgradeData, event::addModifier, event::removeModifier, Optional.of(event.getUuid()));
      }
   }

   @SubscribeEvent
   public static void onExperienceDroppedEvent(LivingExperienceDropEvent event) {
      Player player = event.getAttackingPlayer();
      if (player != null) {
         int ringCount = CuriosApi.getCuriosInventory(player)
            .map(inventory -> inventory.findCurios((Item)ItemRegistry.EMERALD_STONEPLATE_RING.get()).size())
            .orElse(0);

         for (int i = 0; i < ringCount; i++) {
            event.setDroppedExperience((int)(event.getDroppedExperience() * 1.25));
         }
      }
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof ServerPlayer targetPlayer) {
         MagicData.getPlayerMagicData(serverPlayer).getSyncedData().syncToPlayer(targetPlayer);
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
         playerMagicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
         playerMagicData.getPlayerRecasts().syncAllToPlayer();
         playerMagicData.getSyncedData().syncToPlayer(serverPlayer);
         PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(playerMagicData));
         CameraShakeManager.doSync(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onPlayerStartTrackingEntity(StartTracking event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayerRecipient && event.getTarget() instanceof LivingEntity livingEntity) {
         for (MobEffectInstance inst : livingEntity.m_21220_()) {
            if (inst.m_19544_() instanceof ISyncedMobEffect) {
               serverPlayerRecipient.f_8906_.m_9829_(new ClientboundUpdateMobEffectPacket(livingEntity.m_19879_(), inst));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEvent(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.f_19853_.f_46443_) {
         if (entity instanceof ServerPlayer serverPlayer) {
            Utils.serverSideCancelCast(serverPlayer);
            MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().removeAll(RecastResult.DEATH);
         }

         entity.m_21220_().forEach(mobEffectInstance -> {
            if (mobEffectInstance.m_19544_() instanceof IMobEffectEndCallback callback) {
               callback.onEffectRemoved(entity, mobEffectInstance.m_19564_());
            }
         });
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onSpellTeleport(SpellTeleportEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity && ((CurioBaseItem)ItemRegistry.TELEPORTATION_AMULET.get()).isEquippedBy(livingEntity)) {
         livingEntity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.EVASION.get(), 60, 0, false, false, true));
      }
   }

   @SubscribeEvent
   public static void onPlayerCloned(Clone event) {
      if (event.getEntity() instanceof ServerPlayer newServerPlayer) {
         event.getOriginal().m_21220_().forEach(effect -> {
            if (effect.m_19544_() instanceof SummonTimer) {
               newServerPlayer.m_147207_(effect, newServerPlayer);
            }
         });
         MagicData oldMagicData = MagicData.getPlayerMagicData(event.getOriginal());
         MagicData newMagicData = MagicData.getPlayerMagicData(newServerPlayer);
         newMagicData.setSyncedData(oldMagicData.getSyncedData().getPersistentData(newServerPlayer));
         oldMagicData.getPlayerCooldowns()
            .getSpellCooldowns()
            .forEach((spellId, cooldown) -> newMagicData.getPlayerCooldowns().getSpellCooldowns().put(spellId, cooldown));
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         Utils.serverSideCancelCast(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_20095_();
         serverPlayer.m_146917_(0);
         List<DataValue<?>> data = serverPlayer.m_20088_().m_135378_();
         if (data != null) {
            serverPlayer.f_8906_.m_9829_(new ClientboundSetEntityDataPacket(serverPlayer.m_19879_(), data));
         }

         Utils.serverSideCancelCast(serverPlayer);
         MagicData.getPlayerMagicData(serverPlayer)
            .setMana((int)(serverPlayer.m_21133_((Attribute)AttributeRegistry.MAX_MANA.get()) * (Double)ServerConfigs.MANA_SPAWN_PERCENT.get()));
      }
   }

   @SubscribeEvent
   public static void fixDragonCrits(CriticalHitEvent event) {
      if (!event.getTarget().f_19853_.f_46443_) {
         if (event.getTarget() instanceof EnderDragonPart dragonPartEntity) {
            Entity part = (Entity)dragonPartEntity;
            Player attacker = event.getEntity();
            boolean defaultShouldCrit = attacker.m_36403_(0.5F) > 0.9
               && attacker.f_19789_ > 0.0F
               && !attacker.m_20096_()
               && !attacker.m_6147_()
               && !attacker.m_20069_()
               && !attacker.m_21023_(MobEffects.f_19610_)
               && !attacker.m_20159_()
               && !attacker.m_20142_();
            if (defaultShouldCrit) {
               if (event.getDamageModifier() == 1.0F) {
                  event.setDamageModifier(1.5F);
               }

               AABB boundingBox = part.m_20191_();
               Vec3 vec3 = boundingBox.m_82399_();
               MagicManager.spawnParticles(
                  event.getEntity().f_19853_,
                  ParticleTypes.f_123797_,
                  vec3.f_82479_,
                  vec3.f_82480_,
                  vec3.f_82481_,
                  25,
                  boundingBox.m_82362_() * 0.6,
                  boundingBox.m_82376_() * 0.6,
                  boundingBox.m_82385_() * 0.6,
                  0.0,
                  false
               );
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingIncomingDamage(LivingAttackEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (event.getSource().m_7639_() != null
         && livingEntity.m_20202_() instanceof IceTombEntity iceTomb
         && !DamageSources.isFriendlyFireBetween(event.getSource().m_7639_(), livingEntity)) {
         event.setCanceled(true);
         iceTomb.m_6469_(event.getSource(), event.getAmount());
      } else {
         if (livingEntity instanceof ServerPlayer || livingEntity instanceof IMagicEntity) {
            if (((CurioBaseItem)ItemRegistry.FIREWARD_RING.get()).isEquippedBy(livingEntity) && event.getSource().m_269533_(DamageTypeTags.f_268745_)) {
               event.getEntity().m_20095_();
               event.setCanceled(true);
               return;
            }

            MagicData playerMagicData = MagicData.getPlayerMagicData(livingEntity);
            if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.EVASION.get())) {
               if (EvasionEffect.doEffect(livingEntity, event.getSource())) {
                  event.setCanceled(true);
                  return;
               }
            } else if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.ABYSSAL_SHROUD.get())
               && AbyssalShroudEffect.doEffect(livingEntity, event.getSource())) {
               event.setCanceled(true);
               return;
            }

            if (livingEntity instanceof ServerPlayer serverPlayer
               && playerMagicData.isCasting()
               && playerMagicData.getCastingSpell().getSpell().canBeInterrupted(serverPlayer)
               && playerMagicData.getCastDurationRemaining() > 0
               && !event.getSource().m_269533_(DamageTypeTagGenerator.LONG_CAST_IGNORE)
               && !playerMagicData.popMarkedPoison()) {
               Utils.serverSideCancelCast(serverPlayer);
            }
         }

         if ((Boolean)ServerConfigs.BETTER_CREEPER_THUNDERHIT.get()
            && event.getSource().m_269533_(DamageTypeTags.f_268745_)
            && event.getEntity() instanceof Creeper creeper
            && creeper.m_7090_()) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onBeforeDamageTaken(LivingDamageEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (livingEntity instanceof IMagicEntity || livingEntity instanceof ServerPlayer) {
         MagicData playerMagicData = MagicData.getPlayerMagicData(livingEntity);
         if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.HEARTSTOP.get())) {
            playerMagicData.getSyncedData().addHeartstopDamage(event.getAmount() * 0.5F);
            event.setAmount(0.0F);
         }
      }

      if (event.getSource().m_276093_(ISSDamageTypes.FIRE_MAGIC)
         && event.getSource().m_7639_() instanceof LivingEntity livingAttacker
         && livingAttacker.m_6844_(EquipmentSlot.CHEST).m_150930_((Item)ItemRegistry.INFERNAL_SORCERER_CHESTPLATE.get())
         && !(livingAttacker instanceof Player player && player.m_36335_().m_41519_((Item)ItemRegistry.INFERNAL_SORCERER_CHESTPLATE.get()))) {
         ImmolateEffect.addImmolateStack(livingEntity, livingAttacker);
      }
   }

   @SubscribeEvent
   public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
      LivingEntity newTarget = event.getNewTarget();
      LivingEntity entity = event.getEntity();
      if (newTarget != null) {
         if (newTarget.m_6095_().m_204039_(ModTags.VILLAGE_ALLIES) && entity.m_6095_().m_204039_(ModTags.VILLAGE_ALLIES)) {
            event.setCanceled(true);
            return;
         }

         if (newTarget instanceof IMagicSummon summon && summon instanceof Enemy && !entity.equals(((Mob)newTarget).m_5448_())) {
            event.setCanceled(true);
            return;
         }

         if (newTarget.m_21023_((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get())) {
            event.setCanceled(true);
            return;
         }
      }
   }

   @SubscribeEvent
   public static void preventDismount(EntityMountEvent event) {
      Entity mount = event.getEntityBeingMounted();
      Entity entity = event.getEntity();
      if (!entity.f_19853_.f_46443_
         && event.isDismounting()
         && mount instanceof PreventDismount preventDismount
         && !mount.m_213877_()
         && !entity.m_213877_()
         && !preventDismount.canEntityDismount(entity)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult) {
         Entity victim = entityHitResult.m_82443_();
         if (victim instanceof IMagicEntity || victim instanceof Player) {
            LivingEntity livingEntity = (LivingEntity)victim;
            if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.EVASION.get())) {
               if (EvasionEffect.doEffect(livingEntity, victim.m_269291_().m_269104_(event.getProjectile(), event.getProjectile().m_19749_()))) {
                  event.setImpactResult(ImpactResult.SKIP_ENTITY);
               }
            } else if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.ABYSSAL_SHROUD.get())
               && AbyssalShroudEffect.doEffect(livingEntity, victim.m_269291_().m_269104_(event.getProjectile(), event.getProjectile().m_19749_()))) {
               event.setImpactResult(ImpactResult.SKIP_ENTITY);
            }
         }
      }
   }

   @SubscribeEvent
   public static void useOnEntityEvent(EntityInteractSpecific event) {
      if (event.getTarget() instanceof Creeper creeper) {
         Player player = event.getEntity();
         InteractionHand hand = event.getHand();
         ItemStack useItem = player.m_21120_(hand);
         if (useItem.m_150930_(Items.f_42590_) && creeper.m_7090_()) {
            creeper.m_6469_(creeper.m_269291_().m_269264_(), 5.0F);
            player.f_19853_
               .m_6263_((Player)null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_11771_, SoundSource.NEUTRAL, 1.0F, 1.0F);
            player.m_6674_(hand);
            player.m_21008_(hand, ItemUtils.m_41813_(useItem, player, new ItemStack((ItemLike)ItemRegistry.LIGHTNING_BOTTLE.get())));
            event.setCancellationResult(InteractionResultHolder.m_19096_(player.m_21120_(hand)).m_19089_());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void handleResistanceAttributesOnSpawn(FinalizeSpawn event) {
      Mob mob = event.getEntity();
      if (mob.m_6336_() == MobType.f_21641_) {
         setIfNonNull(mob, AttributeRegistry.HOLY_MAGIC_RESIST, 0.5);
         setIfNonNull(mob, AttributeRegistry.BLOOD_MAGIC_RESIST, 1.5);
      } else if (mob.m_6336_() == MobType.f_21644_) {
         setIfNonNull(mob, AttributeRegistry.LIGHTNING_MAGIC_RESIST, 0.5);
      }

      if (mob.m_5825_()) {
         setIfNonNull(mob, AttributeRegistry.FIRE_MAGIC_RESIST, 1.5);
      }

      if (mob.m_6095_() == EntityType.f_20551_) {
         setIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
      }
   }

   private static void setIfNonNull(LivingEntity mob, Supplier<Attribute> attribute, double value) {
      AttributeInstance instance = mob.m_21204_().m_22146_(attribute.get());
      if (instance != null) {
         instance.m_22100_(value);
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      LivingEntity entity = event.getEntity();
      Level level = entity.f_19853_;
      if (!level.f_46443_ && entity.f_19797_ % 40 == 0) {
         BlockPos pos = entity.m_20183_();
         BlockState blockState = entity.f_19853_.m_8055_(pos);
         if (blockState.m_60713_(Blocks.f_50256_)) {
            BloodCauldronBlock.attemptCookEntity(blockState, entity.f_19853_, pos, entity, () -> {
               level.m_46597_(pos, ((Block)BlockRegistry.BLOOD_CAULDRON_BLOCK.get()).m_49966_());
               level.m_142346_(null, GameEvent.f_157792_, pos);
            });
         }
      }
   }

   @SubscribeEvent
   public static void onAnvilRecipe(AnvilUpdateEvent event) {
      if (event.getRight().m_150930_((Item)ItemRegistry.SHRIVING_STONE.get())) {
         ItemStack result = Utils.handleShriving(event.getLeft());
         if (!result.m_41619_()) {
            event.setOutput(result);
            event.setCost(1);
            event.setMaterialCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if ((Boolean)ServerConfigs.PORTAL_FRAME_RESTRICT_BREAKING.get() && event.getState().m_60713_((Block)BlockRegistry.PORTAL_FRAME.get())) {
         Player player = event.getPlayer();
         if (event.getLevel().m_7702_(event.getPos()) instanceof PortalFrameBlockEntity portalFrameBlockEntity
            && portalFrameBlockEntity.getOwnerUUID() != null
            && !player.m_20148_().equals(portalFrameBlockEntity.getOwnerUUID())) {
            if (player instanceof ServerPlayer serverPlayer) {
               serverPlayer.f_8906_
                  .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.portal_break_failure").m_130940_(ChatFormatting.RED)));
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void preventBlockPlacement(RightClickBlock event) {
      Level level = event.getLevel();
      if (level.m_46472_().equals(PocketDimensionManager.POCKET_DIMENSION)
         && event.getItemStack().m_41720_() instanceof BlockItem blockItem
         && blockItem.m_40614_().m_204297_().m_203656_(ModTags.PREVENT_POCKET_DIMENSION_PLACEMENT)) {
         event.setCanceled(true);
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.m_5661_(Component.m_237115_("ui.irons_spellbooks.error_place_block_dimension").m_130940_(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void preventPocketDimensionTeleportation(EntityTeleportEvent event) {
      if (event.getEntity().f_19853_ instanceof ServerLevel serverLevel
         && serverLevel.m_46472_().equals(PocketDimensionManager.POCKET_DIMENSION)
         && !(event instanceof TeleportCommand)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void changeDigSpeed(BreakSpeed event) {
      Player player = event.getEntity();
      if (player.m_21023_((MobEffect)MobEffectRegistry.HASTENED.get())) {
         int i = 1 + player.m_21124_((MobEffect)MobEffectRegistry.HASTENED.get()).m_19564_();
         event.setNewSpeed(event.getNewSpeed() * Utils.intPow(1.2F, i));
      }

      if (player.m_21023_((MobEffect)MobEffectRegistry.SLOWED.get())) {
         int i = 1 + player.m_21124_((MobEffect)MobEffectRegistry.SLOWED.get()).m_19564_();
         event.setNewSpeed(event.getNewSpeed() * Utils.intPow(0.8F, i));
      }
   }

   @SubscribeEvent
   public static void changeBreedOutcome(BabyEntitySpawnEvent event) {
      if ((Boolean)ServerConfigs.HOGLIN_OFFSPRING_PROTECTION.get()
         && event.getChild() instanceof Hoglin baby
         && event.getParentA() instanceof Hoglin parent1
         && event.getParentB() instanceof Hoglin parent2) {
         double i = (parent1.m_34557_() ? 0.5 : 0.0) + (parent2.m_34557_() ? 0.5 : 0.0);
         if (Utils.random.m_188501_() < i) {
            baby.m_34564_(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onChangeDimensions(EntityTravelToDimensionEvent event) {
      Entity entity = event.getEntity();
      if (entity.f_19853_ instanceof ServerLevel serverLevel) {
         Entity owner = SummonManager.getOwner(entity);
         if (owner != null) {
            event.setCanceled(true);
         } else {
            Set<UUID> summons = SummonManager.getSummons(entity);
            if (!summons.isEmpty()) {
               for (UUID uuid : summons) {
                  Entity summon = serverLevel.m_8791_(uuid);
                  if (summon instanceof IMagicSummon magicSummon) {
                     magicSummon.onUnSummon();
                  } else if (summon != null) {
                     SummonManager.removeSummon(summon);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDataLoaded(OnDatapackSyncEvent event) {
      Map<Item, CauldronInteraction> map = CauldronInteraction.f_175607_;

      for (RegistryObject<Item> item : ItemRegistry.getIronsItems()) {
         if (item.get() instanceof DyeableLeatherItem) {
            map.put((Item)item.get(), CauldronInteraction.f_175615_);
         }
      }
   }
}
