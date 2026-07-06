package io.redspace.ironsspellbooks.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SculkTentaclesSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sculk_tentacles");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(30.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRings(spellLevel, caster) * 1.3F, 1)})
      );
   }

   public SculkTentaclesSpell() {
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 3;
      this.castTime = 20;
      this.baseManaCost = 150;
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
      return Optional.of((SoundEvent)SoundRegistry.VOID_TENTACLES_START.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.VOID_TENTACLES_FINISH.get());
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.15F, false);
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int rings = this.getRings(spellLevel, entity);
      int count = 2;
      Vec3 center = null;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
         if (target != null) {
            center = target.m_20182_();
         }
      }

      if (center == null) {
         center = Utils.raycastForEntity(level, entity, 48.0F, true, 0.15F).m_82450_();
         center = Utils.moveToRelativeGroundLevel(level, center, 6);
      }

      level.m_6263_(
         entity instanceof Player player ? player : null,
         center.f_82479_,
         center.f_82480_,
         center.f_82481_,
         (SoundEvent)SoundRegistry.VOID_TENTACLES_FINISH.get(),
         SoundSource.AMBIENT,
         1.0F,
         1.0F
      );

      for (int r = 0; r < rings; r++) {
         float tentacles = count + r * 2;

         for (int i = 0; i < tentacles; i++) {
            Vec3 random = new Vec3(Utils.getRandomScaled(1.0), Utils.getRandomScaled(1.0), Utils.getRandomScaled(1.0));
            Vec3 spawn = center.m_82549_(new Vec3(0.0, 0.0, 1.3 * (r + 1)).m_82524_(6.281F / tentacles * i)).m_82549_(random);
            spawn = Utils.moveToRelativeGroundLevel(level, spawn, 8);
            if (!level.m_8055_(BlockPos.m_274446_(spawn).m_7495_()).m_60795_()) {
               VoidTentacle tentacle = new VoidTentacle(level, entity, this.getDamage(spellLevel, entity));
               tentacle.m_20219_(spawn);
               tentacle.m_146922_(Utils.random.m_188503_(360));
               level.m_7967_(tentacle);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.baseSpellPower * this.getEntityPowerMultiplier(entity);
   }

   private int getRings(int spellLevel, LivingEntity entity) {
      return 1 + spellLevel;
   }
}
