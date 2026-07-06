package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.ClothingVariantHelper;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class GenericArmorModel<T extends ExtendedArmorItem> extends DefaultedItemGeoModel<T> {
   private final ResourceLocation model;
   private final ResourceLocation texture;
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   private final Map<String, GenericArmorModel.ModelVariantResult> modelVariants;

   public GenericArmorModel(String modid, String name) {
      super(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", ""));
      this.model = ResourceLocation.fromNamespaceAndPath(modid, String.format("geo/%s_armor.geo.json", name));
      this.texture = ResourceLocation.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name));
      this.modelVariants = new HashMap<>();
   }

   public GenericArmorModel(String name) {
      this("irons_spellbooks", name);
   }

   public GenericArmorModel<T> variants(Map<String, ResourceLocation> modelVariants) {
      modelVariants.forEach((string, location) -> this.modelVariants.put(string, new GenericArmorModel.ModelVariantResult(location, false)));
      return this;
   }

   public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
      if (renderer instanceof GeoArmorRenderer<?> armorRenderer && armorRenderer.getCurrentStack() != null) {
         String transmogVariant = ClothingVariantHelper.getClothingVariant(armorRenderer.getCurrentStack());
         if (transmogVariant != null) {
            GenericArmorModel.ModelVariantResult result = this.modelVariants.get(transmogVariant);
            if (result != null && this.validateModelLocation(result, transmogVariant)) {
               return result.location;
            }
         }
      }

      return this.model;
   }

   private boolean validateModelLocation(GenericArmorModel.ModelVariantResult result, String transmogVariant) {
      if (result.validated) {
         return true;
      } else if (GeckoLibCache.getBakedModels().get(result.location) != null) {
         this.modelVariants.put(transmogVariant, new GenericArmorModel.ModelVariantResult(result.location, true));
         return true;
      } else {
         this.modelVariants.remove(transmogVariant);
         IronsSpellbooks.LOGGER.error("Could not find model variant location \"{}\", ignoring for the future!", result.location);
         return false;
      }
   }

   public ResourceLocation getTextureResource(T animatable) {
      return this.texture;
   }

   public ResourceLocation getAnimationResource(T animatable) {
      return ANIMATION;
   }

   record ModelVariantResult(ResourceLocation location, boolean validated) {
   }
}
