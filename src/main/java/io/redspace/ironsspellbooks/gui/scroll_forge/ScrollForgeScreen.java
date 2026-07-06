package io.redspace.ironsspellbooks.gui.scroll_forge;

import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.network.ScrollForgeSelectSpellPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ScrollForgeScreen extends AbstractContainerScreen<ScrollForgeMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/gui/scroll_forge.png");
   private static final int SPELL_LIST_X = 89;
   private static final int SPELL_LIST_Y = 15;
   private static final int SCROLL_BAR_X = 199;
   private static final int SCROLL_BAR_Y = 15;
   private static final int SCROLL_BAR_WIDTH = 12;
   private static final int SCROLL_BAR_HEIGHT = 56;
   public static final ResourceLocation RUNIC_FONT = ResourceLocation.withDefaultNamespace("illageralt");
   public static final ResourceLocation ENCHANT_FONT = ResourceLocation.withDefaultNamespace("alt");
   private List<ScrollForgeScreen.SpellCardInfo> availableSpells;
   private ItemStack[] oldMenuSlots = new ItemStack[]{ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_};
   private AbstractSpell selectedSpell = SpellRegistry.none();
   private int scrollOffset;
   private boolean isScrollbarHeld;

   public ScrollForgeScreen(ScrollForgeMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.f_97726_ = 218;
      this.f_97727_ = 166;
   }

   protected void m_7856_() {
      this.availableSpells = new ArrayList<>();
      this.generateSpellList();
      super.m_7856_();
   }

   public void m_7379_() {
      this.setSelectedSpell(SpellRegistry.none());
      this.resetList();
      super.m_7379_();
   }

   private void resetList() {
      if (((ScrollForgeMenu)this.f_97732_).getInkSlot().m_7993_().m_41619_()
         || !(
            ((ScrollForgeMenu)this.f_97732_).getInkSlot().m_7993_().m_41720_() instanceof InkItem inkItem
               && inkItem.getRarity().compareRarity(SpellRarity.values()[this.selectedSpell.getMinRarity()]) >= 0
         )) {
         this.setSelectedSpell(SpellRegistry.none());
      }

      this.scrollOffset = 0;

      for (ScrollForgeScreen.SpellCardInfo s : this.availableSpells) {
         this.m_169411_(s.button);
      }

      this.availableSpells.clear();
   }

   public void m_88315_(GuiGraphics guiHelper, int mouseX, int mouseY, float delta) {
      this.m_280273_(guiHelper);
      super.m_88315_(guiHelper, mouseX, mouseY, delta);
      this.m_280072_(guiHelper, mouseX, mouseY);
   }

   protected void m_7286_(GuiGraphics guiHelper, float partialTick, int mouseX, int mouseY) {
      guiHelper.m_280218_(TEXTURE, this.f_97735_, this.f_97736_, 0, 0, this.f_97726_, this.f_97727_);
      float scrollOffset = Mth.m_14036_((float)this.scrollOffset / (this.totalRowCount() - 3), 0.0F, 1.0F);
      guiHelper.m_280218_(
         TEXTURE, this.f_97735_ + 199, (int)(this.f_97736_ + 15 + scrollOffset * 41.0F), this.f_97726_ + (this.isScrollbarHeld ? 12 : 0), 0, 12, 15
      );
      if (this.menuSlotsChanged()) {
         this.generateSpellList();
      }

      this.renderSpellList(guiHelper, partialTick, mouseX, mouseY);
   }

   private boolean menuSlotsChanged() {
      if (((ScrollForgeMenu)this.f_97732_).getInkSlot().m_7993_().m_41720_() == this.oldMenuSlots[0].m_41720_()
         && ((ScrollForgeMenu)this.f_97732_).getFocusSlot().m_7993_().m_41720_() == this.oldMenuSlots[2].m_41720_()) {
         return false;
      }

      this.oldMenuSlots = new ItemStack[]{
         ((ScrollForgeMenu)this.f_97732_).getInkSlot().m_7993_(),
         ((ScrollForgeMenu)this.f_97732_).getBlankScrollSlot().m_7993_(),
         ((ScrollForgeMenu)this.f_97732_).getFocusSlot().m_7993_()
      };
      return true;
   }

   private void renderSpellList(GuiGraphics guiHelper, float partialTick, int mouseX, int mouseY) {
      ItemStack inkStack = ((ScrollForgeMenu)this.f_97732_).getInkSlot().m_7993_();
      SpellRarity inkRarity = this.getRarityFromInk(inkStack.m_41720_());
      this.availableSpells
         .sort(
            (a, b) -> SpellConfigManager.getSpellConfigValue(a.spell, SpellConfigParameter.MIN_RARITY)
               .compareRarity(SpellConfigManager.getSpellConfigValue(b.spell, SpellConfigParameter.MIN_RARITY))
         );
      List<FormattedCharSequence> additionalTooltip = null;

      for (int i = 0; i < this.availableSpells.size(); i++) {
         ScrollForgeScreen.SpellCardInfo spellCard = this.availableSpells.get(i);
         if (i - this.scrollOffset >= 0 && i - this.scrollOffset < 3) {
            if (inkRarity == null || spellCard.spell.getMinRarity() > inkRarity.getValue()) {
               spellCard.activityState = ScrollForgeScreen.SpellCardInfo.ActivityState.INK_ERROR;
            } else if (this.f_96541_ != null && !spellCard.spell.canBeCraftedBy(this.f_96541_.f_91074_)) {
               spellCard.activityState = ScrollForgeScreen.SpellCardInfo.ActivityState.UNLEARNED_ERROR;
            } else {
               spellCard.activityState = ScrollForgeScreen.SpellCardInfo.ActivityState.ENABLED;
            }

            int x = this.f_97735_ + 89;
            int y = this.f_97736_ + 15 + (i - this.scrollOffset) * 19;
            spellCard.button.m_252865_(x);
            spellCard.button.m_253211_(y);
            spellCard.draw(this, guiHelper, x, y, mouseX, mouseY);
            if (additionalTooltip == null) {
               additionalTooltip = spellCard.getTooltip(x, y, mouseX, mouseY);
            }
         } else {
            spellCard.activityState = ScrollForgeScreen.SpellCardInfo.ActivityState.DISABLED;
         }

         spellCard.button.f_93623_ = spellCard.activityState == ScrollForgeScreen.SpellCardInfo.ActivityState.ENABLED;
      }

      if (additionalTooltip != null) {
         guiHelper.m_280245_(this.f_96547_, additionalTooltip, mouseX, mouseY);
      }
   }

   public boolean m_6050_(double pMouseX, double pMouseY, double pScrollY) {
      int length = this.availableSpells.size();
      int newScroll = this.scrollOffset - (int)pScrollY;
      if (newScroll <= length - 3 && newScroll >= 0) {
         this.scrollOffset -= (int)pScrollY;
         return true;
      } else {
         return false;
      }
   }

   public void generateSpellList() {
      this.resetList();
      ItemStack focusStack = ((ScrollForgeMenu)this.f_97732_).getFocusSlot().m_7993_();
      if (!focusStack.m_41619_() && focusStack.m_204117_(ModTags.SCHOOL_FOCUS)) {
         List<AbstractSpell> spells = SchoolRegistry.getSchoolsFromFocus(focusStack)
            .stream()
            .flatMap(school -> SpellRegistry.getSpellsForSchool(school).stream())
            .filter(AbstractSpell::allowCrafting)
            .toList();

         for (int i = 0; i < spells.size(); i++) {
            int tempIndex = i;
            if (spells.get(i).isEnabled() && this.f_96541_ != null) {
               this.availableSpells
                  .add(
                     new ScrollForgeScreen.SpellCardInfo(
                        spells.get(i),
                        i + 1,
                        i,
                        (Button)this.m_7787_(
                           new Builder(spells.get(i).getDisplayName(this.f_96541_.f_91074_), b -> this.setSelectedSpell(spells.get(tempIndex)))
                              .m_252794_(0, 0)
                              .m_253046_(108, 19)
                              .m_253136_()
                        )
                     )
                  );
            }
         }
      }
   }

   private void setSelectedSpell(AbstractSpell spell) {
      this.selectedSpell = spell;
      PacketDistributor.sendToServer(new ScrollForgeSelectSpellPacket(((ScrollForgeMenu)this.f_97732_).blockEntity.m_58899_(), spell.getSpellId()));
   }

   private SpellRarity getRarityFromInk(Item ink) {
      return ink instanceof InkItem inkItem ? inkItem.getRarity() : null;
   }

   public AbstractSpell getSelectedSpell() {
      return this.selectedSpell;
   }

   public boolean m_6375_(double pMouseX, double pMouseY, int pButton) {
      this.isScrollbarHeld = this.m_6774_(199, 15, 12, 56, pMouseX, pMouseY);
      return super.m_6375_(pMouseX, pMouseY, pButton);
   }

   public boolean m_6348_(double pMouseX, double pMouseY, int pButton) {
      this.isScrollbarHeld = false;
      return super.m_6348_(pMouseX, pMouseY, pButton);
   }

   public boolean m_7979_(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      int i = this.totalRowCount() - 3;
      if (this.isScrollbarHeld) {
         int j = this.f_97736_ + 15;
         int k = j + 56;
         float scrollOffs = ((float)pMouseY - j - 7.5F) / (k - j - 15.0F);
         scrollOffs = Mth.m_14036_(scrollOffs, 0.0F, 1.0F);
         this.scrollOffset = Math.max((int)(scrollOffs * i + 0.5), 0);
         return true;
      } else {
         return super.m_7979_(pMouseX, pMouseY, pButton, pDragX, pDragY);
      }
   }

   private int totalRowCount() {
      return this.availableSpells.size();
   }

   private class SpellCardInfo {
      ScrollForgeScreen.SpellCardInfo.ActivityState activityState = ScrollForgeScreen.SpellCardInfo.ActivityState.DISABLED;
      AbstractSpell spell;
      int spellLevel;
      SpellRarity rarity;
      Button button;
      int index;

      SpellCardInfo(AbstractSpell spell, int spellLevel, int index, Button button) {
         this.spell = spell;
         this.spellLevel = spellLevel;
         this.index = index;
         this.button = button;
         this.rarity = spell.getRarity(spellLevel);
      }

      void draw(ScrollForgeScreen screen, GuiGraphics guiHelper, int x, int y, int mouseX, int mouseY) {
         if (this.activityState != ScrollForgeScreen.SpellCardInfo.ActivityState.ENABLED
            && this.activityState != ScrollForgeScreen.SpellCardInfo.ActivityState.UNLEARNED_ERROR) {
            guiHelper.m_280218_(ScrollForgeScreen.TEXTURE, x, y, 0, 185, 108, 19);
         } else if (this.spell == screen.getSelectedSpell()) {
            guiHelper.m_280218_(ScrollForgeScreen.TEXTURE, x, y, 0, 204, 108, 19);
         } else {
            guiHelper.m_280218_(ScrollForgeScreen.TEXTURE, x, y, 0, 166, 108, 19);
         }

         ResourceLocation texture = this.activityState == ScrollForgeScreen.SpellCardInfo.ActivityState.ENABLED
            ? this.spell.getSpellIconResource()
            : SpellRegistry.none().getSpellIconResource();
         guiHelper.m_280163_(texture, x + 108 - 18, y + 1, 0.0F, 0.0F, 16, 16, 16, 16);
         int maxWidth = 88;
         FormattedText text = this.trimText(
            ScrollForgeScreen.this.f_96547_,
            this.getDisplayName()
               .m_130948_(
                  this.activityState == ScrollForgeScreen.SpellCardInfo.ActivityState.ENABLED
                     ? Style.f_131099_
                     : Style.f_131099_.m_131150_(ScrollForgeScreen.RUNIC_FONT)
               ),
            maxWidth
         );
         int textX = x + 2;
         int textY = y + 3;
         guiHelper.m_280554_(ScrollForgeScreen.this.f_96547_, text, textX, textY, maxWidth, 16777215);
      }

      @Nullable
      List<FormattedCharSequence> getTooltip(int x, int y, int mouseX, int mouseY) {
         MutableComponent text = this.getDisplayName();
         int textX = x + 2;
         int textY = y + 3;
         return mouseX >= textX && mouseY >= textY && mouseX < textX + ScrollForgeScreen.this.f_96547_.m_92852_(text) && mouseY < textY + 9
            ? this.getHoverText()
            : null;
      }

      List<FormattedCharSequence> getHoverText() {
         if (this.activityState == ScrollForgeScreen.SpellCardInfo.ActivityState.INK_ERROR) {
            return List.of(FormattedCharSequence.m_13714_(Component.m_237115_("ui.irons_spellbooks.ink_rarity_error").getString(), Style.f_131099_));
         } else {
            return this.activityState == ScrollForgeScreen.SpellCardInfo.ActivityState.UNLEARNED_ERROR
               ? List.of(FormattedCharSequence.m_13714_(this.spell.getLockedMessage().getString(), this.spell.getLockedMessage().m_7383_()))
               : TooltipsUtils.createSpellDescriptionTooltip(this.spell, ScrollForgeScreen.this.f_96547_);
         }
      }

      private FormattedText trimText(Font font, Component component, int maxWidth) {
         FormattedText text = (FormattedText)font.m_92865_().m_92414_(component, maxWidth, component.m_7383_()).get(0);
         if (text.getString().length() < component.getString().length()) {
            text = FormattedText.m_130773_(new FormattedText[]{text, FormattedText.m_130775_("...")});
         }

         return text;
      }

      MutableComponent getDisplayName() {
         return this.spell.getDisplayName(ScrollForgeScreen.this.f_96541_.f_91074_);
      }

      enum ActivityState {
         DISABLED,
         ENABLED,
         INK_ERROR,
         UNLEARNED_ERROR;
      }
   }
}
