package io.redspace.ironsspellbooks.api.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;

public class ExtendedSwordItem extends SwordItem {
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public <T extends Tier & IronsWeaponTier> ExtendedSwordItem(T tier, Properties pProperties) {
      super(tier, (int)tier.m_6631_(), tier.m_6624_(), pProperties);
      float attackDamage = tier.m_6631_();
      float attackSpeed = tier.m_6624_();
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", attackDamage, Operation.ADDITION));
      builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", attackSpeed, Operation.ADDITION));

      for (AttributeContainer container : tier.getAdditionalAttributes()) {
         builder.put(container.attribute().get(), container.createModifier(EquipmentSlot.MAINHAND.m_20751_()));
      }

      this.defaultModifiers = builder.build();
   }

   @Deprecated(forRemoval = true)
   public ExtendedSwordItem(Tier tier, double attackDamage, double attackSpeed, Map<Attribute, AttributeModifier> additionalAttributes, Properties properties) {
      super(tier, 3, -2.4F, properties);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", attackDamage, Operation.ADDITION));
      builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", attackSpeed, Operation.ADDITION));

      for (Entry<Attribute, AttributeModifier> modifierEntry : additionalAttributes.entrySet()) {
         builder.put(modifierEntry.getKey(), modifierEntry.getValue());
      }

      this.defaultModifiers = builder.build();
   }

   @NotNull
   public Multimap<Attribute, AttributeModifier> m_7167_(@NotNull EquipmentSlot pEquipmentSlot) {
      return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.m_7167_(pEquipmentSlot);
   }
}
