package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SummonHorseSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "summon_horse");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public SummonHorseSpell() {
      this.manaCostPerLevel = 2;
      this.baseSpellPower = 85;
      this.spellPowerPerLevel = 15;
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
      return Optional.of(SoundEvents.f_12054_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.f_12052_);
   }

   @Override
   public ICastDataSerializable getEmptyCastData() {
      return new SummonedEntitiesCastData();
   }

   @Override
   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   @Override
   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
         super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      }
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         int summonTime = 12000;
         Vec3 spawn = entity.m_20182_();
         Vec3 forward = entity.m_20156_().m_82541_().m_82490_(1.5);
         spawn.m_82520_(forward.f_82479_, 0.15F, forward.f_82481_);
         SummonedHorse horse = new SummonedHorse(world, entity);
         horse.m_146884_(spawn);
         this.setAttributes(horse, this.getSpellPower(spellLevel, entity) / 100.0F);
         world.m_7967_(horse);
         SummonManager.initSummon(entity, horse, summonTime, summonedEntitiesCastData);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private void setAttributes(AbstractHorse horse, float powerMultiplier) {
      float speed = 0.22F * powerMultiplier;
      float jump = 0.4F * powerMultiplier;
      float health = 15.0F * powerMultiplier;
      int safeFall = 6 + (int)((jump - 0.2F) * 3.0F);
      horse.m_21051_(Attributes.f_22279_).m_22100_(speed);
      horse.m_21051_(Attributes.f_22288_).m_22100_(jump);
      horse.m_21051_(Attributes.f_22276_).m_22100_(health);
      horse.m_21153_(health);
   }
}
