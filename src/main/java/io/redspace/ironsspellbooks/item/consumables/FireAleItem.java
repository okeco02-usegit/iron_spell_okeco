package io.redspace.ironsspellbooks.item.consumables;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;

public class FireAleItem extends DrinkableItem {
   boolean foilOverride;

   public FireAleItem(Properties pProperties) {
      super(pProperties, (itemstack, livingentity) -> {
         livingentity.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 100, 3, false, true, true));
         livingentity.m_7292_(new MobEffectInstance(MobEffects.f_19607_, 900, 0, false, true, true));
         livingentity.m_7292_(new MobEffectInstance(MobEffects.f_19598_, 900, 2, false, true, true));
      }, Items.f_42590_, false);
   }
}
