package io.redspace.ironsspellbooks.item.consumables;

import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class DrinkableItem extends Item {
   private final BiConsumer<ItemStack, LivingEntity> drinkAction;
   private final Item returnItem;
   private final boolean showDesc;

   public DrinkableItem(Properties pProperties, BiConsumer<ItemStack, LivingEntity> drinkAction, @Nullable Item returnItem, boolean showDescription) {
      super(pProperties);
      this.drinkAction = drinkAction;
      this.returnItem = returnItem;
      this.showDesc = showDescription;
   }

   public DrinkableItem(Properties pProperties, BiConsumer<ItemStack, LivingEntity> drinkAction) {
      this(pProperties, drinkAction, null, true);
   }

   public ItemStack m_5922_(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
      Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
      if (player instanceof ServerPlayer) {
         CriteriaTriggers.f_10592_.m_23682_((ServerPlayer)player, pStack);
      }

      if (!pLevel.f_46443_) {
         this.drinkAction.accept(pStack, pEntityLiving);
      }

      if (player != null && !player.m_150110_().f_35937_) {
         pStack.m_41774_(1);
      }

      if (this.returnItem != null && (player == null || !player.m_150110_().f_35937_)) {
         if (pStack.m_41619_()) {
            return new ItemStack(this.returnItem);
         }

         if (player != null) {
            player.m_150109_().m_36054_(new ItemStack(this.returnItem));
         }
      }

      pEntityLiving.m_146850_(GameEvent.f_223704_);
      return pStack;
   }

   public int m_8105_(ItemStack pStack) {
      return 32;
   }

   public UseAnim m_6164_(ItemStack pStack) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player pPlayer, InteractionHand pHand) {
      return ItemUtils.m_150959_(pLevel, pPlayer, pHand);
   }

   public void m_7373_(ItemStack pStack, Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, level, pTooltipComponents, pIsAdvanced);
      if (this.showDesc) {
         pTooltipComponents.add(Component.m_237119_());
         pTooltipComponents.add(Component.m_237115_("potion.whenDrank").m_130940_(ChatFormatting.DARK_PURPLE));
         pTooltipComponents.add(Component.m_237115_(this.m_5524_() + ".desc").m_130940_(ChatFormatting.BLUE));
      }
   }
}
