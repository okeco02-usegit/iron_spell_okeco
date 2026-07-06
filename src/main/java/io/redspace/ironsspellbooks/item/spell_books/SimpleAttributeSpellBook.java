package io.redspace.ironsspellbooks.item.spell_books;

import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item.Properties;

@Deprecated(forRemoval = true)
public class SimpleAttributeSpellBook extends SpellBook {
   public SimpleAttributeSpellBook(int spellSlots, SpellRarity rarity, Attribute attribute, double value) {
      super(spellSlots);
      this.withAttribute(BuiltInRegistries.f_256951_.m_263177_(attribute), value);
   }

   public SimpleAttributeSpellBook(int spellSlots, SpellRarity rarity, Attribute attribute, double value, double mana) {
      super(spellSlots);
      this.withSpellbookAttributes(
         new AttributeContainer(() -> attribute, value, Operation.MULTIPLY_BASE), new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADDITION)
      );
   }

   public SimpleAttributeSpellBook(int spellSlots, SpellRarity rarity, Multimap<Attribute, AttributeModifier> defaultModifiers) {
      super(spellSlots);
      AttributeContainer[] ary = defaultModifiers.entries()
         .stream()
         .map(
            entry -> new AttributeContainer(entry::getKey, ((AttributeModifier)entry.getValue()).m_22218_(), ((AttributeModifier)entry.getValue()).m_22217_())
         )
         .toArray(AttributeContainer[]::new);
      this.withSpellbookAttributes(ary);
   }

   public SimpleAttributeSpellBook(int spellSlots, SpellRarity rarity, Multimap<Attribute, AttributeModifier> defaultModifiers, Properties properties) {
      super(spellSlots, properties);
      AttributeContainer[] ary = defaultModifiers.entries()
         .stream()
         .map(
            entry -> new AttributeContainer(entry::getKey, ((AttributeModifier)entry.getValue()).m_22218_(), ((AttributeModifier)entry.getValue()).m_22217_())
         )
         .toArray(AttributeContainer[]::new);
      this.withSpellbookAttributes(ary);
   }
}
