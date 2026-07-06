package io.redspace.ironsspellbooks.spells.nature;

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
import io.redspace.ironsspellbooks.entity.spells.StompAoe;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class StompSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "stomp");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(16.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{this.getRange(spellLevel, caster)})
      );
   }

   public StompSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 2;
      this.castTime = 10;
      this.baseManaCost = 50;
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.EARTHQUAKE_CAST.get());
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
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
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 spawn = Utils.moveToRelativeGroundLevel(level, entity.m_146892_().m_82549_(entity.m_20156_().m_82542_(1.0, 0.0, 1.0)), 1);
      BlockPos bpos = BlockPos.m_274446_(spawn);
      ((ServerLevel)level)
         .m_8767_(
            new BlockParticleOption(ParticleTypes.f_123794_, level.m_8055_(bpos)).setPos(bpos),
            spawn.f_82479_,
            spawn.f_82480_,
            spawn.f_82481_,
            40,
            0.0,
            0.0,
            0.0,
            0.2 + 0.05F * spellLevel
         );
      StompAoe stomp = new StompAoe(level, this.getRange(spellLevel, entity), entity.m_146908_());
      stomp.m_20219_(spawn);
      stomp.setDamage(this.getDamage(spellLevel, entity));
      stomp.setExplosionRadius(this.getEntityPowerMultiplier(entity));
      stomp.m_5602_(entity);
      level.m_7967_(stomp);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private int getRange(int spellLevel, LivingEntity caster) {
      return (int)(4.0F + spellLevel * this.getEntityPowerMultiplier(caster));
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.STOMP;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      float f = this.getRange(spellLevel, mob);
      return mob.m_20280_(target) > f * f * 1.2;
   }
}
