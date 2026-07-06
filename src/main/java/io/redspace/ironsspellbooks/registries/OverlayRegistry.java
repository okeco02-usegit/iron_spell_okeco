package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.gui.overlays.CastBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.RecastOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ScreenEffectsOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ScreenTooltipOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.MOD, value = Dist.CLIENT)
public class OverlayRegistry {
   @SubscribeEvent
   public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
      event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "cast_bar", CastBarOverlay.instance);
      event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "mana_overlay", ManaBarOverlay.instance);
      event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "spell_bar", SpellBarOverlay.instance);
      event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "recast_bar", RecastOverlay.instance);
      event.registerAboveAll("spell_wheel", SpellWheelOverlay.instance);
      event.registerAboveAll("screen_effects", ScreenEffectsOverlay.instance);
      event.registerAboveAll("screen_tooltip", ScreenTooltipOverlay.instance);
   }
}
