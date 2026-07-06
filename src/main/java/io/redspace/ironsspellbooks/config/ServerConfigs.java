package io.redspace.ironsspellbooks.config;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ServerConfigs {
   private static final Builder BUILDER = new Builder();
   public static final ForgeConfigSpec SPEC = BUILDER.build();
   public static final ServerConfigs.SpellConfigParameters DEFAULT_CONFIG = new ServerConfigs.SpellConfigParameters(
      null, () -> true, SchoolRegistry.EVOCATION_RESOURCE::toString, () -> 10, () -> SpellRarity.COMMON, () -> 1.0, () -> 1.0, () -> 10.0, () -> true
   );
   public static final ConfigValue<Boolean> SWORDS_CONSUME_MANA = BUILDER.worldRestart().define("swordsConsumeMana", true);
   public static final ConfigValue<Double> SWORDS_CD_MULTIPLIER = BUILDER.worldRestart().define("swordsCooldownMultiplier", 0.5);
   public static final ConfigValue<Boolean> CAN_ATTACK_OWN_SUMMONS = BUILDER.worldRestart().define("canAttackOwnSummons", false);
   public static final ConfigValue<Integer> MAX_UPGRADES = BUILDER.worldRestart().define("maxUpgrades", 3);
   public static final ConfigValue<Double> MANA_SPAWN_PERCENT = BUILDER.worldRestart().define("manaSpawnPercent", 0.0);
   public static final ConfigValue<Double> SCROLL_RECYCLE_CHANCE = BUILDER.worldRestart().define("scrollRecycleChance", 0.5);
   public static final ConfigValue<Boolean> SCROLL_MERGING = BUILDER.define("scrollMerging", true);
   private static final ConfigValue<List<? extends String>> UPGRADE_WHITELIST = BUILDER.defineListAllowEmpty("upgradeWhitelist", ArrayList::new, string -> true);
   private static final ConfigValue<List<? extends String>> UPGRADE_BLACKLIST = BUILDER.defineListAllowEmpty("upgradeBlacklist", ArrayList::new, string -> true);
   private static final ConfigValue<List<? extends String>> IMBUE_WHITELIST = BUILDER.defineListAllowEmpty("imbueWhitelist", ArrayList::new, string -> true);
   private static final ConfigValue<List<? extends String>> IMBUE_BLACKLIST = BUILDER.defineListAllowEmpty("imbueBlacklist", ArrayList::new, string -> true);
   public static final ConfigValue<Integer> PRIEST_TOWER_SPAWNRATE = BUILDER.comment("The weight of the priest house spawning in a village. Default: 4")
      .define("priestHouseWeight", 4);
   public static final ConfigValue<Boolean> AQUIFER_DETECTION = BUILDER.comment(
         "Whether to prevent aquifers from intersecting designated underground structures. May affect performance. Default: true"
      )
      .define("aquiferDetection", true);
   public static final ConfigValue<Boolean> ALLOW_CAULDRON_BREWING = BUILDER.worldRestart().define("allowCauldronBrewing", true);
   public static final ConfigValue<Boolean> FURLED_MAPS_SKIP_CHUNKS = BUILDER.worldRestart().define("furledMapSkipsExistingChunks", true);
   public static final ConfigValue<Boolean> APPLY_ALL_MULTIHAND_ATTRIBUTES = BUILDER.worldRestart().define("applyAllMultihandAttributes", true);
   public static final ConfigValue<Boolean> BETTER_CREEPER_THUNDERHIT = BUILDER.worldRestart().define("betterCreeperThunderHit", true);
   public static final ConfigValue<Boolean> SPELL_GREIFING = BUILDER.worldRestart().define("spellGriefing", false);
   public static final ConfigValue<Boolean> ADDITIONAL_WANDERING_TRADER_TRADES = BUILDER.worldRestart().define("additionalWanderingTraderTrades", true);
   public static final ConfigValue<Boolean> DISABLE_ADVENTURE_MODE_CASTING = BUILDER.worldRestart().define("disableAdventureModeCasting", false);
   public static final ConfigValue<Boolean> HOGLIN_OFFSPRING_PROTECTION = BUILDER.worldRestart().define("hoglinOffspringProtection", true);
   public static final ConfigValue<Double> MANA_REGEN_MULTIPLIER = BUILDER.worldRestart().define("manaRegenMultiplier", 1.0);
   public static final ConfigValue<Boolean> CREATIVE_MANA_COST = BUILDER.worldRestart().define("creativeMana", false);
   public static final ConfigValue<Boolean> CREATIVE_COOLDOWN = BUILDER.worldRestart().define("creativeCooldowns", false);
   public static final ConfigValue<Boolean> ICE_SPIDER_PATROLS = BUILDER.comment(
         "Whether to enabled Ice Spider patrols in snowy biomes during snowy weather. Default: true"
      )
      .define("iceSpiderPatrols", true);
   public static final ConfigValue<Boolean> PORTAL_FRAME_RESTRICT_DYE = BUILDER.comment(
         "Whether Portal Frames can only be dyed by the block's owner. Default: true"
      )
      .define("portalFrameRestrictDye", true);
   public static final ConfigValue<Boolean> PORTAL_FRAME_RESTRICT_BREAKING = BUILDER.comment(
         "Whether Portal Frames can only be destroyed by the block's owner. Default: false"
      )
      .define("portalFrameRestrictBreaking", false);
   public static final ConfigValue<Double> TYROS_ADDITIONAL_HEALTH = BUILDER.comment("Additional Health").define("additionalHealth", 0.0);
   public static final ConfigValue<Double> TYROS_ADDITIONAL_ATTACK_DAMAGE = BUILDER.comment("Additional Melee Attack Damage")
      .define("additionalAttackDamage", 0.0);
   public static final ConfigValue<Double> TYROS_ADDITIONAL_SPELL_POWER = BUILDER.comment("Additional Spell Power (additive percent)")
      .define("additionalSpellPower", 0.0);
   public static final ConfigValue<List<? extends Double>> RARITY_CONFIG = BUILDER.worldRestart()
      .comment("Defines percentage brackets of spell level to corresponding rarity, ie first 30% of spell levels are common.")
      .comment(
         String.format(
            "rarityConfig array values must sum to 1: [%s, %s, %s, %s, %s]. Default: [.3d, .25d, .2d, .15d, .1d]",
            SpellRarity.COMMON,
            SpellRarity.UNCOMMON,
            SpellRarity.RARE,
            SpellRarity.EPIC,
            SpellRarity.LEGENDARY
         )
      )
      .defineList("rarityConfig", List.of(0.3, 0.25, 0.2, 0.15, 0.1), x -> true);
   public static final Set<Item> UPGRADE_WHITELIST_ITEMS = new HashSet<>();
   public static final Set<Item> UPGRADE_BLACKLIST_ITEMS = new HashSet<>();
   public static final Set<Item> IMBUE_WHITELIST_ITEMS = new HashSet<>();
   public static final Set<Item> IMBUE_BLACKLIST_ITEMS = new HashSet<>();
   private static final Map<String, ServerConfigs.SpellConfigParameters> SPELL_CONFIGS = new HashMap<>();

   @Deprecated(forRemoval = true)
   public static ServerConfigs.SpellConfigParameters getSpellConfig(AbstractSpell abstractSpell) {
      IronsSpellbooks.LOGGER.warn("Spell {} attempting to lookup raw config values, may be reading incorrect data", abstractSpell.getSpellId());
      return SPELL_CONFIGS.getOrDefault(abstractSpell.getSpellId(), DEFAULT_CONFIG);
   }

   @Deprecated(forRemoval = true)
   public static Map<String, ServerConfigs.SpellConfigParameters> getSpellConfigs() {
      return SPELL_CONFIGS;
   }

   public static void onConfigReload() {
      IronsSpellbooks.LOGGER.debug("ServerConfigs load item blacklists:");
      cacheItemList((List<? extends String>)UPGRADE_WHITELIST.get(), UPGRADE_WHITELIST_ITEMS);
      cacheItemList((List<? extends String>)UPGRADE_BLACKLIST.get(), UPGRADE_BLACKLIST_ITEMS);
      cacheItemList((List<? extends String>)IMBUE_WHITELIST.get(), IMBUE_WHITELIST_ITEMS);
      cacheItemList((List<? extends String>)IMBUE_BLACKLIST.get(), IMBUE_BLACKLIST_ITEMS);
   }

   private static void cacheItemList(List<? extends String> ids, Set<Item> output) {
      output.clear();

      for (String name : ids) {
         try {
            if (name.startsWith("#")) {
               TagKey<Item> tag = new TagKey(Registries.f_256913_, ResourceLocation.parse(name.substring(1)));
               output.addAll(BuiltInRegistries.f_257033_.m_123024_().filter(item -> item.m_204114_().m_203656_(tag)).toList());
            } else {
               Item item = (Item)BuiltInRegistries.f_257033_.m_7745_(ResourceLocation.parse(name));
               if (item != null) {
                  output.add(item);
               } else {
                  IronsSpellbooks.LOGGER.warn("Unable to add item to config, no such item id: {}", name);
               }
            }
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.warn("Unable to validate item config: {}", e.getMessage());
         }
      }
   }

   @Deprecated(forRemoval = true)
   private static void createSpellConfig(AbstractSpell spell) {
      DefaultConfig config = spell.getDefaultConfig();
      SPELL_CONFIGS.put(
         spell.getSpellId(),
         new ServerConfigs.SpellConfigParameters(
            config,
            () -> config.enabled,
            () -> config.schoolResource.toString(),
            () -> config.maxLevel,
            () -> config.minRarity,
            () -> 1.0,
            () -> 1.0,
            () -> config.cooldownInSeconds,
            () -> config.allowCrafting
         )
      );
   }

   private static String createSpellConfigTitle(String str) {
      String[] words = str.split("[_| ]");

      for (int i = 0; i < words.length; i++) {
         words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
      }

      return Arrays.stream(words).sequential().collect(Collectors.joining("-"));
   }

   static {
      BUILDER.comment("Other Configuration");
      BUILDER.push("Blocks");
      BUILDER.pop();
      BUILDER.push("Misc");
      BUILDER.comment("Whether or not imbued weapons require mana to be casted. Default: true");
      BUILDER.comment("The multiplier on the cooldown of imbued weapons. Default: 0.5 (50% of default cooldown)");
      BUILDER.comment("Whether or not players can harm their own magic summons. Default: false");
      BUILDER.comment("The maximum amount of times an applicable piece of equipment can be upgraded in the arcane anvil. Default: 3");
      BUILDER.comment("From 0-1, the percent of max mana a player respawns with. Default: 0.0");
      BUILDER.comment("From 0-1, the percent chance for scrolls to be successfully recycled. Default: 0.5 (50%)");
      BUILDER.comment("Whether or not potions should be allowed to be brewed in the alchemist cauldron)");
      BUILDER.comment(
         "Whether or not Furled Map items should skip chunks when searching for structures (only find new structures). Can impact performance while searching. Default: true"
      );
      BUILDER.comment("Whether or not casting items should apply all attributes while in the offhand, or just magic related ones. Default: true");
      BUILDER.comment("Whether or not creepers should be healed and become fire immune when struck by lightning. Default: true");
      BUILDER.comment("Whether or not spells such as Fireball or Fire Breath should destroy terrain or create fire. Default: false");
      BUILDER.comment("Whether or not the wandering trader can have magic related trades, such as ink or scrolls. Default: true");
      BUILDER.comment("Whether casting spells should be disabled in adventure mode. Default: false");
      BUILDER.comment("Whether hoglins have the ability to pass overworld zombification immunity to their offspring. Default: true");
      BUILDER.comment("Global multiplier to all players' mana regeneration. Default: 1.0");
      BUILDER.comment("Whether merging scrolls with ink to upgrade them in the Arcane Anvil is enabled.");
      BUILDER.comment("Whether mana is required in creative mode. Default: false");
      BUILDER.comment("Whether cooldowns are respected in creative mode. Default: false");
      BUILDER.pop();
      BUILDER.push("Upgrade Overrides");
      BUILDER.comment("Use these lists to change what items can interact with the Arcane Anvil's upgrade system. This can also be done via datapack.");
      BUILDER.comment("Upgrade Whitelist. Use an item's id to allow it to be upgraded, ex: \"minecraft:iron_sword\"");
      BUILDER.comment("Upgrade Blacklist. Use an item's id to prevent it from being upgraded, ex: \"minecraft:iron_sword\"");
      BUILDER.pop();
      BUILDER.push("Imbue Overrides");
      BUILDER.comment("Use these lists to change what items can interact with the Arcane Anvil's imbue system.");
      BUILDER.comment("/!\\ Unsupported item types are not guaranteed to work out of the box.");
      BUILDER.comment("Imbue Whitelist. Use an item's id to allow it to be imbued, ex: \"minecraft:iron_sword\"");
      BUILDER.comment("Imbue Blacklist. Use an item's id to prevent it from being imbued, ex: \"minecraft:iron_sword\"");
      BUILDER.pop();
      BUILDER.push("Worldgen");
      BUILDER.pop();
      BUILDER.push("Boss Config");
      BUILDER.comment("Configure Boss Stats");
      BUILDER.push("Tyros");
      BUILDER.pop();
      BUILDER.pop();
   }

   @Deprecated(forRemoval = true)
   public static class SpellConfigParameters {
      final Supplier<Boolean> ENABLED;
      final Supplier<String> SCHOOL;
      final Supplier<SchoolType> ACTUAL_SCHOOL;
      final Supplier<Integer> MAX_LEVEL;
      final Supplier<SpellRarity> MIN_RARITY;
      final Supplier<Double> M_MULT;
      final Supplier<Double> P_MULT;
      final Supplier<Double> CS;
      final Supplier<Boolean> ALLOW_CRAFTING;

      SpellConfigParameters(
         DefaultConfig defaultConfig,
         Supplier<Boolean> ENABLED,
         Supplier<String> SCHOOL,
         Supplier<Integer> MAX_LEVEL,
         Supplier<SpellRarity> MIN_RARITY,
         Supplier<Double> M_MULT,
         Supplier<Double> P_MULT,
         Supplier<Double> CS,
         Supplier<Boolean> ALLOW_CRAFTING
      ) {
         this.ENABLED = ENABLED;
         this.SCHOOL = SCHOOL;
         this.MAX_LEVEL = MAX_LEVEL;
         this.MIN_RARITY = MIN_RARITY;
         this.M_MULT = M_MULT;
         this.P_MULT = P_MULT;
         this.CS = CS;
         this.ALLOW_CRAFTING = ALLOW_CRAFTING;
         this.ACTUAL_SCHOOL = () -> {
            SchoolType school = SchoolRegistry.getSchool(ResourceLocation.parse(SCHOOL.get()));
            if (school != null) {
               return school;
            }

            IronsSpellbooks.LOGGER.warn("Bad school config entry: {}. Reverting to default ({}).", SCHOOL.get(), defaultConfig.schoolResource);
            return SchoolRegistry.getSchool(defaultConfig.schoolResource);
         };
      }

      public boolean enabled() {
         return this.ENABLED.get();
      }

      public int maxLevel() {
         return this.MAX_LEVEL.get();
      }

      public SpellRarity minRarity() {
         return this.MIN_RARITY.get();
      }

      public double powerMultiplier() {
         return this.P_MULT.get();
      }

      public double manaMultiplier() {
         return this.M_MULT.get();
      }

      public int cooldownInTicks() {
         return (int)(this.CS.get() * 20.0);
      }

      public boolean allowCrafting() {
         return this.ALLOW_CRAFTING.get();
      }

      public SchoolType school() {
         return this.ACTUAL_SCHOOL.get();
      }
   }
}
