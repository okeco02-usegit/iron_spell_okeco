package io.redspace.ironsspellbooks.worldgen;

import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VillageAddition {
   private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.m_135785_(
      Registries.f_257011_, ResourceLocation.fromNamespaceAndPath("minecraft", "empty")
   );

   private static void addBuildingToPool(
      Registry<StructureTemplatePool> templatePoolRegistry,
      Registry<StructureProcessorList> processorListRegistry,
      ResourceLocation poolRL,
      String nbtPieceRL,
      int weight
   ) {
      Holder<StructureProcessorList> emptyProcessorList = processorListRegistry.m_246971_(ProcessorLists.f_127206_);
      StructureTemplatePool pool = (StructureTemplatePool)templatePoolRegistry.m_7745_(poolRL);
      if (pool != null) {
         SinglePoolElement piece = (SinglePoolElement)SinglePoolElement.m_210512_(nbtPieceRL, emptyProcessorList).apply(Projection.RIGID);

         for (int i = 0; i < weight; i++) {
            pool.f_210560_.add(piece);
         }

         List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.f_210559_);
         listOfPieceEntries.add(new Pair(piece, weight));
         pool.f_210559_ = listOfPieceEntries;
      }
   }

   @SubscribeEvent
   public static void addNewVillageBuilding(ServerAboutToStartEvent event) {
      Registry<StructureTemplatePool> templatePoolRegistry = (Registry<StructureTemplatePool>)event.getServer()
         .m_206579_()
         .m_6632_(Registries.f_256948_)
         .orElseThrow();
      Registry<StructureProcessorList> processorListRegistry = (Registry<StructureProcessorList>)event.getServer()
         .m_206579_()
         .m_6632_(Registries.f_257011_)
         .orElseThrow();
      int weight = (Integer)ServerConfigs.PRIEST_TOWER_SPAWNRATE.get();
      if (weight > 0) {
         addBuildingToPool(
            templatePoolRegistry, processorListRegistry, ResourceLocation.parse("minecraft:village/plains/houses"), "irons_spellbooks:priest_house", weight
         );
         addBuildingToPool(
            templatePoolRegistry,
            processorListRegistry,
            ResourceLocation.parse("minecraft:village/taiga/houses"),
            "irons_spellbooks:priest_house_taiga",
            weight
         );
      }
   }
}
