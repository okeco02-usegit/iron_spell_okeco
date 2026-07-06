package io.redspace.ironsspellbooks.config;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.RecastOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ClientConfigs {
   public static final Builder BUILDER = new Builder();
   public static final ConfigValue<Boolean> SHOW_FIRST_PERSON_ARMS = BUILDER.define("showFirstPersonArms", true);
   public static final ConfigValue<Boolean> SHOW_FIRST_PERSON_ITEMS = BUILDER.define("showFirstPersonItems", true);
   public static final ConfigValue<Boolean> REPLACE_GHAST_FIREBALL = BUILDER.define("replaceGhastFireballs", true);
   public static final ConfigValue<Boolean> REPLACE_BLAZE_FIREBALL = BUILDER.define("replaceBlazeFireballs", true);
   public static final ConfigValue<Integer> MANA_BAR_Y_OFFSET = BUILDER.define("manaBarYOffset", 0);
   public static final ConfigValue<Integer> MANA_BAR_X_OFFSET = BUILDER.define("manaBarXOffset", 0);
   public static final ConfigValue<Integer> MANA_TEXT_X_OFFSET = BUILDER.define("manaTextXOffset", 0);
   public static final ConfigValue<Integer> MANA_TEXT_Y_OFFSET = BUILDER.define("manaTextYOffset", 0);
   public static final ConfigValue<Boolean> MANA_BAR_TEXT_VISIBLE = BUILDER.define("manaBarTextVisible", true);
   public static final ConfigValue<Boolean> ENABLE_BOSS_MUSIC = BUILDER.define("enableBossMusic", true);
   public static final ConfigValue<ManaBarOverlay.Anchor> MANA_BAR_ANCHOR = BUILDER.defineEnum("manaBarAnchor", ManaBarOverlay.Anchor.Hunger);
   public static final ConfigValue<ManaBarOverlay.Display> MANA_BAR_DISPLAY = BUILDER.defineEnum("manaBarDisplay", ManaBarOverlay.Display.Contextual);
   public static final ConfigValue<ManaBarOverlay.Display> SPELL_BAR_DISPLAY = BUILDER.defineEnum("spellBarDisplay", ManaBarOverlay.Display.Always);
   public static final ConfigValue<Integer> SPELL_BAR_Y_OFFSET = BUILDER.define("spellBarYOffset", 0);
   public static final ConfigValue<Integer> SPELL_BAR_X_OFFSET = BUILDER.define("spellBarXOffset", 0);
   public static final ConfigValue<SpellBarOverlay.Anchor> SPELL_BAR_ANCHOR = BUILDER.defineEnum("spellBarAnchor", SpellBarOverlay.Anchor.Hotbar);
   public static final ConfigValue<Boolean> SHIELD_PARTICLE_COLLISIONS = BUILDER.comment(
         "Whether shield spells can collide with particles. Can affect performance. Default: true"
      )
      .define("shieldParticleCollisions", true);
   public static final ConfigValue<Boolean> SPELL_WHEEL_CONSISTENT_SIZE = BUILDER.comment("Whether to Spell Wheel size ignores the Gui scale option")
      .define("ignoreGuiScale", false);
   public static final ConfigValue<Double> SPELL_WHEEL_SCALE = BUILDER.comment("If ignoreGuiScale is enabled, apply this multiplier to its size")
      .define("ignoreGuiScaleSizeMultiplier", 1.0);
   public static final ConfigValue<Boolean> SUMMONS_GLOW = BUILDER.comment("Whether owned summons appear glowing to yourself. Default: true")
      .define("ownedSummonsGlow", true);
   public static final ConfigValue<String> SUMMONS_GLOW_HEX_COLOR = BUILDER.comment("Hex Color Value of owned summons glow outline. Default: 0xAAFFAA")
      .define("summonGlowColor", "0xAAFFAA");
   public static final ConfigValue<RecastOverlay.Anchor> RECAST_ANCHOR = BUILDER.defineEnum("recastAnchor", RecastOverlay.Anchor.TopCenter);
   public static final ConfigValue<Integer> RECAST_Y_OFFSET = BUILDER.define("recastYOffset", 0);
   public static final ConfigValue<Integer> RECAST_X_OFFSET = BUILDER.define("recastXOffset", 0);
   public static final ForgeConfigSpec SPEC = BUILDER.build();
   public static int summonGlowColor;

   public static void onConfigReload() {
      try {
         summonGlowColor = Integer.decode((String)SUMMONS_GLOW_HEX_COLOR.get());
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.warn("Failed to parse summonGlowColor \"{}\", reverting to default", SUMMONS_GLOW_HEX_COLOR.get());
         summonGlowColor = 11206570;
      }
   }

   static {
      BUILDER.comment("##############################################################################################");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##   ATTENTION: These are client configs. For gameplay settings, go to the SERVER CONFIGS   ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##                                                                                          ##");
      BUILDER.comment("##############################################################################################");
      BUILDER.comment("");
      BUILDER.push("UI");
      BUILDER.push("ManaBar");
      BUILDER.comment("By default (Contextual), the mana bar only appears when you are holding a magic item or are not at max mana.");
      BUILDER.comment("Used to adjust mana bar's position (11 is one full hunger bar up).");
      BUILDER.pop();
      BUILDER.push("SpellBar");
      BUILDER.comment("By default (Always), the spell bar always shows the spells in your equipped spellbook. Contextual will hide them when not in use.");
      BUILDER.comment("Used to adjust spell bar's position.");
      BUILDER.pop();
      BUILDER.push("RecastOverlay");
      BUILDER.pop();
      BUILDER.push("SpellWheel");
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.push("Animations");
      BUILDER.comment("What to render in first person while casting.");
      BUILDER.pop();
      BUILDER.push("Renderers");
      BUILDER.comment("By default, both fireballs are replaced with an enhanced model used by fire spells.");
      BUILDER.pop();
      BUILDER.push("Music");
      BUILDER.pop();
      BUILDER.push("Misc");
      BUILDER.pop();
      BUILDER.push("Summons");
      BUILDER.pop();
   }
}
