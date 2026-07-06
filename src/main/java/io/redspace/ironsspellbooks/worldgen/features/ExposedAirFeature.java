package io.redspace.ironsspellbooks.worldgen.features;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ExposedAirFeature extends Feature<StructureFeatureConfiguration> {
   private static final BlockState AIR = Blocks.f_50627_.m_49966_();

   public ExposedAirFeature(Codec<StructureFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean m_142674_(FeaturePlaceContext<StructureFeatureConfiguration> context) {
      Predicate<BlockState> cannotReplacePredicate = Feature.m_204735_(BlockTags.f_144287_);
      BlockPos origin = context.m_159777_();
      WorldGenLevel level = context.m_159774_();
      int xsize = ((StructureFeatureConfiguration)context.m_159778_()).xsize();
      int ysize = ((StructureFeatureConfiguration)context.m_159778_()).ysize();
      int zsize = ((StructureFeatureConfiguration)context.m_159778_()).zsize();
      int minX = -xsize / 2;
      int maxX = xsize / 2;
      int minY = -1;
      int maxY = ysize - 1;
      int minZ = -zsize / 2;
      int maxZ = zsize / 2;
      int sideOpenings = 0;

      for (int dx = minX; dx <= maxX; dx++) {
         for (int dy = minY; dy <= maxY; dy++) {
            for (int dz = minZ; dz <= maxZ; dz++) {
               BlockPos currentPos = origin.m_7918_(dx, dy, dz);
               boolean isSolid = level.m_8055_(currentPos).m_280296_();
               if (dy == minY && !isSolid) {
                  return false;
               }

               if (dy == maxY && isSolid) {
                  return false;
               }

               if ((dx == minX || dx == maxX || dz == minZ || dz == maxZ) && dy == 0 && level.m_46859_(currentPos) && level.m_46859_(currentPos.m_7494_())) {
                  sideOpenings++;
               }
            }
         }
      }

      int perimeter = xsize * 2 + zsize * 2;
      if (sideOpenings < perimeter * 0.25) {
         return false;
      }

      StructureTemplateManager structureTemplateManager = level.m_7654_().m_236738_();
      StructureTemplate structureTemplate = structureTemplateManager.m_230359_(((StructureFeatureConfiguration)context.m_159778_()).structureTemplateLocation());
      StructurePlaceSettings placementSettings = new StructurePlaceSettings().m_74377_(Mirror.NONE).m_74379_(Rotation.NONE);
      BlockPos configuredOffset = ((StructureFeatureConfiguration)context.m_159778_()).offset();
      BlockPos structurePos = origin.m_7918_(minX, -1, minZ).m_121955_(configuredOffset);
      structureTemplate.m_230328_(level, structurePos, structurePos, placementSettings, level.m_213780_(), 2);
      return true;
   }
}
