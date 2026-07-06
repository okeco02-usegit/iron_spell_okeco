package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BlightEffect extends MagicMobEffect {
   public static final float DAMAGE_PER_LEVEL = -0.05F;
   public static final float HEALING_PER_LEVEL = -0.1F;

   public BlightEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void reduceHealing(LivingHealEvent event) {
      MobEffectInstance effect = event.getEntity().m_21124_((MobEffect)MobEffectRegistry.BLIGHT.get());
      if (effect != null) {
         int lvl = effect.m_19564_() + 1;
         float healingMult = 1.0F + -0.1F * lvl;
         float before = event.getAmount();
         event.setAmount(event.getAmount() * healingMult);
      }
   }

   @SubscribeEvent
   public static void reduceDamageOutput(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof LivingEntity livingAttacker) {
         MobEffectInstance effect = livingAttacker.m_21124_((MobEffect)MobEffectRegistry.BLIGHT.get());
         if (effect != null) {
            int lvl = effect.m_19564_() + 1;
            float before = event.getAmount();
            float multiplier = 1.0F + -0.05F * lvl;
            event.setAmount(event.getAmount() * multiplier);
         }
      }
   }
}
