package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueItem;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.consumables.SimpleElixir;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

public class GenerateSiteData {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.generate_recipe_data.failed")
   );
   private static final String RECIPE_DATA_TEMPLATE = "- id: \"%s\"\n  name: \"%s\"\n  path: \"%s\"\n  group: \"%s\"\n  sortOverride: \"%s\"\n  craftingType: \"%s\"\n  item0Id: \"%s\"\n  item0: \"%s\"\n  item0Path: \"%s\"\n  item1Id: \"%s\"\n  item1: \"%s\"\n  item1Path: \"%s\"\n  item2Id: \"%s\"\n  item2: \"%s\"\n  item2Path: \"%s\"\n  item3Id: \"%s\"\n  item3: \"%s\"\n  item3Path: \"%s\"\n  item4Id: \"%s\"\n  item4: \"%s\"\n  item4Path: \"%s\"\n  item5Id: \"%s\"\n  item5: \"%s\"\n  item5Path: \"%s\"\n  item6Id: \"%s\"\n  item6: \"%s\"\n  item6Path: \"%s\"\n  item7Id: \"%s\"\n  item7: \"%s\"\n  item7Path: \"%s\"\n  item8Id: \"%s\"\n  item8: \"%s\"\n  item8Path: \"%s\"\n  tooltip: \"%s\"\n  description: \"\"\n\n";
   private static final String SPELL_DATA_TEMPLATE = "- name: \"%s\"\n  school: \"%s\"\n  icon: \"%s\"\n  level: \"%d to %d\"\n  mana: \"%d to %d\"\n  cooldown: \"%ds\"\n  cast_type: \"%s\"\n  rarity: \"%s to %s\"\n  description: \"%s\"\n  u1: \"%s\"\n  u2: \"%s\"\n  u3: \"%s\"\n  u4: \"%s\"\n\n";
   static ServerLevel level;

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("generateSiteData").requires(p_138819_ -> p_138819_.m_6761_(2)))
            .executes(commandContext -> generateSiteData((CommandSourceStack)commandContext.getSource()))
      );
   }

   private static int generateSiteData(CommandSourceStack source) {
      generateRecipeData(source);
      generateSpellData();
      return 1;
   }

   private static void generateRecipeData(CommandSourceStack source) {
      try {
         StringBuilder itemBuilder = new StringBuilder();
         StringBuilder armorBuilder = new StringBuilder();
         StringBuilder spellbookBuilder = new StringBuilder();
         StringBuilder curioBuilder = new StringBuilder();
         StringBuilder blockBuilder = new StringBuilder();
         level = source.m_81372_();
         Set<Item> itemsTracked = new HashSet<>();
         ClientInputEvents.isShiftKeyDown = true;
         handleAffinityRingEntry(curioBuilder, itemsTracked, source);
         getVisibleItems()
            .stream()
            .sorted(Comparator.comparing(Item::m_5524_))
            .forEach(
               item -> {
                  ResourceLocation itemResource = BuiltInRegistries.f_257033_.m_7981_(item);
                  String tooltip = getTooltip(source.m_230896_(), new ItemStack(item));
                  if (itemResource.m_135827_().equals("irons_spellbooks") && !itemsTracked.contains(item)) {
                     Recipe recipe = getRecipeFor(source, item);
                     String name = item.m_7626_(ItemStack.f_41583_).getString();
                     if (!item.m_5524_().contains("patchouli")
                        && !item.m_5524_().contains("spawn_egg")
                        && !item.m_5524_().equals("item.irons_spellbooks.scroll")) {
                        if (item instanceof ArmorItem armorItem) {
                           Class<? extends ArmorItem> armortype = (Class<? extends ArmorItem>)armorItem.getClass();
                           boolean hasGroup = ItemRegistry.getIronsItems()
                                 .stream()
                                 .filter(holder -> armortype.isAssignableFrom(((Item)holder.get()).getClass()))
                                 .toList()
                                 .size()
                              > 1;
                           int sort = 0;
                           String group = "All Armor";
                           if (hasGroup) {
                              String[] words = name.split(" ");
                              group = Arrays.stream(words).limit(words.length - 1).collect(Collectors.joining(" ")) + " Armor";
                              sort = 3 - armorItem.m_40402_().m_20749_();
                           }

                           if (recipe != null) {
                              appendRecipeSorted(armorBuilder, recipe, getRecipeData(recipe), group, tooltip, sort);
                           } else {
                              appendSimpleGroupedSorted(armorBuilder, name, itemResource, group, tooltip, sort);
                           }
                        } else if (!(item instanceof SpellBook)
                           && !(item instanceof ExtendedSwordItem)
                           && !(item instanceof CastingItem)
                           && !(item instanceof ProjectileWeaponItem)
                           && !(item instanceof UniqueItem)) {
                           if (item instanceof CurioBaseItem) {
                              if (recipe != null) {
                                 appendRecipe(curioBuilder, recipe, getRecipeData(recipe), "", tooltip);
                              } else {
                                 appendSimple(curioBuilder, name, itemResource, tooltip);
                              }
                           } else if (item instanceof BlockItem) {
                              if (recipe != null) {
                                 appendRecipe(blockBuilder, recipe, getRecipeData(recipe), "", tooltip);
                              } else {
                                 appendSimple(blockBuilder, name, itemResource, tooltip);
                              }
                           } else if (recipe != null) {
                              appendRecipe(itemBuilder, recipe, getRecipeData(recipe), handleGenericItemGrouping(item), tooltip);
                           } else {
                              appendSimpleGrouped(itemBuilder, name, itemResource, handleGenericItemGrouping(item), tooltip);
                           }
                        } else {
                           String group = item instanceof SpellBook ? "Spellbooks" : (item instanceof CastingItem ? "Staves" : "Weapons");
                           if (recipe != null) {
                              appendRecipe(spellbookBuilder, recipe, getRecipeData(recipe), group, tooltip);
                           } else {
                              appendSimpleGrouped(spellbookBuilder, name, itemResource, group, tooltip);
                           }
                        }
                     }

                     itemsTracked.add(item);
                  }
               }
            );
         ClientInputEvents.isShiftKeyDown = false;
         BufferedWriter file = new BufferedWriter(new FileWriter("item_data.yml"));
         file.write(postProcess(itemBuilder));
         file.close();
         file = new BufferedWriter(new FileWriter("armor_data.yml"));
         file.write(postProcess(armorBuilder));
         file.close();
         file = new BufferedWriter(new FileWriter("curio_data.yml"));
         file.write(postProcess(curioBuilder));
         file.close();
         file = new BufferedWriter(new FileWriter("spellbook_data.yml"));
         file.write(postProcess(spellbookBuilder));
         file.close();
         file = new BufferedWriter(new FileWriter("block_data.yml"));
         file.write(postProcess(blockBuilder));
         file.close();
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.debug(e.getMessage());
      }
   }

   private static void handleAffinityRingEntry(StringBuilder curioBuilder, Set<Item> itemsTracked, CommandSourceStack source) {
      CurioBaseItem item = (CurioBaseItem)ItemRegistry.AFFINITY_RING.get();
      itemsTracked.add(item);
      ResourceLocation itemResource = BuiltInRegistries.f_257033_.m_7981_(item);
      String name = item.m_7626_(ItemStack.f_41583_).getString();
      appendSimple(
         curioBuilder,
         name,
         itemResource,
         "Affinity Rings are randomly generated as loot, and will boost the level of a select spell by one. This effect can stack. Spell can be set in the Arcane Anvil using a scroll."
      );
   }

   private static String handleGenericItemGrouping(Item item) {
      if (item instanceof InkItem) {
         return "Ink";
      } else if (item instanceof RecordItem) {
         return "Music Discs";
      } else if (item.m_5524_().contains("rune")) {
         return "Runes";
      } else if (item instanceof UpgradeOrbItem || item == ItemRegistry.UPGRADE_ORB.get()) {
         return "Upgrade Orbs";
      } else {
         return item instanceof SimpleElixir ? "Elixirs" : "All";
      }
   }

   @NotNull
   private static ArrayList<GenerateSiteData.RecipeIngredientData> getRecipeData(Recipe<?> recipe) {
      ResourceLocation resultItemResourceLocation = BuiltInRegistries.f_257033_.m_7981_(recipe.m_8043_(level.m_9598_()).m_41720_());
      ArrayList<GenerateSiteData.RecipeIngredientData> recipeData = new ArrayList<>(10);
      recipeData.add(
         new GenerateSiteData.RecipeIngredientData(
            resultItemResourceLocation.toString(),
            recipe.m_8043_(level.m_9598_()).m_41720_().m_7626_(ItemStack.f_41583_).getString(),
            String.format("/img/items/%s.png", resultItemResourceLocation.m_135815_()),
            recipe.m_8043_(level.m_9598_()).m_41720_()
         )
      );
      if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
         recipe.m_7527_().forEach(ingredient -> handleIngredient(ingredient, recipeData, recipe));
      } else if (recipe instanceof SmithingTransformRecipe smithingRecipe) {
         handleIngredient(
            Ingredient.m_43927_(
               new ItemStack[]{
                  BuiltInRegistries.f_257033_
                     .m_123024_()
                     .<ItemStack>map(Item::m_7968_)
                     .filter(smithingRecipe::m_266166_)
                     .findFirst()
                     .orElse(ItemStack.f_41583_)
               }
            ),
            recipeData,
            recipe
         );
         handleIngredient(
            Ingredient.m_43927_(
               new ItemStack[]{
                  BuiltInRegistries.f_257033_
                     .m_123024_()
                     .<ItemStack>map(Item::m_7968_)
                     .filter(smithingRecipe::m_266343_)
                     .findFirst()
                     .orElse(ItemStack.f_41583_)
               }
            ),
            recipeData,
            recipe
         );
         handleIngredient(
            Ingredient.m_43927_(
               new ItemStack[]{
                  BuiltInRegistries.f_257033_
                     .m_123024_()
                     .<ItemStack>map(Item::m_7968_)
                     .filter(smithingRecipe::m_266253_)
                     .findFirst()
                     .orElse(ItemStack.f_41583_)
               }
            ),
            recipeData,
            recipe
         );
      }

      return recipeData;
   }

   @Nullable
   private static Recipe getRecipeFor(CommandSourceStack sourceStack, Item item) {
      for (Recipe<?> recipe : sourceStack.getRecipeManager().m_44051_()) {
         if (recipe.m_8043_(level.m_9598_()).m_150930_(item)) {
            return recipe;
         }
      }

      return null;
   }

   private static String postProcess(StringBuilder sb) {
      return sb.toString()
         .replace("netherite_spell_book.png", "netherite_spell_book.gif")
         .replace("ruined_book.png", "ruined_book.gif")
         .replace("lightning_bottle.png", "lightning_bottle.gif")
         .replace("cinder_essence.png", "cinder_essence.gif")
         .replace("nature_upgrade_orb.png", "nature_upgrade_orb.gif")
         .replace("evasion_elixir.png", "evasion_elixir.gif")
         .replace("/upgrade_orb.png", "/upgrade_orb.gif")
         .replace("fire_upgrade_orb.png", "fire_upgrade_orb.gif")
         .replace("holy_upgrade_orb.png", "holy_upgrade_orb.gif")
         .replace("lightning_upgrade_orb.png", "lightning_upgrade_orb.gif")
         .replace("ender_upgrade_orb.png", "ender_upgrade_orb.gif")
         .replace("mana_upgrade_orb.png", "upgrade_orb_mana.gif")
         .replace("protection_upgrade_orb.png", "upgrade_orb_protection.gif")
         .replace("ice_upgrade_orb.png", "upgrade_orb_ice.gif")
         .replace("evocation_upgrade_orb.png", "upgrade_orb_evocation.gif")
         .replace("cooldown_upgrade_orb.png", "upgrade_orb_cooldown.gif")
         .replace("blood_upgrade_orb.png", "upgrade_orb_blood.gif")
         .replace("wayward_compass.png", "wayward_compass.gif")
         .replace("affinity_ring.png", "affinity_rings.gif")
         .replace("energized_core.png", "energized_core.gif")
         .replace("Deepslate Mithril Ore", "Mithril Ore (Deepslate)");
   }

   private static String getSpells(ItemStack itemStack) {
      if (itemStack.m_41720_() instanceof SpellBook) {
         ISpellContainer spellList = ISpellContainer.get(itemStack);
         return spellList.getActiveSpells()
            .stream()
            .map(spell -> spell.getSpell().getDisplayName(null).getString() + " (" + spell.getLevel() + ")")
            .collect(Collectors.joining(", "));
      } else {
         return "";
      }
   }

   private static String getTooltip(ServerPlayer player, ItemStack itemStack) {
      return Arrays.stream(
            itemStack.m_41651_(player, Default.f_256752_)
               .stream()
               .skip(1L)
               .<CharSequence>map(Component::getString)
               .filter(x -> x.trim().length() > 0)
               .collect(Collectors.joining(", "))
               .replace(":,", ": ")
               .replace("  ", " ")
               .split(",")
         )
         .filter(item -> !item.contains("Slot"))
         .collect(Collectors.joining(","))
         .trim()
         .replace(":", ":<br>");
   }

   private static void appendRecipeSorted(
      StringBuilder sb, Recipe recipe, List<GenerateSiteData.RecipeIngredientData> recipeIngredientData, String group, String tooltip, int sort
   ) {
      sb.append(
         String.format(
            "- id: \"%s\"\n  name: \"%s\"\n  path: \"%s\"\n  group: \"%s\"\n  sortOverride: \"%s\"\n  craftingType: \"%s\"\n  item0Id: \"%s\"\n  item0: \"%s\"\n  item0Path: \"%s\"\n  item1Id: \"%s\"\n  item1: \"%s\"\n  item1Path: \"%s\"\n  item2Id: \"%s\"\n  item2: \"%s\"\n  item2Path: \"%s\"\n  item3Id: \"%s\"\n  item3: \"%s\"\n  item3Path: \"%s\"\n  item4Id: \"%s\"\n  item4: \"%s\"\n  item4Path: \"%s\"\n  item5Id: \"%s\"\n  item5: \"%s\"\n  item5Path: \"%s\"\n  item6Id: \"%s\"\n  item6: \"%s\"\n  item6Path: \"%s\"\n  item7Id: \"%s\"\n  item7: \"%s\"\n  item7Path: \"%s\"\n  item8Id: \"%s\"\n  item8: \"%s\"\n  item8Path: \"%s\"\n  tooltip: \"%s\"\n  description: \"\"\n\n",
            getRecipeDataAtIndex(recipeIngredientData, 0).id,
            getRecipeDataAtIndex(recipeIngredientData, 0).name,
            getRecipeDataAtIndex(recipeIngredientData, 0).path,
            group,
            sort,
            recipe.m_6671_(),
            getRecipeDataAtIndex(recipeIngredientData, 1).id,
            getRecipeDataAtIndex(recipeIngredientData, 1).name,
            getRecipeDataAtIndex(recipeIngredientData, 1).path,
            getRecipeDataAtIndex(recipeIngredientData, 2).id,
            getRecipeDataAtIndex(recipeIngredientData, 2).name,
            getRecipeDataAtIndex(recipeIngredientData, 2).path,
            getRecipeDataAtIndex(recipeIngredientData, 3).id,
            getRecipeDataAtIndex(recipeIngredientData, 3).name,
            getRecipeDataAtIndex(recipeIngredientData, 3).path,
            getRecipeDataAtIndex(recipeIngredientData, 4).id,
            getRecipeDataAtIndex(recipeIngredientData, 4).name,
            getRecipeDataAtIndex(recipeIngredientData, 4).path,
            getRecipeDataAtIndex(recipeIngredientData, 5).id,
            getRecipeDataAtIndex(recipeIngredientData, 5).name,
            getRecipeDataAtIndex(recipeIngredientData, 5).path,
            getRecipeDataAtIndex(recipeIngredientData, 6).id,
            getRecipeDataAtIndex(recipeIngredientData, 6).name,
            getRecipeDataAtIndex(recipeIngredientData, 6).path,
            getRecipeDataAtIndex(recipeIngredientData, 7).id,
            getRecipeDataAtIndex(recipeIngredientData, 7).name,
            getRecipeDataAtIndex(recipeIngredientData, 7).path,
            getRecipeDataAtIndex(recipeIngredientData, 8).id,
            getRecipeDataAtIndex(recipeIngredientData, 8).name,
            getRecipeDataAtIndex(recipeIngredientData, 8).path,
            getRecipeDataAtIndex(recipeIngredientData, 9).id,
            getRecipeDataAtIndex(recipeIngredientData, 9).name,
            getRecipeDataAtIndex(recipeIngredientData, 9).path,
            tooltip
         )
      );
   }

   private static void appendRecipe(
      StringBuilder sb, Recipe recipe, List<GenerateSiteData.RecipeIngredientData> recipeIngredientData, String group, String tooltip
   ) {
      appendRecipeSorted(sb, recipe, recipeIngredientData, group, tooltip, 0);
   }

   private static void appendSimple(StringBuilder sb, String name, ResourceLocation itemResource, String tooltip) {
      sb.append(
         String.format(
            "- id: \"%s\"\n  name: \"%s\"\n  path: \"%s\"\n  group: \"%s\"\n  sortOverride: \"%s\"\n  craftingType: \"%s\"\n  item0Id: \"%s\"\n  item0: \"%s\"\n  item0Path: \"%s\"\n  item1Id: \"%s\"\n  item1: \"%s\"\n  item1Path: \"%s\"\n  item2Id: \"%s\"\n  item2: \"%s\"\n  item2Path: \"%s\"\n  item3Id: \"%s\"\n  item3: \"%s\"\n  item3Path: \"%s\"\n  item4Id: \"%s\"\n  item4: \"%s\"\n  item4Path: \"%s\"\n  item5Id: \"%s\"\n  item5: \"%s\"\n  item5Path: \"%s\"\n  item6Id: \"%s\"\n  item6: \"%s\"\n  item6Path: \"%s\"\n  item7Id: \"%s\"\n  item7: \"%s\"\n  item7Path: \"%s\"\n  item8Id: \"%s\"\n  item8: \"%s\"\n  item8Path: \"%s\"\n  tooltip: \"%s\"\n  description: \"\"\n\n",
            itemResource.toString(),
            name,
            String.format("/img/items/%s.png", itemResource.m_135815_()),
            "",
            "0",
            "none",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            tooltip
         )
      );
   }

   private static void appendSimpleGrouped(StringBuilder sb, String name, ResourceLocation itemResource, String group, String tooltip) {
      sb.append(
         String.format(
            "- id: \"%s\"\n  name: \"%s\"\n  path: \"%s\"\n  group: \"%s\"\n  sortOverride: \"%s\"\n  craftingType: \"%s\"\n  item0Id: \"%s\"\n  item0: \"%s\"\n  item0Path: \"%s\"\n  item1Id: \"%s\"\n  item1: \"%s\"\n  item1Path: \"%s\"\n  item2Id: \"%s\"\n  item2: \"%s\"\n  item2Path: \"%s\"\n  item3Id: \"%s\"\n  item3: \"%s\"\n  item3Path: \"%s\"\n  item4Id: \"%s\"\n  item4: \"%s\"\n  item4Path: \"%s\"\n  item5Id: \"%s\"\n  item5: \"%s\"\n  item5Path: \"%s\"\n  item6Id: \"%s\"\n  item6: \"%s\"\n  item6Path: \"%s\"\n  item7Id: \"%s\"\n  item7: \"%s\"\n  item7Path: \"%s\"\n  item8Id: \"%s\"\n  item8: \"%s\"\n  item8Path: \"%s\"\n  tooltip: \"%s\"\n  description: \"\"\n\n",
            itemResource.toString(),
            name,
            String.format("/img/items/%s.png", itemResource.m_135815_()),
            group,
            "0",
            "none",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            tooltip
         )
      );
   }

   private static void appendSimpleGroupedSorted(StringBuilder sb, String name, ResourceLocation itemResource, String group, String tooltip, int sort) {
      sb.append(
         String.format(
            "- id: \"%s\"\n  name: \"%s\"\n  path: \"%s\"\n  group: \"%s\"\n  sortOverride: \"%s\"\n  craftingType: \"%s\"\n  item0Id: \"%s\"\n  item0: \"%s\"\n  item0Path: \"%s\"\n  item1Id: \"%s\"\n  item1: \"%s\"\n  item1Path: \"%s\"\n  item2Id: \"%s\"\n  item2: \"%s\"\n  item2Path: \"%s\"\n  item3Id: \"%s\"\n  item3: \"%s\"\n  item3Path: \"%s\"\n  item4Id: \"%s\"\n  item4: \"%s\"\n  item4Path: \"%s\"\n  item5Id: \"%s\"\n  item5: \"%s\"\n  item5Path: \"%s\"\n  item6Id: \"%s\"\n  item6: \"%s\"\n  item6Path: \"%s\"\n  item7Id: \"%s\"\n  item7: \"%s\"\n  item7Path: \"%s\"\n  item8Id: \"%s\"\n  item8: \"%s\"\n  item8Path: \"%s\"\n  tooltip: \"%s\"\n  description: \"\"\n\n",
            itemResource.toString(),
            name,
            String.format("/img/items/%s.png", itemResource.m_135815_()),
            group,
            sort,
            "none",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            tooltip
         )
      );
   }

   private static void handleIngredient(Ingredient ingredient, ArrayList<GenerateSiteData.RecipeIngredientData> recipeData, Recipe recipe) {
      Arrays.stream(ingredient.m_43908_())
         .findFirst()
         .ifPresentOrElse(
            itemStack -> {
               ResourceLocation itemResource = BuiltInRegistries.f_257033_.m_7981_(itemStack.m_41720_());
               String path = "";
               if (itemResource.toString().contains("irons_spellbooks")) {
                  path = String.format("/img/items/%s.png", itemResource.m_135815_());
               } else {
                  path = String.format("/img/items/minecraft/%s.png", itemResource.m_135815_());
               }

               if (itemStack.m_204117_(ModTags.INSCRIBED_RUNES) && recipe.m_8043_(level.m_9598_()).m_150930_((Item)ItemRegistry.BLANK_RUNE.get())) {
                  path = "/img/items/all_runes.gif";
               }

               recipeData.add(
                  new GenerateSiteData.RecipeIngredientData(
                     itemResource.toString(), itemStack.m_41720_().m_7626_(ItemStack.f_41583_).getString(), path, recipe.m_8043_(level.m_9598_()).m_41720_()
                  )
               );
            },
            () -> recipeData.add(GenerateSiteData.RecipeIngredientData.EMPTY)
         );
   }

   private static GenerateSiteData.RecipeIngredientData getRecipeDataAtIndex(List<GenerateSiteData.RecipeIngredientData> recipeIngredientData, int index) {
      return index < recipeIngredientData.size() ? recipeIngredientData.get(index) : GenerateSiteData.RecipeIngredientData.EMPTY;
   }

   private static List<Item> getVisibleItems() {
      return BuiltInRegistries.f_257033_
         .m_123024_()
         .filter(item -> CreativeModeTabs.m_257478_().stream().anyMatch(tab -> tab.m_257694_(new ItemStack(item))))
         .toList();
   }

   private static void generateSpellData() {
      try {
         StringBuilder sb = new StringBuilder();
         SpellRegistry.REGISTRY
            .get()
            .getValues()
            .stream()
            .filter(st -> st.isEnabled() && st != SpellRegistry.none())
            .forEach(
               spellType -> {
                  int spellMin = spellType.getMinLevel();
                  int spellMax = spellType.getMaxLevel();
                  List<String> uniqueInfo = processUniqueInfo(spellType);
                  String u1 = uniqueInfo.size() >= 1 ? uniqueInfo.get(0) : "";
                  String u2 = uniqueInfo.size() >= 2 ? uniqueInfo.get(1) : "";
                  String u3 = uniqueInfo.size() >= 3 ? uniqueInfo.get(2) : "";
                  String u4 = uniqueInfo.size() >= 4 ? uniqueInfo.get(3) : "";
                  sb.append(
                     String.format(
                        "- name: \"%s\"\n  school: \"%s\"\n  icon: \"%s\"\n  level: \"%d to %d\"\n  mana: \"%d to %d\"\n  cooldown: \"%ds\"\n  cast_type: \"%s\"\n  rarity: \"%s to %s\"\n  description: \"%s\"\n  u1: \"%s\"\n  u2: \"%s\"\n  u3: \"%s\"\n  u4: \"%s\"\n\n",
                        handleCapitalization(spellType.getSpellName()),
                        handleCapitalization(spellType.getSchoolType().getDisplayName().getString()),
                        String.format("/img/spells/%s.png", spellType.getSpellName()),
                        spellType.getMinLevel(),
                        spellType.getMaxLevel(),
                        spellType.getManaCost(spellMin),
                        spellType.getManaCost(spellMax),
                        spellType.getSpellCooldown(),
                        handleCapitalization(spellType.getCastType().name()),
                        handleCapitalization(spellType.getRarity(spellMin).name()),
                        handleCapitalization(spellType.getRarity(spellMax).name()),
                        Component.m_237115_(String.format("%s.guide", spellType.getComponentId())).getString(),
                        u1,
                        u2,
                        u3,
                        u4
                     )
                  );
               }
            );
         BufferedWriter file = new BufferedWriter(new FileWriter("spell_data.yml"));
         file.write(sb.toString());
         file.close();
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.debug(e.getMessage());
      }
   }

   private static List<String> processUniqueInfo(AbstractSpell spell) {
      List<String> text = new ArrayList<>();
      List<MutableComponent> uniqueInfoMin = spell.getUniqueInfo(spell.getMinLevel(), null);
      List<MutableComponent> uniqueInfoMax = spell.getUniqueInfo(spell.getMaxLevel(), null);

      for (int i = 0; i < uniqueInfoMax.size(); i++) {
         String[] lineMinLevel = uniqueInfoMin.get(i).getString().split(" ");
         String[] lineMaxLevel = uniqueInfoMax.get(i).getString().split(" ");
         int k = -1;

         for (int j = 0; j < lineMinLevel.length; j++) {
            if (lineMinLevel[j].matches("([+\\-])?\\d\\.?\\d*(s|m|%)*")) {
               k = j;
               break;
            }
         }

         if (k >= 0 && !lineMinLevel[k].equals(lineMaxLevel[k])) {
            StringBuilder builder = new StringBuilder();

            for (int j = 0; j < lineMinLevel.length; j++) {
               if (j == k) {
                  builder.append(String.format("%s-%s", lineMinLevel[k], lineMaxLevel[k])).append(" ");
               } else {
                  builder.append(lineMinLevel[j]).append(" ");
               }
            }

            text.add(builder.toString().strip());
         } else {
            text.add(uniqueInfoMin.get(i).getString());
         }
      }

      return text;
   }

   public static String handleCapitalization(String input) {
      return Arrays.stream(input.toLowerCase().split("[ |_]")).map(word -> {
         if (word.equals("spell")) {
            return "";
         }

         String first = word.substring(0, 1);
         String rest = word.substring(1);
         return first.toUpperCase() + rest;
      }).collect(Collectors.joining(" ")).trim();
   }

   private enum CraftingType {
      CRAFTING_TABLE,
      SMITHING_TABLE,
      NOT_CRAFTABLE;
   }

   private record RecipeIngredientData(String id, String name, String path, Item item) {
      public static GenerateSiteData.RecipeIngredientData EMPTY = new GenerateSiteData.RecipeIngredientData("", "", "", null);
   }
}
