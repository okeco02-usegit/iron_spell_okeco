package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.particles.BloodSiphonParticlesPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.CastingMobAimingData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class RayOfSiphoningSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ray_of_siphoning");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getTickDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel), 1)})
      );
   }

   public RayOfSiphoningSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 8;
   }

   @Override
   public CastType getCastType() {
      return CastType.CONTINUOUS;
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
      return Optional.of((SoundEvent)SoundRegistry.RAY_OF_SIPHONING.get());
   }

   @Override
   public ICastDataSerializable getEmptyCastData() {
      return new CastingMobAimingData();
   }

   @Override
   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
      if (playerMagicData.getAdditionalCastData() instanceof CastingMobAimingData aimData && entity instanceof Mob mob) {
         LivingEntity target = mob.m_5448_();
         if (target != null) {
            aimData.updateAim(target, 0.15F);
         }
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 forward = entity.m_20156_();
      if (playerMagicData.getAdditionalCastData() instanceof CastingMobAimingData aimData && entity instanceof Mob mob) {
         forward = aimData.getForward(entity);
      }

      HitResult hitResult = Utils.raycastForEntity(
         level, entity, entity.m_146892_(), entity.m_146892_().m_82549_(forward.m_82490_(getRange(spellLevel))), true, 0.15F, Utils::canHitWithRaycast
      );
      if (hitResult.m_6662_() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).m_82443_();
         if (target.m_271807_() && DamageSources.applyDamage(target, this.getTickDamage(spellLevel, entity), this.getDamageSource(entity))) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
               entity,
               new BloodSiphonParticlesPacket(
                  target.m_20182_().m_82520_(0.0, target.m_20206_() / 2.0F, 0.0), entity.m_20182_().m_82520_(0.0, entity.m_20206_() / 2.0F, 0.0)
               )
            );
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setLifestealPercent(1.0F);
   }

   public static float getRange(int level) {
      return 12.0F;
   }

   private float getTickDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.25F;
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return mob.m_20280_(target) > getRange(spellLevel) * getRange(spellLevel) * 1.2;
   }
}
