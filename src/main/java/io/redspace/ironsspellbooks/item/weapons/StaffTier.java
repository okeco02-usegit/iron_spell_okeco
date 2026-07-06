package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StaffTier implements IronsWeaponTier {
   public static StaffTier GRAYBEARD = new StaffTier(
      2.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.25, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.MULTIPLY_BASE)
   );
   public static StaffTier ARTIFICER = new StaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.MULTIPLY_BASE)
   );
   public static StaffTier ICE_STAFF = new StaffTier(
      4.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.25, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.MULTIPLY_BASE)
   );
   public static StaffTier LIGHTNING_ROD = new StaffTier(
      4.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.MULTIPLY_BASE)
   );
   public static StaffTier BLOOD_STAFF = new StaffTier(
      7.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.1, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.MULTIPLY_BASE)
   );
   public static StaffTier PYRIUM_STAFF = new StaffTier(
      8.0F,
      -2.5F,
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1, Operation.MULTIPLY_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.MULTIPLY_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributes;

   public StaffTier(float damage, float speed, AttributeContainer... attributes) {
      this.damage = damage;
      this.speed = speed;
      this.attributes = attributes;
   }

   @Override
   public float m_6624_() {
      return this.speed;
   }

   @Override
   public float m_6631_() {
      return this.damage;
   }

   @Override
   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }
}
