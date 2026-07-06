package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.PaladinArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class PaladinArmorModel extends DefaultedItemGeoModel<PaladinArmorItem> {
   public PaladinArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(PaladinArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/paladin_chestplate.geo.json");
   }

   public ResourceLocation getTextureResource(PaladinArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/paladin_chestplate.png");
   }

   public ResourceLocation getAnimationResource(PaladinArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
