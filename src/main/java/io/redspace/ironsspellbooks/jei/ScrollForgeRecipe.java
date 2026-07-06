package io.redspace.ironsspellbooks.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ScrollForgeRecipe(List<ItemStack> inkInputs, Ingredient paperInput, Ingredient focusInput, List<ItemStack> scrollOutputs) {
   public ScrollForgeRecipe(List<ItemStack> inkInputs, Ingredient paperInput, Ingredient focusInput, List<ItemStack> scrollOutputs) {
      this.inkInputs = List.copyOf(inkInputs);
      this.paperInput = paperInput;
      this.focusInput = focusInput;
      this.scrollOutputs = List.copyOf(scrollOutputs);
   }

   public boolean isValid() {
      return !this.inkInputs.isEmpty() && !this.scrollOutputs.isEmpty() && !this.paperInput.m_43947_() && !this.focusInput.m_43947_();
   }
}
