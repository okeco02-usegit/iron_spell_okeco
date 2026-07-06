package io.redspace.ironsspellbooks.gui;

import java.util.List;
import net.minecraft.client.gui.screens.inventory.BookViewScreen.BookAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public record IronBookAccess(List<Component> pages) implements BookAccess {
   public int m_5732_() {
      return this.pages.size();
   }

   public FormattedText m_7303_(int pIndex) {
      return (FormattedText)this.pages.get(pIndex);
   }

   public FormattedText m_98310_(int page) {
      return page >= 0 && page < this.m_5732_() ? (FormattedText)this.pages.get(page) : FormattedText.f_130760_;
   }
}
