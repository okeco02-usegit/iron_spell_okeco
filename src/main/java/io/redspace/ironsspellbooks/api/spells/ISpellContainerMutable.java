package io.redspace.ironsspellbooks.api.spells;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ISpellContainerMutable {
   void setMaxSpellCount(int var1);

   void setImproved(boolean var1);

   boolean addSpellAtIndex(AbstractSpell var1, int var2, int var3, boolean var4);

   boolean addSpell(AbstractSpell var1, int var2, boolean var3);

   boolean removeSpellAtIndex(int var1);

   boolean removeSpell(AbstractSpell var1);

   @NotNull
   SpellSlot[] getAllSpells();

   @NotNull
   List<SpellSlot> getActiveSpells();

   int getMaxSpellCount();

   int getActiveSpellCount();

   int getNextAvailableIndex();

   boolean mustEquip();

   boolean isImproved();

   boolean isSpellWheel();

   @NotNull
   SpellData getSpellAtIndex(int var1);

   int getIndexForSpell(AbstractSpell var1);

   boolean isEmpty();

   ISpellContainer toImmutable();
}
