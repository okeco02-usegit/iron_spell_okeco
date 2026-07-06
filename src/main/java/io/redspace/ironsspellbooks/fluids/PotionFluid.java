package io.redspace.ironsspellbooks.fluids;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.registries.FluidRegistry;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import org.jetbrains.annotations.NotNull;

public class PotionFluid extends NoopFluid {
   public PotionFluid(Properties properties) {
      super(properties);
   }

   public static FluidStack of(int amount, Potion potionContents, PotionFluid.BottleType bottleType) {
      FluidStack fluidStack = new FluidStack((Fluid)FluidRegistry.POTION_FLUID.get(), amount);
      addPotionToFluidStack(fluidStack, potionContents);
      PotionFluid.BottleType.set(fluidStack, bottleType);
      return fluidStack;
   }

   public static FluidStack of(int amount, Holder<Potion> potion, PotionFluid.BottleType bottleType) {
      return of(amount, (Potion)potion.get(), bottleType);
   }

   public static FluidStack addPotionToFluidStack(FluidStack fs, Potion potionContents) {
      if (potionContents == Potions.f_43598_) {
         return fs;
      }

      FluidHelper.setPotionContents(fs, potionContents);
      return fs;
   }

   public static FluidStack from(ItemStack stack) {
      Potion potion = PotionUtils.m_43579_(stack);
      if (potion == Potions.f_43598_) {
         return FluidStack.EMPTY;
      }

      PotionFluid.BottleType type = stack.m_150930_(Items.f_42739_)
         ? PotionFluid.BottleType.LINGERING
         : (stack.m_150930_(Items.f_42736_) ? PotionFluid.BottleType.SPLASH : PotionFluid.BottleType.REGULAR);
      FluidStack fs = new FluidStack((Fluid)FluidRegistry.POTION_FLUID.get(), 250);
      FluidHelper.setPotionContents(fs, potion);
      PotionFluid.BottleType.set(fs, type);
      return fs;
   }

   public static ItemStack from(FluidStack stack) {
      if (stack.getAmount() >= 250 && (stack.getFluid().m_205067_(FluidTags.f_13131_) || FluidHelper.hasPotionContents(stack))) {
         PotionFluid.BottleType type = PotionFluid.BottleType.get(stack);
         Item item = type == PotionFluid.BottleType.LINGERING ? Items.f_42739_ : (type == PotionFluid.BottleType.SPLASH ? Items.f_42736_ : Items.f_42589_);
         ItemStack is = new ItemStack(item);
         PotionUtils.m_43549_(is, FluidHelper.hasPotionContents(stack) ? FluidHelper.getPotionContents(stack) : Potions.f_43599_);
         return is;
      } else {
         return ItemStack.f_41583_;
      }
   }

   public enum BottleType implements StringRepresentable {
      REGULAR("regular", "potion"),
      SPLASH("splash", "splash_potion"),
      LINGERING("lingering", "lingering_potion");

      final String id;
      final String descriptionId;
      public static final Codec<PotionFluid.BottleType> CODEC = StringRepresentable.m_216439_(PotionFluid.BottleType::values);
      private static final String NBT = "irons_spellbooks:bottle_type";

      BottleType(String id, String descriptionId) {
         this.id = id;
         this.descriptionId = descriptionId;
      }

      public String descriptionId() {
         return this.descriptionId;
      }

      @NotNull
      public String m_7912_() {
         return this.id;
      }

      public static PotionFluid.BottleType get(FluidStack stack) {
         return stack.hasTag() ? valueOf(stack.getOrCreateTag().m_128461_("irons_spellbooks:bottle_type")) : REGULAR;
      }

      public static void set(FluidStack stack, PotionFluid.BottleType type) {
         stack.getOrCreateTag().m_128359_("irons_spellbooks:bottle_type", type.name());
      }
   }
}
