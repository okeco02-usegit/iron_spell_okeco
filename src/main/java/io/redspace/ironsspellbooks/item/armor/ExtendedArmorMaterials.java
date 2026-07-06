package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum ExtendedArmorMaterials implements IronsExtendedArmorMaterial {
   TARNISHED(
      "tarnished",
      25,
      makeArmorMap(0, 0, 0, 0),
      15,
      SoundEvents.f_11673_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42416_}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 150.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.MANA_REGEN.get(),
         new AttributeModifier("Mana Regen", 0.25, Operation.MULTIPLY_TOTAL),
         Attributes.f_22281_,
         new AttributeModifier("minus damage", -0.15, Operation.MULTIPLY_TOTAL)
      )
   ),
   DEV(
      "dev",
      25,
      makeArmorMap(0, 0, 0, 20),
      15,
      SoundEvents.f_11673_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42417_}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 10000.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.COOLDOWN_REDUCTION.get(),
         new AttributeModifier("Mana Regen", 0.75, Operation.MULTIPLY_TOTAL),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Mana Regen", 1.0, Operation.MULTIPLY_TOTAL)
      )
   ),
   WANDERING_MAGICIAN(
      "wandering_magician",
      10,
      makeArmorMap(2, 5, 6, 2),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42454_}),
      Map.of((Attribute)AttributeRegistry.MAX_MANA.get(), new AttributeModifier("Max Mana", 25.0, Operation.ADDITION))
   ),
   PUMPKIN(
      "pumpkin",
      33,
      schoolArmorMap(),
      15,
      SoundEvents.f_11680_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42129_}),
      Map.of((Attribute)AttributeRegistry.MAX_MANA.get(), new AttributeModifier("Max Mana", 75.0, Operation.ADDITION))
   ),
   PYROMANCER(
      "pyromancer",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.FIRE_SPELL_POWER.get(),
         new AttributeModifier("Fire Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   ARCHEVOKER(
      "archevoker",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.EVOCATION_SPELL_POWER.get(),
         new AttributeModifier("Evocation Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   CULTIST(
      "cultist",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.BLOOD_SPELL_POWER.get(),
         new AttributeModifier("Blood Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   PRIEST(
      "priest",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.HOLY_SPELL_POWER.get(),
         new AttributeModifier("Holy Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   CRYOMANCER(
      "cryomancer",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.ICE_SPELL_POWER.get(),
         new AttributeModifier("Ice Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   SHADOWWALKER(
      "shadowwalker",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.ENDER_SPELL_POWER.get(),
         new AttributeModifier("Ender Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   PLAGUED(
      "plagued",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.NATURE_SPELL_POWER.get(),
         new AttributeModifier("Nature Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   ELECTROMANCER(
      "electromancer",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.LIGHTNING_SPELL_POWER.get(),
         new AttributeModifier("Lightning Power", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   NETHERITE_BATTLEMAGE(
      "netherite",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11679_,
      3.0F,
      0.0F,
      () -> Ingredient.m_204132_(net.minecraftforge.common.Tags.Items.INGOTS_NETHERITE),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   ),
   PALADIN(
      "paladin",
      40,
      schoolArmorMap(),
      15,
      SoundEvents.f_11679_,
      4.0F,
      0.4F,
      () -> Ingredient.m_204132_(ModTags.MITHRIL_INGOT),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("artifact", 150.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("artifact", 0.1, Operation.MULTIPLY_BASE)
      )
   ),
   INFERNAL_SORCERER(
      "infernal_sorcerer",
      40,
      schoolArmorMap(),
      15,
      SoundEvents.f_11679_,
      0.0F,
      0.0F,
      () -> Ingredient.m_204132_(ModTags.MITHRIL_INGOT),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("artifact", 150.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("artifact", 0.1, Operation.MULTIPLY_BASE)
      )
   ),
   BOOTS_OF_SPEED(
      "speed_boots",
      40,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_204132_(ModTags.MITHRIL_INGOT),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("artifact", 150.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("artifact", 0.1, Operation.MULTIPLY_BASE),
         (Attribute)AttributeRegistry.CASTING_MOVESPEED.get(),
         new AttributeModifier("artifact", 0.6, Operation.MULTIPLY_BASE),
         Attributes.f_22279_,
         new AttributeModifier("artifact", 0.25, Operation.MULTIPLY_BASE)
      )
   ),
   WIZARD(
      "wizard",
      38,
      schoolArmorMap(),
      15,
      SoundEvents.f_11678_,
      0.0F,
      0.0F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      Map.of(
         (Attribute)AttributeRegistry.MAX_MANA.get(),
         new AttributeModifier("Max Mana", 125.0, Operation.ADDITION),
         (Attribute)AttributeRegistry.SPELL_POWER.get(),
         new AttributeModifier("Base Power", 0.05, Operation.MULTIPLY_BASE)
      )
   );

   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final EnumMap<Type, Integer> protectionFunctionForType;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final float knockbackResistance;
   private final LazyLoadedValue<Ingredient> repairIngredient;
   private final Map<Attribute, AttributeModifier> additionalAttributes;
   private static final EnumMap<Type, Integer> HEALTH_FUNCTION_FOR_TYPE = (EnumMap<Type, Integer>)Util.m_137469_(new EnumMap(Type.class), p_266653_ -> {
      p_266653_.put(Type.BOOTS, 13);
      p_266653_.put(Type.LEGGINGS, 15);
      p_266653_.put(Type.CHESTPLATE, 16);
      p_266653_.put(Type.HELMET, 11);
   });

   ExtendedArmorMaterials(
      String pName,
      int pDurabilityMultiplier,
      EnumMap<Type, Integer> protectionMap,
      int pEnchantmentValue,
      SoundEvent pSound,
      float pToughness,
      float pKnockbackResistance,
      Supplier<Ingredient> pRepairIngredient,
      Map<Attribute, AttributeModifier> additionalAttributes
   ) {
      this.name = pName;
      this.durabilityMultiplier = pDurabilityMultiplier;
      this.protectionFunctionForType = protectionMap;
      this.enchantmentValue = pEnchantmentValue;
      this.sound = pSound;
      this.toughness = pToughness;
      this.knockbackResistance = pKnockbackResistance;
      this.repairIngredient = new LazyLoadedValue(pRepairIngredient);
      this.additionalAttributes = additionalAttributes;
   }

   public static EnumMap<Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
      return (EnumMap<Type, Integer>)Util.m_137469_(new EnumMap(Type.class), p_266655_ -> {
         p_266655_.put(Type.BOOTS, boots);
         p_266655_.put(Type.LEGGINGS, leggings);
         p_266655_.put(Type.CHESTPLATE, chestplate);
         p_266655_.put(Type.HELMET, helmet);
      });
   }

   public static EnumMap<Type, Integer> schoolArmorMap() {
      return makeArmorMap(3, 8, 6, 3);
   }

   public int getDurabilityForSlot(EquipmentSlot pSlot) {
      return HEALTH_PER_SLOT[pSlot.m_20749_()] * this.durabilityMultiplier;
   }

   public int m_266425_(Type p_266745_) {
      return HEALTH_FUNCTION_FOR_TYPE.get(p_266745_) * this.durabilityMultiplier;
   }

   public int m_7366_(Type p_266752_) {
      return this.protectionFunctionForType.get(p_266752_);
   }

   public int m_6646_() {
      return this.enchantmentValue;
   }

   public SoundEvent m_7344_() {
      return this.sound;
   }

   public Ingredient m_6230_() {
      return (Ingredient)this.repairIngredient.m_13971_();
   }

   public String m_6082_() {
      return this.name;
   }

   public float m_6651_() {
      return this.toughness;
   }

   @Override
   public Map<Attribute, AttributeModifier> getAdditionalAttributes() {
      return this.additionalAttributes;
   }

   public float m_6649_() {
      return this.knockbackResistance;
   }
}
