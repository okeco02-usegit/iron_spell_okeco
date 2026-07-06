package io.redspace.ironsspellbooks.block.pedestal;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalTile extends BlockEntity {
   private static final String NBT_HELD_ITEM = "heldItem";
   private ItemStack heldItem = ItemStack.f_41583_;

   public PedestalTile(BlockPos pWorldPosition, BlockState pBlockState) {
      super((BlockEntityType)BlockRegistry.PEDESTAL_TILE.get(), pWorldPosition, pBlockState);
   }

   public ItemStack getHeldItem() {
      return this.heldItem;
   }

   public void setHeldItem(ItemStack newItem) {
      this.heldItem = newItem;
      this.m_6596_();
   }

   public void drops() {
      SimpleContainer simpleContainer = new SimpleContainer(new ItemStack[]{this.heldItem});
      Containers.m_19002_(this.f_58857_, this.f_58858_, simpleContainer);
   }

   public void m_142466_(CompoundTag nbt) {
      super.m_142466_(nbt);
      this.readNBT(nbt);
   }

   protected void m_183515_(@Nonnull CompoundTag tag) {
      this.writeNBT(tag);
   }

   public void m_6596_() {
      super.m_6596_();
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 2);
      }
   }

   public CompoundTag m_5995_() {
      CompoundTag tag = new CompoundTag();
      this.writeNBT(tag);
      return tag;
   }

   public boolean m_7531_(int pId, int pType) {
      return super.m_7531_(pId, pType);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      CompoundTag nbt = this.writeNBT(new CompoundTag());
      return ClientboundBlockEntityDataPacket.m_195642_(this, block -> nbt);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.handleUpdateTag(pkt.m_131708_());
      this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
   }

   public void handleUpdateTag(CompoundTag tag) {
      if (tag != null) {
         this.m_142466_(tag);
      }
   }

   private CompoundTag writeNBT(CompoundTag nbt) {
      nbt.m_128365_("heldItem", this.heldItem.serializeNBT());
      return nbt;
   }

   private CompoundTag readNBT(CompoundTag nbt) {
      if (nbt.m_128441_("heldItem")) {
         this.heldItem = ItemStack.m_41712_(nbt.m_128469_("heldItem"));
      }

      return nbt;
   }
}
