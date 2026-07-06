package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.BootsOfSpeedArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BootsOfSpeedArmorModel extends DefaultedItemGeoModel<BootsOfSpeedArmorItem> {
   public BootsOfSpeedArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(BootsOfSpeedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/boots_of_speed.geo.json");
   }

   public ResourceLocation getTextureResource(BootsOfSpeedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/boots_of_speed.png");
   }

   public ResourceLocation getAnimationResource(BootsOfSpeedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
