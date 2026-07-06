package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TrueInvisibilityEffect extends MagicMobEffect implements ISyncedMobEffect {
   public TrueInvisibilityEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @Override
   public void onEffectAdded(LivingEntity livingEntity, int pAmplifier) {
      super.onEffectAdded(livingEntity, pAmplifier);
      TargetingConditions targetingCondition = TargetingConditions.m_148352_().m_148355_().m_26888_(e -> ((Mob)e).m_5448_() == livingEntity);
      livingEntity.f_19853_.m_45971_(Mob.class, targetingCondition, livingEntity, livingEntity.m_20191_().m_82400_(40.0)).forEach(entityTargetingCaster -> {
         entityTargetingCaster.m_6710_(null);
         entityTargetingCaster.f_21346_.m_148105_().forEach(WrappedGoal::m_8041_);
         entityTargetingCaster.m_6274_().m_21936_(MemoryModuleType.f_26372_);
      });
   }

   @SubscribeEvent
   public static void onDealDamage(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof LivingEntity livingAttacker && livingAttacker.m_21023_((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get())) {
         livingAttacker.m_21195_((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get());
      }
   }
}
