package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AscensionEffect extends MagicMobEffect implements ISyncedMobEffect {
   public AscensionEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public void m_6742_(LivingEntity pLivingEntity, int pAmplifier) {
      pLivingEntity.m_183634_();
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   public static void ambientParticles(ClientLevel level, LivingEntity entity) {
      RandomSource random = entity.m_217043_();

      for (int i = 0; i < 2; i++) {
         Vec3 motion = new Vec3(random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F);
         motion = motion.m_82490_(0.04F);
         level.m_7106_(
            ParticleHelper.ELECTRICITY, entity.m_20208_(0.4F), entity.m_20187_(), entity.m_20262_(0.4F), motion.f_82479_, motion.f_82480_, motion.f_82481_
         );
      }
   }

   @Override
   public void clientTick(LivingEntity livingEntity, MobEffectInstance instance) {
      if (livingEntity.f_19853_ instanceof ClientLevel level) {
         ambientParticles(level, livingEntity);
      }
   }
}
