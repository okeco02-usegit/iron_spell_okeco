package io.redspace.ironsspellbooks.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SonicBoomSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sonic_boom");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(25.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel, caster), 1)})
      );
   }

   public SonicBoomSpell() {
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 8;
      this.castTime = 30;
      this.baseManaCost = 110;
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
      return Optional.of(SoundEvents.f_215772_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.SONIC_BOOM.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, entity.m_20182_(), 20.0F));
      float range = getRange(spellLevel, entity);
      Vec3 start = entity.m_146892_();
      Vec3 end = start.m_82549_(entity.m_20156_().m_82490_(range));
      AABB boundingBox = entity.m_20191_().m_82369_(end.m_82546_(start));

      for (Entity target : level.m_45933_(entity, boundingBox)) {
         HitResult hit = Utils.checkEntityIntersecting(target, start, end, 0.4F);
         if (hit.m_6662_() != Type.MISS) {
            DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
         }
      }

      Vec3 vec3 = entity.m_20154_().m_82541_();

      for (int i = 0; i < range; i++) {
         Vec3 vec32 = vec3.m_82490_(i).m_82549_(entity.m_146892_());
         MagicManager.spawnParticles(level, ParticleTypes.f_235902_, vec32.f_82479_, vec32.f_82480_, vec32.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, false);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public void playSound(Optional<SoundEvent> sound, Entity entity) {
      if (sound == this.getCastFinishSound()) {
         entity.m_5496_(sound.get(), 3.5F, 0.9F + entity.f_19853_.f_46441_.m_188501_() * 0.2F);
      } else {
         super.playSound(sound, entity);
      }
   }

   public static float getRange(int level, LivingEntity caster) {
      return 15 + 5 * level;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }
}
