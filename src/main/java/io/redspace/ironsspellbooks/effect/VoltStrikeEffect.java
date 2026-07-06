package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.mixin.LivingEntityAccessor;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.UUID;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class VoltStrikeEffect extends MagicMobEffect implements ISyncedMobEffect {
   public VoltStrikeEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void m_6742_(LivingEntity livingEntity, int amplifier) {
      Level level = livingEntity.f_19853_;
      if (!level.f_46443_) {
         List<Entity> list = level.m_45933_(livingEntity, livingEntity.m_20191_().m_82377_(0.25, 0.5, 0.25));
         boolean hit = false;
         UUID ignore = null;
         if (!list.isEmpty()) {
            for (Entity entity : list) {
               if (DamageSources.applyDamage(entity, amplifier, ((AbstractSpell)SpellRegistry.VOLT_STRIKE_SPELL.get()).getDamageSource(livingEntity))) {
                  entity.f_19802_ = 20;
                  hit = true;
                  ignore = entity.m_20148_();
               }
            }
         }

         if (!hit
            && !level.m_45772_(
               livingEntity.m_20191_().m_82383_(livingEntity.m_20184_()).m_82383_(livingEntity.m_20184_().m_82541_().m_82490_(0.1)).m_82406_(0.1)
            )) {
            hit = true;
         }

         if (hit) {
            float explosionRadius = 4.0F;
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = level.m_45933_(livingEntity, livingEntity.m_20191_().m_82400_(explosionRadius));
            Vec3 losPoint = Utils.raycastForBlock(level, livingEntity.m_20182_(), livingEntity.m_20182_().m_82520_(0.0, 1.0, 0.0), Fluid.NONE).m_82450_();

            for (Entity entity : entities) {
               double distanceSqr = entity.m_20238_(livingEntity.m_20182_());
               if (ignore != entity.m_20148_()
                  && distanceSqr < explosionRadiusSqr
                  && entity.m_271807_()
                  && Utils.hasLineOfSight(level, losPoint, entity.m_20191_().m_82399_(), true)) {
                  double p = 1.0 - distanceSqr / explosionRadiusSqr;
                  float damage = (float)(amplifier * p * 0.5);
                  DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.VOLT_STRIKE_SPELL.get()).getDamageSource(livingEntity));
               }
            }

            livingEntity.m_20256_(livingEntity.m_20184_().m_82541_().m_82490_(-0.5).m_82520_(0.0, 0.5, 0.0));
            livingEntity.f_19864_ = true;
            double x = livingEntity.m_20185_();
            double y = livingEntity.m_20186_() + 1.0;
            double z = livingEntity.m_20189_();
            MagicManager.spawnParticles(level, ParticleHelper.ELECTRIC_SPARKS, x, y, z, 25, 0.08, 0.08, 0.08, 0.3, false);
            MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, x, y, z, 75, 0.1, 0.1, 0.1, 0.5, false);
            MagicManager.spawnParticles(
               level, new BlastwaveParticleOptions(new Vector3f(0.7F, 1.0F, 1.0F), explosionRadius * 2.0F), x, y + 0.15F, z, 1, 0.0, 0.0, 0.0, 0.0, true
            );
            level.m_6263_(null, x, y, z, SoundEvents.f_12521_, livingEntity.m_5720_(), 4.0F, 0.8F);
            livingEntity.m_21195_(this);
         }

         livingEntity.f_19789_ = 0.0F;
      }
   }

   @Override
   public void clientTick(LivingEntity entity, MobEffectInstance instance) {
      Level level = entity.f_19853_;

      for (int i = 0; i < 2; i++) {
         Vec3 random = Utils.getRandomVec3(0.2);
         level.m_7106_(
            ParticleHelper.ELECTRIC_SPARKS,
            entity.m_20208_(0.75),
            entity.m_20186_() + Utils.getRandomScaled(0.75),
            entity.m_20262_(0.75),
            random.f_82479_,
            random.f_82480_,
            random.f_82481_
         );
      }

      for (int i = 0; i < 4; i++) {
         Vec3 random = Utils.getRandomVec3(0.2);
         level.m_7106_(
            ParticleHelper.ELECTRICITY,
            entity.m_20208_(0.75),
            entity.m_20186_() + Utils.getRandomScaled(0.75),
            entity.m_20262_(0.75),
            random.f_82479_,
            random.f_82480_,
            random.f_82481_
         );
      }
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   @Override
   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectAdded(pLivingEntity, pAmplifier);
      ((LivingEntityAccessor)pLivingEntity).setLivingEntityFlagInvoker(4, true);
   }

   @Override
   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectRemoved(pLivingEntity, pAmplifier);
      ((LivingEntityAccessor)pLivingEntity).setLivingEntityFlagInvoker(4, false);
   }
}
