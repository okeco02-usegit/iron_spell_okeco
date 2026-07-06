package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.fluids.PotionFluid;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fluids.FluidStack;

public class PotionFluidInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {
   public String apply(FluidStack stack, UidContext uidContext) {
      if (stack.hasTag()) {
         String potionname = BuiltInRegistries.f_256980_.m_7981_(FluidHelper.getPotionContents(stack)).toString();
         String bottlename = PotionFluid.BottleType.get(stack).m_7912_();
         return String.format("fluid:%s:%s", potionname, bottlename);
      } else {
         return "";
      }
   }
}
