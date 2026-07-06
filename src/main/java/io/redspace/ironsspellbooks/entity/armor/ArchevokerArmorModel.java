package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.ArchevokerArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ArchevokerArmorModel extends DefaultedItemGeoModel<ArchevokerArmorItem> {
   public ArchevokerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(ArchevokerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/archevoker_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ArchevokerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/archevoker.png");
   }

   public ResourceLocation getAnimationResource(ArchevokerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
