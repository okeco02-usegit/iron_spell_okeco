package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;

public class HandleLitBlocksProcessor extends StructureProcessor {
   public static final MapCodec<HandleLitBlocksProcessor> MAPCODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            Codec.DOUBLE.fieldOf("chanceLit").forGetter(obj -> obj.chanceLit),
            Codec.unboundedMap(ResourceLocation.f_135803_, Codec.DOUBLE).optionalFieldOf("byBlock", Map.of()).forGetter(obj -> obj.byBlock)
         )
         .apply(builder, HandleLitBlocksProcessor::new)
   );
   public static final Codec<HandleLitBlocksProcessor> CODEC = MAPCODEC.codec();
   public final double chanceLit;
   public final Map<ResourceLocation, Double> byBlock;

   private HandleLitBlocksProcessor(double chanceLit, Map<ResourceLocation, Double> byBlock) {
      this.chanceLit = chanceLit;
      this.byBlock = byBlock;
   }

   public StructureBlockInfo m_7382_(
      @NotNull LevelReader levelReader,
      @NotNull BlockPos jigsawPiecePos,
      @NotNull BlockPos jigsawPieceBottomCenterPos,
      @NotNull StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.f_74676_().m_61138_(BlockStateProperties.f_61443_)) {
         double chanceToBeLit = this.byBlock.getOrDefault(BuiltInRegistries.f_256975_.m_7981_(blockInfoGlobal.f_74676_().m_60734_()), this.chanceLit);
         RandomSource random = structurePlacementData.m_230326_(blockInfoGlobal.f_74675_());
         blockInfoGlobal = new StructureBlockInfo(
            blockInfoGlobal.f_74675_(),
            (BlockState)blockInfoGlobal.f_74676_().m_61124_(BlockStateProperties.f_61443_, random.m_188501_() < chanceToBeLit),
            blockInfoGlobal.f_74677_()
         );
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> m_6953_() {
      return StructureProcessorRegistry.HANDLE_LIT_BLOCKS_PROCESSOR.get();
   }
}
