package io.redspace.ironsspellbooks.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public record AlchemistCauldronJeiRecipe(Ingredient itemIn, FluidStack fluidIn, List<FluidStack> results, ItemStack resultByproduct) {
}
