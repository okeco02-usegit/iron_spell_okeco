package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WaywardCompass extends Item {
   private static final Component description = Component.m_237115_("item.irons_spellbooks.wayward_compass_desc").m_130940_(ChatFormatting.DARK_AQUA);

   public WaywardCompass() {
      super(ItemPropertiesHelper.equipment());
   }

   public static GlobalPos getCatacombsLocation(Entity entity, CompoundTag compoundTag) {
      return entity.f_19853_.m_46472_() == Level.f_46428_ && compoundTag.m_128441_("CatacombsPos")
         ? GlobalPos.m_122643_(entity.f_19853_.m_46472_(), NbtUtils.m_129239_(compoundTag.m_128469_("CatacombsPos")))
         : null;
   }

   public void m_6883_(ItemStack itemStack, Level level, Entity pEntity, int pSlotId, boolean pIsSelected) {
      if (!level.f_46443_) {
         CompoundTag tag = itemStack.m_41784_();
         if (!tag.m_128441_("isInInventory")) {
            tag.m_128379_("isInInventory", true);
         }
      }
   }

   public void m_7836_(ItemStack pStack, Level pLevel, Player pPlayer) {
      findCatacombs(pStack, pLevel, pPlayer);
   }

   private static void findCatacombs(ItemStack pStack, Level pLevel, Player pPlayer) {
      if (pLevel instanceof ServerLevel serverlevel) {
         BlockPos blockpos = serverlevel.m_215011_(ModTags.WAYWARD_COMPASS_LOCATOR, pPlayer.m_20183_(), 100, false);
         if (blockpos != null) {
            CompoundTag tag = pStack.m_41784_();
            tag.m_128365_("CatacombsPos", NbtUtils.m_129224_(blockpos));
         }
      }
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemStack = pPlayer.m_21120_(pUsedHand);
      if (this.missingWarning(itemStack)) {
         findCatacombs(itemStack, pLevel, pPlayer);
         pPlayer.m_36335_().m_41524_((Item)ItemRegistry.WAYWARD_COMPASS.get(), 200);
         return InteractionResultHolder.m_19092_(itemStack, pLevel.f_46443_);
      } else {
         return super.m_7203_(pLevel, pPlayer, pUsedHand);
      }
   }

   public boolean missingWarning(ItemStack itemStack) {
      return itemStack.m_41783_() != null && itemStack.m_41783_().m_128441_("isInInventory") && !itemStack.m_41783_().m_128441_("CatacombsPos");
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pLevel, pTooltipComponents, pIsAdvanced);
      pTooltipComponents.add(description);
      if (this.missingWarning(pStack)) {
         pTooltipComponents.add(
            Component.m_237110_("item.irons_spellbooks.wayward_compass.error", new Object[]{Minecraft.m_91087_().f_91066_.f_92095_.m_90863_()})
               .m_130940_(ChatFormatting.RED)
         );
      }
   }
}
