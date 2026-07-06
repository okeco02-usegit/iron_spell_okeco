package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ArcaneAnvilRecipeCategory implements IRecipeCategory<ArcaneAnvilJeiRecipe> {
   public static final RecipeType<ArcaneAnvilJeiRecipe> ARCANE_ANVIL_RECIPE_RECIPE_TYPE = RecipeType.create(
      "irons_spellbooks", "arcane_anvil", ArcaneAnvilJeiRecipe.class
   );
   private final IDrawable background;
   private final IDrawable icon;
   private final String leftSlotName = "leftSlot";
   private final String rightSlotName = "rightSlot";
   private final String outputSlotName = "outputSlot";
   private final int paddingBottom = 15;

   public ArcaneAnvilRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.drawableBuilder(JeiPlugin.RECIPE_GUI_VANILLA, 0, 168, 125, 18).addPadding(0, 15, 0, 0).build();
      this.icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)BlockRegistry.ARCANE_ANVIL_BLOCK.get()));
   }

   public RecipeType<ArcaneAnvilJeiRecipe> getRecipeType() {
      return ARCANE_ANVIL_RECIPE_RECIPE_TYPE;
   }

   public Component getTitle() {
      return ((Block)BlockRegistry.ARCANE_ANVIL_BLOCK.get()).m_49954_();
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, ArcaneAnvilJeiRecipe recipe, IFocusGroup focuses) {
      ArcaneAnvilJeiRecipe.Tuple<List<ItemStack>, List<ItemStack>, List<ItemStack>> recipeitems = recipe.getRecipeItems();
      List<ItemStack> leftInputs = recipeitems.a();
      List<ItemStack> rightInputs = recipeitems.b();
      List<ItemStack> outputs = recipeitems.c();
      IRecipeSlotBuilder leftInputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStacks(leftInputs))
         .setSlotName("leftSlot");
      IRecipeSlotBuilder rightInputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 50, 1).addItemStacks(rightInputs))
         .setSlotName("rightSlot");
      IRecipeSlotBuilder outputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 1).addItemStacks(outputs))
         .setSlotName("outputSlot");
      if (leftInputs.size() == rightInputs.size()) {
         if (leftInputs.size() == outputs.size()) {
            builder.createFocusLink(new IIngredientAcceptor[]{leftInputSlot, rightInputSlot, outputSlot});
         }
      } else if (leftInputs.size() == outputs.size() && rightInputs.size() == 1) {
         builder.createFocusLink(new IIngredientAcceptor[]{leftInputSlot, outputSlot});
      } else if (rightInputs.size() == outputs.size() && leftInputs.size() == 1) {
         builder.createFocusLink(new IIngredientAcceptor[]{rightInputSlot, outputSlot});
      }
   }

   public void draw(ArcaneAnvilJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      Optional<ItemStack> leftStack = recipeSlotsView.findSlotByName("leftSlot").flatMap(IRecipeSlotView::getDisplayedItemStack);
      Optional<ItemStack> rightStack = recipeSlotsView.findSlotByName("rightSlot").flatMap(IRecipeSlotView::getDisplayedItemStack);
      Optional<ItemStack> outputStack = recipeSlotsView.findSlotByName("outputSlot").flatMap(IRecipeSlotView::getDisplayedItemStack);
      if (!leftStack.isEmpty() && !rightStack.isEmpty() && !outputStack.isEmpty()) {
         if (leftStack.get().m_41720_() instanceof Scroll leftScroll
            && rightStack.get().m_41720_() instanceof Scroll rightScroll
            && outputStack.get().m_41720_() instanceof Scroll outputScroll) {
            Minecraft minecraft = Minecraft.m_91087_();
            this.drawScrollInfo(minecraft, guiGraphics, ISpellContainer.get(leftStack.get()), ISpellContainer.get(outputStack.get()));
         }
      }
   }

   private void drawScrollInfo(Minecraft minecraft, GuiGraphics guiGraphics, ISpellContainer leftScroll, ISpellContainer outputScroll) {
      SpellData inputSpellData = leftScroll.getSpellAtIndex(0);
      String inputText = String.format("L%d", inputSpellData.getLevel());
      int inputColor = inputSpellData.getSpell().getRarity(inputSpellData.getLevel()).getChatFormatting().m_126665_();
      SpellData outputSpellData = outputScroll.getSpellAtIndex(0);
      String outputText = String.format("L%d", outputSpellData.getLevel());
      int outputColor = outputSpellData.getSpell().getRarity(outputSpellData.getLevel()).getChatFormatting().m_126665_();
      int y = this.getHeight() / 2 + 7 + 9 / 2 - 4;
      int x = 3;
      guiGraphics.m_280488_(minecraft.f_91062_, inputText, x, y, inputColor);
      x += 50;
      guiGraphics.m_280488_(minecraft.f_91062_, inputText, x, y, inputColor);
      int outputWidth = minecraft.f_91062_.m_92895_(outputText);
      guiGraphics.m_280488_(minecraft.f_91062_, outputText, this.getWidth() - (outputWidth + 3), y, outputColor);
   }
}
