package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FallDamageImmunityEffect extends MobEffect {
   public FallDamageImmunityEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public void m_6742_(LivingEntity livingEntity, int amplifier) {
      livingEntity.m_183634_();
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }
}
