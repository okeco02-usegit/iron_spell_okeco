package io.redspace.ironsspellbooks.entity.armor.priest;

import io.redspace.ironsspellbooks.item.armor.PriestArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class PriestArmorModel extends DefaultedItemGeoModel<PriestArmorItem> {
   public PriestArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "armor/priest"));
   }

   public ResourceLocation getModelResource(PriestArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/priest_armor.geo.json");
   }

   public ResourceLocation getTextureResource(PriestArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/priest.png");
   }

   public ResourceLocation getAnimationResource(PriestArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
