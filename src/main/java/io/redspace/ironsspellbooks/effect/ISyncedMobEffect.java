package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public interface ISyncedMobEffect {
   default void clientTick(LivingEntity livingEntity, MobEffectInstance instance) {
   }
}
