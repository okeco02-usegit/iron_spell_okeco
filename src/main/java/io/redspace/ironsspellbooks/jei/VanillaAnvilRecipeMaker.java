package io.redspace.ironsspellbooks.jei;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.world.item.ItemStack;

public class VanillaAnvilRecipeMaker {
   static List<IJeiAnvilRecipe> getAnvilRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      return Stream.concat(getArmorRepairRecipes(vanillaRecipeFactory, itemFinder), getItemRepairRecipes(vanillaRecipeFactory, itemFinder)).toList();
   }

   static Stream<IJeiAnvilRecipe> getItemRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      return itemFinder.ironsTieredItems
         .stream()
         .mapMulti(
            (item, consumer) -> {
               ItemStack damagedThreeQuarters = new ItemStack(item);
               damagedThreeQuarters.m_41721_(damagedThreeQuarters.m_41776_() * 3 / 4);
               ItemStack damagedHalf = new ItemStack(item);
               damagedHalf.m_41721_(damagedHalf.m_41776_() / 2);
               IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(
                  List.of(damagedThreeQuarters), List.of(damagedThreeQuarters), List.of(damagedHalf)
               );
               consumer.accept(repairWithSame);
               List<ItemStack> repairMaterials = Arrays.stream(item.m_43314_().m_6282_().m_43908_()).toList();
               ItemStack damagedFully = new ItemStack(item);
               damagedFully.m_41721_(damagedFully.m_41776_());
               IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(
                  List.of(damagedFully), repairMaterials, List.of(damagedThreeQuarters)
               );
               consumer.accept(repairWithMaterial);
            }
         );
   }

   static Stream<IJeiAnvilRecipe> getArmorRepairRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      return itemFinder.ironsArmorItems
         .stream()
         .mapMulti(
            (item, consumer) -> {
               ItemStack damagedThreeQuarters = new ItemStack(item);
               damagedThreeQuarters.m_41721_(damagedThreeQuarters.m_41776_() * 3 / 4);
               ItemStack damagedHalf = new ItemStack(item);
               damagedHalf.m_41721_(damagedHalf.m_41776_() / 2);
               IJeiAnvilRecipe repairWithSame = vanillaRecipeFactory.createAnvilRecipe(
                  List.of(damagedThreeQuarters), List.of(damagedThreeQuarters), List.of(damagedHalf)
               );
               consumer.accept(repairWithSame);
               List<ItemStack> repairMaterials = Arrays.stream(item.m_40401_().m_6230_().m_43908_()).toList();
               ItemStack damagedFully = new ItemStack(item);
               damagedFully.m_41721_(damagedFully.m_41776_());
               IJeiAnvilRecipe repairWithMaterial = vanillaRecipeFactory.createAnvilRecipe(
                  List.of(damagedFully), repairMaterials, List.of(damagedThreeQuarters)
               );
               consumer.accept(repairWithMaterial);
            }
         );
   }
}
