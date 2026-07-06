package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.EchoingStrikesEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EchoingStrikesSpell extends AbstractSpell {
   public static final float radius = 2.0F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "echoing_strikes");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(60.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_(
            "ui.irons_spellbooks.percent_damage",
            new Object[]{Utils.stringTruncation(EchoingStrikesEffect.getDamageModifier(this.getAmplifierForLevel(spellLevel, caster), caster) * 100.0F, 0)}
         ),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{2.0F}),
         Component.m_237110_("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)})
      );
   }

   public EchoingStrikesSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.m_7292_(
         new MobEffectInstance(
            (MobEffect)MobEffectRegistry.ECHOING_STRIKES.get(),
            (int)(this.getSpellPower(spellLevel, entity) * 20.0F),
            this.getAmplifierForLevel(spellLevel, entity),
            false,
            false,
            true
         )
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getAmplifierForLevel(int spellLevel, LivingEntity caster) {
      return 1 + spellLevel;
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
