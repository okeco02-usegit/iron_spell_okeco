package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TooltipsUtils {
   public static final Style UNIQUE_STYLE = Style.f_131099_.m_178520_(14697252);
   private static final Style INFO_STYLE = Style.f_131099_.m_131140_(ChatFormatting.DARK_GREEN);
   private static final Style OBFUSCATED_STYLE = AbstractSpell.ELDRITCH_OBFUSCATED_STYLE.m_131146_(INFO_STYLE);

   public static int indexOfComponent(List<Component> lines, String key) {
      return indexOfInternal(lines, key::equals);
   }

   public static int indexOfComponentRegex(List<Component> lines, String regex) {
      return indexOfInternal(lines, string -> string.matches(regex));
   }

   public static int indexOfAdvancedText(List<Component> lines, ItemStack itemStack) {
      return indexOfComponentRegex(lines, "item.durability|item.components|" + BuiltInRegistries.f_257033_.m_7981_(itemStack.m_41720_()));
   }

   private static int indexOfInternal(List<Component> lines, Predicate<String> comparator) {
      int size = lines.size();

      for (int i = 0; i < size; i++) {
         Component component = lines.get(i);
         if (component.m_214077_() instanceof TranslatableContents translatableContents) {
            if (comparator.test(translatableContents.m_237508_())) {
               return i;
            }
         } else if (component.m_214077_() instanceof LiteralContents literalContents && comparator.test(literalContents.f_237368_())) {
            return i;
         }
      }

      return -1;
   }

   public static List<MutableComponent> formatActiveSpellTooltip(ItemStack stack, SpellData spellData, CastSource castSource, @Nonnull LocalPlayer player) {
      AbstractSpell spell = spellData.getSpell();
      int spellLevel = spell.getLevelFor(spellData.getLevel(), player);
      MutableComponent title = getTitleComponent(spellData, player);
      List<MutableComponent> uniqueInfo = spell.getUniqueInfo(spellLevel, player);
      MutableComponent manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost(spellLevel)).m_130940_(ChatFormatting.BLUE);
      MutableComponent cooldownTime = Component.m_237110_(
            "tooltip.irons_spellbooks.cooldown_length_seconds",
            new Object[]{Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spell, player, castSource), 2)}
         )
         .m_130940_(ChatFormatting.BLUE);
      List<MutableComponent> lines = new ArrayList<>();
      lines.add(Component.m_237119_());
      lines.add(title);
      uniqueInfo.forEach(line -> lines.add(Component.m_237113_(" ").m_7220_(line.m_130948_(getStyleFor(player, spell)))));
      if (spell.getCastType() != CastType.INSTANT) {
         lines.add(
            Component.m_237113_(" ")
               .m_7220_(
                  getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(spellLevel, player), 2))
                     .m_130940_(ChatFormatting.BLUE)
               )
         );
      }

      if ((castSource != CastSource.SWORD || (Boolean)ServerConfigs.SWORDS_CONSUME_MANA.get()) && spell.getManaCost(spellLevel) > 0) {
         lines.add(manaCost);
      }

      if ((castSource != CastSource.SWORD || ((Double)ServerConfigs.SWORDS_CD_MULTIPLIER.get()).floatValue() > 0.0F) && spell.getSpellCooldown() > 0) {
         lines.add(cooldownTime);
      }

      return lines;
   }

   public static List<Component> formatScrollTooltip(ItemStack stack, Player player) {
      if (stack.m_41720_() instanceof Scroll && ISpellContainer.isSpellContainer(stack)) {
         ISpellContainer spellList = ISpellContainer.get(stack);
         if (spellList.isEmpty()) {
            return List.of();
         }

         SpellData spellData = spellList.getSpellAtIndex(0);
         AbstractSpell spell = spellData.getSpell();
         int spellLevel = spell.getLevelFor(spellData.getLevel(), player);
         MutableComponent levelText = getLevelComponenet(spellData, player);
         MutableComponent title = Component.m_237110_("tooltip.irons_spellbooks.level", new Object[]{levelText})
            .m_130946_(" ")
            .m_7220_(
               Component.m_237110_("tooltip.irons_spellbooks.rarity", new Object[]{spell.getRarity(spellData.getLevel()).getDisplayName()})
                  .m_130948_(spell.getRarity(spellData.getLevel()).getDisplayName().m_7383_())
            )
            .m_130940_(ChatFormatting.GRAY);
         List<MutableComponent> uniqueInfo = spell.getUniqueInfo(spellLevel, player);
         MutableComponent whenInSpellBook = Component.m_237115_("tooltip.irons_spellbooks.scroll_tooltip").m_130940_(ChatFormatting.GRAY);
         MutableComponent manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost(spellLevel)).m_130940_(ChatFormatting.BLUE);
         MutableComponent cooldownTime = Component.m_237110_(
               "tooltip.irons_spellbooks.cooldown_length_seconds",
               new Object[]{Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spell, player, CastSource.SCROLL), 2)}
            )
            .m_130940_(ChatFormatting.BLUE);
         MutableComponent castType = null;
         if (spell.getCastType() != CastType.INSTANT) {
            castType = Component.m_237113_(" ")
               .m_7220_(
                  getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(spellLevel, player), 2))
                     .m_130940_(ChatFormatting.BLUE)
               );
         }

         List<Component> lines = new ArrayList<>();
         lines.add(Component.m_237113_(" ").m_7220_(title));
         uniqueInfo.forEach(line -> lines.add(Component.m_237113_(" ").m_7220_(line.m_130948_(line.m_7383_().m_131146_(getStyleFor(player, spell))))));
         if (castType != null) {
            lines.add(castType);
         }

         lines.add(Component.m_237119_());
         lines.add(whenInSpellBook);
         if (spell.getManaCost(spellLevel) > 0) {
            lines.add(manaCost);
         }

         if (spell.getSpellCooldown() > 0) {
            lines.add(cooldownTime);
         }

         lines.add(spell.getSchoolType().getDisplayName().m_6881_());
         return lines;
      } else {
         return List.of();
      }
   }

   public static void addShiftTooltip(List<Component> currentTooltip, List<Component> tooltipToAdd) {
      addShiftTooltip(currentTooltip, Component.m_237115_("tooltip.irons_spellbooks.shift_tooltip").m_130940_(ChatFormatting.GRAY), tooltipToAdd);
   }

   public static void addShiftTooltip(List<Component> currentTooltip, Component shiftHeader, List<Component> tooltipToAdd) {
      if (ClientInputEvents.isShiftKeyDown) {
         currentTooltip.addAll(tooltipToAdd);
      } else {
         currentTooltip.add(shiftHeader);
      }
   }

   public static MutableComponent getLevelComponenet(SpellData spellData, LivingEntity caster) {
      int levelTotal = spellData.getSpell().getLevelFor(spellData.getLevel(), caster);
      int diff = levelTotal - spellData.getLevel();
      if (diff > 0) {
         return Component.m_237110_("tooltip.irons_spellbooks.level_plus", new Object[]{levelTotal, diff});
      } else {
         return diff < 0
            ? Component.m_237110_("tooltip.irons_spellbooks.level_minus", new Object[]{levelTotal, diff})
            : Component.m_237113_(String.valueOf(levelTotal));
      }
   }

   public static MutableComponent getCastTimeComponent(CastType type, String castTime) {
      return switch (type) {
         case CONTINUOUS -> Component.m_237110_("tooltip.irons_spellbooks.cast_continuous", new Object[]{castTime});
         case LONG -> Component.m_237110_("tooltip.irons_spellbooks.cast_long", new Object[]{castTime});
         default -> Component.m_237115_("ui.irons_spellbooks.cast_instant");
      };
   }

   public static MutableComponent getManaCostComponent(CastType castType, int manaCost) {
      return castType == CastType.CONTINUOUS
         ? Component.m_237110_("tooltip.irons_spellbooks.mana_cost_per_second", new Object[]{manaCost * 2})
         : Component.m_237110_("tooltip.irons_spellbooks.mana_cost", new Object[]{manaCost});
   }

   public static MutableComponent getTitleComponent(SpellData spellData, @NotNull LocalPlayer player) {
      MutableComponent levelText = getLevelComponenet(spellData, player);
      AbstractSpell spell = spellData.getSpell();
      return Component.m_237110_("tooltip.irons_spellbooks.selected_spell", new Object[]{spell.getDisplayName(player), levelText})
         .m_130948_(spell.getSchoolType().getDisplayName().m_7383_());
   }

   public static List<FormattedCharSequence> createSpellDescriptionTooltip(AbstractSpell spell, Font font) {
      Player player = MinecraftInstanceHelper.instance.player();
      MutableComponent name = spell.getDisplayName(player);
      List<FormattedCharSequence> description = font.m_92923_(
         Component.m_237115_(String.format("%s.guide", spell.getComponentId())).m_130940_(ChatFormatting.GRAY), 180
      );
      ArrayList<FormattedCharSequence> hoverText = new ArrayList<>();
      hoverText.add(FormattedCharSequence.m_13714_(name.getString(), name.m_7383_().m_131162_(true)));
      if (!spell.obfuscateStats(player)) {
         hoverText.addAll(description);
      }

      return hoverText;
   }

   public static Style getStyleFor(Player player, AbstractSpell spell) {
      return spell.obfuscateStats(player) ? OBFUSCATED_STYLE : INFO_STYLE;
   }
}
