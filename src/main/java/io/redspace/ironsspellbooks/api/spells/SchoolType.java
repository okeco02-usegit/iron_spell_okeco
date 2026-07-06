package io.redspace.ironsspellbooks.api.spells;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Vector3f;

public class SchoolType {
   final ResourceLocation id;
   final TagKey<Item> focus;
   final Component displayName;
   final Style displayStyle;
   final Supplier<Attribute> powerAttribute;
   final Supplier<Attribute> resistanceAttribute;
   final Supplier<SoundEvent> defaultCastSound;
   final ResourceKey<DamageType> damageType;
   final boolean requiresLearning;
   final boolean allowLooting;

   @Deprecated(forRemoval = true)
   public SchoolType(
      ResourceLocation id,
      TagKey<Item> focus,
      Component displayName,
      LazyOptional<Attribute> powerAttribute,
      LazyOptional<Attribute> resistanceAttribute,
      LazyOptional<SoundEvent> defaultCastSound,
      ResourceKey<DamageType> damageType
   ) {
      this.id = id;
      this.focus = focus;
      this.displayName = displayName;
      this.displayStyle = displayName.m_7383_();
      this.powerAttribute = () -> (Attribute)powerAttribute.orElse((Attribute)AttributeRegistry.SPELL_POWER.get());
      this.resistanceAttribute = () -> (Attribute)resistanceAttribute.orElse((Attribute)AttributeRegistry.SPELL_RESIST.get());
      this.defaultCastSound = () -> (SoundEvent)defaultCastSound.orElse(SoundEvents.f_11862_);
      this.damageType = damageType;
      this.requiresLearning = false;
      this.allowLooting = true;
   }

   public SchoolType(
      ResourceLocation id,
      TagKey<Item> focus,
      Component displayName,
      Supplier<Attribute> powerAttribute,
      Supplier<Attribute> resistanceAttribute,
      Supplier<SoundEvent> defaultCastSound,
      ResourceKey<DamageType> damageType,
      boolean requiresLearning,
      boolean allowLooting
   ) {
      this.id = id;
      this.focus = focus;
      this.displayName = displayName;
      this.displayStyle = displayName.m_7383_();
      this.powerAttribute = powerAttribute;
      this.resistanceAttribute = resistanceAttribute;
      this.defaultCastSound = defaultCastSound;
      this.damageType = damageType;
      this.requiresLearning = requiresLearning;
      this.allowLooting = allowLooting;
   }

   public SchoolType(
      ResourceLocation id,
      TagKey<Item> focus,
      Component displayName,
      Supplier<Attribute> powerAttribute,
      Supplier<Attribute> resistanceAttribute,
      Supplier<SoundEvent> defaultCastSound,
      ResourceKey<DamageType> damageType
   ) {
      this(id, focus, displayName, powerAttribute, resistanceAttribute, defaultCastSound, damageType, false, true);
   }

   public double getResistanceFor(LivingEntity livingEntity) {
      return livingEntity.m_21204_().m_22171_(this.resistanceAttribute.get()) ? livingEntity.m_21133_(this.resistanceAttribute.get()) : 1.0;
   }

   public double getPowerFor(LivingEntity livingEntity) {
      return livingEntity.m_21204_().m_22171_(this.powerAttribute.get()) ? livingEntity.m_21133_(this.powerAttribute.get()) : 1.0;
   }

   public SoundEvent getCastSound() {
      return this.defaultCastSound.get();
   }

   public ResourceKey<DamageType> getDamageType() {
      return this.damageType;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public boolean isFocus(ItemStack itemStack) {
      return itemStack.m_204117_(this.focus);
   }

   public TagKey<Item> getFocus() {
      return this.focus;
   }

   public Vector3f getTargetingColor() {
      return Utils.deconstructRGB(this.displayStyle.m_131135_().m_131265_());
   }
}
