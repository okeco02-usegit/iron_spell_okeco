package io.redspace.ironsspellbooks.entity.spells.root;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RootModel extends GeoModel<RootEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/root.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/root.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/root_animations.json");

   public ResourceLocation getTextureResource(RootEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(RootEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(RootEntity animatable) {
      return ANIMS;
   }
}
