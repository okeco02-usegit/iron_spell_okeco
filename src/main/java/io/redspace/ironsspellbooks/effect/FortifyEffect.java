package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class FortifyEffect extends MagicMobEffect {
   public FortifyEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void m_6386_(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
      pLivingEntity.m_7911_(pLivingEntity.m_6103_() - (pAmplifier + 1));
      super.m_6386_(pLivingEntity, pAttributeMap, pAmplifier);
   }

   @Override
   public void m_6385_(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
      pLivingEntity.m_7911_(pLivingEntity.m_6103_() + (pAmplifier + 1));
      super.m_6385_(pLivingEntity, pAttributeMap, pAmplifier);
   }
}
