package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import io.redspace.ironsspellbooks.entity.spells.shield.ShieldRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpectralHammerModel extends GeoModel<SpectralHammer> {
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/spectral_hammer.geo.json");
   public static final ResourceLocation textureResource = ShieldRenderer.SPECTRAL_OVERLAY_TEXTURE;
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "animations/spectral_hammer.animation.json"
   );

   public ResourceLocation getModelResource(SpectralHammer object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(SpectralHammer object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(SpectralHammer animatable) {
      return animationResource;
   }
}
