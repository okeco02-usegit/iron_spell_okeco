package io.redspace.ironsspellbooks.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class SimpleDescriptiveBlockItem extends BlockItem {
   public SimpleDescriptiveBlockItem(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   public void m_7373_(ItemStack pStack, Level context, List<Component> lines, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, context, lines, pIsAdvanced);
      lines.add(
         Component.m_237115_(String.format("%s.description", this.m_5524_())).m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC})
      );
   }
}
