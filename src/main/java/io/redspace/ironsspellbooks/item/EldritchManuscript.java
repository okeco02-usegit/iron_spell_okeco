package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.network.OpenEldritchScreenPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class EldritchManuscript extends Item {
   private static final Component description = Component.m_237115_("item.irons_spellbooks.eldritch_manuscript_desc").m_130940_(ChatFormatting.GRAY);

   public EldritchManuscript(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand pUsedHand) {
      if (player instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, new OpenEldritchScreenPacket(pUsedHand));
      }

      return super.m_7203_(level, player, pUsedHand);
   }

   public void m_7373_(ItemStack pStack, Level pContext, List<Component> lines, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pContext, lines, pIsAdvanced);
      lines.add(description);
   }
}
