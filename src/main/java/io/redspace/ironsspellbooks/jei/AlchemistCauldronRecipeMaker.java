package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.fluids.PotionFluid;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public final class AlchemistCauldronRecipeMaker {
   public static List<AlchemistCauldronJeiRecipe> recipes = List.of();

   private AlchemistCauldronRecipeMaker() {
   }

   static List<AlchemistCauldronJeiRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      recipes = Stream.of(
            getScrollRecipes(vanillaRecipeFactory, itemFinder),
            getCauldronRecipes(vanillaRecipeFactory, itemFinder),
            getPotionRecipes(vanillaRecipeFactory, itemFinder)
         )
         .flatMap(Function.identity())
         .toList();
      return recipes;
   }

   private static Stream<AlchemistCauldronJeiRecipe> getScrollRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      return Arrays.stream(SpellRarity.values()).map(AlchemistCauldronRecipeMaker::enumerateSpellsForRarity);
   }

   private static Stream<AlchemistCauldronJeiRecipe> getCauldronRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      if (Minecraft.m_91087_().f_91073_ == null) {
         return Stream.of();
      }

      RecipeManager manager = Minecraft.m_91087_().f_91073_.m_7465_();
      return manager.m_44013_((RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_BREW_TYPE.get())
         .stream()
         .map(recipe -> new AlchemistCauldronJeiRecipe(recipe.reagent(), recipe.fluidIn(), recipe.results(), recipe.byproduct().orElse(ItemStack.f_41583_)));
   }

   private static Stream<Item> getBrewingReagents() {
      return Stream.<Mix>concat(PotionBrewing.f_43495_.stream(), PotionBrewing.f_43494_.stream())
         .map(mix -> mix.f_43533_)
         .flatMap(i -> Arrays.stream(i.m_43908_()))
         .<Item>map(ItemStack::m_41720_)
         .distinct();
   }

   private static Stream<AlchemistCauldronJeiRecipe> getPotionRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      if (!(Boolean)ServerConfigs.ALLOW_CAULDRON_BREWING.get()) {
         return Stream.of();
      }

      ClientLevel level = Minecraft.m_91087_().f_91073_;
      if (level == null) {
         return Stream.of();
      }

      Stream<ItemStack> brewablePotions = BuiltInRegistries.f_256980_
         .m_203611_()
         .flatMap(
            potion -> Stream.of(
               FluidHelper.createItemStack(Items.f_42589_, potion),
               FluidHelper.createItemStack(Items.f_42736_, potion),
               FluidHelper.createItemStack(Items.f_42739_, potion)
            )
         );
      return brewablePotions.flatMap(
         potion -> getBrewingReagents()
            .filter(reagent -> PotionBrewing.m_43508_(potion, reagent.m_7968_()))
            .map(
               reagent -> new AlchemistCauldronJeiRecipe(
                  Ingredient.m_43929_(new ItemLike[]{reagent}),
                  PotionFluid.from(potion),
                  List.of(PotionFluid.from(PotionBrewing.m_43529_(reagent.m_7968_(), potion))),
                  ItemStack.f_41583_
               )
            )
      );
   }

   private static AlchemistCauldronJeiRecipe enumerateSpellsForRarity(SpellRarity spellRarity) {
      ItemStack scrollStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
      Stream<ItemStack> scrolls = SpellRegistry.getEnabledSpells()
         .stream()
         .flatMap(
            spell -> IntStream.rangeClosed(spell.getMinLevel(), spell.getMaxLevel())
               .filter(spellLevel -> spell.getRarity(spellLevel) == spellRarity)
               .mapToObj(i -> getScrollStack(scrollStack, spell, i))
         );
      FluidStack ink = new FluidStack(InkItem.getInkForRarity(spellRarity).fluid().get(), 250);
      FluidStack water = new FluidStack(Fluids.f_76193_, 250);
      return new AlchemistCauldronJeiRecipe(Ingredient.m_43921_(scrolls), water, List.of(ink), ItemStack.f_41583_);
   }

   private static ItemStack getScrollStack(ItemStack stack, AbstractSpell spell, int spellLevel) {
      ItemStack scrollStack = stack.m_41777_();
      ISpellContainer.createScrollContainer(spell, spellLevel, scrollStack);
      return scrollStack;
   }

   private static boolean isIngredient(ItemStack itemStack) {
      try {
         return PotionBrewing.m_43506_(itemStack);
      } catch (RuntimeException | LinkageError e) {
         IronsSpellbooks.LOGGER.error("Failed to check if item is a potion reagent {}.", itemStack.toString(), e);
         return false;
      }
   }
}
