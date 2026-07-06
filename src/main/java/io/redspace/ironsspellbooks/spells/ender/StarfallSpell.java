package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class StarfallSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "starfall");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(16.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public StarfallSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 160;
      this.baseManaCost = 5;
   }

   @Override
   public CastType getCastType() {
      return CastType.CONTINUOUS;
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
      return Optional.of((SoundEvent)SoundRegistry.ENDER_CAST.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!(playerMagicData.getAdditionalCastData() instanceof StarfallSpell.StarfallCastData)) {
         Vec3 targetArea = Utils.moveToRelativeGroundLevel(world, Utils.raycastForEntity(world, entity, 40.0F, true).m_82450_(), 12);
         playerMagicData.setAdditionalCastData(new StarfallSpell.StarfallCastData(targetArea));
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public static void particleTrail(Level level, Vec3 a, Vec3 b, ParticleOptions particleType) {
      double d = a.m_82554_(b) * 4.0;

      for (int i = 0; i < d; i++) {
         double p = i / d;
         Vec3 vec = a.m_82549_(b.m_82546_(a).m_82490_(p));
         MagicManager.spawnParticles(level, particleType, vec.f_82479_, vec.f_82480_, vec.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, true);
      }
   }

   @Override
   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && playerMagicData.getAdditionalCastData() instanceof StarfallSpell.StarfallCastData castData) {
         float var16 = this.getRadius(entity);
         int tick = playerMagicData.getCastDurationRemaining() - 1;
         if (tick % 20 == 0) {
            castData.updateTrackedEntities(
               level.m_6249_(
                  entity,
                  AABB.m_165882_(castData.center, var16 * 3.0F, var16, var16 * 3.0F),
                  e -> e instanceof LivingEntity && !DamageSources.isFriendlyFireBetween(entity, e)
               )
            );
         }

         if (tick % 4 == 0) {
            for (int i = 0; i < 2; i++) {
               Vec3 center = castData.center;
               Vec3 weightedArea = Vec3.f_82478_;

               for (Entity target : castData.trackedEntities) {
                  weightedArea = weightedArea.m_82549_(target.m_20182_().m_82546_(center).m_82490_(1.0F / castData.trackedEntities.size()));
               }

               double spawnRadius = Mth.m_14085_(var16, var16 * 0.5, weightedArea.m_82553_() / var16);
               Vec3 spawnTarget = Utils.moveToRelativeGroundLevel(
                     level,
                     center.m_82549_(weightedArea)
                        .m_82549_(
                           new Vec3(0.0, 0.0, entity.m_217043_().m_188501_() * spawnRadius)
                              .m_82524_(entity.m_217043_().m_188503_(360) * (float) (Math.PI / 180.0))
                        ),
                     3
                  )
                  .m_82520_(0.0, 0.5, 0.0);
               Vec3 trajectory = new Vec3(0.15F, -0.85F, 0.0).m_82541_();
               Vec3 spawn = Utils.raycastForBlock(level, spawnTarget, spawnTarget.m_82549_(trajectory.m_82490_(-12.0)), Fluid.NONE)
                  .m_82450_()
                  .m_82549_(trajectory);
               this.shootComet(level, spellLevel, entity, spawn, trajectory);
               MagicManager.spawnParticles(level, ParticleHelper.COMET_FOG, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, 1, 1.0, 1.0, 1.0, 1.0, false);
               MagicManager.spawnParticles(level, ParticleHelper.COMET_FOG, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, 1, 1.0, 1.0, 1.0, 1.0, true);
            }
         }
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.5F;
   }

   private float getRadius(LivingEntity caster) {
      return 6.0F;
   }

   public void shootComet(Level world, int spellLevel, LivingEntity entity, Vec3 spawn, Vec3 trajectory) {
      Comet fireball = new Comet(world, entity);
      fireball.m_146884_(spawn.m_82520_(-1.0, 0.0, 0.0));
      fireball.shoot(trajectory, 0.075F);
      fireball.setDamage(this.getDamage(spellLevel, entity));
      fireball.setExplosionRadius(2.0F);
      world.m_7967_(fireball);
      world.m_6263_(
         null, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, SoundEvents.f_11932_, SoundSource.PLAYERS, 3.0F, 0.7F + Utils.random.m_188501_() * 0.3F
      );
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }

   public static class StarfallCastData implements ICastData {
      Vec3 center;
      final List<Entity> trackedEntities = new ArrayList<>();

      public StarfallCastData(Vec3 center) {
         this.center = center;
      }

      @Override
      public void reset() {
         this.trackedEntities.clear();
      }

      public void updateTrackedEntities(List<Entity> entities) {
         this.trackedEntities.clear();
         this.trackedEntities.addAll(entities);
      }
   }
}
