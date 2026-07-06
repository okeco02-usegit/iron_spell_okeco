package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.FurledMapItem;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.MOD)
public class CreativeTabRegistry {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.f_279569_, "irons_spellbooks");
   public static final RegistryObject<CreativeModeTab> EQUIPMENT_TAB = TABS.register(
      "spellbook_equipment",
      () -> CreativeModeTab.builder()
         .m_257941_(Component.m_237115_("itemGroup.irons_spellbooks.spell_equipment_tab"))
         .m_257737_(() -> new ItemStack((ItemLike)ItemRegistry.IRON_SPELL_BOOK.get()))
         .m_257501_((enabledFeatures, entries) -> {
            entries.m_246326_((ItemLike)ItemRegistry.NETHERITE_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.DIAMOND_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.GOLD_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.IRON_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.COPPER_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.EVOKER_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.NECRONOMICON.get());
            entries.m_246326_((ItemLike)ItemRegistry.ROTTEN_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.BLAZE_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.DRAGONSKIN_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.VILLAGER_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.DRUIDIC_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.CURSED_DOLL_SPELLBOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.ICE_SPELL_BOOK.get());
            entries.m_246326_((ItemLike)ItemRegistry.BLOOD_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.GRAYBEARD_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.ICE_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.ARTIFICER_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.LIGHTNING_ROD_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.PYRIUM_STAFF.get());
            entries.m_246326_((ItemLike)ItemRegistry.MAGEHUNTER.get());
            entries.m_246326_((ItemLike)ItemRegistry.SPELLBREAKER.get());
            entries.m_246326_((ItemLike)ItemRegistry.AMETHYST_RAPIER.get());
            entries.m_246326_((ItemLike)ItemRegistry.ICE_GREATSWORD.get());
            entries.m_246326_((ItemLike)ItemRegistry.TWILIGHT_GALE.get());
            entries.m_246326_((ItemLike)ItemRegistry.KEEPER_FLAMBERGE.get());
            entries.m_246326_((ItemLike)ItemRegistry.LEGIONNAIRE_FLAMBERGE.get());
            entries.m_246326_((ItemLike)ItemRegistry.DECREPIT_SCYTHE.get());
            entries.m_246326_((ItemLike)ItemRegistry.HELLRAZOR.get());
            entries.m_246326_((ItemLike)ItemRegistry.AUTOLOADER_CROSSBOW.get());
            entries.m_246326_((ItemLike)ItemRegistry.WAYWARD_COMPASS.get());
            entries.m_246326_((ItemLike)ItemRegistry.WANDERING_MAGICIAN_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.WANDERING_MAGICIAN_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.WANDERING_MAGICIAN_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.WANDERING_MAGICIAN_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PUMPKIN_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.PUMPKIN_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.PUMPKIN_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PUMPKIN_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PYROMANCER_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.PYROMANCER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.PYROMANCER_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PYROMANCER_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.ELECTROMANCER_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.ELECTROMANCER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.ELECTROMANCER_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.ELECTROMANCER_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.ARCHEVOKER_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.ARCHEVOKER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.ARCHEVOKER_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.ARCHEVOKER_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.CULTIST_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.CULTIST_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.CULTIST_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.CULTIST_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.CRYOMANCER_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.CRYOMANCER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.CRYOMANCER_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.CRYOMANCER_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.SHADOWWALKER_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.SHADOWWALKER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.SHADOWWALKER_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.SHADOWWALKER_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PRIEST_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.PRIEST_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.PRIEST_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PRIEST_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PLAGUED_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.PLAGUED_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.PLAGUED_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.PLAGUED_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.NETHERITE_MAGE_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.NETHERITE_MAGE_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.NETHERITE_MAGE_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.NETHERITE_MAGE_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.WIZARD_HELMET.get());
            entries.m_246326_((ItemLike)ItemRegistry.WIZARD_HAT.get());
            entries.m_246326_((ItemLike)ItemRegistry.WIZARD_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.WIZARD_LEGGINGS.get());
            entries.m_246326_((ItemLike)ItemRegistry.WIZARD_BOOTS.get());
            entries.m_246326_((ItemLike)ItemRegistry.INFERNAL_SORCERER_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.PALADIN_CHESTPLATE.get());
            entries.m_246326_((ItemLike)ItemRegistry.BOOTS_OF_SPEED.get());
            entries.m_246326_((ItemLike)ItemRegistry.TARNISHED_CROWN.get());
            entries.m_246326_((ItemLike)ItemRegistry.HITHER_THITHER_WAND.get());
            entries.m_246326_((ItemLike)ItemRegistry.MANA_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.SILVER_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.COOLDOWN_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.CAST_TIME_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.HEAVY_CHAIN.get());
            entries.m_246326_((ItemLike)ItemRegistry.EMERALD_STONEPLATE_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.FIREWARD_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.FROSTWARD_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.POISONWARD_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.CONJURERS_TALISMAN.get());
            entries.m_246326_((ItemLike)ItemRegistry.GREATER_CONJURERS_TALISMAN.get());
            entries.m_246326_((ItemLike)ItemRegistry.AFFINITY_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.CONCENTRATION_AMULET.get());
            entries.m_246326_((ItemLike)ItemRegistry.AMETHYST_RESONANCE_NECKLACE.get());
            entries.m_246326_((ItemLike)ItemRegistry.VISIBILITY_RING.get());
            entries.m_246326_((ItemLike)ItemRegistry.TELEPORTATION_AMULET.get());
            entries.m_246326_((ItemLike)ItemRegistry.SIGNET_OF_THE_BETRAYER.get());
            entries.m_246326_((ItemLike)ItemRegistry.INVISIBILITY_RING.get());
         })
         .withTabsBefore(new ResourceKey[]{CreativeModeTabs.f_256731_})
         .m_257652_()
   );
   public static final RegistryObject<CreativeModeTab> MATERIALS_TAB = TABS.register(
      "spellbook_materials",
      () -> CreativeModeTab.builder()
         .m_257941_(Component.m_237115_("itemGroup.irons_spellbooks.spell_materials_tab"))
         .m_257737_(() -> new ItemStack((ItemLike)ItemRegistry.DIVINE_PEARL.get()))
         .m_257501_(
            (enabledFeatures, entries) -> {
               entries.m_246326_((ItemLike)ItemRegistry.INK_COMMON.get());
               entries.m_246326_((ItemLike)ItemRegistry.INK_UNCOMMON.get());
               entries.m_246326_((ItemLike)ItemRegistry.INK_RARE.get());
               entries.m_246326_((ItemLike)ItemRegistry.INK_EPIC.get());
               entries.m_246326_((ItemLike)ItemRegistry.INK_LEGENDARY.get());
               entries.m_246326_((ItemLike)ItemRegistry.LESSER_SPELL_SLOT_UPGRADE.get());
               entries.m_246326_((ItemLike)ItemRegistry.UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.FIRE_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICE_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.LIGHTNING_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.HOLY_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.ENDER_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.BLOOD_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.EVOCATION_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.NATURE_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.MANA_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.COOLDOWN_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.PROTECTION_UPGRADE_ORB.get());
               entries.m_246326_((ItemLike)ItemRegistry.LIGHTNING_BOTTLE.get());
               entries.m_246326_((ItemLike)ItemRegistry.FROZEN_BONE_SHARD.get());
               entries.m_246326_((ItemLike)ItemRegistry.BLOOD_VIAL.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICE_VENOM_VIAL.get());
               entries.m_246326_((ItemLike)ItemRegistry.DIVINE_PEARL.get());
               entries.m_246326_((ItemLike)ItemRegistry.MAGIC_CLOTH.get());
               entries.m_246326_((ItemLike)ItemRegistry.HOGSKIN.get());
               entries.m_246326_((ItemLike)ItemRegistry.BLOODY_VELLUM.get());
               entries.m_246326_((ItemLike)ItemRegistry.DRAGONSKIN.get());
               entries.m_246326_((ItemLike)ItemRegistry.ARCANE_ESSENCE.get());
               entries.m_246326_((ItemLike)ItemRegistry.RUINED_BOOK.get());
               entries.m_246326_((ItemLike)ItemRegistry.CHAINED_BOOK.get());
               entries.m_246326_((ItemLike)ItemRegistry.THE_CHRONICLE.get());
               entries.m_246326_((ItemLike)ItemRegistry.CINDER_ESSENCE.get());
               entries.m_246326_((ItemLike)ItemRegistry.TIMELESS_SLURRY.get());
               entries.m_246326_((ItemLike)ItemRegistry.MITHRIL_INGOT.get());
               entries.m_246326_((ItemLike)ItemRegistry.MITHRIL_SCRAP.get());
               entries.m_246326_((ItemLike)ItemRegistry.RAW_MITHRIL.get());
               entries.m_246326_((ItemLike)ItemRegistry.WEAPON_PARTS.get());
               entries.m_246326_((ItemLike)ItemRegistry.MITHRIL_WEAVE.get());
               entries.m_246326_((ItemLike)ItemRegistry.DIVINE_SOULSHARD.get());
               entries.m_246326_((ItemLike)ItemRegistry.PYRIUM_INGOT.get());
               entries.m_246326_((ItemLike)ItemRegistry.ARCANE_INGOT.get());
               entries.m_246326_((ItemLike)ItemRegistry.SHRIVING_STONE.get());
               entries.m_246326_((ItemLike)ItemRegistry.ELDRITCH_PAGE.get());
               entries.m_246326_((ItemLike)ItemRegistry.LOST_KNOWLEDGE_FRAGMENT.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICY_FANG.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICE_CRYSTAL.get());
               entries.m_246326_((ItemLike)ItemRegistry.FROSTED_HELVE.get());
               entries.m_246326_((ItemLike)ItemRegistry.ENERGIZED_CORE.get());
               entries.m_246342_(
                  FurledMapItem.of(
                     IronsSpellbooks.id("evoker_fort"),
                     ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.withDefaultNamespace("overworld")),
                     Component.m_237115_("item.irons_spellbooks.evoker_fort_battle_plans"),
                     false
                  )
               );
               entries.m_246342_(
                  FurledMapItem.of(
                     IronsSpellbooks.id("mangrove_hut"),
                     ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.withDefaultNamespace("overworld")),
                     Component.m_237115_("item.irons_spellbooks.alchemical_trade_route"),
                     false
                  )
               );
               entries.m_246326_((ItemLike)ItemRegistry.ICE_SPIDER_FURLED_MAP.get());
               entries.m_246326_((ItemLike)ItemRegistry.CITADEL_FURLED_MAP.get());
               entries.m_246326_((ItemLike)ItemRegistry.DECREPIT_KEY.get());
               entries.m_246326_((ItemLike)ItemRegistry.CINDEROUS_SOULCALLER.get());
               entries.m_246326_((ItemLike)ItemRegistry.BLANK_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.FIRE_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICE_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.LIGHTNING_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.ENDER_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.HOLY_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.BLOOD_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.EVOCATION_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.MANA_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.COOLDOWN_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.PROTECTION_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.NATURE_RUNE.get());
               entries.m_246326_((ItemLike)ItemRegistry.OAKSKIN_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.GREATER_OAKSKIN_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.GREATER_HEALING_POTION.get());
               entries.m_246326_((ItemLike)ItemRegistry.INVISIBILITY_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.GREATER_INVISIBILITY_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.EVASION_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.GREATER_EVASION_ELIXIR.get());
               entries.m_246326_((ItemLike)ItemRegistry.FIRE_ALE.get());
               entries.m_246326_((ItemLike)ItemRegistry.NETHERWARD_TINCTURE.get());
               entries.m_246326_((ItemLike)ItemRegistry.MUSIC_DISC_DEAD_KING_LULLABY.get());
               entries.m_246326_((ItemLike)ItemRegistry.MUSIC_DISC_FLAME_STILL_BURNS.get());
               entries.m_246326_((ItemLike)ItemRegistry.FLAME_STILL_BURNS_FRAGMENT.get());
               entries.m_246326_((ItemLike)ItemRegistry.MUSIC_DISC_WHISPERS_OF_ICE.get());
               entries.m_246326_((ItemLike)ItemRegistry.KEEPER_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.DEAD_KING_CORPSE_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.ARCHEVOKER_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.NECROMANCER_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.CRYOMANCER_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.PYROMANCER_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.PRIEST_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.APOTHECARIST_SPAWN_EGG.get());
               entries.m_246326_((ItemLike)ItemRegistry.ICE_SPIDER_SPAWN_EGG.get());
            }
         )
         .withTabsBefore(new ResourceKey[]{EQUIPMENT_TAB.getKey()})
         .m_257652_()
   );
   public static final RegistryObject<CreativeModeTab> BLOCKS_TAB = TABS.register(
      "spellbook_blocks",
      () -> CreativeModeTab.builder()
         .m_257941_(Component.m_237115_("itemGroup.irons_spellbooks.blocks_tab"))
         .m_257737_(() -> new ItemStack((ItemLike)ItemRegistry.INSCRIPTION_TABLE_BLOCK_ITEM.get()))
         .m_257501_((enabledFeatures, entries) -> ItemRegistry.getIronsItems().forEach(holder -> {
            if (holder.get() instanceof BlockItem && holder != ItemRegistry.ARCANE_DEBRIS_BLOCK_ITEM) {
               entries.m_246326_((ItemLike)holder.get());
            }
         }))
         .withTabsBefore(new ResourceKey[]{MATERIALS_TAB.getKey()})
         .m_257652_()
   );
   public static final Supplier<CreativeModeTab> SCROLLS_TAB = TABS.register(
      "spellbook_scrolls",
      () -> CreativeModeTab.builder()
         .m_257941_(Component.m_237115_("itemGroup.irons_spellbooks.spellbook_scrolls_tab"))
         .m_257737_(() -> new ItemStack((ItemLike)ItemRegistry.SCROLL.get()))
         .withTabsBefore(new ResourceKey[]{MATERIALS_TAB.getKey()})
         .m_257652_()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }

   @SubscribeEvent
   public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_256791_)) {
         event.m_246326_((ItemLike)ItemRegistry.INSCRIPTION_TABLE_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.SCROLL_FORGE_BLOCK.get());
         event.m_246326_((ItemLike)ItemRegistry.ACANE_ANVIL_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.PEDESTAL_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.ARMOR_PILE_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.ALCHEMIST_CAULDRON_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.FIREFLY_JAR_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.PORTAL_FRAME_ITEM.get());
      }

      if (event.getTab() == SCROLLS_TAB.get()) {
         SpellRegistry.getEnabledSpells().stream().filter(spellType -> spellType != SpellRegistry.none()).forEach(spell -> {
            for (int i = spell.getMinLevel(); i <= spell.getMaxLevel(); i++) {
               ItemStack itemstack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
               ISpellContainer spellList = ISpellContainer.createScrollContainer(spell, i, itemstack);
               event.m_246342_(itemstack);
            }
         });
      }

      if (event.getTab() == BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_256776_)) {
         event.m_246326_((ItemLike)ItemRegistry.MITHRIL_ORE_BLOCK_ITEM.get());
         event.m_246326_((ItemLike)ItemRegistry.MITHRIL_ORE_DEEPSLATE_BLOCK_ITEM.get());
      }
   }
}
