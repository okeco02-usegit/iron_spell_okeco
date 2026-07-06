package io.redspace.ironsspellbooks.entity.spells.wisp;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WispModel extends GeoModel<WispEntity> {
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/wisp.geo.json");
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/wisp/wisp.png");
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wisp.animation.json");

   public ResourceLocation getModelResource(WispEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(WispEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(WispEntity animatable) {
      return animationResource;
   }
}
