package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;

public class StructureFoundationProcessor extends StructureProcessor {
   public static final Codec<StructureFoundationProcessor> CODEC = BlockState.f_61039_
      .fieldOf("block")
      .xmap(StructureFoundationProcessor::new, proc -> proc.block)
      .codec();
   public final BlockState block;

   private StructureFoundationProcessor(BlockState block) {
      this.block = block;
   }

   public StructureBlockInfo m_7382_(
      @NotNull LevelReader levelReader,
      @NotNull BlockPos jigsawPiecePos,
      @NotNull BlockPos jigsawPieceBottomCenterPos,
      @NotNull StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoLocal.f_74675_().m_123342_() == 0
         && !blockInfoGlobal.f_74676_().m_60713_(Blocks.f_50016_)
         && !(levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.m_143488_().equals(new ChunkPos(blockInfoGlobal.f_74675_())))) {
         MutableBlockPos mutable = blockInfoGlobal.f_74675_().m_122032_().m_122173_(Direction.DOWN);

         for (BlockState currentState = levelReader.m_8055_(mutable);
            mutable.m_123342_() > levelReader.m_141937_()
               && mutable.m_123342_() < levelReader.m_151558_()
               && (currentState.m_60795_() || !levelReader.m_6425_(mutable).m_76178_());
            currentState = levelReader.m_8055_(mutable)
         ) {
            levelReader.m_46865_(mutable).m_6978_(mutable, this.block, false);
            mutable.m_122173_(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> m_6953_() {
      return StructureProcessorRegistry.STRUCTURE_FOUNDATION_PROCESSOR.get();
   }
}
