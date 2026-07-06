package io.redspace.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.ice_spike.IceSpikeEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IceSpikesSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_spikes");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.spike_count", new Object[]{this.getCount(spellLevel, caster)})
      );
   }

   public IceSpikesSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 30;
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
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, (int)(this.getCount(spellLevel, entity) * 1.25F), 0.15F, false);
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 forward = entity.m_20156_().m_82542_(1.0, 0.0, 1.0).m_82541_();
      Vec3 start = entity.m_146892_().m_82549_(forward.m_82490_(1.5));
      float damage = this.getDamage(spellLevel, entity);
      float minScale = 1.0F;
      float maxScale = 3.0F;
      int count = this.getCount(spellLevel, entity);
      start = Utils.moveToRelativeGroundLevel(level, start, 1, 3).m_82520_(0.0, 0.1, 0.0);
      double distance = count;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
         if (target != null) {
            distance = start.m_82546_(target.m_20182_()).m_165924_();
            Vec3 targetPos = target.m_20182_().m_82549_(target.m_20184_().m_82542_(distance, 0.0, distance));
            distance = targetPos.m_82546_(start).m_165924_();
         }
      }

      float distanceCovered = 0.0F;

      for (int i = 0; i < count; i++) {
         float f = (float)Math.max((float)i / count, (distanceCovered + 1.0F) / distance);
         float scale = Mth.m_14179_(f, minScale, maxScale);
         Vec3 spawn = start.m_82549_(forward.m_82490_(i));
         Vec3 ground = Utils.moveToRelativeGroundLevel(level, spawn, 8);
         spawn = ground.m_82546_(spawn).m_82490_(Mth.m_14036_(i / 3.0F, 0.0F, 1.0F)).m_82549_(spawn);
         boolean isFinalSpike = i == count - 1 || distanceCovered + 1.0F > distance;
         if (isFinalSpike) {
            scale = maxScale * 1.5F;
         }

         distanceCovered += (float)forward.m_165924_();
         int delay = i;
         if (level.m_8055_(BlockPos.m_274446_(spawn).m_7495_()).m_60783_(level, BlockPos.m_274446_(spawn).m_7495_(), Direction.UP)) {
            IceSpikeEntity spike = new IceSpikeEntity(level, entity);
            if (i % 2 == count % 2) {
               spike.m_20225_(true);
            }

            spike.setSpikeSize(scale);
            spike.m_20219_(spawn.m_82520_(0.0, 0.0, 0.0));
            spike.setWaitTime(delay);
            spike.setDamage(damage * (isFinalSpike ? 1.0F : 0.5F));
            spike.m_146922_(entity.m_146908_() - 45.0F + Utils.random.m_216332_(-20, 20));
            spike.m_146926_(Utils.random.m_216332_(-15, 15));
            level.m_7967_(spike);
         }

         if (isFinalSpike) {
            break;
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      float f = this.getCount(spellLevel, mob) * 1.5F;
      return mob.m_20280_(target) > f * f;
   }

   private int getCount(int spellLevel, LivingEntity entity) {
      return 7 + 3 * spellLevel / 2;
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }
}
