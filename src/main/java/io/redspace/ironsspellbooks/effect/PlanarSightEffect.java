package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer.FogData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlanarSightEffect extends MagicMobEffect {
   public PlanarSightEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   public void m_6742_(LivingEntity livingEntity, int pAmplifier) {
      if (livingEntity.f_19853_.f_46443_ && livingEntity == Minecraft.m_91087_().f_91074_) {
         for (int i = 0; i < 3; i++) {
            Vec3 pos = new Vec3(Utils.getRandomScaled(16.0), Utils.getRandomScaled(5.0) + 5.0, Utils.getRandomScaled(16.0)).m_82549_(livingEntity.m_20182_());
            Vec3 random = new Vec3(Utils.getRandomScaled(0.08F), Utils.getRandomScaled(0.08F), Utils.getRandomScaled(0.08F));
            livingEntity.f_19853_.m_7106_(ParticleTypes.f_123790_, pos.f_82479_, pos.f_82480_, pos.f_82481_, random.f_82479_, random.f_82480_, random.f_82481_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class EcholocationBlindnessFogFunction implements MobEffectFogFunction {
      public MobEffect m_213948_() {
         return (MobEffect)MobEffectRegistry.PLANAR_SIGHT.get();
      }

      public void m_213725_(FogData fogData, LivingEntity entity, MobEffectInstance mobEffectInstance, float p_234184_, float p_234185_) {
         float f = 160.0F;
         if (fogData.f_234199_ == FogMode.FOG_SKY) {
            fogData.f_234200_ = 0.0F;
            fogData.f_234201_ = f * 0.25F;
         } else {
            fogData.f_234200_ = -f * 0.5F;
            fogData.f_234201_ = f;
         }
      }
   }
}
