package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AirborneEffect extends MobEffect {
   public static final float damage_per_amp = 0.5F;

   public AirborneEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   public void m_6742_(LivingEntity livingEntity, int pAmplifier) {
      if (!livingEntity.f_19853_.f_46443_ && livingEntity.f_19862_) {
         double d11 = livingEntity.m_20184_().m_165924_();
         float f1 = (float)(d11 * 10.0 - 1.0);
         if (f1 > 0.0F) {
            livingEntity.m_5496_(SoundEvents.f_12037_, 2.0F, 1.5F);
            livingEntity.m_6469_(livingEntity.m_269291_().m_269515_(), getDamageFromLevel(pAmplifier + 1));
            livingEntity.m_21195_((MobEffect)MobEffectRegistry.AIRBORNE.get());
         }
      }
   }

   public static float getDamageFromLevel(int level) {
      return 4.0F + level * 0.5F;
   }
}
