package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class BloodStepSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blood_step");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(12.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   public BloodStepSpell() {
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 4;
      this.baseManaCost = 30;
      this.manaCostPerLevel = 10;
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
      return Optional.of((SoundEvent)SoundRegistry.BLOOD_STEP.get());
   }

   @Override
   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
      Vec3 forward = entity.m_20156_().m_82541_();

      for (int i = 0; i < 35; i++) {
         Vec3 motion = forward.m_82490_(Utils.random.m_188500_() * 0.25);
         level.m_7106_(
            ParticleTypes.f_123777_, entity.m_20208_(0.4F), entity.m_20187_(), entity.m_20262_(0.4F), motion.f_82479_, motion.f_82480_, motion.f_82481_
         );
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 dest = null;
      TeleportSpell.TeleportData teleportData = (TeleportSpell.TeleportData)playerMagicData.getAdditionalCastData();
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         if (potentialTarget != null) {
            dest = potentialTarget;
            Utils.handleSpellTeleport(this, entity, dest);
         }
      } else {
         HitResult hitResult = Utils.raycastForEntity(level, entity, this.getDistance(spellLevel, entity), true);
         if (entity.m_20159_()) {
            entity.m_8127_();
         }

         if (hitResult.m_6662_() == Type.ENTITY && ((EntityHitResult)hitResult).m_82443_() instanceof LivingEntity target) {
            for (int i = 0; i < 8; i++) {
               dest = target.m_20182_().m_82546_(new Vec3(0.0, 0.0, 1.5).m_82524_(-(target.m_146908_() + i * 45) * (float) (Math.PI / 180.0)));
               if (level.m_8055_(BlockPos.m_274446_(dest).m_7494_()).m_60795_()) {
                  break;
               }
            }

            Utils.handleSpellTeleport(this, entity, dest.m_82520_(0.0, 1.0, 0.0));
            entity.m_7618_(Anchor.EYES, target.m_146892_().m_82492_(0.0, 0.15, 0.0));
         } else {
            dest = TeleportSpell.findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
            Utils.handleSpellTeleport(this, entity, dest);
         }
      }

      entity.m_183634_();
      level.m_6263_(null, dest.f_82479_, dest.f_82480_, dest.f_82481_, this.getCastFinishSound().get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
      entity.m_6842_(true);
      entity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get(), 100, 0, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * this.getSpellPower(spellLevel, null));
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
