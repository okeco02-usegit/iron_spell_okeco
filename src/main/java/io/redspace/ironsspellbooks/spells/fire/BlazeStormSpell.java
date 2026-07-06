package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlazeStormSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blaze_storm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public BlazeStormSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 55;
      this.baseManaCost = 5;
   }

   @Override
   public int getCastTime(int spellLevel) {
      return this.castTime + 5 * spellLevel;
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
      return Optional.of(SoundEvents.f_11701_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
         this.shootBlazeFireball(level, spellLevel, entity);
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.4F;
   }

   public void shootBlazeFireball(Level world, int spellLevel, LivingEntity entity) {
      Vec3 origin = entity.m_146892_().m_82549_(entity.m_20156_().m_82541_().m_82490_(0.4F));
      SmallMagicFireball fireball = new SmallMagicFireball(world, entity);
      fireball.m_146884_(origin.m_82492_(0.0, fireball.m_20206_(), 0.0));
      fireball.shoot(entity.m_20154_(), 0.05F);
      fireball.setDamage(this.getDamage(spellLevel, entity));
      world.m_6263_(null, origin.f_82479_, origin.f_82480_, origin.f_82481_, SoundEvents.f_11705_, SoundSource.PLAYERS, 2.0F, 1.0F);
      world.m_7967_(fireball);
   }

   @Override
   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFireTicks(40).setIFrames(0);
   }
}
