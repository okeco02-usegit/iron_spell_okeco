package io.redspace.ironsspellbooks.api.spells;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public enum SpellRarity implements StringRepresentable {
   COMMON(0),
   UNCOMMON(1),
   RARE(2),
   EPIC(3),
   LEGENDARY(4);

   private final int value;
   public static final Codec<SpellRarity> CODEC = StringRepresentable.m_216439_(SpellRarity::values);
   private static List<Double> rawRarityConfig;
   private static List<Double> rarityConfig = null;
   private final MutableComponent[] DISPLAYS = new MutableComponent[]{
      Component.m_237115_("rarity.irons_spellbooks.common").m_130940_(ChatFormatting.GRAY),
      Component.m_237115_("rarity.irons_spellbooks.uncommon").m_130940_(ChatFormatting.GREEN),
      Component.m_237115_("rarity.irons_spellbooks.rare").m_130940_(ChatFormatting.AQUA),
      Component.m_237115_("rarity.irons_spellbooks.epic").m_130940_(ChatFormatting.LIGHT_PURPLE),
      Component.m_237115_("rarity.irons_spellbooks.legendary").m_130940_(ChatFormatting.GOLD),
      Component.m_237115_("rarity.irons_spellbooks.mythic").m_130940_(ChatFormatting.GOLD),
      Component.m_237115_("rarity.irons_spellbooks.ancient").m_130940_(ChatFormatting.GOLD)
   };

   SpellRarity(int newValue) {
      this.value = newValue;
   }

   public int getValue() {
      return this.value;
   }

   public MutableComponent getDisplayName() {
      return this.DISPLAYS[this.getValue()];
   }

   public static List<Double> getRawRarityConfig() {
      if (rarityConfig == null) {
         rawRarityConfig = getRawRarityConfigInternal();
      }

      return rawRarityConfig;
   }

   private static List<Double> getRawRarityConfigInternal() {
      List<Double> fromConfig = (List<Double>)ServerConfigs.RARITY_CONFIG.get();
      if (fromConfig.size() != 5) {
         List<Double> configDefault = (List<Double>)ServerConfigs.RARITY_CONFIG.getDefault();
         IronsSpellbooks.LOGGER.info("INVALID RARITY CONFIG FOUND (Size != 5): {} FALLING BACK TO DEFAULT: {}", fromConfig, configDefault);
         return configDefault;
      } else if (fromConfig.stream().mapToDouble(a -> a).sum() != 1.0) {
         List<Double> configDefault = (List<Double>)ServerConfigs.RARITY_CONFIG.getDefault();
         IronsSpellbooks.LOGGER.info("INVALID RARITY CONFIG FOUND (Values must add up to 1): {} FALLING BACK TO DEFAULT: {}", fromConfig, configDefault);
         return configDefault;
      } else {
         return fromConfig;
      }
   }

   public static List<Double> getRarityConfig() {
      if (rarityConfig == null) {
         AtomicDouble counter = new AtomicDouble();
         rarityConfig = new ArrayList<>();
         getRawRarityConfig().forEach(item -> rarityConfig.add(counter.addAndGet(item)));
      }

      return rarityConfig;
   }

   public int compareRarity(SpellRarity other) {
      return Integer.compare(this.getValue(), other.getValue());
   }

   public static void rarityTest() {
      StringBuilder sb = new StringBuilder();
      SpellRegistry.REGISTRY.get().getValues().forEach(s -> {
         sb.append(String.format("\nSpellType:%s\n", s));
         sb.append(String.format("\tMinRarity:%s, MaxRarity:%s\n", s.getMinRarity(), s.getMaxRarity()));
         sb.append(String.format("\tMinLevel:%s, MaxLevel:%s\n", s.getMinLevel(), s.getMaxLevel()));
         sb.append(String.format("\tRawRarityConfig:%s\n", getRawRarityConfig().stream().map(Object::toString).collect(Collectors.joining(","))));
         sb.append(String.format("\tRarityConfig:%s\n", getRarityConfig().stream().map(Object::toString).collect(Collectors.joining(","))));

         for (int i = s.getMinLevel(); i <= s.getMaxLevel(); i++) {
            List<Double> rarityConfig = getRawRarityConfig();
            double d = (double)i / s.getMaxLevel();
            int start = s.getMinRarity();
            int end = s.getMaxRarity();
            List<Double> modifiedRarityBrackets = rarityConfig.subList(start, end + 1);
            double total = modifiedRarityBrackets.stream().mapToDouble(a -> a).sum();
            double current = 0.0;
            SpellRarity rarity = null;

            for (int j = 0; j < modifiedRarityBrackets.size(); j++) {
               current += modifiedRarityBrackets.get(j) / total;
               if (d <= current) {
                  rarity = values()[j + s.getMinRarity()];
                  break;
               }
            }

            if (rarity == null) {
               throw new RuntimeException();
            }

            sb.append(String.format("\t\tLevel %s -> %s\n", i, s.getRarity(i)));
            sb.append(String.format("\t\tTESTL %s -> %s\n", i, rarity));
            sb.append(String.format("\t\tEQUAL:%s\n", rarity == s.getRarity(i)));
         }

         sb.append("\n");

         for (int i = s.getMinRarity(); i <= s.getMaxRarity(); i++) {
            sb.append(String.format("\t\t%s -> Level %s\n", values()[i], s.getMinLevelForRarity(values()[i])));
         }
      });
      IronsSpellbooks.LOGGER.debug(sb.toString());
   }

   public ChatFormatting getChatFormatting() {
      return switch (this) {
         case COMMON -> ChatFormatting.GRAY;
         case UNCOMMON -> ChatFormatting.GREEN;
         case RARE -> ChatFormatting.AQUA;
         case EPIC -> ChatFormatting.LIGHT_PURPLE;
         case LEGENDARY -> ChatFormatting.GOLD;
      };
   }

   public String m_7912_() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
