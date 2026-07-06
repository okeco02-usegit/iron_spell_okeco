package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.IronsSpellbooks;
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
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CleanseSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "cleanse");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(60.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{3}));
   }

   public CleanseSpell() {
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
      this.castTime = 60;
      this.baseManaCost = 100;
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
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
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.CLEANSE_CAST.get());
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      float radius = 3.0F;
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, entity.m_20182_(), radius, Utils.packRGB(this.getTargetingColor()), entity);
      playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.m_20182_(), area));
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      level.m_45976_(LivingEntity.class, AABB.m_165882_(entity.m_20191_().m_82399_(), 6.0, 6.0, 6.0))
         .forEach(
            livingEntity -> {
               IronsSpellbooks.LOGGER.debug("cleanse: {}", livingEntity);
               if (Utils.shouldHealEntity(entity, livingEntity)) {
                  List<MobEffect> effects = livingEntity.m_21220_()
                     .stream()
                     .<MobEffect>map(MobEffectInstance::m_19544_)
                     .filter(
                        effect -> effect.m_19483_() == MobEffectCategory.HARMFUL
                           && !BuiltInRegistries.f_256974_.m_263177_(effect).m_203656_(ModTags.CLEANSE_IMMUNE)
                     )
                     .toList();
                  effects.forEach(livingEntity::m_21195_);
                  MagicManager.spawnParticles(
                     level,
                     ParticleHelper.CLEANSE_PARTICLE,
                     livingEntity.m_20185_(),
                     livingEntity.m_20186_() + 0.25,
                     livingEntity.m_20189_(),
                     15,
                     livingEntity.m_20205_() * 0.5,
                     livingEntity.m_20205_() * 0.5,
                     livingEntity.m_20205_() * 0.5,
                     0.0,
                     false
                  );
               }
            }
         );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CAST_KNEELING_PRAYER;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SELF_CAST_TWO_HANDS;
   }
}
