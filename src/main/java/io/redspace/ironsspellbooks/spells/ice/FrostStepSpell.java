package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.network.particles.FrostStepParticlesPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrostStepSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "frost_step");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(12.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.shatter_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public FrostStepSpell() {
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.baseManaCost = 15;
      this.manaCostPerLevel = 5;
      this.castTime = 0;
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
      return Optional.of((SoundEvent)SoundRegistry.FROST_STEP.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      TeleportSpell.TeleportData teleportData = (TeleportSpell.TeleportData)playerMagicData.getAdditionalCastData();
      FrozenHumanoid shadow = new FrozenHumanoid(level, entity);
      shadow.setShatterDamage(this.getDamage(spellLevel, entity));
      shadow.setDeathTimer(100);
      level.m_7967_(shadow);
      LivingEntity tauntTarget = entity.m_21188_();
      Predicate<Entity> predicate = tauntTarget == null
         ? mob -> entity instanceof Enemy ^ mob instanceof Enemy
         : mob -> mob.getClass().isAssignableFrom(tauntTarget.getClass()) || mob.m_7307_(tauntTarget) || entity instanceof Enemy ^ mob instanceof Enemy;
      Utils.performTaunt(shadow, 10.0F, predicate);
      Vec3 dest = null;
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         dest = potentialTarget;
      }

      if (dest == null) {
         dest = this.findTeleportLocation(spellLevel, level, entity);
      }

      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new FrostStepParticlesPacket(entity.m_20182_(), dest));
      if (entity.m_20159_()) {
         entity.m_8127_();
      }

      Utils.handleSpellTeleport(this, entity, dest);
      entity.m_183634_();
      level.m_6263_(null, dest.f_82479_, dest.f_82480_, dest.f_82481_, this.getCastFinishSound().get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
      playerMagicData.resetAdditionalCastData();
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private Vec3 findTeleportLocation(int spellLevel, Level level, LivingEntity entity) {
      return TeleportSpell.findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
   }

   public static void particleCloud(Level level, Vec3 pos) {
      if (level.f_46443_) {
         double width = 0.5;
         float height = 1.0F;

         for (int i = 0; i < 25; i++) {
            double x = pos.f_82479_ + Utils.random.m_188500_() * width * 2.0 - width;
            double y = pos.f_82480_ + height + Utils.random.m_188500_() * height * 1.2 * 2.0 - height * 1.2;
            double z = pos.f_82481_ + Utils.random.m_188500_() * width * 2.0 - width;
            double dx = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            double dy = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            double dz = Utils.random.m_188500_() * 0.1 * (Utils.random.m_188499_() ? 1 : -1);
            level.m_6493_(ParticleHelper.SNOWFLAKE, true, x, y, z, dx, dy, dz);
            level.m_6493_(ParticleTypes.f_175821_, true, x, y, z, -dx, -dy, -dz);
         }
      }
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return 9.0F + (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * spellLevel * 1.5);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
