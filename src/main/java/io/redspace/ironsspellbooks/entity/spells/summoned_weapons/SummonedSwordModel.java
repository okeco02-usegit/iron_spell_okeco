package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SummonedSwordModel extends GeoModel<SummonedWeaponEntity> {
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/entity/summoned_weapons/summoned_sword.png"
   );
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/summoned_sword.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/summoned_weapon_animations.json");

   public ResourceLocation getModelResource(SummonedWeaponEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(SummonedWeaponEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(SummonedWeaponEntity animatable) {
      return ANIMATIONS;
   }
}
