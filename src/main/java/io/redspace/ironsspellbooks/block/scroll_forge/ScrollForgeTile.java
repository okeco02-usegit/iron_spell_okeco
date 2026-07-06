package io.redspace.ironsspellbooks.block.scroll_forge;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ScrollForgeTile extends BlockEntity implements MenuProvider {
   private ScrollForgeMenu menu;
   private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
      protected void onContentsChanged(int slot) {
         ScrollForgeTile.this.updateMenuSlots(slot);
         ScrollForgeTile.this.m_6596_();
      }
   };
   private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> this.itemHandler);

   public ScrollForgeTile(BlockPos pWorldPosition, BlockState pBlockState) {
      super((BlockEntityType)BlockRegistry.SCROLL_FORGE_TILE.get(), pWorldPosition, pBlockState);
   }

   private void updateMenuSlots(int slot) {
      if (this.menu != null) {
         this.menu.onSlotsChanged(slot);
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
      }
   }

   public ItemStackHandler getItemHandler() {
      return this.itemHandler;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.itemHandler.getStackInSlot(slot);
   }

   public MutableComponent getDisplayName() {
      return Component.m_237115_("ui.irons_spellbooks.scroll_forge_title");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int containerId, Inventory inventory, Player player) {
      this.menu = new ScrollForgeMenu(containerId, inventory, this);
      return this.menu;
   }

   public void setRecipeSpell(String spellId) {
      this.menu.setRecipeSpell(SpellRegistry.getSpell(spellId));
   }

   public void drops() {
      SimpleContainer simpleContainer = new SimpleContainer(this.itemHandler.getSlots());

      for (int i = 0; i < this.itemHandler.getSlots() - 1; i++) {
         simpleContainer.m_6836_(i, this.itemHandler.getStackInSlot(i));
      }

      Containers.m_19002_(this.f_58857_, this.f_58858_, simpleContainer);
   }

   public void m_142466_(CompoundTag nbt) {
      super.m_142466_(nbt);
      if (nbt.m_128441_("inventory")) {
         this.itemHandler.deserializeNBT(nbt.m_128469_("inventory"));
      }
   }

   public void m_7651_() {
      super.m_7651_();
      this.lazyItemHandler.invalidate();
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyItemHandler.invalidate();
   }

   protected void m_183515_(@Nonnull CompoundTag tag) {
      tag.m_128365_("inventory", this.itemHandler.serializeNBT());
   }

   public CompoundTag m_5995_() {
      CompoundTag tag = new CompoundTag();
      tag.m_128365_("inventory", this.itemHandler.serializeNBT());
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
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

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
      return cap == ForgeCapabilities.ITEM_HANDLER ? this.lazyItemHandler.cast() : super.getCapability(cap, side);
   }
}
