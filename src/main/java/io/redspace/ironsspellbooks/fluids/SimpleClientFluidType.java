package io.redspace.ironsspellbooks.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class SimpleClientFluidType implements IClientFluidTypeExtensions {
   private final ResourceLocation texture;

   public SimpleClientFluidType(ResourceLocation texture) {
      this.texture = texture;
   }

   @NotNull
   public ResourceLocation getStillTexture() {
      return this.texture;
   }

   @NotNull
   public ResourceLocation getFlowingTexture() {
      return this.texture;
   }
}
