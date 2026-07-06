package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.network.particles.ShockwaveParticlesPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HeatSurgeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "heat_surge");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(45.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.rend", new Object[]{Utils.stringTruncation((this.getRendAmplifier(spellLevel, caster) + 1) * 5, 1)}),
         Component.m_237110_("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)})
      );
   }

   public HeatSurgeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 50;
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
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.HEAT_SURGE_PREPARE.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), radius),
         entity.m_20185_(),
         entity.m_20186_() + 0.165F,
         entity.m_20189_(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(
         entity,
         new ShockwaveParticlesPacket(
            new Vec3(entity.m_20185_(), entity.m_20186_() + 0.165F, entity.m_20189_()), radius, (ParticleType)ParticleRegistry.FIRE_PARTICLE.get()
         )
      );
      level.m_6249_(
            entity,
            entity.m_20191_().m_82377_(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity && livingEntity.m_20280_(entity) < radius * radius) {
                  int i = this.getDuration(spellLevel, entity);
                  livingEntity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.REND.get(), i, this.getRendAmplifier(spellLevel, entity)));
                  livingEntity.m_7311_(Math.min(i / 2, 160));
                  MagicManager.spawnParticles(
                     level,
                     ParticleHelper.EMBERS,
                     livingEntity.m_20185_(),
                     livingEntity.m_20186_() + livingEntity.m_20206_() * 0.5F,
                     livingEntity.m_20189_(),
                     50,
                     livingEntity.m_20205_() * 0.5F,
                     livingEntity.m_20206_() * 0.5F,
                     livingEntity.m_20205_() * 0.5F,
                     0.03,
                     false
                  );
               }
            }
         );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 6.0F + spellLevel * 0.5F;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }

   public int getRendAmplifier(int spellLevel, LivingEntity caster) {
      return 1 + spellLevel;
   }

   @Override
   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   @Override
   public boolean stopSoundOnCancel() {
      return true;
   }
}
