package io.redspace.ironsspellbooks.entity.armor.netherite;

import io.redspace.ironsspellbooks.item.armor.NetheriteMageArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class NetheriteMageArmorModel extends DefaultedItemGeoModel<NetheriteMageArmorItem> {
   public NetheriteMageArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
   }

   public ResourceLocation getModelResource(NetheriteMageArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/netherite_armor.geo.json");
   }

   public ResourceLocation getTextureResource(NetheriteMageArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/netherite.png");
   }

   public ResourceLocation getAnimationResource(NetheriteMageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
