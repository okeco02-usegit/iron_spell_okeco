package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class ScrollJeiInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
   public String apply(ItemStack stack, UidContext uidContext) {
      if (stack.m_41782_()) {
         SpellData ss = ISpellContainer.get(stack).getSpellAtIndex(0);
         return String.format("scroll:%s:%d", ss.getSpell().getSpellId(), ss.getLevel());
      } else {
         return "";
      }
   }
}
