package io.redspace.ironsspellbooks.entity.spells.sunbeam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SunbeamRenderer extends EntityRenderer<SunbeamEntity> {
   public SunbeamRenderer(Context context) {
      super(context);
   }

   public boolean shouldRender(SunbeamEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
      return true;
   }

   public void render(SunbeamEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      float maxRadius = 2.5F;
      float minRadius = 0.005F;
      float deltaTicks = entity.f_19797_ + partialTicks;
      float deltaUV = -deltaTicks % 10.0F;
      float max = Mth.m_14187_(deltaUV * 0.2F - Mth.m_14143_(deltaUV * 0.1F));
      float min = -1.0F + max;
      float f = deltaTicks / 15.0F;
      f *= f;
      float radius = Mth.m_144920_(maxRadius, minRadius, f);
      VertexConsumer inner = bufferSource.m_6299_(RenderHelper.CustomerRenderType.magic(SpellRenderingHelper.BEACON));
      float halfRadius = radius * 0.5F;
      float quarterRadius = halfRadius * 0.5F;
      float yMin = entity.m_20096_() ? 0.0F : Utils.findRelativeGroundLevel(entity.f_19853_, entity.m_20182_(), 8) - (float)entity.m_20186_();

      for (int i = 0; i < 4; i++) {
         int r = (int)(Mth.m_14036_(0.8F * f, 0.0F, 1.0F) * 255.0F);
         int g = (int)(Mth.m_14036_(0.8F * f * f, 0.0F, 1.0F) * 255.0F);
         int b = (int)(Mth.m_14036_(0.5F * f * f, 0.0F, 1.0F) * 255.0F);
         int a = 255;
         Matrix4f poseMatrix = poseStack.m_85850_().m_252922_();
         Matrix3f normalMatrix = poseStack.m_85850_().m_252943_();
         inner.m_252986_(poseMatrix, -halfRadius, yMin, -halfRadius)
            .m_6122_(r, g, b, a)
            .m_7421_(0.0F, min)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -halfRadius, yMin, halfRadius)
            .m_6122_(r, g, b, a)
            .m_7421_(1.0F, min)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -halfRadius, 250.0F, halfRadius)
            .m_6122_(r, g, b, a)
            .m_7421_(1.0F, max)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -halfRadius, 250.0F, -halfRadius)
            .m_6122_(r, g, b, a)
            .m_7421_(0.0F, max)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         int color = RenderHelper.colorf(Mth.m_14036_(1.0F * f, 0.0F, 1.0F), Mth.m_14036_(0.85F * f, 0.0F, 1.0F), Mth.m_14036_(0.7F * f * f, 0.0F, 1.0F));
         inner.m_252986_(poseMatrix, -quarterRadius, yMin, -quarterRadius)
            .m_193479_(color)
            .m_7421_(0.0F, min)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -quarterRadius, yMin, quarterRadius)
            .m_193479_(color)
            .m_7421_(1.0F, min)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -quarterRadius, 250.0F, quarterRadius)
            .m_193479_(color)
            .m_7421_(1.0F, max)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         inner.m_252986_(poseMatrix, -quarterRadius, 250.0F, -quarterRadius)
            .m_193479_(color)
            .m_7421_(0.0F, max)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
            .m_5752_();
         poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      }

      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(SunbeamEntity entity) {
      return SpellRenderingHelper.BEACON;
   }
}
