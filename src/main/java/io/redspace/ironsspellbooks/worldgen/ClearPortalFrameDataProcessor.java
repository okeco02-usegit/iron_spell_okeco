package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlock;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;

public class ClearPortalFrameDataProcessor extends StructureProcessor {
   public static final Codec<ClearPortalFrameDataProcessor> CODEC = MapCodec.unit(ClearPortalFrameDataProcessor::new).codec();

   public StructureBlockInfo m_7382_(
      @NotNull LevelReader levelReader,
      @NotNull BlockPos jigsawPiecePos,
      @NotNull BlockPos jigsawPieceBottomCenterPos,
      @NotNull StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.f_74676_().m_60734_() instanceof PortalFrameBlock) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.f_74675_(), blockInfoGlobal.f_74676_(), null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> m_6953_() {
      return StructureProcessorRegistry.CLEAR_PORTAL_FRAME_DATA.get();
   }
}
