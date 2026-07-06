package io.redspace.ironsspellbooks.api.item;

import net.minecraft.world.item.ItemStack;

public class CastingImplementData {
   public static final String NBT = "irons_spellbooks:casting_implement";

   public static boolean has(ItemStack stack) {
      return stack.m_41782_() && stack.m_41784_().m_128441_("irons_spellbooks:casting_implement");
   }

   public static boolean get(ItemStack stack) {
      return stack.m_41784_().m_128471_("irons_spellbooks:casting_implement");
   }

   public static void set(ItemStack stack, boolean isCastingImplement) {
      stack.m_41784_().m_128379_("irons_spellbooks:casting_implement", isCastingImplement);
   }
}
