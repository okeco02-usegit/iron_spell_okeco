package io.redspace.ironsspellbooks.entity.spells.blood_needle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodNeedleRenderer extends EntityRenderer<BloodNeedle> {
   private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/blood_needle/needle_5.png");

   public BloodNeedleRenderer(Context context) {
      super(context);
   }

   public void render(BloodNeedle entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Pose pose = poseStack.m_85850_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(entity.getZRot() + (entity.f_19797_ + partialTicks) * 40.0F));
      float width = 2.5F;
      poseStack.m_252781_(Axis.f_252529_.m_252977_(45.0F));
      float scale = entity.getScale();
      poseStack.m_85841_(scale, scale, scale);
      this.drawSlash(pose, entity, bufferSource, light, width);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   private void drawSlash(Pose pose, BloodNeedle entity, MultiBufferSource bufferSource, int light, float width) {
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
      float halfWidth = width * 0.5F;
      consumer.m_252986_(poseMatrix, 0.0F, -halfWidth, -halfWidth)
         .m_6122_(90, 0, 10, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, halfWidth, -halfWidth)
         .m_6122_(90, 0, 10, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, halfWidth, halfWidth)
         .m_6122_(90, 0, 10, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, -halfWidth, halfWidth)
         .m_6122_(90, 0, 10, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
   }

   public ResourceLocation getTextureLocation(BloodNeedle entity) {
      return TEXTURE;
   }
}
