package io.redspace.ironsspellbooks.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoLivingEntityRenderer<T extends LivingEntity & GeoAnimatable> extends GeoEntityRenderer<T> {
   public GeoLivingEntityRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
   }

   public boolean shouldShowName(T animatable) {
      double d0 = this.f_114476_.m_114471_(animatable);
      float f = animatable.m_6047_() ? 32.0F : 64.0F;
      return !(d0 >= f * f) && animatable.m_20151_();
   }
}
