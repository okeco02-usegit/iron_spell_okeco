package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.InfernalSorcererArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InfernalSorcererArmorModel extends DefaultedItemGeoModel<InfernalSorcererArmorItem> {
   public InfernalSorcererArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(InfernalSorcererArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/infernal_sorcerer.geo.json");
   }

   public ResourceLocation getTextureResource(InfernalSorcererArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/infernal_sorcerer.png");
   }

   public ResourceLocation getAnimationResource(InfernalSorcererArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
