package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
   @Unique
   @Nullable
   private static BlockState irons_spellbooks$blockStateCapture;
   @Unique
   @Nullable
   private static BlockPos irons_spellbooks$blockPosCapture;
   @Unique
   @Nullable
   private static ServerLevel irons_spellbooks$levelCapture;

   @Inject(method = "dispenseFrom", at = @At("HEAD"))
   private void irons_spellbooks$captureParameters(ServerLevel pLevel, BlockPos pPos, CallbackInfo ci) {
      irons_spellbooks$blockStateCapture = pLevel.m_8055_(pPos);
      irons_spellbooks$blockPosCapture = pPos;
      irons_spellbooks$levelCapture = pLevel;
   }

   @Inject(method = "getDispenseMethod", at = @At("HEAD"), cancellable = true)
   private void irons_spellbooks$injectCauldronInteractions(ItemStack pStack, CallbackInfoReturnable<DispenseItemBehavior> cir) {
      if (irons_spellbooks$blockStateCapture != null
         && irons_spellbooks$blockPosCapture != null
         && irons_spellbooks$levelCapture != null
         && irons_spellbooks$levelCapture.m_7702_(
            irons_spellbooks$blockPosCapture.m_122032_().m_121945_((Direction)irons_spellbooks$blockStateCapture.m_61143_(DirectionalBlock.f_52588_))
         ) instanceof AlchemistCauldronTile alchemistCauldronTile) {
         final ItemStack cauldronResult = alchemistCauldronTile.tryExecuteRecipeInteractions(irons_spellbooks$levelCapture, pStack);
         if (!cauldronResult.m_41619_()) {
            cir.setReturnValue(new DefaultDispenseItemBehavior() {
               protected ItemStack m_7498_(BlockSource blockSource, ItemStack dispensingStack) {
                  ItemStack stack = dispensingStack;
                  ItemStack remainder = cauldronResult;
                  stack.m_41774_(1);
                  if (stack.m_41619_()) {
                     return remainder;
                  }

                  ItemStack itemstack = remainder;
                  int i = ((DispenserBlockEntity)blockSource.m_8118_()).m_59237_(remainder);
                  if (i == -1) {
                     Direction direction = (Direction)blockSource.m_6414_().m_61143_(DispenserBlock.f_52659_);
                     m_123378_(blockSource.m_7727_(), itemstack, 6, direction, DispenserBlock.m_52720_(blockSource));
                  }

                  return stack;
               }
            });
         }
      }
   }
}
