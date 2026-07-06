package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class OakskinEffect extends CustomDescriptionMobEffect {
   public static final float REDUCTION_PER_LEVEL = 0.05F;
   public static final float BASE_REDUCTION = 0.1F;
   public static final float SLOWNESS_MAGNITUDE = 0.2F;

   public OakskinEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @Override
   public Component getDescriptionLine(MobEffectInstance instance) {
      int amp = instance.m_19564_() + 1;
      float reductionAmount = getReductionAmount(amp);
      return Component.m_237110_("tooltip.irons_spellbooks.oakskin_description", new Object[]{(int)(reductionAmount * 100.0F)}).m_130940_(ChatFormatting.BLUE);
   }

   @SubscribeEvent
   public static void reduceDamage(LivingHurtEvent event) {
      LivingEntity entity = event.getEntity();
      MobEffectInstance effect = entity.m_21124_((MobEffect)MobEffectRegistry.OAKSKIN.get());
      if (effect != null) {
         int lvl = effect.m_19564_() + 1;
         float multiplier = 1.0F - getReductionAmount(lvl);
         event.setAmount(event.getAmount() * multiplier);
      }
   }

   public static float getReductionAmount(int level) {
      return 0.1F + 0.05F * level;
   }
}
