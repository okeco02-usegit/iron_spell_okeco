package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class DegradeSlabsStairsProcessor extends StructureProcessor {
   public static final Codec<DegradeSlabsStairsProcessor> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Codec.DOUBLE.fieldOf("chance_stairs").forGetter(obj -> (double)((DegradeSlabsStairsProcessor)obj).chanceStairs),
               Codec.DOUBLE.fieldOf("chance_slabs").forGetter(obj -> (double)((DegradeSlabsStairsProcessor)obj).chanceSlabs)
            )
            .apply(builder, (a, b) -> new DegradeSlabsStairsProcessor(a.floatValue(), b.floatValue()))
      )
      .codec();
   private final float chanceStairs;
   private final float chanceSlabs;

   public DegradeSlabsStairsProcessor(float stairs, float slabs) {
      this.chanceStairs = stairs;
      this.chanceSlabs = slabs;
   }

   @Nullable
   public StructureBlockInfo m_7382_(
      LevelReader pLevel,
      BlockPos pOffset,
      BlockPos pPos,
      StructureBlockInfo pBlockInfo,
      StructureBlockInfo pRelativeBlockInfo,
      StructurePlaceSettings pSettings
   ) {
      RandomSource randomsource = pSettings.m_230326_(pRelativeBlockInfo.f_74675_());
      BlockState blockstate = pRelativeBlockInfo.f_74676_();
      BlockPos blockpos = pRelativeBlockInfo.f_74675_();
      BlockState blockstate1 = null;
      if (blockstate.m_204336_(BlockTags.f_13030_)) {
         blockstate1 = this.maybeReplaceStairs(randomsource, pRelativeBlockInfo.f_74676_());
      } else if (blockstate.m_204336_(BlockTags.f_13031_)) {
         blockstate1 = this.maybeReplaceSlab(randomsource);
      }

      return blockstate1 != null ? new StructureBlockInfo(blockpos, blockstate1, pRelativeBlockInfo.f_74677_()) : pRelativeBlockInfo;
   }

   @Nullable
   private BlockState maybeReplaceStairs(RandomSource pRandom, BlockState pState) {
      Half half = (Half)pState.m_61143_(StairBlock.f_56842_);
      if (pRandom.m_188501_() >= this.chanceStairs) {
         return null;
      }

      Optional<Block> block = this.tryGetSlab(pState.m_60734_());
      return block.isPresent() ? (BlockState)block.get().m_49966_().m_61124_(SlabBlock.f_56353_, half == Half.TOP ? SlabType.TOP : SlabType.BOTTOM) : null;
   }

   private Optional<Block> tryGetSlab(Block original) {
      try {
         String stringKey = BuiltInRegistries.f_256975_.m_7981_(original).toString().replace("_stairs", "_slab");
         Block block = (Block)BuiltInRegistries.f_256975_.m_7745_(ResourceLocation.parse(stringKey));
         return block.equals(Blocks.f_50016_) ? Optional.empty() : Optional.of(block);
      } catch (Exception ignored) {
         return Optional.empty();
      }
   }

   @Nullable
   private BlockState maybeReplaceSlab(RandomSource pRandom) {
      return pRandom.m_188501_() >= this.chanceSlabs ? Blocks.f_50016_.m_49966_() : null;
   }

   protected StructureProcessorType<?> m_6953_() {
      return StructureProcessorRegistry.DEGRADE_SLABS_STAIRS.get();
   }
}
