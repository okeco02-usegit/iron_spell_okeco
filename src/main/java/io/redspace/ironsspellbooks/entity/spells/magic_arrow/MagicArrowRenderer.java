package io.redspace.ironsspellbooks.entity.spells.magic_arrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MagicArrowRenderer extends EntityRenderer<MagicArrowProjectile> {
   private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_arrow.png");

   public MagicArrowRenderer(Context context) {
      super(context);
   }

   public void render(MagicArrowProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      renderModel(poseStack, bufferSource);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
      poseStack.m_85841_(0.13F, 0.13F, 0.13F);
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      VertexConsumer consumer = bufferSource.m_6299_(RenderHelper.CustomerRenderType.magic(getTextureLocation()));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      poseStack.m_252880_(-2.0F, 0.0F, 0.0F);

      for (int j = 0; j < 4; j++) {
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         vertex(poseMatrix, normalMatrix, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 15728880);
      }
   }

   public static void vertex(
      Matrix4f pMatrix,
      Matrix3f pNormals,
      VertexConsumer pVertexBuilder,
      int pOffsetX,
      int pOffsetY,
      int pOffsetZ,
      float pTextureX,
      float pTextureY,
      int pNormalX,
      int p_113835_,
      int p_113836_,
      int pPackedLight
   ) {
      pVertexBuilder.m_252986_(pMatrix, pOffsetX, pOffsetY, pOffsetZ)
         .m_6122_(200, 200, 200, 255)
         .m_7421_(pTextureX, pTextureY)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(pPackedLight)
         .m_5601_(pNormalX, p_113836_, p_113835_)
         .m_5752_();
   }

   public ResourceLocation getTextureLocation(MagicArrowProjectile entity) {
      return getTextureLocation();
   }

   public static ResourceLocation getTextureLocation() {
      return TEXTURE;
   }
}
