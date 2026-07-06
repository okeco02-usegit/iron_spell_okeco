package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class ScrollForgeRecipeMaker {
   private ScrollForgeRecipeMaker() {
   }

   public static List<ScrollForgeRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      List<InkItem> inkItems = itemFinder.inkItems;
      Stream<ScrollForgeRecipe> recipes = SchoolRegistry.REGISTRY.get().getValues().stream().map(school -> {
         Ingredient paperInput = Ingredient.m_43929_(new ItemLike[]{Items.f_42516_});
         Ingredient focusInput = Ingredient.m_204132_(school.getFocus());
         List<AbstractSpell> spells = SpellRegistry.getSpellsForSchool(school);
         ArrayList<ItemStack> scrollOutputs = new ArrayList<>();
         ArrayList<ItemStack> inkOutputs = new ArrayList<>();
         inkItems.forEach(ink -> {
            for (AbstractSpell spell : spells) {
               if (spell.isEnabled() && spell.allowCrafting()) {
                  int spellLevel = spell.getMinLevelForRarity(ink.getRarity());
                  if (spellLevel > 0 && spell != SpellRegistry.none()) {
                     inkOutputs.add(new ItemStack(ink));
                     scrollOutputs.add(getScrollStack(spell, spellLevel));
                  }
               }
            }
         });
         return new ScrollForgeRecipe(inkOutputs, paperInput, focusInput, scrollOutputs);
      });
      return recipes.toList();
   }

   private static ItemStack getScrollStack(AbstractSpell spell, int spellLevel) {
      ItemStack scrollStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
      ISpellContainer.createScrollContainer(spell, spellLevel, scrollStack);
      return scrollStack;
   }

   private record FocusToSchool(Item item, SchoolType schoolType) {
   }
}
