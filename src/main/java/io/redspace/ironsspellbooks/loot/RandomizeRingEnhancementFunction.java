package io.redspace.ironsspellbooks.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.curios.AffinityRing;
import io.redspace.ironsspellbooks.registries.LootRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RandomizeRingEnhancementFunction extends LootItemConditionalFunction {
   final SpellFilter spellFilter;

   protected RandomizeRingEnhancementFunction(LootItemCondition[] lootConditions, SpellFilter spellFilter) {
      super(lootConditions);
      this.spellFilter = spellFilter;
   }

   protected ItemStack m_7372_(ItemStack itemStack, LootContext lootContext) {
      if (itemStack.m_41720_() instanceof AffinityRing) {
         AbstractSpell spell = this.spellFilter.getRandomSpell(lootContext.m_230907_(), s -> s.isEnabled() && s.getMaxLevel() > 1);
         AffinityData.setAffinityData(itemStack, spell);
         return spell == SpellRegistry.none() ? ItemStack.f_41583_ : itemStack;
      } else {
         return itemStack;
      }
   }

   public LootItemFunctionType m_7162_() {
      return (LootItemFunctionType)LootRegistry.RANDOMIZE_SPELL_RING_FUNCTION.get();
   }

   public static class Serializer
      extends net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Serializer<RandomizeRingEnhancementFunction> {
      public void serialize(JsonObject json, RandomizeRingEnhancementFunction scrollFunction, JsonSerializationContext jsonDeserializationContext) {
         super.m_6170_(json, scrollFunction, jsonDeserializationContext);
         scrollFunction.spellFilter.serialize(json);
      }

      public RandomizeRingEnhancementFunction deserialize(
         JsonObject json, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions
      ) {
         SpellFilter applicableSpells = SpellFilter.deserializeSpellFilter(json);
         return new RandomizeRingEnhancementFunction(lootConditions, applicableSpells);
      }
   }
}
