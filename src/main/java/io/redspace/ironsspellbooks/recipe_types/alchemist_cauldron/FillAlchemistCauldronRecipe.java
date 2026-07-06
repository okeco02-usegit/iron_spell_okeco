package io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record FillAlchemistCauldronRecipe(
   ResourceLocation getId, Ingredient input, ItemStack returned, FluidStack result, boolean mustFitAll, Holder<SoundEvent> fillSound
) implements Recipe<Container> {
   public FluidStack result() {
      return this.result.copy();
   }

   public ItemStack returned() {
      return this.returned.m_41777_();
   }

   public boolean m_5818_(Container input, Level level) {
      return this.input.test(input.m_8020_(0));
   }

   public ItemStack m_5874_(Container input, RegistryAccess registries) {
      return this.returned.m_41777_();
   }

   public boolean m_5598_() {
      return true;
   }

   public boolean m_8004_(int width, int height) {
      return false;
   }

   public ItemStack m_8043_(RegistryAccess registries) {
      return this.returned.m_41777_();
   }

   public RecipeSerializer<?> m_7707_() {
      return (RecipeSerializer<?>)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_SERIALIZER.get();
   }

   public RecipeType<?> m_6671_() {
      return (RecipeType<?>)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get();
   }

   public ResourceLocation m_6423_() {
      return this.getId;
   }

   public static class Serializer implements RecipeSerializer<FillAlchemistCauldronRecipe> {
      public FillAlchemistCauldronRecipe fromJson(ResourceLocation id, JsonObject recipejson) {
         Ingredient input = Ingredient.m_43917_(GsonHelper.m_289747_(recipejson, "input"));
         ItemStack result = ShapedRecipe.m_151274_(GsonHelper.m_13930_(recipejson, "result"));
         FluidStack fluid = (FluidStack)((Pair)FluidStack.CODEC
               .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "fluid"))
               .getOrThrow(false, IronsSpellbooks.LOGGER::error))
            .getFirst();
         boolean mustFitAll = GsonHelper.m_13855_(recipejson, "mustFitAll", true);
         Holder<SoundEvent> sound = BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_11769_);
         if (recipejson.has("sound")) {
            sound = (Holder<SoundEvent>)((Pair)BuiltInRegistries.f_256894_
                  .m_206110_()
                  .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "sound"))
                  .getOrThrow(false, IronsSpellbooks.LOGGER::error))
               .getFirst();
         }

         return new FillAlchemistCauldronRecipe(id, input, result, fluid, mustFitAll, sound);
      }

      @Nullable
      public FillAlchemistCauldronRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
         Ingredient input = Ingredient.m_43940_(buf);
         ItemStack result = buf.m_130267_();
         FluidStack fluid = FluidStack.readFromPacket(buf);
         boolean mustFitAll = buf.readBoolean();
         Holder<SoundEvent> sound = BuiltInRegistries.f_256894_
            .m_263177_((SoundEvent)Objects.requireNonNull((SoundEvent)BuiltInRegistries.f_256894_.m_7745_(buf.m_130281_())));
         return new FillAlchemistCauldronRecipe(pRecipeId, input, result, fluid, mustFitAll, sound);
      }

      public void toNetwork(FriendlyByteBuf buf, FillAlchemistCauldronRecipe recipe) {
         recipe.input.m_43923_(buf);
         buf.m_130055_(recipe.returned);
         recipe.result.writeToPacket(buf);
         buf.writeBoolean(recipe.mustFitAll);
         buf.m_130085_(((SoundEvent)recipe.fillSound.get()).m_11660_());
      }
   }
}
