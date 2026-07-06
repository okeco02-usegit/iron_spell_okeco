package io.redspace.ironsspellbooks.api.config;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;

@Deprecated(forRemoval = true)
public class IronConfigParameters {
   public static final SpellConfigParameter<SchoolType> SCHOOL = SpellConfigParameter.SCHOOL;
   public static final SpellConfigParameter<SpellRarity> MIN_RARITY = SpellConfigParameter.MIN_RARITY;
   public static final SpellConfigParameter<Integer> MAX_LEVEL = SpellConfigParameter.MAX_LEVEL;
   public static final SpellConfigParameter<Boolean> ENABLED = SpellConfigParameter.ENABLED;
   public static final SpellConfigParameter<Double> COOLDOWN_IN_SECONDS = SpellConfigParameter.COOLDOWN_IN_SECONDS;
   public static final SpellConfigParameter<Boolean> ALLOW_CRAFTING = SpellConfigParameter.ALLOW_CRAFTING;
   public static final SpellConfigParameter<Double> POWER_MULTIPLIER = SpellConfigParameter.POWER_MULTIPLIER;
   public static final SpellConfigParameter<Double> MANA_MULTIPLIER = SpellConfigParameter.MANA_MULTIPLIER;
}
