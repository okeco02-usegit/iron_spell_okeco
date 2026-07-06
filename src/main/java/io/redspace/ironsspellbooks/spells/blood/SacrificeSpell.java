package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SacrificeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sacrifice");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(1.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.base_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{3})
      );
   }

   public SacrificeSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 25;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      float aimAssist = 0.25F;
      float range = 25.0F;
      Vec3 start = entity.m_146892_();
      Vec3 end = entity.m_20154_().m_82541_().m_82490_(range).m_82549_(start);
      if (Utils.raycastForEntity(entity.f_19853_, entity, start, end, true, aimAssist, e -> e instanceof IMagicSummon summon && summon.getSummoner() == entity) instanceof EntityHitResult entityHit
         && entityHit.m_82443_() instanceof LivingEntity livingTarget) {
         playerMagicData.setAdditionalCastData(new TargetEntityCastData(livingTarget));
         if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(livingTarget, this));
            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(
                     Component.m_237110_(
                           "ui.irons_spellbooks.spell_target_success", new Object[]{livingTarget.m_5446_().getString(), this.getDisplayName(serverPlayer)}
                        )
                        .m_130940_(ChatFormatting.GREEN)
                  )
               );
         }

         return true;
      } else {
         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.sacrifice_target_failure").m_130940_(ChatFormatting.RED))
               );
         }

         return false;
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity instanceof IMagicSummon summon && summon.getSummoner().m_20148_().equals(entity.m_20148_())) {
            float damage = this.getDamage(spellLevel, entity) + targetEntity.m_21223_() * 0.5F;
            float explosionRadius = 3.0F * (1.0F + 0.5F * targetEntity.m_21223_() / targetEntity.m_21233_());
            MagicManager.spawnParticles(
               level, ParticleHelper.BLOOD, targetEntity.m_20185_(), targetEntity.m_20186_() + 0.25, targetEntity.m_20189_(), 100, 0.03, 0.4, 0.03, 0.4, true
            );
            MagicManager.spawnParticles(
               level, ParticleHelper.BLOOD, targetEntity.m_20185_(), targetEntity.m_20186_() + 0.25, targetEntity.m_20189_(), 100, 0.03, 0.4, 0.03, 0.4, false
            );
            MagicManager.spawnParticles(
               level,
               new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.BLOOD.get()).getTargetingColor(), explosionRadius),
               targetEntity.m_20185_(),
               targetEntity.m_20191_().m_82399_().f_82480_,
               targetEntity.m_20189_(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );

            for (Entity victim : level.m_45933_(targetEntity, targetEntity.m_20191_().m_82400_(explosionRadius))) {
               double distanceSqr = victim.m_20238_(targetEntity.m_20182_());
               if (victim.m_271807_()
                  && distanceSqr < explosionRadius * explosionRadius
                  && Utils.hasLineOfSight(level, targetEntity.m_20191_().m_82399_(), victim.m_20191_().m_82399_(), true)) {
                  float p = (float)(distanceSqr / (explosionRadius * explosionRadius));
                  p = 1.0F - p * p * p;
                  DamageSources.applyDamage(victim, damage * p, this.getDamageSource(targetEntity, entity));
               }
            }

            CameraShakeManager.addCameraShake(new CameraShakeData(level, 10, targetEntity.m_20182_(), 20.0F));
            targetEntity.m_142687_(RemovalReason.KILLED);
            level.m_5594_(
               null, targetEntity.m_20183_(), (SoundEvent)SoundRegistry.BLOOD_EXPLOSION.get(), SoundSource.PLAYERS, 3.0F, Utils.random.m_216332_(8, 12) * 0.1F
            );
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, @Nullable LivingEntity caster) {
      return (10.0F + this.getSpellPower(spellLevel, caster))
         * (caster == null ? 1.0F : (float)caster.m_21133_((Attribute)AttributeRegistry.SUMMON_DAMAGE.get()));
   }
}
