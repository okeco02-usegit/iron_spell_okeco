package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class PortalFrameBlockItem extends BlockItem {
   private static final Component DESCRIPTION = Component.m_237115_("block.irons_spellbooks.portal_frame.desc").m_130940_(ChatFormatting.GRAY);

   public PortalFrameBlockItem() {
      super((Block)BlockRegistry.PORTAL_FRAME.get(), new Properties().m_41486_().m_41497_(Rarity.RARE));
   }

   public void m_7373_(ItemStack pStack, Level pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      super.m_7373_(pStack, pContext, pTooltipComponents, pTooltipFlag);
      pTooltipComponents.add(DESCRIPTION);
   }
}
