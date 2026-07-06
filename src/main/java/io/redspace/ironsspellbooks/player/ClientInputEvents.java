package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.network.casting.CastPacket;
import io.redspace.ironsspellbooks.network.casting.QuickCastPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.InputEvent.MouseScrollingEvent;
import net.minecraftforge.client.event.InputEvent.MouseButton.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.loading.FMLLoader;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.FORGE, value = Dist.CLIENT)
public final class ClientInputEvents {
   private static final ArrayList<KeyState> KEY_STATES = new ArrayList<>();
   private static final KeyState SPELL_WHEEL_STATE = register(KeyMappings.SPELL_WHEEL_KEYMAP);
   private static final KeyState SPELL_WHEEL_TOGGLE_STATE = register(KeyMappings.SPELL_WHEEL_TOGGLE_KEYMAP);
   private static final KeyState SPELLBAR_MODIFIER_STATE = register(KeyMappings.SPELLBAR_SCROLL_MODIFIER_KEYMAP);
   private static final KeyState SPELLBOOK_CAST_STATE = register(KeyMappings.SPELLBOOK_CAST_ACTIVE_KEYMAP);
   private static final List<KeyState> QUICK_CAST_STATES = registerQuickCast(KeyMappings.QUICK_CAST_MAPPINGS);
   private static int useKeyId = Integer.MIN_VALUE;
   public static boolean isUseKeyDown;
   public static boolean hasReleasedSinceCasting;
   public static boolean isShiftKeyDown;

   @SubscribeEvent
   public static void clientMouseScrolled(MouseScrollingEvent event) {
      Player player = MinecraftInstanceHelper.getPlayer();
      if (player != null) {
         if (Minecraft.m_91087_().f_91080_ == null && SPELLBAR_MODIFIER_STATE.isHeld()) {
            SpellSelectionManager spellSelectionManager = ClientMagicData.getSpellSelectionManager();
            if (spellSelectionManager.getSpellCount() > 0) {
               int direction = Mth.m_14045_((int)event.getScrollDelta(), -1, 1);
               List<SpellSelectionManager.SelectionOption> spellbookSpells = spellSelectionManager.getAllSpells();
               int spellCount = spellbookSpells.size();
               int scrollIndex = Mth.m_14045_(spellSelectionManager.getSelectionIndex(), 0, spellCount) - direction;
               int selectedIndex = (Mth.m_14045_(scrollIndex, -1, spellCount + 1) + spellCount) % spellCount;
               spellSelectionManager.makeSelection(selectedIndex);
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onUseInput(InteractionKeyMappingTriggered event) {
      if (event.isUseItem()) {
         if (ClientSpellCastHelper.shouldSuppressRightClicks()) {
            event.setSwingHand(false);
            event.setCanceled(true);
         }
      } else if (event.isAttack() && ClientMagicData.isCasting()) {
         event.setSwingHand(false);
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onKeyInput(Key event) {
      if (!FMLLoader.isProduction() && event.getKey() == 329 && event.getAction() == 1) {
         IronsSpellbooks.LOGGER.debug("breakpoint");
      }

      handleInputEvent(event.getKey(), event.getAction());
   }

   @SubscribeEvent
   public static void onMouseInput(Pre event) {
      handleInputEvent(event.getButton(), event.getAction());
   }

   private static void handleInputEvent(int button, int action) {
      Minecraft minecraft = Minecraft.m_91087_();
      Player player = minecraft.f_91074_;
      if (player != null) {
         handleRightClickSuppression(button, action);
         if (button == 340) {
            isShiftKeyDown = action >= 1;
         }

         for (int i = 0; i < QUICK_CAST_STATES.size(); i++) {
            if (QUICK_CAST_STATES.get(i).wasPressed()) {
               PacketDistributor.sendToServer(new QuickCastPacket(i));
               break;
            }
         }

         if (SPELLBOOK_CAST_STATE.wasPressed() && minecraft.f_91080_ == null) {
            PacketDistributor.sendToServer(new CastPacket());
         }

         if (SPELL_WHEEL_STATE.wasPressed() && minecraft.f_91080_ == null) {
            SpellWheelOverlay.instance.open();
         }

         if (SPELL_WHEEL_STATE.wasReleased() && minecraft.f_91080_ == null && SpellWheelOverlay.instance.active) {
            SpellWheelOverlay.instance.close();
         }

         if (SPELL_WHEEL_TOGGLE_STATE.wasPressed() && minecraft.f_91080_ == null) {
            if (SpellWheelOverlay.instance.active) {
               SpellWheelOverlay.instance.close();
            } else {
               SpellWheelOverlay.instance.open();
            }
         }

         if (SPELLBAR_MODIFIER_STATE.isHeld() && ((ManaBarOverlay.Display)ClientConfigs.SPELL_BAR_DISPLAY.get()).equals(ManaBarOverlay.Display.Contextual)) {
            SpellBarOverlay.fadeoutDelay = 40;
         }

         update();
      }
   }

   private static void handleRightClickSuppression(int button, int action) {
      if (useKeyId == Integer.MIN_VALUE) {
         useKeyId = Minecraft.m_91087_().f_91066_.f_92095_.getKey().m_84873_();
      }

      if (button == useKeyId) {
         if (action == 0) {
            ClientSpellCastHelper.setSuppressRightClicks(false);
            isUseKeyDown = false;
            hasReleasedSinceCasting = true;
         } else if (action == 1) {
            isUseKeyDown = true;
         }
      }
   }

   private static void update() {
      for (KeyState k : KEY_STATES) {
         k.update();
      }
   }

   private static KeyState register(KeyMapping key) {
      KeyState k = new KeyState(key);
      KEY_STATES.add(k);
      return k;
   }

   private static List<KeyState> registerQuickCast(List<KeyMapping> mappings) {
      ArrayList<KeyState> keyStates = new ArrayList<>();
      mappings.forEach(keyMapping -> {
         KeyState k = new KeyState(keyMapping);
         KEY_STATES.add(k);
         keyStates.add(k);
      });
      return keyStates;
   }
}
