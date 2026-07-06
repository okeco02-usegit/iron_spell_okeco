package io.redspace.ironsspellbooks.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.render.StaffArmPose;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class StaffItem extends CastingItem {
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public StaffItem(Properties properties, StaffTier staffTier) {
      super(properties);
      float attackDamage = staffTier.m_6631_();
      float attackSpeed = staffTier.m_6624_();
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", attackDamage, Operation.ADDITION));
      builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", attackSpeed, Operation.ADDITION));

      for (AttributeContainer container : staffTier.getAdditionalAttributes()) {
         builder.put(container.attribute().get(), container.createModifier(EquipmentSlot.MAINHAND.m_20751_()));
      }

      this.defaultModifiers = builder.build();
   }

   @Deprecated(forRemoval = true)
   public StaffItem(Properties properties, double attackDamage, double attackSpeed, Map<Attribute, AttributeModifier> additionalAttributes) {
      super(properties);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", attackDamage, Operation.ADDITION));
      builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", attackSpeed, Operation.ADDITION));

      for (Entry<Attribute, AttributeModifier> modifierEntry : additionalAttributes.entrySet()) {
         builder.put(modifierEntry.getKey(), modifierEntry.getValue());
      }

      this.defaultModifiers = builder.build();
   }

   public boolean m_8120_(ItemStack pStack) {
      return true;
   }

   public int getEnchantmentValue(ItemStack stack) {
      return 20;
   }

   public boolean hasCustomRendering() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      StaffArmPose.initializeClientHelper(consumer);
   }

   @NotNull
   public Multimap<Attribute, AttributeModifier> m_7167_(@NotNull EquipmentSlot pEquipmentSlot) {
      return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.m_7167_(pEquipmentSlot);
   }
}
