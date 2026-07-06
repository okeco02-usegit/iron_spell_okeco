package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CastingItem extends Item {
   public CastingItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.m_21120_(hand);
      SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
      SpellSelectionManager.SelectionOption selectionOption = spellSelectionManager.getSelection();
      if (selectionOption != null && !selectionOption.spellData.equals(SpellData.EMPTY)) {
         SpellData spellData = selectionOption.spellData;
         int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);
         if (!level.m_5776_()) {
            String castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;
            return spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, castingSlot)
               ? InteractionResultHolder.m_19096_(itemStack)
               : InteractionResultHolder.m_19100_(itemStack);
         } else if (ClientMagicData.isCasting()) {
            return InteractionResultHolder.m_19096_(itemStack);
         } else {
            return ClientMagicData.getPlayerMana() >= spellData.getSpell().getManaCost(spellLevel)
                  && !ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                  && ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell())
               ? InteractionResultHolder.m_19096_(itemStack)
               : InteractionResultHolder.m_19098_(itemStack);
         }
      } else {
         return InteractionResultHolder.m_19098_(itemStack);
      }
   }

   public int m_8105_(ItemStack itemStack) {
      return 7200;
   }

   public UseAnim m_6164_(ItemStack pStack) {
      return UseAnim.BOW;
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged;
   }

   public void m_5551_(ItemStack itemStack, Level p_41413_, LivingEntity entity, int p_41415_) {
      entity.m_5810_();
      Utils.releaseUsingHelper(entity, itemStack, p_41415_);
      super.m_5551_(itemStack, p_41413_, entity, p_41415_);
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pLevel, pTooltipComponents, pIsAdvanced);
   }
}
