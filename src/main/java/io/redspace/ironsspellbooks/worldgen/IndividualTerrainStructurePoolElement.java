package io.redspace.ironsspellbooks.worldgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.StructureElementRegistry;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class IndividualTerrainStructurePoolElement extends SinglePoolElement {
   public static final Codec<IndividualTerrainStructurePoolElement> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            m_210465_(),
            m_210462_(),
            m_210538_(),
            TerrainAdjustment.f_226918_.optionalFieldOf("terrain_adjustment").forGetter(element -> Optional.ofNullable(element.terrainAdjustment))
         )
         .apply(
            instance,
            (either, processorListHolder, projection, terrainAdjustment) -> new IndividualTerrainStructurePoolElement(
               either, processorListHolder, projection, (TerrainAdjustment)terrainAdjustment.orElse(null)
            )
         )
   );
   @Nullable
   private final TerrainAdjustment terrainAdjustment;

   public IndividualTerrainStructurePoolElement(
      Either<ResourceLocation, StructureTemplate> resourceLocation,
      Holder<StructureProcessorList> processors,
      Projection projection,
      @Nullable TerrainAdjustment terrainAdjustment
   ) {
      super(resourceLocation, processors, projection);
      this.terrainAdjustment = terrainAdjustment;
   }

   public TerrainAdjustment getTerrainAdjustment() {
      return this.terrainAdjustment != null ? this.terrainAdjustment : TerrainAdjustment.NONE;
   }

   public StructurePoolElementType<?> m_207234_() {
      return StructureElementRegistry.INDIVIDUAL_TERRAIN_ELEMENT.get();
   }
}
