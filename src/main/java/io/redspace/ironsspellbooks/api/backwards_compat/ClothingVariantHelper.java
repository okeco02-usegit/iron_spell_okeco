package io.redspace.ironsspellbooks.api.backwards_compat;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public class ClothingVariantHelper {
   public static final String NBT = "irons_spellbooks:clothing_variant";

   @Nullable
   public static String getClothingVariant(ItemStack stack) {
      return stack.m_41782_()
         ? (stack.m_41784_().m_128441_("irons_spellbooks:clothing_variant") ? stack.m_41784_().m_128461_("irons_spellbooks:clothing_variant") : null)
         : null;
   }

   public static String getClothingVariantOrElse(ItemStack stack, String entry) {
      return stack.m_41782_()
         ? (stack.m_41784_().m_128441_("irons_spellbooks:clothing_variant") ? stack.m_41784_().m_128461_("irons_spellbooks:clothing_variant") : entry)
         : entry;
   }
}
