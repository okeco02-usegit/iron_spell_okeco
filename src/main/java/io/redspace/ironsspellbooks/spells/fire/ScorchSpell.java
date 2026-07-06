package io.redspace.ironsspellbooks.spells.fire;

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
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ScorchSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "scorch");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(12.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public ScorchSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
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
      return Optional.of((SoundEvent)SoundRegistry.SCORCH_PREPARE.get());
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      float radius = this.getRadius(entity);
      HitResult hitResult = Utils.raycastForEntity(level, entity, 32.0F, true, 0.2F);
      Vec3 location = Utils.moveToRelativeGroundLevel(level, hitResult.m_82450_(), 3, 6);
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, location, radius, Utils.packRGB(this.getTargetingColor()));
      playerMagicData.setAdditionalCastData(new TargetAreaCastData(location, area));
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData castData) {
         Vec3 targetArea = castData.getCenter();
         MagicManager.spawnParticles(
            level, ParticleTypes.f_123756_, targetArea.f_82479_, targetArea.f_82480_, targetArea.f_82481_, 25, 1.0, 1.0, 1.0, 1.0, true
         );
         MagicManager.spawnParticles(
            level, ParticleTypes.f_123756_, targetArea.f_82479_, targetArea.f_82480_ + 1.0, targetArea.f_82481_, 25, 0.25, 1.5, 0.25, 1.0, false
         );
         level.m_6263_(
            null,
            targetArea.f_82479_,
            targetArea.f_82480_,
            targetArea.f_82481_,
            (SoundEvent)SoundRegistry.FIERY_EXPLOSION.get(),
            SoundSource.PLAYERS,
            2.0F,
            Utils.random.m_216332_(8, 12) * 0.1F
         );
         float radius = castData.getCastingEntity().getRadius();
         float radiusSqr = radius * radius;
         float damage = this.getDamage(spellLevel, entity);
         SpellDamageSource source = this.getDamageSource(entity);
         level.m_6443_(
               LivingEntity.class,
               new AABB(targetArea.m_82492_(radius, radius, radius), targetArea.m_82520_(radius, radius, radius)),
               livingEntity -> livingEntity != entity
                  && this.horizontalDistanceSqr(livingEntity, targetArea) < radiusSqr
                  && livingEntity.m_6087_()
                  && !DamageSources.isFriendlyFireBetween(livingEntity, entity)
                  && Utils.hasLineOfSight(level, targetArea.m_82520_(0.0, 1.5, 0.0), livingEntity.m_20191_().m_82399_(), true)
            )
            .forEach(livingEntity -> {
               DamageSources.applyDamage(livingEntity, damage, source);
               DamageSources.ignoreNextKnockback(livingEntity);
            });
         FireField fire = new FireField(level);
         fire.m_5602_(entity);
         fire.setDuration(200);
         fire.setDamage(damage * 0.1F);
         fire.setRadius(radius);
         fire.setCircular();
         fire.m_20219_(targetArea);
         level.m_7967_(fire);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float horizontalDistanceSqr(LivingEntity livingEntity, Vec3 vec3) {
      double dx = livingEntity.m_20185_() - vec3.f_82479_;
      double dz = livingEntity.m_20189_() - vec3.f_82481_;
      return (float)(dx * dx + dz * dz);
   }

   @Override
   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFireTicks(60);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private float getRadius(LivingEntity caster) {
      return 2.5F;
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
