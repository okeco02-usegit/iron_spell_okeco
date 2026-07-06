package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.BrewAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.EmptyAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.FillAlchemistCauldronRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
   private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.f_256954_, "irons_spellbooks");
   private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.f_256764_, "irons_spellbooks");
   public static final RegistryObject<RecipeSerializer<?>> ALCHEMIST_CAULDRON_FILL_SERIALIZER = RECIPE_SERIALIZERS.register(
      "alchemist_cauldron_fill", FillAlchemistCauldronRecipe.Serializer::new
   );
   public static final RegistryObject<RecipeType<FillAlchemistCauldronRecipe>> ALCHEMIST_CAULDRON_FILL_TYPE = RECIPE_TYPES.register(
      "alchemist_cauldron_fill", () -> new RecipeType<FillAlchemistCauldronRecipe>() {
         @Override
         public String toString() {
            return "irons_spellbooks:alchemist_cauldron_fill";
         }
      }
   );
   public static final RegistryObject<RecipeSerializer<?>> ALCHEMIST_CAULDRON_EMPTY_SERIALIZER = RECIPE_SERIALIZERS.register(
      "alchemist_cauldron_empty", EmptyAlchemistCauldronRecipe.Serializer::new
   );
   public static final RegistryObject<RecipeType<EmptyAlchemistCauldronRecipe>> ALCHEMIST_CAULDRON_EMPTY_TYPE = RECIPE_TYPES.register(
      "alchemist_cauldron_empty", () -> new RecipeType<EmptyAlchemistCauldronRecipe>() {
         @Override
         public String toString() {
            return "irons_spellbooks:alchemist_cauldron_empty";
         }
      }
   );
   public static final RegistryObject<RecipeSerializer<?>> ALCHEMIST_CAULDRON_BREW_SERIALIZER = RECIPE_SERIALIZERS.register(
      "alchemist_cauldron_brew", BrewAlchemistCauldronRecipe.Serializer::new
   );
   public static final RegistryObject<RecipeType<BrewAlchemistCauldronRecipe>> ALCHEMIST_CAULDRON_BREW_TYPE = RECIPE_TYPES.register(
      "alchemist_cauldron_brew", () -> new RecipeType<BrewAlchemistCauldronRecipe>() {
         @Override
         public String toString() {
            return "irons_spellbooks:alchemist_cauldron_brew";
         }
      }
   );

   public static void register(IEventBus eventBus) {
      RECIPE_TYPES.register(eventBus);
      RECIPE_SERIALIZERS.register(eventBus);
   }
}
