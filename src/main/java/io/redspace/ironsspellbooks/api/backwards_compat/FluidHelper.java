package io.redspace.ironsspellbooks.api.backwards_compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidHelper {
   public static boolean isSameFluidSameComponents(FluidStack a, FluidStack b) {
      return a.isFluidEqual(b) && FluidStack.areFluidStackTagsEqual(a, b);
   }

   public static FluidStack copyWithAmount(FluidStack fluidStack, int amount) {
      FluidStack stack = fluidStack.copy();
      stack.setAmount(amount);
      return stack;
   }

   public static boolean hasPotionContents(ItemStack stack) {
      return stack.m_41782_() && stack.m_41784_().m_128441_("Potion");
   }

   public static boolean isWater(ItemStack stack) {
      return hasPotionContents(stack) && PotionUtils.m_43579_(stack) == Potions.f_43599_;
   }

   public static boolean isBrewingIngredient(ItemStack stack, Level level) {
      return BrewingRecipeRegistry.isValidIngredient(stack);
   }

   public static ItemStack getNonDestructiveBrewingResult(ItemStack base, ItemStack reagent, Level level) {
      return BrewingRecipeRegistry.getOutput(base, reagent);
   }

   public static boolean hasPotionContents(FluidStack stack) {
      return stack.hasTag() && stack.getOrCreateTag().m_128441_("Potion");
   }

   public static Potion getPotionContents(FluidStack stack) {
      return hasPotionContents(stack) ? PotionUtils.m_43577_(stack.getOrCreateTag()) : Potions.f_43598_;
   }

   public static void setPotionContents(FluidStack stack, Potion potion) {
      stack.getOrCreateTag().m_128359_("Potion", BuiltInRegistries.f_256980_.m_7981_(potion).toString());
   }

   public static ItemStack createItemStack(Item item, Holder<Potion> potion) {
      ItemStack stack = new ItemStack(item);
      PotionUtils.m_43549_(stack, (Potion)potion.get());
      return stack;
   }
}
