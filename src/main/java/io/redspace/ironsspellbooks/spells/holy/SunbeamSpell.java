package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.sunbeam.SunbeamEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SunbeamSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sunbeam");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public SunbeamSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 24;
      this.spellPowerPerLevel = 3;
      this.castTime = 0;
      this.baseManaCost = 40;
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
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 0.5F, false);
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 spawn = null;
      SunbeamEntity sunbeam = new SunbeamEntity(level);
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         spawn = castTargetingData.getTargetPosition((ServerLevel)level);
         sunbeam.setTarget(castTargetingData.getTarget((ServerLevel)level));
      }

      if (spawn == null) {
         HitResult raycast = Utils.raycastForEntity(level, entity, 48.0F, true);
         if (raycast.m_6662_() == Type.ENTITY) {
            spawn = ((EntityHitResult)raycast).m_82443_().m_20182_();
         } else {
            spawn = Utils.moveToRelativeGroundLevel(level, raycast.m_82450_().m_82546_(entity.m_20156_().m_82541_()).m_82520_(0.0, 2.0, 0.0), 3, 18);
         }
      }

      sunbeam.m_5602_(entity);
      sunbeam.m_20219_(spawn);
      sunbeam.setDamage(this.getDamage(spellLevel, entity));
      level.m_7967_(sunbeam);
      level.m_5594_(null, sunbeam.m_20183_(), (SoundEvent)SoundRegistry.SUNBEAM_WINDUP.get(), SoundSource.NEUTRAL, 3.5F, 1.0F);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.5F;
   }

   private int getDuration(int spellLevel, LivingEntity entity) {
      return 100 + spellLevel * 40;
   }
}
