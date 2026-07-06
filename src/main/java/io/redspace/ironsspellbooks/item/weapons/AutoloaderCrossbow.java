package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoloaderCrossbow extends CrossbowItem {
   public static final String LOADING = "Loading";
   public static final String LOADING_TIMESTAMP = "LoadingTimestamp";

   public AutoloaderCrossbow(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player player, InteractionHand pHand) {
      ItemStack itemstack = player.m_21120_(pHand);
      if (m_40932_(itemstack)) {
         m_40887_(pLevel, player, pHand, itemstack, m_40945_(itemstack), 1.0F);
         m_40884_(itemstack, false);
         if (!player.m_6298_(itemstack).m_41619_()) {
            startLoading(player, itemstack);
         } else {
            player.m_5496_(SoundEvents.f_12018_, 0.75F, 1.5F);
         }

         return InteractionResultHolder.m_19096_(itemstack);
      } else if (isLoading(itemstack)) {
         if (player.m_6047_()) {
            setLoadingTicks(itemstack, 0);
            setLoading(itemstack, false);
         }

         return InteractionResultHolder.m_19098_(itemstack);
      } else if (!player.m_6298_(itemstack).m_41619_()) {
         startLoading(player, itemstack);
         return InteractionResultHolder.m_19096_(itemstack);
      } else {
         return InteractionResultHolder.m_19100_(itemstack);
      }
   }

   public static void startLoading(Player player, ItemStack itemstack) {
      setLoading(itemstack, true);
      setLoadingTicks(itemstack, 0);
   }

   public void m_6883_(ItemStack itemstack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      handleTicking(itemstack, pLevel, pEntity);
      super.m_6883_(itemstack, pLevel, pEntity, pSlotId, pIsSelected);
   }

   public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
      int i = getLoadingTicks(stack);
      handleTicking(stack, entity.f_19853_, entity);
      if (i != getLoadingTicks(stack)) {
         ItemStack cloneStack = stack.m_41777_();
         entity.m_32045_(cloneStack);
      }

      return super.onEntityItemUpdate(stack, entity);
   }

   protected static void handleTicking(ItemStack itemStack, Level level, @NotNull Entity entity) {
      if (!level.f_46443_ && isLoading(itemStack)) {
         int i = getLoadingTicks(itemStack);
         if (i > m_40939_(itemStack)) {
            setLoading(itemStack, false);
            if (entity instanceof LivingEntity livingEntity && !m_40932_(itemStack) && m_40859_(livingEntity, itemStack)) {
               m_40884_(itemStack, true);
            }

            SoundSource soundsource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.BLOCKS;
            if (m_40932_(itemStack)) {
               level.m_6263_(
                  null,
                  entity.m_20185_(),
                  entity.m_20186_(),
                  entity.m_20189_(),
                  SoundEvents.f_11841_,
                  soundsource,
                  1.0F,
                  1.0F / (level.m_213780_().m_188501_() * 0.5F + 1.0F) + 0.2F
               );
            } else {
               level.m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), SoundEvents.f_12018_, soundsource, 1.0F, 1.7F);
            }
         }

         setLoadingTicks(itemStack, ++i);
      }
   }

   public static int m_40939_(ItemStack pCrossbowStack) {
      return CrossbowItem.m_40939_(pCrossbowStack) * 3;
   }

   public static boolean isLoading(ItemStack pCrossbowStack) {
      CompoundTag compoundtag = pCrossbowStack.m_41783_();
      return compoundtag != null && compoundtag.m_128471_("Loading");
   }

   public static void setLoading(ItemStack pCrossbowStack, boolean isLoading) {
      pCrossbowStack.m_41784_().m_128379_("Loading", isLoading);
   }

   public static int getLoadingTicks(ItemStack pCrossbowStack) {
      CompoundTag compoundtag = pCrossbowStack.m_41783_();
      return compoundtag != null ? compoundtag.m_128451_("LoadingTimestamp") : 0;
   }

   public static void setLoadingTicks(ItemStack pCrossbowStack, int timestamp) {
      pCrossbowStack.m_41784_().m_128405_("LoadingTimestamp", timestamp);
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
      TooltipsUtils.addShiftTooltip(pTooltip, List.of(Component.m_237115_("item.irons_spellbooks.autoloader_crossbow.desc").m_130940_(ChatFormatting.YELLOW)));
      super.m_7373_(pStack, pLevel, pTooltip, pFlag);
   }
}
