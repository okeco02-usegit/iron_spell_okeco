package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.item.armor.TarnishedCrownArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TarnishedCrownModel extends GeoModel<TarnishedCrownArmorItem> {
   public ResourceLocation getModelResource(TarnishedCrownArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/tarnished_armor.geo.json");
   }

   public ResourceLocation getTextureResource(TarnishedCrownArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/tarnished.png");
   }

   public ResourceLocation getAnimationResource(TarnishedCrownArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
