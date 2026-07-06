package io.redspace.ironsspellbooks.api.spells;

public record SpellSlot(SpellData spellData, int index) {
   public AbstractSpell getSpell() {
      return this.spellData.getSpell();
   }

   public int getLevel() {
      return this.spellData.getLevel();
   }

   public boolean isLocked() {
      return this.spellData.isLocked();
   }

   public static SpellSlot of(SpellData data, int index) {
      return new SpellSlot(data, index);
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof SpellSlot o && o.spellData.equals(this.spellData) && o.index == this.index;
   }

   @Override
   public int hashCode() {
      return this.spellData.hashCode() * 31 + this.index;
   }
}
