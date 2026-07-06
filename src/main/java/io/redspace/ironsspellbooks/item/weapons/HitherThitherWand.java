package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.item.UniqueItem;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class HitherThitherWand extends Item implements IPresetSpellContainer, UniqueItem {
   public HitherThitherWand(Properties pProperties) {
      super(pProperties);
   }

   public void m_7373_(ItemStack pStack, Level context, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, context, pTooltipComponents, pIsAdvanced);
      pTooltipComponents.add(Component.m_237115_("tooltip.irons_spellbooks.spellbook_unique").m_130948_(TooltipsUtils.UNIQUE_STYLE));
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
            spellContainer.addSpell((AbstractSpell)SpellRegistry.PORTAL_SPELL.get(), 1, true);
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
         }
      }
   }
}
