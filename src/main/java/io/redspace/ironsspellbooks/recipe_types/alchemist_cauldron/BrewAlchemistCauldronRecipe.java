package io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record BrewAlchemistCauldronRecipe(ResourceLocation id, FluidStack fluidIn, Ingredient reagent, List<FluidStack> results, Optional<ItemStack> byproduct)
   implements Recipe<BrewAlchemistCauldronRecipe.Input> {
   public FluidStack fluidIn() {
      return this.fluidIn.copy();
   }

   public List<FluidStack> results() {
      return List.copyOf(this.results);
   }

   public Optional<ItemStack> byproduct() {
      return this.byproduct.map(ItemStack::m_41777_);
   }

   public boolean matches(BrewAlchemistCauldronRecipe.Input input, Level level) {
      return FluidHelper.isSameFluidSameComponents(this.fluidIn, input.fluidIn()) && this.reagent.test(input.reagent());
   }

   public ItemStack assemble(BrewAlchemistCauldronRecipe.Input input, RegistryAccess registries) {
      return ItemStack.f_41583_.m_41777_();
   }

   public boolean m_8004_(int width, int height) {
      return false;
   }

   public ItemStack m_8043_(RegistryAccess registries) {
      return ItemStack.f_41583_.m_41777_();
   }

   public ResourceLocation m_6423_() {
      return this.id;
   }

   public RecipeSerializer<?> m_7707_() {
      return (RecipeSerializer<?>)RecipeRegistry.ALCHEMIST_CAULDRON_BREW_SERIALIZER.get();
   }

   public RecipeType<?> m_6671_() {
      return (RecipeType<?>)RecipeRegistry.ALCHEMIST_CAULDRON_BREW_TYPE.get();
   }

   public record Input(FluidStack fluidIn, ItemStack reagent) implements Container {
      public int m_6643_() {
         return 1;
      }

      public boolean m_7983_() {
         return false;
      }

      public ItemStack m_8020_(int index) {
         return this.reagent;
      }

      public ItemStack m_7407_(int pSlot, int pAmount) {
         return null;
      }

      public ItemStack m_8016_(int pSlot) {
         return null;
      }

      public void m_6836_(int pSlot, ItemStack pStack) {
      }

      public void m_6596_() {
      }

      public boolean m_6542_(Player pPlayer) {
         return false;
      }

      public void m_6211_() {
      }
   }

   public static class Serializer implements RecipeSerializer<BrewAlchemistCauldronRecipe> {
      public BrewAlchemistCauldronRecipe fromJson(ResourceLocation pRecipeId, JsonObject recipejson) {
         FluidStack baseFluid = (FluidStack)((Pair)FluidStack.CODEC
               .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "base_fluid"))
               .getOrThrow(false, IronsSpellbooks.LOGGER::error))
            .getFirst();
         Ingredient input = Ingredient.m_43917_(GsonHelper.m_289747_(recipejson, "input"));
         List<FluidStack> results = (List<FluidStack>)((Pair)Codec.list(FluidStack.CODEC)
               .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "results"))
               .getOrThrow(false, IronsSpellbooks.LOGGER::error))
            .getFirst();
         Optional<ItemStack> byproduct = Optional.empty();
         if (recipejson.has("byproduct")) {
            byproduct = Optional.of(ShapedRecipe.m_151274_(GsonHelper.m_13930_(recipejson, "byproduct")));
         }

         return new BrewAlchemistCauldronRecipe(pRecipeId, baseFluid, input, results, byproduct);
      }

      @Nullable
      public BrewAlchemistCauldronRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
         FluidStack baseFluid = FluidStack.readFromPacket(buf);
         Ingredient input = Ingredient.m_43940_(buf);
         int i = buf.readInt();
         List<FluidStack> results = new ArrayList<>();

         for (int j = 0; j < i; j++) {
            results.add(FluidStack.readFromPacket(buf));
         }

         Optional<ItemStack> byproduct = Optional.empty();
         if (buf.readBoolean()) {
            byproduct = Optional.of(buf.m_130267_());
         }

         return new BrewAlchemistCauldronRecipe(pRecipeId, baseFluid, input, results, byproduct);
      }

      public void toNetwork(FriendlyByteBuf buf, BrewAlchemistCauldronRecipe recipe) {
         recipe.fluidIn.writeToPacket(buf);
         recipe.reagent.m_43923_(buf);
         buf.writeInt(recipe.results.size());

         for (FluidStack result : recipe.results) {
            result.writeToPacket(buf);
         }

         buf.writeBoolean(recipe.byproduct.isPresent());
         if (recipe.byproduct.isPresent()) {
            buf.m_130055_(recipe.byproduct.get());
         }
      }
   }
}
