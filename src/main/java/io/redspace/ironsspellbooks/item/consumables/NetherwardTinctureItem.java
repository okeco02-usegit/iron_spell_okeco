package io.redspace.ironsspellbooks.item.consumables;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class NetherwardTinctureItem extends DrinkableItem {
   private static final Component description = Component.m_237115_("item.irons_spellbooks.netherward_tincture.desc").m_130940_(ChatFormatting.GRAY);

   public NetherwardTinctureItem() {
      super(ItemPropertiesHelper.material(16), NetherwardTinctureItem::applyEffect, null, false);
   }

   @Override
   public void m_7373_(ItemStack pStack, Level context, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, context, pTooltipComponents, pIsAdvanced);
      pTooltipComponents.add(description);
   }

   private static void applyEffect(ItemStack itemStack, LivingEntity livingEntity) {
      if (livingEntity instanceof AbstractPiglin piglin) {
         piglin.m_34670_(true);
         piglin.m_216990_(SoundEvents.f_12300_);
      } else if (livingEntity instanceof Hoglin hoglin) {
         hoglin.m_34564_(true);
         hoglin.m_216990_(SoundEvents.f_11959_);
      }

      livingEntity.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 200));
      livingEntity.m_216990_(SoundEvents.f_144181_);
   }

   public SoundEvent m_6023_() {
      return SoundEvents.f_11970_;
   }

   public InteractionResult m_6880_(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
      if (!(pInteractionTarget instanceof AbstractPiglin) && !(pInteractionTarget instanceof Hoglin)) {
         return super.m_6880_(pStack, pPlayer, pInteractionTarget, pUsedHand);
      }

      applyEffect(pStack, pInteractionTarget);
      if (!pPlayer.m_150110_().f_35937_) {
         pStack.m_41774_(1);
      }

      return InteractionResult.SUCCESS;
   }
}
