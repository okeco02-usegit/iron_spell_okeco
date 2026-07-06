package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class SpectralHammerRenderer extends GeoLivingEntityRenderer<SpectralHammer> {
   public SpectralHammerRenderer(Context renderManager) {
      super(renderManager, new SpectralHammerModel());
      this.f_114477_ = 0.3F;
   }

   public ResourceLocation getTextureLocation(SpectralHammer animatable) {
      return SpectralHammerModel.textureResource;
   }

   public void preRender(
      PoseStack poseStack,
      SpectralHammer animatable,
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
      poseStack.m_85841_(2.0F, 2.0F, 2.0F);
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, r, g, b, a);
   }

   public RenderType getRenderType(SpectralHammer animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      Vec2 vec2 = getEnergySwirlOffset(animatable, partialTick);
      return RenderType.m_110436_(texture, vec2.f_82470_, vec2.f_82471_);
   }

   private static float shittyNoise(float f) {
      return (float)(Math.sin(f / 4.0F) + 2.0 * Math.sin(f / 3.0F) + 3.0 * Math.sin(f / 2.0F) + 4.0 * Math.sin(f)) * 0.25F;
   }

   public static Vec2 getEnergySwirlOffset(SpectralHammer entity, float partialTicks, int offset) {
      float f = (entity.f_19797_ + partialTicks) * 0.02F;
      return new Vec2(shittyNoise(1.2F * f + offset), shittyNoise(f + 456.0F + offset));
   }

   public static Vec2 getEnergySwirlOffset(SpectralHammer entity, float partialTicks) {
      return getEnergySwirlOffset(entity, partialTicks, 0);
   }
}
