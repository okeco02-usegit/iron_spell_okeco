package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FrostbiteEffect extends MagicMobEffect {
   public FrostbiteEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void handleFrostbiteDeathEffects(LivingDeathEvent event) {
      DamageSource damageSource = event.getSource();
      LivingEntity entity = event.getEntity();
      if (damageSource != null && damageSource.m_7639_() instanceof LivingEntity attacker) {
         MobEffectInstance effect = attacker.m_21124_((MobEffect)MobEffectRegistry.FROSTBITTEN_STRIKES.get());
         if (effect != null && entity.m_146890_()) {
            FrozenHumanoid iceClone = new FrozenHumanoid(entity.f_19853_, entity);
            iceClone.setSummoner(attacker);
            iceClone.setShatterDamage(getDamageForAmplifier(effect.m_19564_(), attacker));
            iceClone.setDeathTimer(100);
            entity.f_19853_.m_7967_(iceClone);
            entity.f_20919_ = 1000;
            iceClone.m_5496_((SoundEvent)SoundRegistry.FROSTBITE_FREEZE.get(), 2.0F, Utils.random.m_216339_(9, 11) * 0.1F);
         }
      }
   }

   public static float getDamageForAmplifier(int effectAmplifier, @Nullable LivingEntity caster) {
      float power = caster == null ? 1.0F : ((AbstractSpell)SpellRegistry.FROSTBITE_SPELL.get()).getEntityPowerMultiplier(caster);
      return (1 + effectAmplifier) * power;
   }
}
