package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.mixin.LivingEntityAccessor;
import java.util.List;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BurningDashEffect extends MagicMobEffect {
   public BurningDashEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void m_6742_(LivingEntity livingEntity, int amplifier) {
      List<Entity> list = livingEntity.f_19853_.m_45933_(livingEntity, livingEntity.m_20191_().m_82377_(0.25, 0.5, 0.25));
      if (!list.isEmpty()) {
         for (Entity entity : list) {
            if (entity instanceof LivingEntity) {
               DamageSources.applyDamage(entity, amplifier, ((AbstractSpell)SpellRegistry.BURNING_DASH_SPELL.get()).getDamageSource(livingEntity));
               entity.f_19802_ = 20;
            }
         }
      } else if (livingEntity.f_19862_) {
         livingEntity.m_21195_(this);
         return;
      }

      livingEntity.f_19789_ = 0.0F;
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
