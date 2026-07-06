package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.ShadowwalkerArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadowwalkerArmorModel extends GeoModel<ShadowwalkerArmorItem> {
   public ResourceLocation getModelResource(ShadowwalkerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/shadowwalker_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowwalkerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/shadowwalker.png");
   }

   public ResourceLocation getAnimationResource(ShadowwalkerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
