package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.util.NoopWorldBorder;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {
   @Shadow
   WorldBorder f_46447_;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void noopWorldBorder(
      WritableLevelData levelData,
      ResourceKey dimension,
      RegistryAccess registryAccess,
      Holder dimensionTypeRegistration,
      Supplier profiler,
      boolean isClientSide,
      boolean isDebug,
      long biomeZoomSeed,
      int maxChainedNeighborUpdates,
      CallbackInfo ci
   ) {
      if (dimension != null && dimension.equals(PocketDimensionManager.POCKET_DIMENSION)) {
         this.f_46447_ = new NoopWorldBorder();
      }
   }
}
