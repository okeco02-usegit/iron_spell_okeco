package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.creeper_head.CreeperHeadProjectile;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ChainCreeperSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "chain_creeper");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.projectile_count", new Object[]{this.getCount(spellLevel, caster)})
      );
   }

   public ChainCreeperSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 0;
      this.castTime = 30;
      this.baseManaCost = 40;
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
      return Optional.of(SoundEvents.f_11837_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.f_11862_);
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 0.25F, false);
      return true;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 spawn = null;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         spawn = castTargetingData.getTargetPosition((ServerLevel)level);
      }

      if (spawn == null) {
         HitResult raycast = Utils.raycastForEntity(level, entity, 32.0F, true);
         if (raycast.m_6662_() == Type.ENTITY) {
            spawn = ((EntityHitResult)raycast).m_82443_().m_20182_();
         } else {
            spawn = Utils.moveToRelativeGroundLevel(level, raycast.m_82450_().m_82546_(entity.m_20156_().m_82541_()).m_82520_(0.0, 2.0, 0.0), 5);
         }
      }

      summonCreeperRing(level, entity, spawn.m_82520_(0.0, 0.5, 0.0), this.getDamage(spellLevel, entity), this.getCount(spellLevel, entity));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static void summonCreeperRing(Level level, LivingEntity owner, Vec3 origin, float damage, int count) {
      if (count < 3) {
         count = 3;
      }

      int degreesPerCreeper = 360 / count;

      for (int i = 0; i < count; i++) {
         Vec3 motion = new Vec3(0.0, 0.0, 0.4 + count * 0.015F);
         motion = motion.m_82496_((float) (Math.PI * 5.0 / 12.0));
         motion = motion.m_82524_(degreesPerCreeper * i * (float) (Math.PI / 180.0));
         CreeperHeadProjectile head = new CreeperHeadProjectile(owner, level, motion, damage);
         head.setChainOnKill(true);
         head.setChainCount(count - 2);
         Vec3 spawn = origin.m_82549_(motion.m_82542_(1.0, 0.0, 1.0).m_82541_().m_82490_(0.6F));
         Vec2 angle = Utils.rotationFromDirection(motion);
         head.m_7678_(spawn.f_82479_, spawn.f_82480_ - head.m_20191_().m_82376_() / 2.0, spawn.f_82481_, angle.f_82471_, angle.f_82470_);
         level.m_7967_(head);
      }
   }

   private int getCount(int spellLevel, LivingEntity entity) {
      return 3 + spellLevel - 1;
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }
}
