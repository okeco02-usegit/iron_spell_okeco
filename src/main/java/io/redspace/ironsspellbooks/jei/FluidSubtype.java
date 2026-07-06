package io.redspace.ironsspellbooks.jei;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidSubtype implements IIngredientTypeWithSubtypes<Fluid, FluidStack> {
   public Class<? extends FluidStack> getIngredientClass() {
      return FluidStack.class;
   }

   public Class<? extends Fluid> getIngredientBaseClass() {
      return Fluid.class;
   }

   public Fluid getBase(FluidStack ingredient) {
      return ingredient.getFluid();
   }
}
