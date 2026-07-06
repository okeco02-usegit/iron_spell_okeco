package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class SpiderAspectEffect extends MagicMobEffect {
   public static final float DAMAGE_PER_LEVEL = 0.05F;

   public SpiderAspectEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void increaseDamage(LivingDamageEvent event) {
      if (event.getSource().m_7639_() instanceof LivingEntity livingAttacker && livingAttacker.m_21023_((MobEffect)MobEffectRegistry.SPIDER_ASPECT.get())) {
         boolean targetHasTagEffect = event.getEntity()
            .m_21220_()
            .stream()
            .anyMatch(instance -> BuiltInRegistries.f_256974_.m_263177_(instance.m_19544_()).m_203656_(ModTags.AFFECTED_BY_SPIDER_ASPECT));
         if (targetHasTagEffect) {
            int lvl = livingAttacker.m_21124_((MobEffect)MobEffectRegistry.SPIDER_ASPECT.get()).m_19564_() + 1;
            float before = event.getAmount();
            float multiplier = 1.0F + 0.05F * lvl;
            event.setAmount(event.getAmount() * multiplier);
         }
      }
   }
}
