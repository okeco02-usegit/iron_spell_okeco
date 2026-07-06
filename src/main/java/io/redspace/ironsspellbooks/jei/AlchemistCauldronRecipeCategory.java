package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class AlchemistCauldronRecipeCategory implements IRecipeCategory<AlchemistCauldronJeiRecipe> {
   public static final RecipeType<AlchemistCauldronJeiRecipe> ALCHEMIST_CAULDRON_RECIPE_TYPE = RecipeType.create(
      "irons_spellbooks", "alchemist_cauldron", AlchemistCauldronJeiRecipe.class
   );
   private final IDrawable background;
   private final IDrawable cauldron_block_icon;
   private final String inputSlotName = "itemIn";
   private final String fluidInputSlotName = "fluidIn";
   private final String outputSlotNameBase = "outputSlot";
   private final String byproductSlotName = "byproductSlot";
   private final int paddingBottom = 20;

   public AlchemistCauldronRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.drawableBuilder(JeiPlugin.ALCHEMIST_CAULDRON_GUI, 0, 0, 125, 19).addPadding(0, 20, 0, 0).build();
      this.cauldron_block_icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)BlockRegistry.ALCHEMIST_CAULDRON.get()));
   }

   public RecipeType<AlchemistCauldronJeiRecipe> getRecipeType() {
      return ALCHEMIST_CAULDRON_RECIPE_TYPE;
   }

   public Component getTitle() {
      return ((Block)BlockRegistry.ALCHEMIST_CAULDRON.get()).m_49954_();
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.cauldron_block_icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, AlchemistCauldronJeiRecipe recipe, IFocusGroup focuses) {
      int fluidRenderHeight = 16;
      IRecipeSlotBuilder itemInput = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
            .addItemStacks(Arrays.stream(recipe.itemIn().m_43908_()).toList()))
         .setSlotName("itemIn");
      IRecipeSlotBuilder fluidInput = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 54, 17 - fluidRenderHeight)
            .addFluidStack(recipe.fluidIn().getFluid(), recipe.fluidIn().getAmount(), recipe.fluidIn().getTag()))
         .setFluidRenderer(recipe.fluidIn().getAmount(), false, 16, fluidRenderHeight)
         .setSlotName("fluidIn");
      if (!recipe.results().isEmpty()) {
         int width = 16 / recipe.results().size();
         int diff = 16 - width * recipe.results().size();
         int xpos = 108;
         int maxCap = recipe.results().stream().mapToInt(FluidStack::getAmount).max().getAsInt();

         for (int i = 0; i < recipe.results().size(); i++) {
            int w = width + (i == 0 ? diff : 0);
            FluidStack stack = recipe.results().get(i);
            IRecipeSlotBuilder outputSlot = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, xpos, 17 - fluidRenderHeight)
                  .addFluidStack(stack.getFluid(), stack.getAmount(), stack.getTag()))
               .setFluidRenderer(maxCap, false, w, fluidRenderHeight)
               .setSlotName("outputSlot" + i);
            xpos += w;
         }
      }

      if (!recipe.resultByproduct().m_41619_()) {
         int ypos = recipe.results().isEmpty() ? 1 : 17;
         IRecipeSlotBuilder var16 = ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 108, ypos)
               .addItemStacks(List.of(recipe.resultByproduct())))
            .setSlotName("byproductSlot");
      }
   }

   public void draw(@NotNull AlchemistCauldronJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiHelper, double mouseX, double mouseY) {
      Optional<ItemStack> leftStack = recipeSlotsView.findSlotByName("itemIn").flatMap(IRecipeSlotView::getDisplayedItemStack);
      guiHelper.m_280168_().m_85836_();
      guiHelper.m_280168_().m_252880_(this.getWidth() / 2 - 11.2F, this.getHeight() / 2 - 2, 0.0F);
      guiHelper.m_280168_().m_85841_(1.4F, 1.4F, 1.4F);
      this.cauldron_block_icon.draw(guiHelper);
      guiHelper.m_280168_().m_85849_();
      if (leftStack.isPresent() && leftStack.get().m_150930_((Item)ItemRegistry.SCROLL.get())) {
         String inputText = String.format("%s%%", (int)((Double)ServerConfigs.SCROLL_RECYCLE_CHANCE.get() * 100.0));
         Font font = Minecraft.m_91087_().f_91062_;
         int y = this.getHeight() / 2;
         int x = (this.getWidth() - font.m_92895_(inputText)) * 3 / 4;
         guiHelper.m_280488_(
            font,
            inputText,
            x,
            y,
            Math.min((Double)ServerConfigs.SCROLL_RECYCLE_CHANCE.get(), 1.0) == 1.0 ? ChatFormatting.GREEN.m_126665_() : ChatFormatting.RED.m_126665_()
         );
      }
   }
}
