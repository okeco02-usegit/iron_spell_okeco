package io.redspace.ironsspellbooks.api.magic;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.gui.overlays.SpellSelection;
import io.redspace.ironsspellbooks.network.gui.SelectSpellPacket;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class SpellSelectionManager {
   public static final String MAINHAND = EquipmentSlot.MAINHAND.m_20751_();
   public static final String OFFHAND = EquipmentSlot.OFFHAND.m_20751_();
   private final List<SpellSelectionManager.SelectionOption> selectionOptionList;
   private SpellSelection spellSelection = null;
   private int selectionIndex = -1;
   private boolean selectionValid = false;
   private final Player player;

   public SpellSelectionManager(@NotNull Player player) {
      this.selectionOptionList = new ArrayList<>();
      this.player = player;
      this.init(player);
   }

   private void init(Player player) {
      if (player != null) {
         if (player.f_19853_.f_46443_) {
            this.spellSelection = ClientMagicData.getSyncedSpellData(player).getSpellSelection();
         } else {
            this.spellSelection = MagicData.getPlayerMagicData(player).getSyncedData().getSpellSelection();
         }

         this.initCurioItems(player);
         this.initItem(player.m_6844_(EquipmentSlot.HEAD), EquipmentSlot.HEAD.m_20751_());
         this.initItem(player.m_6844_(EquipmentSlot.CHEST), EquipmentSlot.CHEST.m_20751_());
         this.initItem(player.m_6844_(EquipmentSlot.LEGS), EquipmentSlot.LEGS.m_20751_());
         this.initItem(player.m_6844_(EquipmentSlot.FEET), EquipmentSlot.FEET.m_20751_());
         this.initItem(player.m_6844_(EquipmentSlot.MAINHAND), MAINHAND);
         this.initItem(player.m_6844_(EquipmentSlot.OFFHAND), OFFHAND);
         MinecraftForge.EVENT_BUS.post(new SpellSelectionManager.SpellSelectionEvent(this.player, this));
         if (!this.selectionValid && !this.selectionOptionList.isEmpty()) {
            this.tryLastSelectionOrDefault();
         }

         if (this.selectionIndex == -1 && !this.selectionOptionList.isEmpty()) {
            this.selectionIndex = 0;
         }
      }
   }

   private void initCurioItems(Player player) {
      CuriosApi.getCuriosInventory(player)
         .ifPresent(
            inv -> {
               ItemStack spellbook = Utils.getPlayerSpellbookStack(player);
               if (spellbook != null) {
                  this.initItem(spellbook, Curios.SPELLBOOK_SLOT);
               }

               inv.findCurios(ISpellContainer::isSpellContainer)
                  .stream()
                  .filter(slot -> !slot.slotContext().identifier().equals(Curios.SPELLBOOK_SLOT))
                  .forEach(
                     slotResult -> this.initItem(
                        slotResult.stack(), String.format("%s_%s", slotResult.slotContext().identifier(), slotResult.slotContext().index())
                     )
                  );
            }
         );
   }

   private int sortSpellbookSlot(SlotResult s1, SlotResult s2) {
      if (s1.slotContext().identifier().equals(Curios.SPELLBOOK_SLOT)) {
         return -1;
      } else {
         return s2.slotContext().identifier().equals(Curios.SPELLBOOK_SLOT) ? 1 : s1.slotContext().identifier().compareTo(s2.slotContext().identifier());
      }
   }

   private void initItem(@Nullable ItemStack itemStack, String equipmentSlot) {
      if (ISpellContainer.isSpellContainer(itemStack)) {
         ISpellContainer spellContainer = ISpellContainer.get(itemStack);
         if (spellContainer.isSpellWheel() && (!spellContainer.mustEquip() || !equipmentSlot.equals(MAINHAND) && !equipmentSlot.equals(OFFHAND))) {
            List<SpellSlot> activeSpells = spellContainer.getActiveSpells();

            for (int i = 0; i < activeSpells.size(); i++) {
               SpellSlot spellSlot = activeSpells.get(i);
               this.addOrMergeSelectionOption(
                  new SpellSelectionManager.SelectionOption(spellSlot.spellData(), equipmentSlot, i, this.selectionOptionList.size())
               );
               if (this.spellSelection.index == i && this.spellSelection.equipmentSlot.equals(equipmentSlot)) {
                  this.selectionIndex = this.selectionOptionList.size() - 1;
                  this.selectionValid = true;
               }
            }
         }
      }
   }

   private void addOrMergeSelectionOption(SpellSelectionManager.SelectionOption option) {
      SpellSelectionManager.SelectionOption existing = this.findExistingSpell(option.spellData.getSpell());
      if (existing != null) {
         if (option.spellData.getLevel() > existing.spellData.getLevel()) {
            option.globalIndex = existing.globalIndex;
            this.selectionOptionList.set(existing.globalIndex, option);
         }
      } else {
         this.selectionOptionList.add(option);
      }
   }

   @Nullable
   private SpellSelectionManager.SelectionOption findExistingSpell(AbstractSpell spell) {
      for (SpellSelectionManager.SelectionOption selectionOption : this.selectionOptionList) {
         if (selectionOption.spellData.getSpell().equals(spell)) {
            return selectionOption;
         }
      }

      return null;
   }

   private void tryLastSelectionOrDefault() {
      if (this.spellSelection.lastEquipmentSlot.isEmpty()) {
         Optional<SpellSelectionManager.SelectionOption> select = this.selectionOptionList.stream().findFirst();
         select.ifPresent(selection -> this.makeLocalSelection(selection.slot, selection.slotIndex, selection.globalIndex, false));
      } else if (this.spellSelection.lastIndex != -1) {
         List<SpellSelectionManager.SelectionOption> spellsForSlot = this.getSpellsForSlot(this.spellSelection.lastEquipmentSlot);
         if (!spellsForSlot.isEmpty()) {
            if (this.spellSelection.lastIndex < spellsForSlot.size()) {
               this.makeLocalSelection(
                  this.spellSelection.lastEquipmentSlot, this.spellSelection.lastIndex, spellsForSlot.get(this.spellSelection.lastIndex).globalIndex, false
               );
            } else {
               this.makeLocalSelection(this.spellSelection.lastEquipmentSlot, 0, spellsForSlot.get(0).globalIndex, false);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void makeSelection(int index) {
      if (index != this.selectionIndex && index >= 0 && index < this.selectionOptionList.size()) {
         SpellSelectionManager.SelectionOption item = this.selectionOptionList.get(index);
         this.makeLocalSelection(item.slot, item.slotIndex, index, true);
      }
   }

   private void makeLocalSelection(String slot, int slotIndex, int globalIndex, boolean doSync) {
      this.selectionIndex = globalIndex;
      this.selectionValid = true;
      if (doSync && this.player.f_19853_.f_46443_) {
         this.spellSelection.makeSelection(slot, slotIndex);
         PacketDistributor.sendToServer(new SelectSpellPacket(this.spellSelection));
      }
   }

   private void setSpellSelection(SpellSelection spellSelection) {
      this.spellSelection = spellSelection;
      if (this.player.f_19853_.f_46443_) {
         PacketDistributor.sendToServer(new SelectSpellPacket(spellSelection));
      } else {
         MagicData.getPlayerMagicData(this.player).getSyncedData().setSpellSelection(spellSelection);
      }
   }

   public SpellSelection getCurrentSelection() {
      return this.spellSelection;
   }

   @Nullable
   public SpellSelectionManager.SelectionOption getSpellSlot(int index) {
      return index >= 0 && index < this.selectionOptionList.size() ? this.selectionOptionList.get(index) : null;
   }

   public SpellData getSpellData(int index) {
      return index >= 0 && index < this.selectionOptionList.size() ? this.selectionOptionList.get(index).spellData : SpellData.EMPTY;
   }

   public int getSelectionIndex() {
      return this.selectionIndex;
   }

   public int getGlobalSelectionIndex() {
      SpellSelectionManager.SelectionOption selection = this.getSelection();
      return selection == null ? -1 : this.getSelection().globalIndex;
   }

   @Nullable
   public SpellSelectionManager.SelectionOption getSelection() {
      if (this.selectionIndex >= 0 && this.selectionIndex < this.selectionOptionList.size()) {
         return this.selectionOptionList.get(this.selectionIndex);
      } else {
         return !this.selectionOptionList.isEmpty() ? this.selectionOptionList.get(0) : null;
      }
   }

   public SpellData getSelectedSpellData() {
      return this.selectionIndex >= 0 && this.selectionIndex < this.selectionOptionList.size()
         ? this.selectionOptionList.get(this.selectionIndex).spellData
         : SpellData.EMPTY;
   }

   public List<SpellSelectionManager.SelectionOption> getSpellsForSlot(String slot) {
      return this.selectionOptionList.stream().filter(selectionOption -> selectionOption.slot.equals(slot)).toList();
   }

   public List<SpellSelectionManager.SelectionOption> getAllSpells() {
      return this.selectionOptionList;
   }

   public SpellData getSpellForSlot(String slot, int index) {
      List<SpellSelectionManager.SelectionOption> spells = this.getSpellsForSlot(slot);
      return index >= 0 && index < spells.size() ? spells.get(index).spellData : SpellData.EMPTY;
   }

   public int getSpellCount() {
      return this.selectionOptionList.size();
   }

   public static class SelectionOption {
      public SpellData spellData;
      public String slot;
      public int slotIndex;
      public int globalIndex;

      public SelectionOption(SpellData spellData, String slot, int slotIndex, int globalIndex) {
         this.spellData = spellData;
         this.slot = slot;
         this.slotIndex = slotIndex;
         this.globalIndex = globalIndex;
      }

      public CastSource getCastSource() {
         return this.slot.startsWith(Curios.SPELLBOOK_SLOT) ? CastSource.SPELLBOOK : CastSource.SWORD;
      }
   }

   public static class SpellSelectionEvent extends PlayerEvent {
      SpellSelectionManager manager;

      public SpellSelectionEvent(Player player, SpellSelectionManager manager) {
         super(player);
         this.manager = manager;
      }

      public void addSelectionOption(SpellData spellData, String slotId, int localSlotIndex) {
         this.addSelectionOption(spellData, slotId, localSlotIndex, this.manager.selectionOptionList.size());
      }

      public void addSelectionOption(SpellData spellData, String slotId, int localSlotIndex, int globalIndex) {
         globalIndex = this.manager.selectionOptionList.size();
         if (globalIndex >= 0 && globalIndex <= this.manager.selectionOptionList.size()) {
            this.manager.selectionOptionList.add(globalIndex, new SpellSelectionManager.SelectionOption(spellData, slotId, localSlotIndex, globalIndex));
            if (this.manager.spellSelection.index == localSlotIndex && this.manager.spellSelection.equipmentSlot.equals(slotId)) {
               this.manager.selectionIndex = this.manager.selectionOptionList.size() - 1;
               this.manager.selectionValid = true;
            }
         }
      }

      public SpellSelectionManager getManager() {
         return this.manager;
      }
   }
}
