package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ChilledEffect extends MagicMobEffect {
   public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   public void m_6742_(LivingEntity pLivingEntity, int pAmplifier) {
      if (!(pLivingEntity.m_20202_() instanceof IceTombEntity)) {
         if (pLivingEntity.m_146890_()) {
            IceTombEntity iceTombEntity = new IceTombEntity(pLivingEntity.f_19853_, null);
            iceTombEntity.m_20219_(pLivingEntity.m_20182_());
            iceTombEntity.m_20256_(pLivingEntity.m_20184_());
            iceTombEntity.setEvil();
            iceTombEntity.setLifetime(100);
            pLivingEntity.f_19853_.m_7967_(iceTombEntity);
            pLivingEntity.m_7998_(iceTombEntity, true);
            pLivingEntity.m_5496_((SoundEvent)SoundRegistry.FROSTBITE_FREEZE.get(), 2.0F, Utils.random.m_216339_(9, 11) * 0.1F);
            pLivingEntity.m_21195_(this);
         }
      }
   }
}
