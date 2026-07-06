package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SpellSlotUpgradeItem extends Item {
   private final int maxSlots;
   private final Component description;

   public SpellSlotUpgradeItem(int maxSlotsToUpgradeTo) {
      super(ItemPropertiesHelper.material().m_41497_(Rarity.RARE));
      this.maxSlots = maxSlotsToUpgradeTo;
      this.description = Component.m_237110_("item.irons_spellbooks.spell_slot_upgrade_desc", new Object[]{maxSlotsToUpgradeTo}).m_130940_(ChatFormatting.GRAY);
   }

   public int maxSlots() {
      return this.maxSlots;
   }

   public void m_7373_(ItemStack pStack, Level context, List<Component> lines, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, context, lines, pIsAdvanced);
      lines.add(this.description);
   }
}
