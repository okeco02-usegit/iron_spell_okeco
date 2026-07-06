package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.backwards_compat.IBackwardsAttributeCompatMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class MagicMobEffect extends MobEffect implements IMobEffectEndCallback, IBackwardsAttributeCompatMobEffect {
   public MagicMobEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @Override
   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
   }

   public void m_6385_(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
      super.m_6385_(pLivingEntity, pAttributeMap, pAmplifier);
      this.onEffectAdded(pLivingEntity, pAmplifier);
   }
}
