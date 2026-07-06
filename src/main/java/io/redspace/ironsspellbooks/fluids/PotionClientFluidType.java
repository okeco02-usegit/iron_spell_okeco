package io.redspace.ironsspellbooks.fluids;

import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.fluids.FluidStack;

public class PotionClientFluidType extends SimpleClientFluidType {
   public PotionClientFluidType(ResourceLocation texture) {
      super(texture);
   }

   public int getTintColor(FluidStack stack) {
      return (
            FluidHelper.hasPotionContents(stack)
               ? PotionUtils.m_43564_(FluidHelper.getPotionContents(stack).m_43488_())
               : PotionUtils.m_43559_(Potions.f_43599_)
         )
         | 0xFF000000;
   }
}
