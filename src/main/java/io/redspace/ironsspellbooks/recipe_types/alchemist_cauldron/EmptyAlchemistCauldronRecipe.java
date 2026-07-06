package io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
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

public record EmptyAlchemistCauldronRecipe(ResourceLocation getId, Ingredient input, ItemStack result, FluidStack fluid, Holder<SoundEvent> emptySound)
   implements Recipe<EmptyAlchemistCauldronRecipe.Input> {
   public ItemStack result() {
      return this.result.m_41777_();
   }

   public FluidStack fluid() {
      return this.fluid.copy();
   }

   public boolean matches(EmptyAlchemistCauldronRecipe.Input input, Level level) {
      return this.input.test(input.item())
         && input.fluid.getAmount() >= this.fluid.getAmount()
         && FluidHelper.isSameFluidSameComponents(this.fluid, input.fluid);
   }

   public ItemStack assemble(EmptyAlchemistCauldronRecipe.Input pContainer, RegistryAccess pRegistryAccess) {
      return this.result.m_41777_();
   }

   public boolean m_5598_() {
      return true;
   }

   public boolean m_8004_(int width, int height) {
      return false;
   }

   public ItemStack m_8043_(RegistryAccess pRegistryAccess) {
      return this.result.m_41777_();
   }

   public RecipeSerializer<?> m_7707_() {
      return (RecipeSerializer<?>)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_SERIALIZER.get();
   }

   public RecipeType<?> m_6671_() {
      return (RecipeType<?>)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get();
   }

   public ResourceLocation m_6423_() {
      return this.getId;
   }

   public record Input(ItemStack item, FluidStack fluid) implements Container {
      public int m_6643_() {
         return 1;
      }

      public boolean m_7983_() {
         return false;
      }

      public ItemStack m_8020_(int index) {
         return this.item;
      }

      public ItemStack m_7407_(int pSlot, int pAmount) {
         return ItemStack.f_41583_;
      }

      public ItemStack m_8016_(int pSlot) {
         return ItemStack.f_41583_;
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

   public static class Serializer implements RecipeSerializer<EmptyAlchemistCauldronRecipe> {
      public EmptyAlchemistCauldronRecipe fromJson(ResourceLocation id, JsonObject recipejson) {
         Ingredient input = Ingredient.m_43917_(GsonHelper.m_289747_(recipejson, "input"));
         ItemStack result = ShapedRecipe.m_151274_(GsonHelper.m_13930_(recipejson, "result"));
         FluidStack fluid = (FluidStack)((Pair)FluidStack.CODEC
               .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "fluid"))
               .getOrThrow(false, IronsSpellbooks.LOGGER::error))
            .getFirst();
         Holder<SoundEvent> sound = BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_11769_);
         if (recipejson.has("sound")) {
            sound = (Holder<SoundEvent>)((Pair)BuiltInRegistries.f_256894_
                  .m_206110_()
                  .decode(JsonOps.INSTANCE, GsonHelper.m_289747_(recipejson, "sound"))
                  .getOrThrow(false, IronsSpellbooks.LOGGER::error))
               .getFirst();
         }

         return new EmptyAlchemistCauldronRecipe(id, input, result, fluid, sound);
      }

      @Nullable
      public EmptyAlchemistCauldronRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
         Ingredient input = Ingredient.m_43940_(buf);
         ItemStack result = buf.m_130267_();
         FluidStack fluid = FluidStack.readFromPacket(buf);
         Holder<SoundEvent> sound = BuiltInRegistries.f_256894_
            .m_263177_((SoundEvent)Objects.requireNonNull((SoundEvent)BuiltInRegistries.f_256894_.m_7745_(buf.m_130281_())));
         return new EmptyAlchemistCauldronRecipe(pRecipeId, input, result, fluid, sound);
      }

      public void toNetwork(FriendlyByteBuf buf, EmptyAlchemistCauldronRecipe recipe) {
         recipe.input.m_43923_(buf);
         buf.m_130055_(recipe.result);
         recipe.fluid.writeToPacket(buf);
         buf.m_130085_(((SoundEvent)recipe.emptySound.get()).m_11660_());
      }
   }
}
