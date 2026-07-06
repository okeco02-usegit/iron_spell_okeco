package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.network.OpenHeldBookPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ReadableLoreItem extends Item implements ILecternPlaceable {
   private final ResourceLocation lecternLocation;

   public ReadableLoreItem(ResourceLocation lecternLocation, Properties pProperties) {
      super(pProperties);
      this.lecternLocation = lecternLocation;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.m_21120_(pHand);
      if (pPlayer instanceof ServerPlayer serverPlayer) {
         if (WrittenBookItem.m_43461_(itemstack, serverPlayer.m_20203_(), serverPlayer)) {
            serverPlayer.f_36096_.m_38946_();
         }

         PacketDistributor.sendToPlayer(serverPlayer, new OpenHeldBookPacket(pHand));
      }

      return InteractionResultHolder.m_19092_(itemstack, pLevel.m_5776_());
   }

   public InteractionResult m_6225_(UseOnContext pContext) {
      Level level = pContext.m_43725_();
      BlockPos blockpos = pContext.m_8083_();
      BlockState blockstate = level.m_8055_(blockpos);
      if (blockstate.m_60713_(Blocks.f_50624_)) {
         return LecternBlock.m_269125_(pContext.m_43723_(), level, blockpos, blockstate, pContext.m_43722_())
            ? InteractionResult.m_19078_(level.f_46443_)
            : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public List<Component> getPages(ItemStack stack) {
      if (!stack.m_41782_()) {
         return List.of();
      }

      ItemStack copy = stack.m_41777_();
      WrittenBookItem.m_43461_(copy, null, MinecraftInstanceHelper.getPlayer());
      List<Component> resolvedPages = new ArrayList<>();
      ListTag listtag = copy.m_41784_().m_128437_("pages", 8);

      for (int i = 0; i < listtag.size(); i++) {
         resolvedPages.add(Serializer.m_130701_(listtag.m_128778_(i)));
      }

      return resolvedPages;
   }

   @Override
   public Optional<ResourceLocation> simpleTextureOverride(ItemStack stack) {
      return Optional.of(this.lecternLocation);
   }
}
