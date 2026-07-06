package io.redspace.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RootSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "root");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(35.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}));
   }

   public RootSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 45;
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
      return Optional.of(SoundEvents.f_11867_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F);
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
         if (target != null && !target.m_6095_().m_204039_(ModTags.CANT_ROOT)) {
            Vec3 spawn = target.m_20182_();
            RootEntity rootEntity = new RootEntity(level, entity);
            rootEntity.setDuration(this.getDuration(spellLevel, entity));
            rootEntity.setTarget(target);
            rootEntity.m_20219_(spawn);
            level.m_7967_(rootEntity);
            target.m_8127_();
            target.m_7998_(rootEntity, true);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Nullable
   private LivingEntity findTarget(LivingEntity caster) {
      return Utils.raycastForEntity(caster.m_9236_(), caster, 32.0F, true, 0.35F) instanceof EntityHitResult entityHit
            && entityHit.m_82443_() instanceof LivingEntity livingTarget
         ? livingTarget
         : null;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }
}
