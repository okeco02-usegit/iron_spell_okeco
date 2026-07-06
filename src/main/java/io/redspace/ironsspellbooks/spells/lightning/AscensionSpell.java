package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AscensionSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ascension");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public AscensionSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
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
   public ICastDataSerializable getEmptyCastData() {
      return new ImpulseCastData();
   }

   @Override
   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      if (castData instanceof ImpulseCastData data) {
         entity.f_19812_ = data.hasImpulse;
         double y = Math.max(entity.m_20184_().f_82480_, data.y);
         entity.m_20334_(data.x, y, data.z);
      }

      super.onClientCast(level, spellLevel, entity, castData);
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.ASCENSION.get(), 80, 0, false, false, true));
      Vec3 vec = entity.m_20182_();

      for (int i = 0; i < 32 && level.m_8055_(BlockPos.m_274446_(vec).m_7495_()).m_60795_(); i++) {
         vec = vec.m_82492_(0.0, 1.0, 0.0);
      }

      Vec3 strikePos = vec;
      LightningBolt lightningBolt = (LightningBolt)EntityType.f_20465_.m_20615_(level);
      lightningBolt.m_20874_(true);
      lightningBolt.setDamage(0.0F);
      lightningBolt.m_146884_(strikePos);
      level.m_7967_(lightningBolt);
      float radius = 5.0F;
      level.m_45933_(entity, entity.m_20191_().m_82400_(radius)).forEach(target -> {
         double distance = target.m_20238_(strikePos);
         if (distance < radius * radius) {
            float finalDamage = (float)(this.getDamage(spellLevel, entity) * (1.0 - distance / (radius * radius)));
            DamageSources.applyDamage(target, finalDamage, this.getDamageSource(lightningBolt, entity));
            if (target instanceof Creeper creeper) {
               creeper.m_8038_((ServerLevel)level, lightningBolt);
            }

            if (target instanceof LivingEntity livingEntity) {
               livingEntity.m_147240_(0.25F + finalDamage / 10.0F, entity.m_20185_() - livingEntity.m_20185_(), entity.m_20189_() - livingEntity.m_20189_());
            }
         }
      });
      Vec3 motion = entity.m_20154_().m_82542_(1.0, 0.0, 1.0).m_82541_().m_82520_(0.0, 5.0, 0.0).m_82490_(0.125);
      playerMagicData.setAdditionalCastData(new ImpulseCastData((float)motion.f_82479_, (float)motion.f_82480_, (float)motion.f_82481_, true));
      entity.m_20256_(entity.m_20184_().m_82549_(motion));
      entity.f_19812_ = true;
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDamage(int spellLevel, LivingEntity caster) {
      return (int)this.getSpellPower(spellLevel, caster);
   }
}
