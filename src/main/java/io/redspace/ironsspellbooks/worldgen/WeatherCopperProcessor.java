package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class WeatherCopperProcessor extends StructureProcessor {
   public static final Codec<WeatherCopperProcessor> CODEC = Codec.FLOAT.fieldOf("bias").xmap(WeatherCopperProcessor::new, obj -> obj.bias).codec();
   float bias;

   public WeatherCopperProcessor(float bias) {
      this.bias = bias;
   }

   @Nullable
   public StructureBlockInfo process(
      @Nonnull LevelReader level,
      @Nonnull BlockPos jigsawPiecePos,
      @Nonnull BlockPos jigsawPieceBottomCenterPos,
      @Nonnull StructureBlockInfo blockInfoLocal,
      @Nonnull StructureBlockInfo blockInfoGlobal,
      @Nonnull StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      if (blockInfoGlobal.f_74676_().m_60734_() instanceof WeatheringCopper copperBlock) {
         float f = Mth.m_14179_(Utils.random.m_188501_(), this.bias, 1.0F);
         int weatherStage = (int)(f * 4.0F);
         BlockState state = blockInfoGlobal.f_74676_();

         for (int i = 0; i < weatherStage; i++) {
            Optional<BlockState> nextState = copperBlock.m_142123_(state);
            if (nextState.isPresent()) {
               state = nextState.get().m_60734_().m_152465_(blockInfoGlobal.f_74676_());
            }
         }

         return new StructureBlockInfo(blockInfoGlobal.f_74675_(), state, blockInfoGlobal.f_74677_());
      } else {
         return blockInfoGlobal;
      }
   }

   @Nonnull
   protected StructureProcessorType<?> m_6953_() {
      return StructureProcessorRegistry.WEATHER_COPPER.get();
   }
}
