package io.redspace.ironsspellbooks.worldgen;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;

public class AquiferHelper {
   static final Set<ResourceLocation> structuresToTrack = new ObjectOpenHashSet();
   private static final Set<Structure> structuresInterruptingAquifers = new ObjectOpenHashSet();
   private static boolean cached = false;

   public static void registerTrackedStructure(ResourceLocation resourceLocation) {
      structuresToTrack.add(resourceLocation);
   }

   public static Set<Structure> getOrCacheStructures(StructureManager registryAccess) {
      if (!cached) {
         synchronized (structuresInterruptingAquifers) {
            Registry<Structure> registry = registryAccess.m_220521_().m_175515_(Registries.f_256944_);

            for (ResourceLocation r : structuresToTrack) {
               Structure str = (Structure)registry.m_7745_(r);
               if (str != null) {
                  structuresInterruptingAquifers.add(str);
               }
            }

            cached = true;
         }
      }

      return structuresInterruptingAquifers;
   }

   static {
      structuresToTrack.add(IronsSpellbooks.id("ice_spider_den"));
   }
}
