package io.redspace.ironsspellbooks.gui.inscription_table;

import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;

public class InscriptionTableMenu extends AbstractContainerMenu {
   private final Player player;
   private final Level level;
   private final Slot spellBookSlot;
   private final Slot scrollSlot;
   private final Slot resultSlot;
   private int selectedSpellIndex = -1;
   private boolean fromCurioSlot = false;
   protected final ResultContainer resultContainer = new ResultContainer();
   protected final Container scrollContainer = new SimpleContainer(1) {
      public void m_6596_() {
         super.m_6596_();
         InscriptionTableMenu.this.m_6199_(this);
      }
   };
   protected final Container spellbookContainer = new SimpleContainer(1) {
      public void m_6596_() {
         super.m_6596_();
         InscriptionTableMenu.this.m_6199_(this);
      }

      public boolean m_7013_(int pSlot, ItemStack pStack) {
         return super.m_7013_(pSlot, pStack);
      }
   };
   protected final ContainerLevelAccess access;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 3;

   public InscriptionTableMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
      this(containerId, inv, ContainerLevelAccess.f_39287_);
   }

   public InscriptionTableMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
      super(MenuRegistry.INSCRIPTION_TABLE_MENU.get(), containerId);
      this.access = access;
      m_38869_(inv, 3);
      this.level = inv.f_35978_.m_9236_();
      this.player = inv.f_35978_;
      this.addPlayerInventory(inv);
      this.addPlayerHotbar(inv);
      this.spellBookSlot = new Slot(this.spellbookContainer, 0, 17, 21) {
         public boolean m_5857_(ItemStack stack) {
            return stack.m_41720_() instanceof SpellBook;
         }

         public void m_5852_(ItemStack pStack) {
            super.m_5852_(pStack);
            if (InscriptionTableMenu.this.fromCurioSlot && InscriptionTableMenu.this.player != null) {
               Utils.setPlayerSpellbookStack(InscriptionTableMenu.this.player, pStack);
            }
         }

         public void m_142406_(Player pPlayer, ItemStack pStack) {
            InscriptionTableMenu.this.setSelectedSpell(-1);
            super.m_142406_(pPlayer, pStack);
         }
      };
      this.scrollSlot = new Slot(this.scrollContainer, 0, 17, 53) {
         public boolean m_5857_(ItemStack stack) {
            return stack.m_150930_((Item)ItemRegistry.SCROLL.get());
         }
      };
      this.resultSlot = new Slot(this.resultContainer, 2, 208, 136) {
         public boolean m_5857_(ItemStack stack) {
            return false;
         }

         public void m_142406_(Player player, ItemStack stack) {
            ItemStack spellBookStack = InscriptionTableMenu.this.spellBookSlot.m_7993_();
            ISpellContainerMutable spellList = ISpellContainer.get(spellBookStack).mutableCopy();
            spellList.removeSpellAtIndex(InscriptionTableMenu.this.selectedSpellIndex);
            ISpellContainer.set(spellBookStack, spellList.toImmutable());
            super.m_142406_(player, spellBookStack);
         }
      };
      this.m_38897_(this.spellBookSlot);
      this.m_38897_(this.scrollSlot);
      this.m_38897_(this.resultSlot);
      ItemStack spellbookStack = Utils.getPlayerSpellbookStack(inv.f_35978_);
      if (spellbookStack != null) {
         this.fromCurioSlot = true;
         this.spellBookSlot.m_5852_(spellbookStack);
      }
   }

   public Slot getSpellBookSlot() {
      return this.spellBookSlot;
   }

   public Slot getScrollSlot() {
      return this.scrollSlot;
   }

   public Slot getResultSlot() {
      return this.resultSlot;
   }

   public void m_6199_(Container pContainer) {
      super.m_6199_(pContainer);
      this.setupResultSlot();
   }

   public void setSelectedSpell(int index) {
      this.selectedSpellIndex = index;
      this.setupResultSlot();
   }

   public void doInscription(int selectedIndex) {
      ItemStack spellBookItemStack = this.getSpellBookSlot().m_7993_();
      ItemStack scrollItemStack = this.getScrollSlot().m_7993_();
      if (spellBookItemStack.m_41720_() instanceof SpellBook && scrollItemStack.m_41720_() instanceof Scroll) {
         ISpellContainer bookContainer = ISpellContainer.get(spellBookItemStack);
         ISpellContainer scrollContainer = ISpellContainer.get(scrollItemStack);
         SpellData scrollSlot = scrollContainer.getSpellAtIndex(0);
         ISpellContainerMutable mutableBookContainer = bookContainer.mutableCopy();
         if (mutableBookContainer.addSpellAtIndex(scrollSlot.getSpell(), scrollSlot.getLevel(), selectedIndex, false)) {
            this.getScrollSlot().m_6201_(1);
            ISpellContainer.set(spellBookItemStack, mutableBookContainer.toImmutable());
         }
      }
   }

   public boolean m_6366_(Player pPlayer, int pId) {
      if (pId < 0) {
         ItemStack scrollStack = this.getScrollSlot().m_7993_();
         if (this.selectedSpellIndex >= 0 && scrollStack.m_41720_() instanceof Scroll scroll) {
            SpellData spellData = ISpellContainer.get(scrollStack).getSpellAtIndex(0);
            if (MinecraftForge.EVENT_BUS.post(new InscribeSpellEvent(pPlayer, spellData))) {
               return false;
            }

            this.doInscription(this.selectedSpellIndex);
         }
      } else {
         this.setSelectedSpell(pId);
      }

      return true;
   }

   private void setupResultSlot() {
      ItemStack resultStack = ItemStack.f_41583_;
      ItemStack spellBookStack = this.spellBookSlot.m_7993_();
      if (spellBookStack.m_41720_() instanceof SpellBook) {
         ISpellContainer spellList = ISpellContainer.get(spellBookStack);
         if (this.selectedSpellIndex >= 0) {
            SpellData spellData = spellList.getSpellAtIndex(this.selectedSpellIndex);
            if (spellData != SpellData.EMPTY && spellData.canRemove()) {
               resultStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
               resultStack.m_41764_(1);
               ISpellContainer.createScrollContainer(spellData.getSpell(), spellData.getLevel(), resultStack);
            }
         }
      }

      if (!ItemStack.m_41728_(resultStack, this.resultSlot.m_7993_())) {
         this.resultSlot.m_5852_(resultStack);
      }
   }

   public ItemStack m_7648_(Player playerIn, int index) {
      Slot sourceSlot = (Slot)this.f_38839_.get(index);
      if (sourceSlot != null && sourceSlot.m_6657_()) {
         ItemStack sourceStack = sourceSlot.m_7993_();
         ItemStack copyOfSourceStack = sourceStack.m_41777_();
         if (index < 36) {
            if (!this.m_38903_(sourceStack, 36, 39, false)) {
               return ItemStack.f_41583_;
            }
         } else {
            if (index >= 39) {
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

   public boolean m_6875_(Player pPlayer) {
      return (Boolean)this.access
         .m_39299_(
            (level, blockPos) -> !level.m_8055_(blockPos).m_60713_((Block)BlockRegistry.INSCRIPTION_TABLE_BLOCK.get())
               ? false
               : pPlayer.m_20275_(blockPos.m_123341_() + 0.5, blockPos.m_123342_() + 0.5, blockPos.m_123343_() + 0.5) <= 64.0,
            true
         );
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.m_38897_(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         this.m_38897_(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
   }

   public void m_6877_(Player pPlayer) {
      if (pPlayer instanceof ServerPlayer) {
         super.m_6877_(pPlayer);
         this.access.m_39292_((p_39796_, p_39797_) -> {
            this.m_150411_(pPlayer, this.scrollContainer);
            if (this.fromCurioSlot) {
               Utils.setPlayerSpellbookStack(pPlayer, this.spellBookSlot.m_6201_(1));
            } else {
               this.m_150411_(pPlayer, this.spellbookContainer);
            }
         });
      }
   }
}
