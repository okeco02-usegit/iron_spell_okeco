package io.redspace.ironsspellbooks.entity.mobs.horse;

import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SpectralSteedRenderer extends AbstractHorseRenderer<SummonedHorse, HorseModel<SummonedHorse>> {
   public SpectralSteedRenderer(Context p_174167_) {
      super(p_174167_, new HorseModel(p_174167_.m_174023_(ModelLayers.f_171186_)), 1.1F);
   }

   public ResourceLocation getTextureLocation(SummonedHorse pEntity) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/horse/spectral_steed.png");
   }
}
