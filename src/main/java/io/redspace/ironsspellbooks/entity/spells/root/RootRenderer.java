package io.redspace.ironsspellbooks.entity.spells.root;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class RootRenderer extends GeoLivingEntityRenderer<RootEntity> {
   public RootRenderer(Context context) {
      super(context, new RootModel());
   }

   public void preRender(
      PoseStack poseStack,
      RootEntity animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float r,
      float g,
      float b,
      float a
   ) {
      Entity rooted = animatable.m_146895_();
      if (rooted != null) {
         float scale = rooted.m_20205_() / 0.6F;
         poseStack.m_85841_(scale, scale, scale);
      }

      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, r, g, b, a);
   }
}
