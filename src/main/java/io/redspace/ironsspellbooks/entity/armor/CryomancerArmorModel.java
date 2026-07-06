package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.CryomancerArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CryomancerArmorModel extends DefaultedItemGeoModel<CryomancerArmorItem> {
   public CryomancerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(CryomancerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/cryomancer_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CryomancerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/cryomancer.png");
   }

   public ResourceLocation getAnimationResource(CryomancerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
