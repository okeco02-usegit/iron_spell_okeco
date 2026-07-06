package io.redspace.ironsspellbooks.spells.lightning;

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
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ShockwaveSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "shockwave");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(30.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)})
      );
   }

   public ShockwaveSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 16;
      this.baseManaCost = 70;
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
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_PREPARE.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_CAST.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      Vector3f edge = new Vector3f(0.7F, 1.0F, 1.0F);
      Vector3f center = new Vector3f(1.0F, 1.0F, 1.0F);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(edge, radius * 1.02F),
         entity.m_20185_(),
         entity.m_20186_() + 0.15F,
         entity.m_20189_(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(edge, radius * 0.98F),
         entity.m_20185_(),
         entity.m_20186_() + 0.15F,
         entity.m_20189_(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         level, new BlastwaveParticleOptions(center, radius), entity.m_20185_(), entity.m_20186_() + 0.165F, entity.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0, true
      );
      MagicManager.spawnParticles(
         level, new BlastwaveParticleOptions(center, radius), entity.m_20185_(), entity.m_20186_() + 0.135F, entity.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0, true
      );
      MagicManager.spawnParticles(
         level, ParticleHelper.ELECTRICITY, entity.m_20185_(), entity.m_20186_() + 1.0, entity.m_20189_(), 80, 0.25, 0.25, 0.25, 0.7F + radius * 0.1F, false
      );
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, entity.m_20182_(), radius * 2.0F));
      Vec3 start = entity.m_20191_().m_82399_();
      float damage = this.getDamage(spellLevel, entity);
      LightningBolt dummyLightningBolt = new LightningBolt(EntityType.f_20465_, level);
      dummyLightningBolt.setDamage(0.0F);
      dummyLightningBolt.m_20874_(true);
      level.m_6249_(
            entity,
            entity.m_20191_().m_82377_(radius, radius, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity && this.canHit(entity, target) && livingEntity.m_20280_(entity) < radius * radius) {
                  Vec3 destx = livingEntity.m_20191_().m_82399_();
                  ((ServerLevel)level).m_8767_(new ZapParticleOption(destx), start.f_82479_, start.f_82480_, start.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
                  MagicManager.spawnParticles(
                     level,
                     ParticleHelper.ELECTRICITY,
                     livingEntity.m_20185_(),
                     livingEntity.m_20186_() + livingEntity.m_20206_() / 2.0F,
                     livingEntity.m_20189_(),
                     10,
                     livingEntity.m_20205_() / 3.0F,
                     livingEntity.m_20206_() / 3.0F,
                     livingEntity.m_20205_() / 3.0F,
                     0.1,
                     false
                  );
                  DamageSources.applyDamage(target, damage, this.getDamageSource(entity));
                  if (target instanceof Creeper creeper) {
                     creeper.m_8038_((ServerLevel)level, dummyLightningBolt);
                  }
               }
            }
         );

      for (int i = 0; i < 7; i++) {
         Vec3 dest = start.m_82549_(Utils.getRandomVec3(1.0).m_82542_(4.0, 2.5, 4.0).m_82520_(0.0, 4.0, 0.0));
         ((ServerLevel)level).m_8767_(new ZapParticleOption(dest), start.f_82479_, start.f_82480_, start.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private boolean canHit(Entity owner, Entity target) {
      return target != owner && target.m_6084_() && target.m_6087_() && !target.m_5833_();
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 8 + spellLevel;
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 4.0F + this.getSpellPower(spellLevel, caster) * 0.75F;
   }

   @Override
   public void playSound(Optional<SoundEvent> sound, Entity entity) {
      sound.ifPresent(soundEvent -> entity.m_5496_(soundEvent, 3.0F, 0.9F + Utils.random.m_188501_() * 0.2F));
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.PREPARE_CROSS_ARMS;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.CAST_T_POSE;
   }
}
