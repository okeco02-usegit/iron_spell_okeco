package io.redspace.ironsspellbooks.fluids;

import net.minecraft.resources.ResourceLocation;

public class SimpleTintedClientFluidType extends SimpleClientFluidType {
   final int color;

   public SimpleTintedClientFluidType(ResourceLocation texture, int color) {
      super(texture);
      this.color = color | 0xFF000000;
   }

   public int getTintColor() {
      return this.color;
   }
}
