package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ScrollForgeRecipeCategory implements IRecipeCategory<ScrollForgeRecipe> {
   public static final RecipeType<ScrollForgeRecipe> SCROLL_FORGE_RECIPE_RECIPE_TYPE = RecipeType.create(
      "irons_spellbooks", "scroll_forge", ScrollForgeRecipe.class
   );
   private final IDrawable background;
   private final IDrawable icon;
   private final String inkSlotName = "inkSlot";
   private final String paperSlotName = "paperSlot";
   private final String focusSlotName = "focusSlot";
   private final String outputSlotName = "outputSlot";

   public ScrollForgeRecipeCategory(IGuiHelper guiHelper) {
      ResourceLocation location = JeiPlugin.SCROLL_FORGE_GUI;
      this.background = guiHelper.drawableBuilder(location, 11, 16, 64, 49).addPadding(0, 0, 0, 0).build();
      this.icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)BlockRegistry.SCROLL_FORGE_BLOCK.get()));
   }

   public RecipeType<ScrollForgeRecipe> getRecipeType() {
      return SCROLL_FORGE_RECIPE_RECIPE_TYPE;
   }

   public Component getTitle() {
      return ((Block)BlockRegistry.SCROLL_FORGE_BLOCK.get()).m_49954_();
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, ScrollForgeRecipe recipe, IFocusGroup focuses) {
      List<ItemStack> inkInputs = recipe.inkInputs();
      Ingredient paperInput = recipe.paperInput();
      Ingredient focusInput = recipe.focusInput();
      List<ItemStack> outputs = recipe.scrollOutputs();
      IRecipeSlotBuilder inkInputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStacks(inkInputs)).setSlotName("inkSlot");
      IRecipeSlotBuilder paperInputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 24, 1)
            .addItemStacks(Arrays.asList(paperInput.m_43908_())))
         .setSlotName("paperSlot");
      IRecipeSlotBuilder focusInputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 47, 1)
            .addItemStacks(Arrays.asList(focusInput.m_43908_())))
         .setSlotName("focusSlot");
      IRecipeSlotBuilder outputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 24, 31).addItemStacks(outputs))
         .setSlotName("outputSlot");
      if (inkInputs.size() == outputs.size()) {
         builder.createFocusLink(new IIngredientAcceptor[]{inkInputSlot, outputSlot});
      }
   }
}
