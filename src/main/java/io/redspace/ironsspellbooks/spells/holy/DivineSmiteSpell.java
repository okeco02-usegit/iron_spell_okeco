package io.redspace.ironsspellbooks.spells.holy;

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
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DivineSmiteSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "divine_smite");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public DivineSmiteSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 3;
      this.castTime = 16;
      this.baseManaCost = 30;
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
      return Optional.of((SoundEvent)SoundRegistry.DIVINE_SMITE_WINDUP.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.DIVINE_SMITE_CAST.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 2.2F;
      float range = 1.7F;
      Vec3 smiteLocation = Utils.raycastForBlock(
            level, entity.m_146892_(), entity.m_146892_().m_82549_(entity.m_20156_().m_82542_(range, 0.0, range)), Fluid.NONE
         )
         .m_82450_();
      Vec3 particleLocation = level.m_45547_(new ClipContext(smiteLocation, smiteLocation.m_82520_(0.0, -2.0, 0.0), Block.VISUAL, Fluid.NONE, null))
         .m_82450_()
         .m_82520_(0.0, 0.1, 0.0);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.HOLY.get()).getTargetingColor(), radius * 2.0F),
         particleLocation.f_82479_,
         particleLocation.f_82480_,
         particleLocation.f_82481_,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         level, ParticleTypes.f_175830_, particleLocation.f_82479_, particleLocation.f_82480_, particleLocation.f_82481_, 50, 0.0, 0.0, 0.0, 1.0, false
      );
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, particleLocation, 10.0F));
      List<Entity> entities = level.m_45933_(entity, AABB.m_165882_(smiteLocation, radius * 2.0F, radius * 4.0F, radius * 2.0F));
      SpellDamageSource damageSource = this.getDamageSource(entity);

      for (Entity targetEntity : entities) {
         if (targetEntity.m_6084_()
            && targetEntity.m_6087_()
            && Utils.hasLineOfSight(level, smiteLocation.m_82520_(0.0, 1.0, 0.0), targetEntity.m_20191_().m_82399_(), true)
            && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
            EnchantmentHelper.m_44896_(entity, targetEntity);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity, MobType.f_21641_);
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity, MobType.f_21641_);
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
}
