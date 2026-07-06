package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.UpgradeTypeCache;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.item.CastingImplementData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.FogManager;
import io.redspace.ironsspellbooks.api.util.MusicManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.UpgradeOrbTypeData;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.network.casting.CancelCastPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import io.redspace.ironsspellbooks.spells.ender.RecallSpell;
import io.redspace.ironsspellbooks.spells.fire.BurningDashSpell;
import io.redspace.ironsspellbooks.spells.fire.RaiseHellSpell;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {
   @SubscribeEvent
   public static void onCalculatePlayerSpeed(MovementInputUpdateEvent event) {
      if (ClientMagicData.isCasting()) {
         float baseCastingSpeed = 0.2F;
         float castingSpeedModifier = (float)event.getEntity().m_21133_((Attribute)AttributeRegistry.CASTING_MOVESPEED.get());
         float speed = baseCastingSpeed + castingSpeedModifier - 1.0F;
         event.getInput().f_108567_ *= speed;
         event.getInput().f_108566_ *= speed;
      }
   }

   @SubscribeEvent
   public static void onPlayerLogOut(LoggingOut event) {
      IronsSpellbooks.LOGGER.debug("ClientPlayerNetworkEvent onPlayerLogOut");
      MusicManager.clear();
      GuidingBoltManager.handleClientLogout();
      ClientMagicData.spellSelectionManager = null;
      FogManager.clear();
      if (event.getPlayer() != null) {
         ClientMagicData.resetClientCastState(event.getPlayer().m_20148_());
      }
   }

   @SubscribeEvent
   public static void onPlayerOpenScreen(Opening event) {
      if (ClientMagicData.isCasting()) {
         PacketDistributor.sendToServer(new CancelCastPacket(SpellRegistry.getSpell(ClientMagicData.getCastingSpellId()).getCastType() == CastType.CONTINUOUS));
      }
   }

   @SubscribeEvent
   public static void onClientEntityTick(LivingTickEvent event) {
      LivingEntity livingEntity = event.getEntity();

      for (MobEffectInstance inst : livingEntity.m_21220_()) {
         if (inst.m_19544_() instanceof ISyncedMobEffect effect) {
            effect.clientTick(livingEntity, inst);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side.isClient() && event.phase == Phase.END && event.player == Minecraft.m_91087_().f_91074_) {
         ClientLevel level = Minecraft.m_91087_().f_91073_;
         ClientMagicData.getRecasts().tickRecasts();
         ClientMagicData.getCooldowns().tick(1);
         if (ClientMagicData.getCastDuration() > 0) {
            ClientMagicData.handleCastDuration();
         }

         if (level != null) {
            List<Entity> spellcasters = level.m_6249_(
               (Entity)null, event.player.m_20191_().m_82400_(64.0), mob -> mob instanceof Player || mob instanceof IMagicEntity
            );
            spellcasters.forEach(
               entity -> {
                  LivingEntity livingEntity = (LivingEntity)entity;
                  SyncedSpellData spellData = ClientMagicData.getSyncedSpellData(livingEntity);
                  if (livingEntity.m_21209_() && spellData.getSpinAttackType() == SpinAttackType.FIRE) {
                     BurningDashSpell.ambientParticles(level, livingEntity);
                  }

                  if (spellData.isCasting()) {
                     if (spellData.getCastingSpellId().equals(((AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get()).getSpellId())) {
                        Vec3 impact = Utils.raycastForEntity(entity.f_19853_, entity, RayOfSiphoningSpell.getRange(0), true)
                           .m_82450_()
                           .m_82492_(0.0, 0.25, 0.0);

                        for (int i = 0; i < 8; i++) {
                           Vec3 motion = new Vec3(Utils.getRandomScaled(0.2F), Utils.getRandomScaled(0.2F), Utils.getRandomScaled(0.2F));
                           entity.f_19853_
                              .m_7106_(
                                 ParticleHelper.SIPHON,
                                 impact.f_82479_ + motion.f_82479_,
                                 impact.f_82480_ + motion.f_82480_,
                                 impact.f_82481_ + motion.f_82481_,
                                 motion.f_82479_,
                                 motion.f_82480_,
                                 motion.f_82481_
                              );
                        }
                     } else if (spellData.getCastingSpellId().equals(((AbstractSpell)SpellRegistry.RECALL_SPELL.get()).getSpellId())) {
                        RecallSpell.ambientParticles(livingEntity, spellData);
                     } else if (spellData.getCastingSpellId().equals(((AbstractSpell)SpellRegistry.RAISE_HELL_SPELL.get()).getSpellId())) {
                        RaiseHellSpell.ambientParticles(livingEntity, spellData);
                     }
                  }
               }
            );
         }
      }
   }

   @SubscribeEvent
   public static void onClientLogin(LoggingIn event) {
      UpgradeTypeCache.doCache(event.getPlayer().f_108617_.m_105152_());
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof LocalPlayer player) {
         ClientMagicData.spellSelectionManager = new SpellSelectionManager(player);
      }
   }

   @SubscribeEvent
   public static void beforeLivingRender(Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         LivingEntity livingEntity = event.getEntity();
         if (livingEntity.m_21023_((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get()) && livingEntity.m_20177_(player)) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void afterLivingRender(Post<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
      LivingEntity livingEntity = event.getEntity();
      if (livingEntity instanceof Player) {
         SyncedSpellData syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
         if (syncedData.isCasting()) {
            SpellRenderingHelper.renderSpellHelper(syncedData, livingEntity, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick());
         }
      }
   }

   @SubscribeEvent
   public static void onRespawn(PlayerRespawnEvent event) {
      MinecraftInstanceHelper.ifPlayerPresent(player -> {
         if (player.m_20148_().equals(event.getEntity().m_20148_())) {
            ClientMagicData.updateSpellSelectionManager();
         }
      });
   }

   @SubscribeEvent
   public static void imbuedWeaponTooltips(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (!(stack.m_41720_() instanceof Scroll)) {
         MinecraftInstanceHelper.ifPlayerPresent(
            player1 -> {
               LocalPlayer player = (LocalPlayer)player1;
               List<Component> lines = event.getToolTip();
               boolean advanced = event.getFlags().m_7050_();
               if (UpgradeOrbTypeData.has(stack)) {
                  handleUpgradeOrbTooltip(stack, player, lines, advanced);
               }

               if (CastingImplementData.has(stack) && CastingImplementData.get(stack)) {
                  handleCastingImplementTooltip(stack, player, lines, advanced);
               }

               if (ISpellContainer.isSpellContainer(stack) && !(stack.m_41720_() instanceof SpellBook)) {
                  handleImbuedSpellTooltip(stack, player, lines, advanced);
               }

               if (ISpellContainer.isSpellContainer(stack) && Utils.canImbue(stack)) {
                  ISpellContainer spellContainer = ISpellContainer.get(stack);
                  lines.add(
                     1,
                     Component.m_237110_(
                           "tooltip.irons_spellbooks.can_be_imbued_frame",
                           new Object[]{
                              Component.m_237110_(
                                    "tooltip.irons_spellbooks.can_be_imbued_number",
                                    new Object[]{spellContainer.getActiveSpellCount(), spellContainer.getMaxSpellCount()}
                                 )
                                 .m_130940_(ChatFormatting.YELLOW)
                           }
                        )
                        .m_130940_(ChatFormatting.GOLD)
                  );
               }
            }
         );
      }
   }

   private static void handleImbuedSpellTooltip(ItemStack stack, LocalPlayer player, List<Component> lines, boolean advanced) {
      ISpellContainer spellContainer = ISpellContainer.get(stack);
      int tooltipInjectIndex = advanced ? TooltipsUtils.indexOfAdvancedText(lines, stack) : lines.size();
      if (!spellContainer.isEmpty()) {
         ArrayList<Component> additionalLines = new ArrayList<>();
         int spellCount = spellContainer.getActiveSpellCount();
         MutableComponent header = Component.m_237115_(
               spellCount > 1 ? "tooltip.irons_spellbooks.imbued_tooltip_plural" : "tooltip.irons_spellbooks.imbued_tooltip"
            )
            .m_130940_(ChatFormatting.GRAY);
         if (spellCount > 3) {
            additionalLines.add(Component.m_237119_());
            SpellSelectionManager spellSelectionManager = ClientMagicData.getSpellSelectionManager();

            for (int i = 0; i < spellContainer.getActiveSpellCount(); i++) {
               SpellData spellSlot = spellContainer.getSpellAtIndex(i);
               MutableComponent spellText = TooltipsUtils.getTitleComponent(spellSlot, player).m_6270_(Style.f_131099_);
               SpellSelectionManager.SelectionOption option = spellSelectionManager.getSpellSlot(spellSelectionManager.getSelectionIndex());
               if (option == null
                  || option.slotIndex != i
                  || (!option.slot.equals("mainhand") || player.m_21205_() != stack) && (!option.slot.equals("offhand") || player.m_21206_() != stack)) {
                  additionalLines.add(Component.m_237113_(" ").m_7220_(spellText.m_130948_(Style.f_131099_.m_178520_(8947966))));
               } else {
                  List<MutableComponent> shiftMessage = TooltipsUtils.formatActiveSpellTooltip(
                     stack, spellSelectionManager.getSelectedSpellData(), CastSource.SPELLBOOK, player
                  );
                  shiftMessage.remove(0);
                  TooltipsUtils.addShiftTooltip(
                     additionalLines,
                     Component.m_237113_("> ").m_7220_(spellText).m_130940_(ChatFormatting.YELLOW),
                     shiftMessage.stream().map(component -> Component.m_237113_(" ").m_7220_(component)).collect(Collectors.toList())
                  );
               }
            }
         } else {
            spellContainer.getActiveSpells().forEach(spellSlotx -> {
               List<MutableComponent> spellTooltip = TooltipsUtils.formatActiveSpellTooltip(stack, spellSlotx.spellData(), CastSource.SWORD, player);
               spellTooltip.set(1, Component.m_237113_(" ").m_7220_((Component)spellTooltip.get(1)));
               additionalLines.addAll(spellTooltip);
            });
         }

         additionalLines.add(1, header);
         lines.addAll(tooltipInjectIndex < 0 ? lines.size() : tooltipInjectIndex, additionalLines);
      }
   }

   private static void handleCastingImplementTooltip(ItemStack stack, LocalPlayer player, List<Component> lines, boolean advanced) {
      SpellSelectionManager.SelectionOption spellSlot = ClientMagicData.getSpellSelectionManager().getSelection();
      if (spellSlot != null && spellSlot.spellData != SpellData.EMPTY) {
         List<MutableComponent> additionalLines = TooltipsUtils.formatActiveSpellTooltip(stack, spellSlot.spellData, spellSlot.getCastSource(), player);
         additionalLines.add(1, Component.m_237115_("tooltip.irons_spellbooks.casting_implement_tooltip").m_130940_(ChatFormatting.GRAY));
         additionalLines.set(2, Component.m_237113_(" ").m_7220_((Component)additionalLines.get(2)));
         additionalLines.add(
            Component.m_237113_(" ")
               .m_7220_(
                  Component.m_237110_("tooltip.irons_spellbooks.press_to_cast_active", new Object[]{Component.m_237117_("key.use")})
                     .m_130940_(ChatFormatting.GOLD)
               )
         );
         int i = advanced ? TooltipsUtils.indexOfAdvancedText(lines, stack) : lines.size();
         lines.addAll(i < 0 ? lines.size() : i, additionalLines);
      }
   }

   private static void handleUpgradeOrbTooltip(ItemStack stack, LocalPlayer player, List<Component> lines, boolean advanced) {
      UpgradeOrbTypeData data = UpgradeOrbTypeData.get(stack);
      UpgradeOrbType upgrade = (UpgradeOrbType)((Registry)player.f_19853_.m_9598_().m_6632_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY).get())
         .m_6246_(data.type());
      if (upgrade != null) {
         ArrayList<Component> newlines = new ArrayList<>();
         newlines.add(Component.m_237119_());
         newlines.add(UpgradeOrbItem.TOOLTIP_HEADER);
         MutableComponent text = Component.m_237113_(" ")
            .m_7220_(
               Component.m_237110_(
                     "attribute.modifier.plus." + upgrade.operation().m_22235_(),
                     new Object[]{
                        ItemStack.f_41584_.format(upgrade.amount() * (upgrade.operation() == Operation.ADDITION ? 1 : 100)),
                        Component.m_237115_(((Attribute)upgrade.attribute().m_203334_()).m_22087_())
                     }
                  )
                  .m_130940_(ChatFormatting.BLUE)
            );
         newlines.add(text);
         int i = advanced ? TooltipsUtils.indexOfAdvancedText(lines, stack) : lines.size();
         lines.addAll(i < 0 ? lines.size() : i, newlines);
      }
   }

   private static Attribute getAttributeForDescriptionId(String descriptionId) {
      return BuiltInRegistries.f_256951_.m_123024_().filter(attribute -> attribute.m_22087_().equals(descriptionId)).findFirst().orElse(null);
   }

   @SubscribeEvent
   public static void customPotionTooltips(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      List<MobEffectInstance> mobEffects = PotionUtils.m_43547_(stack);
      if (mobEffects.size() > 0) {
         for (MobEffectInstance mobEffectInstance : mobEffects) {
            if (mobEffectInstance.m_19544_() instanceof CustomDescriptionMobEffect customDescriptionMobEffect) {
               CustomDescriptionMobEffect.handleCustomPotionTooltip(
                  stack, event.getToolTip(), event.getFlags().m_7050_(), mobEffectInstance, customDescriptionMobEffect
               );
            }
         }
      }
   }

   @SubscribeEvent
   public static void changeFogColor(ComputeFogColor event) {
      if (Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get())) {
         int color = ((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get()).m_19484_();
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         f += (color >> 16 & 0xFF) / 255.0F;
         f1 += (color >> 8 & 0xFF) / 255.0F;
         f2 += (color >> 0 & 0xFF) / 255.0F;
         event.setRed(f * 0.15F);
         event.setGreen(f1 * 0.15F);
         event.setBlue(f2 * 0.15F);
      }
   }
}
