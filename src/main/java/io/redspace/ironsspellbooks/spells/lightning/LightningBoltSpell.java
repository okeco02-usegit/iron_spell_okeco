package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class LightningBoltSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "lightning_bolt");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(25.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public LightningBoltSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 75;
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
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.f_12053_);
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      HitResult result = Utils.raycastForEntity(level, entity, 64.0F, true, 1.0F);
      Vec3 pos = result.m_82450_();
      if (result.m_6662_() == Type.ENTITY) {
         pos = ((EntityHitResult)result).m_82443_().m_20182_();
      } else {
         pos = Utils.moveToRelativeGroundLevel(level, pos, 10);
      }

      LightningBolt lightningBolt = (LightningBolt)EntityType.f_20465_.m_20615_(level);
      lightningBolt.m_20874_(true);
      lightningBolt.setDamage(0.0F);
      lightningBolt.m_146884_(pos);
      level.m_7967_(lightningBolt);
      float radius = 4.0F;
      float damage = this.getSpellPower(spellLevel, entity);
      Vec3 finalpos = pos;
      level.m_6249_(entity, AABB.m_165882_(finalpos, radius * 2.0F, radius * 2.0F, radius * 2.0F), target -> this.canHit(entity, target)).forEach(target -> {
         double distance = target.m_20238_(finalpos);
         if (distance < radius * radius && Utils.hasLineOfSight(level, finalpos.m_82520_(0.0, 2.0, 0.0), target.m_20191_().m_82399_(), true)) {
            float finalDamage = (float)(damage * (1.0 - distance / (radius * radius)));
            DamageSources.applyDamage(target, finalDamage, this.getDamageSource(lightningBolt, entity));
            if (target instanceof Creeper creeper) {
               creeper.m_8038_((ServerLevel)level, lightningBolt);
            }
         }
      });
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private boolean canHit(Entity owner, Entity target) {
      return target != owner && target.m_6084_() && target.m_6087_() && !target.m_5833_();
   }
}
