package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class MagicEvents {
   public static final ResourceLocation PLAYER_MAGIC_RESOURCE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "player_magic");

   public static void onWorldTick(LevelTickEvent event) {
      if (!event.level.f_46443_) {
         if (event.phase != Phase.START) {
            IronsSpellbooks.MAGIC_MANAGER.tick(event.level);
            PocketDimensionManager.INSTANCE.tick(event.level);
         }
      }
   }
}
