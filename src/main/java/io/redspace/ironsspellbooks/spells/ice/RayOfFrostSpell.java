package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostVisualEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class RayOfFrostSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ray_of_frost");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.freeze_time", new Object[]{Utils.timeFromTicks(this.getFreezeTime(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel, caster), 1)})
      );
   }

   public RayOfFrostSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 25;
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
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAY_OF_FROST.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      HitResult hitResult = Utils.raycastForEntity(level, entity, getRange(spellLevel, entity), true, 0.15F);
      level.m_7967_(new RayOfFrostVisualEntity(level, entity.m_146892_(), hitResult.m_82450_(), entity));
      if (hitResult.m_6662_() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).m_82443_();
         DamageSources.applyDamage(
            target,
            this.getDamage(spellLevel, entity),
            this.getDamageSource(entity).setFreezeTicks(target.m_146891_() + this.getFreezeTime(spellLevel, entity))
         );
         MagicManager.spawnParticles(
            level, ParticleHelper.ICY_FOG, hitResult.m_82450_().f_82479_, target.m_20186_(), hitResult.m_82450_().f_82481_, 4, 0.0, 0.0, 0.0, 0.3, true
         );
      } else if (hitResult.m_6662_() == Type.BLOCK) {
         MagicManager.spawnParticles(
            level,
            ParticleHelper.ICY_FOG,
            hitResult.m_82450_().f_82479_,
            hitResult.m_82450_().f_82480_,
            hitResult.m_82450_().f_82481_,
            4,
            0.0,
            0.0,
            0.0,
            0.3,
            true
         );
      }

      MagicManager.spawnParticles(
         level,
         ParticleHelper.SNOWFLAKE,
         hitResult.m_82450_().f_82479_,
         hitResult.m_82450_().f_82480_,
         hitResult.m_82450_().f_82481_,
         50,
         0.0,
         0.0,
         0.0,
         0.3,
         false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static float getRange(int level, LivingEntity caster) {
      return 30.0F;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 3.0F + this.getSpellPower(spellLevel, caster) * 1.5F;
   }

   private int getFreezeTime(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 15.0F);
   }
}
