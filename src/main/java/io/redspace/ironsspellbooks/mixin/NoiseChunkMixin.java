package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.worldgen.IExtendedNoiseChunk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.Aquifer.FluidPicker;
import net.minecraft.world.level.levelgen.DensityFunctions.BeardifierOrMarker;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin implements IExtendedNoiseChunk {
   @Unique
   IExtendedNoiseChunk.AquifierNuke irons_spellbooks$aquifierNuke = null;
   @Unique
   BlockState irons_spellbooks$defaultBlockState;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void irons_spellbooks$captureDefaultBlockstate(
      int cellCountXZ,
      RandomState random,
      int firstNoiseX,
      int firstNoiseZ,
      NoiseSettings noiseSettings,
      BeardifierOrMarker beardifier,
      NoiseGeneratorSettings noiseGeneratorSettings,
      FluidPicker fluidPicker,
      Blender blendifier,
      CallbackInfo ci
   ) {
      this.irons_spellbooks$defaultBlockState = noiseGeneratorSettings.f_64440_();
   }

   @Inject(method = "getInterpolatedState", at = @At("RETURN"), cancellable = true)
   private void irons_spellbooks$cancelAquifierGeneration(CallbackInfoReturnable<BlockState> cir) {
      BlockState state = (BlockState)cir.getReturnValue();
      if (state != null) {
         IExtendedNoiseChunk.AquifierNuke nuke = this.irons_spellbooks$getAquifierStatus();
         if (nuke != null) {
            if (state.m_60713_(Blocks.f_49990_) || state.m_60713_(Blocks.f_49991_)) {
               NoiseChunk chunk = (NoiseChunk)this;
               int x = chunk.m_207115_();
               int y = chunk.m_207114_();
               int z = chunk.m_207113_();

               for (BoundingBox box : nuke.boundingBoxes()) {
                  int dx = 0;
                  if (x < box.m_162395_()) {
                     dx = box.m_162395_() - x;
                  } else if (x > box.m_162399_()) {
                     dx = x - box.m_162399_();
                  }

                  int dy = 0;
                  if (y < box.m_162396_()) {
                     dy = box.m_162396_() - y;
                  } else if (y > box.m_162400_()) {
                     dy = y - box.m_162400_();
                  }

                  int dz = 0;
                  if (z < box.m_162398_()) {
                     dz = box.m_162398_() - z;
                  } else if (z > box.m_162401_()) {
                     dz = z - box.m_162401_();
                  }

                  int manhattanDistance = dx + dy + dz;
                  if (manhattanDistance <= 5) {
                     if (manhattanDistance <= 3) {
                        cir.setReturnValue(Blocks.f_50627_.m_49966_());
                     } else {
                        cir.setReturnValue(this.irons_spellbooks$defaultBlockState);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   public IExtendedNoiseChunk.AquifierNuke irons_spellbooks$getAquifierStatus() {
      return this.irons_spellbooks$aquifierNuke;
   }

   @Override
   public void irons_spellbooks$setAquifierStatus(IExtendedNoiseChunk.AquifierNuke nuke) {
      this.irons_spellbooks$aquifierNuke = nuke;
   }
}
