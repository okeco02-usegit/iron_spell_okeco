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
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.spells.FireEruptionAoe;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class RaiseHellSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "raise_hell");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .setAllowCrafting(false)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.recast_count", new Object[]{this.getRecastCount(spellLevel, caster)})
      );
   }

   @Override
   public boolean allowLooting() {
      return false;
   }

   public RaiseHellSpell() {
      this.manaCostPerLevel = 45;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 0;
      this.castTime = 16;
      this.baseManaCost = 90;
   }

   @Override
   public boolean canBeInterrupted(Player player) {
      return false;
   }

   @Override
   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
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
      return Optional.of((SoundEvent)SoundRegistry.RAISE_HELL_PREPARE.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.FIRE_ERUPTION_SLAM.get());
   }

   @Override
   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return spellLevel;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerCooldowns().isOnCooldown(this) && !playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 80, castSource, null), playerMagicData);
      }

      float radius = this.getRadius(spellLevel, entity);
      float range = 1.7F;
      Vec3 hitLocation = Utils.moveToRelativeGroundLevel(
         level,
         Utils.raycastForBlock(level, entity.m_146892_(), entity.m_146892_().m_82549_(entity.m_20156_().m_82542_(range, 0.0, range)), Fluid.NONE).m_82450_(),
         3
      );
      FireEruptionAoe aoe = new FireEruptionAoe(level, radius);
      aoe.m_5602_(entity);
      aoe.setDamage(this.getDamage(spellLevel, entity));
      aoe.m_20219_(hitLocation);
      level.m_7967_(aoe);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20 + (int)radius, hitLocation, radius * 2.0F + 5.0F));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity, MobType.f_21640_);
   }

   private float getRadius(int spellLevel, LivingEntity entity) {
      return 8.0F;
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity, MobType.f_21640_);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, entity) + "";
      }
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public static void ambientParticles(LivingEntity entity, SyncedSpellData spellData) {
      Vec3 vec3 = entity.m_20191_().m_82399_();

      for (int i = 0; i < 2; i++) {
         Vec3 pos = vec3.m_82549_(Utils.getRandomVec3(entity.m_20206_() * 2.0F));
         Vec3 motion = vec3.m_82546_(pos).m_82490_(0.1F);
         entity.f_19853_.m_7106_(ParticleHelper.EMBERS, pos.f_82479_, pos.f_82480_, pos.f_82481_, motion.f_82479_, motion.f_82480_, motion.f_82481_);
      }
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      float range = this.getRadius(spellLevel, mob) * 1.1F;
      return Utils.raycastForBlock(mob.f_19853_, mob.m_20182_(), mob.m_20182_().m_82492_(0.0, 0.5, 0.0), Fluid.NONE).m_6662_() == Type.MISS
         || target.m_20280_(mob) > range * range;
   }
}
