package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class ExtendedWeaponTier implements Tier, IronsWeaponTier {
   public static ExtendedWeaponTier HELLRAZOR = new ExtendedWeaponTier(2031, 12.0F, -2.6F, 16, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42419_}));
   public static ExtendedWeaponTier LEGIONNAIRE_FLAMBERGE = new ExtendedWeaponTier(
      2031,
      10.0F,
      -2.5F,
      4,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42419_}),
      new AttributeContainer(() -> Attributes.f_22284_, 4.0, Operation.ADDITION)
   );
   public static ExtendedWeaponTier DECREPIT_FLAMBERGE = new ExtendedWeaponTier(
      1000,
      10.0F,
      -2.7F,
      4,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42419_}),
      new AttributeContainer(() -> Attributes.f_22284_, 4.0, Operation.ADDITION)
   );
   public static ExtendedWeaponTier DECREPIT_SCYTHE = new ExtendedWeaponTier(1000, 10.0F, -2.6F, 4, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42419_}));
   public static ExtendedWeaponTier DREADSWORD = new ExtendedWeaponTier(
      1061, 6.0F, -2.4F, 14, () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.ARCANE_INGOT.get()})
   );
   public static ExtendedWeaponTier MISERY = new ExtendedWeaponTier(
      1061, 7.0F, -2.1F, 14, () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.ARCANE_INGOT.get()})
   );
   public static ExtendedWeaponTier METAL_MAGEHUNTER = new ExtendedWeaponTier(
      1561,
      6.0F,
      -2.4F,
      12,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.ARCANE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15, Operation.MULTIPLY_BASE)
   );
   public static ExtendedWeaponTier CRYSTAL_MAGEHUNTER = new ExtendedWeaponTier(
      1561,
      6.0F,
      -2.4F,
      12,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42415_}),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15, Operation.MULTIPLY_BASE)
   );
   public static ExtendedWeaponTier SPELLBREAKER = new ExtendedWeaponTier(
      2031,
      9.0F,
      -2.2F,
      12,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42415_}),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.MULTIPLY_BASE)
   );
   public static ExtendedWeaponTier TRUTHSEEKER = new ExtendedWeaponTier(
      2031, 11.0F, -3.0F, 10, () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.ARCANE_INGOT.get()})
   );
   public static ExtendedWeaponTier CLAYMORE = new ExtendedWeaponTier(1000, 9.0F, -2.7F, 8, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42416_}));
   public static ExtendedWeaponTier AMETHYST_RAPIER = new ExtendedWeaponTier(2031, 7.0F, -1.7F, 16, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_151049_}));
   public static ExtendedWeaponTier ICE_GREATSWORD = new ExtendedWeaponTier(2031, 15.0F, -3.1F, 16, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_41980_}));
   public static ExtendedWeaponTier TWILIGHT_GALE = new ExtendedWeaponTier(
      2031, 12.0F, -2.6F, 16, () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.LIGHTNING_BOTTLE.get()})
   );
   int uses;
   float damage;
   float speed;
   int enchantmentValue;
   TagKey<Block> incorrectBlocksForDrops;
   Supplier<Ingredient> repairIngredient;
   AttributeContainer[] attributes;

   public ExtendedWeaponTier(int uses, float damage, float speed, int enchantmentValue, Supplier<Ingredient> repairIngredient, AttributeContainer... attributes) {
      this.uses = uses;
      this.damage = damage;
      this.speed = speed;
      this.enchantmentValue = enchantmentValue;
      this.repairIngredient = repairIngredient;
      this.attributes = attributes;
   }

   public int m_6609_() {
      return this.uses;
   }

   @Override
   public float m_6624_() {
      return this.speed;
   }

   @Override
   public float m_6631_() {
      return this.damage;
   }

   public int m_6604_() {
      return 0;
   }

   public int m_6601_() {
      return this.enchantmentValue;
   }

   public Ingredient m_6282_() {
      return this.repairIngredient.get();
   }

   @Override
   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }
}
