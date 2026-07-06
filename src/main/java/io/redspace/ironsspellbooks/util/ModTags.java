package io.redspace.ironsspellbooks.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
   public static final TagKey<Item> SCHOOL_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "school_focus"));
   public static final TagKey<Item> FIRE_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fire_focus"));
   public static final TagKey<Item> ICE_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_focus"));
   public static final TagKey<Item> LIGHTNING_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_focus"));
   public static final TagKey<Item> ENDER_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ender_focus"));
   public static final TagKey<Item> HOLY_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "holy_focus"));
   public static final TagKey<Item> BLOOD_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blood_focus"));
   public static final TagKey<Item> EVOCATION_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "evocation_focus"));
   public static final TagKey<Item> ELDRITCH_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_focus"));
   public static final TagKey<Item> NATURE_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "nature_focus"));
   public static final TagKey<Item> INSCRIBED_RUNES = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "inscribed_rune"));
   public static final TagKey<Item> MITHRIL_INGOT = ItemTags.create(ResourceLocation.parse("c:ingots/mithril"));
   public static final TagKey<Item> CAN_BE_UPGRADED = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "upgrade_whitelist"));
   public static final TagKey<Item> CAN_BE_IMBUED = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "imbue_whitelist"));
   public static final TagKey<Item> BASE_WIZARD_HELMET = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wizard_base_helmet"));
   public static final TagKey<Item> BASE_WIZARD_CHESTPLATE = ItemTags.create(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wizard_base_chestplate")
   );
   public static final TagKey<Item> BASE_WIZARD_LEGGINGS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wizard_base_leggings"));
   public static final TagKey<Item> BASE_WIZARD_BOOTS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wizard_base_boots"));
   public static final TagKey<Block> SPECTRAL_HAMMER_MINEABLE = BlockTags.create(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spectral_hammer_mineable")
   );
   public static final TagKey<Block> GUARDED_BY_WIZARDS = BlockTags.create(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "guarded_by_wizards"));
   public static final TagKey<Block> PREVENT_POCKET_DIMENSION_PLACEMENT = BlockTags.create(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "pocket_dimension_prevent_placement")
   );
   public static final TagKey<MobEffect> CLEANSE_IMMUNE = TagKey.m_203882_(
      Registries.f_256929_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cleanse_immune")
   );
   public static final TagKey<MobEffect> AFFECTED_BY_SPIDER_ASPECT = TagKey.m_203882_(
      Registries.f_256929_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "affected_by_spider_aspect")
   );
   public static final TagKey<Structure> WAYWARD_COMPASS_LOCATOR = TagKey.m_203882_(
      Registries.f_256944_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wayward_compass_locator")
   );
   public static final TagKey<EntityType<?>> ALWAYS_HEAL = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "always_heal")
   );
   public static final TagKey<EntityType<?>> CANT_ROOT = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cant_root")
   );
   public static final TagKey<EntityType<?>> VILLAGE_ALLIES = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "village_allies")
   );
   public static final TagKey<EntityType<?>> CANT_USE_PORTAL = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cant_use_portal")
   );
   public static final TagKey<EntityType<?>> INFERNAL_ALLIES = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "infernal_allies")
   );
   public static final TagKey<EntityType<?>> GUIDING_BOLT_IMMUNE = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "guiding_bolt_immune")
   );
   public static final TagKey<EntityType<?>> CANT_PRODUCE_BLOOD = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cant_produce_blood")
   );
   public static final TagKey<EntityType<?>> CANT_PARRY = TagKey.m_203882_(
      Registries.f_256939_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cant_parry")
   );
   public static final TagKey<Biome> ICE_SPIDER_PATROLS = TagKey.m_203882_(
      Registries.f_256952_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_spider_patrols")
   );
   public static final TagKey<Fluid> CAULDRON_FLUID_DISALLOW = TagKey.m_203882_(
      Registries.f_256808_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "alchemist_cauldron_disallow")
   );

   private static TagKey<DamageType> create(String tag) {
      return TagKey.m_203882_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", tag));
   }
}
