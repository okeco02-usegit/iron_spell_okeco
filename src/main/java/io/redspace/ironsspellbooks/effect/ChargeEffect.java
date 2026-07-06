package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.effect.MobEffectCategory;

public class ChargeEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float ATTACK_DAMAGE_PER_LEVEL = 0.1F;
   public static final float SPEED_PER_LEVEL = 0.2F;
   public static final float SPELL_POWER_PER_LEVEL = 0.05F;

   public ChargeEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
