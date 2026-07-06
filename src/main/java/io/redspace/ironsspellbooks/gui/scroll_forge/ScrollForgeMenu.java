package io.redspace.ironsspellbooks.gui.scroll_forge;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeTile;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ScrollForgeMenu extends AbstractContainerMenu {
   public final ScrollForgeTile blockEntity;
   private final Level level;
   private final Slot inkSlot;
   private final Slot blankScrollSlot;
   private final Slot focusSlot;
   private final Slot resultSlot;
   private AbstractSpell spellRecipeSelection = SpellRegistry.none();
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 4;

   public ScrollForgeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
      this(containerId, inv, inv.f_35978_.m_9236_().m_7702_(extraData.m_130135_()));
   }

   public ScrollForgeMenu(int containerId, Inventory inv, BlockEntity entity) {
      super(MenuRegistry.SCROLL_FORGE_MENU.get(), containerId);
      m_38869_(inv, 4);
      this.blockEntity = (ScrollForgeTile)entity;
      this.level = inv.f_35978_.m_9236_();
      this.addPlayerInventory(inv);
      this.addPlayerHotbar(inv);
      IItemHandler itemHandler = this.blockEntity.getItemHandler();
      this.inkSlot = new SlotItemHandler(itemHandler, 0, 12, 17) {
         public boolean m_5857_(ItemStack stack) {
            return stack.m_41720_() instanceof InkItem;
         }
      };
      this.blankScrollSlot = new SlotItemHandler(itemHandler, 1, 35, 17) {
         public boolean m_5857_(ItemStack stack) {
            return stack.m_150930_(Items.f_42516_);
         }
      };
      this.focusSlot = new SlotItemHandler(itemHandler, 2, 58, 17) {
         public boolean m_5857_(ItemStack stack) {
            return stack.m_204117_(ModTags.SCHOOL_FOCUS);
         }
      };
      this.resultSlot = new SlotItemHandler(itemHandler, 3, 35, 47) {
         public boolean m_5857_(ItemStack stack) {
            return false;
         }

         public void m_142406_(Player player, ItemStack stack) {
            ScrollForgeMenu.this.inkSlot.m_6201_(1);
            ScrollForgeMenu.this.blankScrollSlot.m_6201_(1);
            ScrollForgeMenu.this.focusSlot.m_6201_(1);
            ScrollForgeMenu.this.level.m_5594_(null, ScrollForgeMenu.this.blockEntity.m_58899_(), SoundEvents.f_12493_, SoundSource.BLOCKS, 0.8F, 1.1F);
            super.m_142406_(player, stack);
         }
      };
      this.m_38897_(this.inkSlot);
      this.m_38897_(this.blankScrollSlot);
      this.m_38897_(this.focusSlot);
      this.m_38897_(this.resultSlot);
   }

   public void onSlotsChanged(int slot) {
      if (slot != 3) {
         this.setupResultSlot(this.spellRecipeSelection);
      }
   }

   private void setupResultSlot(AbstractSpell spell) {
      ItemStack scrollStack = this.blankScrollSlot.m_7993_();
      ItemStack inkStack = this.inkSlot.m_7993_();
      ItemStack focusStack = this.focusSlot.m_7993_();
      ItemStack resultStack = ItemStack.f_41583_;
      if (!scrollStack.m_41619_()
         && !inkStack.m_41619_()
         && !focusStack.m_41619_()
         && !spell.equals(SpellRegistry.none())
         && spell.allowCrafting()
         && SchoolRegistry.getSchoolsFromFocus(focusStack).contains(spell.getSchoolType())
         && scrollStack.m_41720_().equals(Items.f_42516_)
         && inkStack.m_41720_() instanceof InkItem inkItem) {
         resultStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
         resultStack.m_41764_(1);
         ISpellContainer.createScrollContainer(spell, spell.getMinLevelForRarity(inkItem.getRarity()), resultStack);
      }

      if (!ItemStack.m_41728_(resultStack, this.resultSlot.m_7993_())) {
         if (resultStack.m_41619_()) {
            this.spellRecipeSelection = SpellRegistry.none();
         }

         this.resultSlot.m_5852_(resultStack);
      }
   }

   public void setRecipeSpell(AbstractSpell typeFromValue) {
      this.spellRecipeSelection = typeFromValue;
      this.setupResultSlot(typeFromValue);
   }

   public Slot getInkSlot() {
      return this.inkSlot;
   }

   public Slot getBlankScrollSlot() {
      return this.blankScrollSlot;
   }

   public Slot getFocusSlot() {
      return this.focusSlot;
   }

   public Slot getResultSlot() {
      return this.resultSlot;
   }

   public ItemStack m_7648_(Player playerIn, int index) {
      Slot sourceSlot = (Slot)this.f_38839_.get(index);
      if (sourceSlot != null && sourceSlot.m_6657_()) {
         ItemStack sourceStack = sourceSlot.m_7993_();
         ItemStack copyOfSourceStack = sourceStack.m_41777_();
         if (index < 36) {
            if (!this.m_38903_(sourceStack, 36, 40, false)) {
               return ItemStack.f_41583_;
            }
         } else {
            if (index >= 40) {
               System.out.println("Invalid slotIndex:" + index);
               return ItemStack.f_41583_;
            }

            if (!this.m_38903_(sourceStack, 0, 36, false)) {
               return ItemStack.f_41583_;
            }
         }

         if (sourceStack.m_41613_() == 0) {
            sourceSlot.m_5852_(ItemStack.f_41583_);
         } else {
            sourceSlot.m_6654_();
         }

         sourceSlot.m_142406_(playerIn, sourceStack);
         return copyOfSourceStack;
      } else {
         return ItemStack.f_41583_;
      }
   }

   public boolean m_5882_(ItemStack pStack, Slot pSlot) {
      return pSlot.f_40218_ != this.resultSlot.f_40218_ && super.m_5882_(pStack, pSlot);
   }

   public boolean m_6875_(Player pPlayer) {
      return m_38889_(ContainerLevelAccess.m_39289_(this.level, this.blockEntity.m_58899_()), pPlayer, (Block)BlockRegistry.SCROLL_FORGE_BLOCK.get());
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.m_38897_(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18 + 21, 84 + i * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         this.m_38897_(new Slot(playerInventory, i, 8 + i * 18 + 21, 142));
      }
   }
}
