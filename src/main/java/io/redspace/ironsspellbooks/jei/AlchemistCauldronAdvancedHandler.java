package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.fluids.PotionFluid;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.EmptyAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.FillAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

public class AlchemistCauldronAdvancedHandler implements IRecipeManagerPlugin {
   public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
      return List.of(AlchemistCauldronRecipeCategory.ALCHEMIST_CAULDRON_RECIPE_TYPE);
   }

   public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
      if (recipeCategory instanceof AlchemistCauldronRecipeCategory cauldronRecipeCategory) {
         ITypedIngredient ingredient = focus.getTypedValue();
         if (focus.getRole() == RecipeIngredientRole.INPUT) {
            if (this.isHandledInput(ingredient)) {
               return (List<T>)this.getRecipesForInput(ingredient);
            }
         } else if (focus.getRole() == RecipeIngredientRole.OUTPUT && this.isHandledOutput(ingredient)) {
            return (List<T>)this.getRecipesForOutput(ingredient);
         }

         return List.of();
      } else {
         return List.of();
      }
   }

   public boolean isHandledInput(ITypedIngredient<?> input) {
      ItemStack stack = (ItemStack)input.getIngredient(VanillaTypes.ITEM_STACK).orElse(null);
      if (stack == null || Minecraft.m_91087_().f_91073_ == null) {
         return false;
      }

      if ((Boolean)ServerConfigs.ALLOW_CAULDRON_BREWING.get() && FluidHelper.hasPotionContents(stack)) {
         return true;
      }

      RecipeManager m = Minecraft.m_91087_().f_91073_.m_7465_();
      return m.m_44013_((net.minecraft.world.item.crafting.RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get())
         .stream()
         .anyMatch(empty -> empty.input().test(stack));
   }

   public boolean isHandledOutput(ITypedIngredient<?> output) {
      ItemStack stack = (ItemStack)output.getIngredient(VanillaTypes.ITEM_STACK).orElse(null);
      if (stack == null || Minecraft.m_91087_().f_91073_ == null) {
         return false;
      }

      if ((Boolean)ServerConfigs.ALLOW_CAULDRON_BREWING.get() && FluidHelper.hasPotionContents(stack)) {
         return true;
      }

      RecipeManager m = Minecraft.m_91087_().f_91073_.m_7465_();
      return m.m_44013_((net.minecraft.world.item.crafting.RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get())
         .stream()
         .anyMatch(empty -> ItemStack.m_150942_(stack, empty.result()));
   }

   public List<AlchemistCauldronJeiRecipe> getRecipesForInput(ITypedIngredient<?> input) {
      Optional<ItemStack> stackopt = input.getIngredient(VanillaTypes.ITEM_STACK);
      if (!stackopt.isEmpty() && Minecraft.m_91087_().f_91073_ != null) {
         ItemStack stack = stackopt.get();
         RecipeManager manager = Minecraft.m_91087_().f_91073_.m_7465_();
         Optional<FluidStack> fluidConversion = manager.m_44015_(
               (net.minecraft.world.item.crafting.RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get(),
               new SimpleContainer(new ItemStack[]{stack}),
               Minecraft.m_91087_().f_91073_
            )
            .map(FillAlchemistCauldronRecipe::result);
         if (fluidConversion.isEmpty()) {
            fluidConversion = Optional.of(PotionFluid.from(stack));
         }

         return fluidConversion.<List<AlchemistCauldronJeiRecipe>>map(
               inputFluid -> AlchemistCauldronRecipeMaker.recipes
                  .stream()
                  .filter(recipe -> FluidHelper.isSameFluidSameComponents(recipe.fluidIn(), inputFluid))
                  .toList()
            )
            .orElse(List.of());
      } else {
         return List.of();
      }
   }

   public List<AlchemistCauldronJeiRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
      Optional<ItemStack> stackopt = output.getIngredient(VanillaTypes.ITEM_STACK);
      if (!stackopt.isEmpty() && Minecraft.m_91087_().f_91073_ != null) {
         ItemStack stack = stackopt.get();
         RecipeManager manager = Minecraft.m_91087_().f_91073_.m_7465_();
         Optional<FluidStack> fluidConversion = manager.m_44013_(
               (net.minecraft.world.item.crafting.RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get()
            )
            .stream()
            .filter(emptyAlchemistCauldronRecipe -> ItemStack.m_150942_(emptyAlchemistCauldronRecipe.result(), stack))
            .map(EmptyAlchemistCauldronRecipe::fluid)
            .findFirst();
         if (fluidConversion.isEmpty() && (Boolean)ServerConfigs.ALLOW_CAULDRON_BREWING.get() && !PotionFluid.from(stack).isEmpty()) {
            fluidConversion = Optional.of(PotionFluid.from(stack));
         }

         return fluidConversion.<List<AlchemistCauldronJeiRecipe>>map(
               outputFluid -> AlchemistCauldronRecipeMaker.recipes
                  .stream()
                  .filter(recipe -> recipe.results().stream().anyMatch(result -> FluidHelper.isSameFluidSameComponents(result, outputFluid)))
                  .toList()
            )
            .orElse(List.of());
      } else {
         return List.of();
      }
   }

   public List<AlchemistCauldronJeiRecipe> getAllRecipes() {
      return AlchemistCauldronRecipeMaker.recipes;
   }

   public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
      return (List<T>)(recipeCategory instanceof AlchemistCauldronRecipeCategory ? this.getAllRecipes() : List.of());
   }
}
