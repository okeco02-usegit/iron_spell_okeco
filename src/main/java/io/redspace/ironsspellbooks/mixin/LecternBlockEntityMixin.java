package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity {
   @Shadow
   ItemStack f_59527_;
   @Shadow
   private int f_59528_;
   @Shadow
   private int f_59529_;

   public LecternBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
      super(pType, pPos, pBlockState);
   }

   @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
   private void fudgeLecternValidity(CallbackInfoReturnable<Boolean> cir) {
      if (this.f_59527_.m_41720_() instanceof ILecternPlaceable) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "setBook(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"))
   private void irons_spellbooks$getPageCount(ItemStack pStack, Player pPlayer, CallbackInfo ci) {
      if (pStack.m_41720_() instanceof ILecternPlaceable lecternPlaceable) {
         this.f_59529_ = lecternPlaceable.getPages(pStack).size();
      }
   }

   @Inject(method = "load", at = @At("RETURN"))
   private void irons_spellbooks$getPageCount2(CompoundTag pTag, CallbackInfo ci) {
      if (!this.f_59527_.m_41619_() && this.f_59527_.m_41720_() instanceof ILecternPlaceable lecternPlaceable) {
         this.f_59529_ = lecternPlaceable.getPages(this.f_59527_).size();
      }
   }

   public void m_6596_() {
      super.m_6596_();
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 2);
      }
   }

   public Packet<ClientGamePacketListener> m_58483_() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public CompoundTag m_5995_() {
      return this.m_187482_();
   }
}
