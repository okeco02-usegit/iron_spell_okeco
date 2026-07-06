package io.redspace.ironsspellbooks.item;

import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class UniqueSpellBook extends SpellBook implements UniqueItem {
   List<SpellData> spellData = null;
   SpellDataRegistryHolder[] spellDataRegistryHolders;

   public UniqueSpellBook(SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(spellDataRegistryHolders.length);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   public UniqueSpellBook(SpellDataRegistryHolder[] spellDataRegistryHolders, int additionalSlots) {
      super(spellDataRegistryHolders.length + additionalSlots);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   @Deprecated(forRemoval = true)
   public UniqueSpellBook(
      SpellRarity rarity,
      SpellDataRegistryHolder[] spellDataRegistryHolders,
      int additionalSlots,
      Supplier<Multimap<Attribute, AttributeModifier>> defaultModifiers
   ) {
      this(spellDataRegistryHolders, additionalSlots);
      AttributeContainer[] ary = defaultModifiers.get()
         .entries()
         .stream()
         .map(
            entry -> new AttributeContainer(entry::getKey, ((AttributeModifier)entry.getValue()).m_22218_(), ((AttributeModifier)entry.getValue()).m_22217_())
         )
         .toArray(AttributeContainer[]::new);
      this.withSpellbookAttributes(ary);
   }

   public List<SpellData> getSpells() {
      if (this.spellData == null) {
         this.spellData = Arrays.stream(this.spellDataRegistryHolders).map(SpellDataRegistryHolder::getSpellData).toList();
         this.spellDataRegistryHolders = null;
      }

      return this.spellData;
   }

   public Component m_7626_(ItemStack stack) {
      return (Component)(ISpellContainer.isSpellContainer(stack) && ISpellContainer.get(stack).isImproved()
         ? Component.m_237110_("tooltip.irons_spellbooks.improved_format", new Object[]{super.m_7626_(stack)})
         : super.m_7626_(stack));
   }

   @Override
   public boolean isUnique() {
      return true;
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainerMutable spellContainer = ISpellContainer.create(this.getMaxSpellSlots(), true, true).mutableCopy();
            this.getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), true));
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
         }
      }
   }
}
