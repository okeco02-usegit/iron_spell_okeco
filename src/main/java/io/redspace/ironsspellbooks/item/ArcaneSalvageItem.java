package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ArcaneSalvageItem extends Item {
   private static final Component description = Component.m_237115_("item.irons_spellbooks.arcane_salvage_desc").m_130940_(ChatFormatting.GRAY);

   public ArcaneSalvageItem() {
      super(ItemPropertiesHelper.material());
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> lines, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pLevel, lines, pIsAdvanced);
      lines.add(description);
   }
}
