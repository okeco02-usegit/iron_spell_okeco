package io.redspace.ironsspellbooks.entity.spells.portal;

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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PortalRenderer extends EntityRenderer<PortalEntity> {
   private static final ResourceLocation ROUND_PORTAL = IronsSpellbooks.id("textures/entity/portal/portal_round.png");
   private static final ResourceLocation ELDRITCH_ROUND_PORTAL = IronsSpellbooks.id("textures/entity/portal/pocket_dimension_portal_round.png");
   private static final ResourceLocation ELDRITCH_SQUARE_PORTAL = IronsSpellbooks.id("textures/entity/portal/pocket_dimension_portal_square.png");
   private static final ResourceLocation SQUARE_PORTAL = IronsSpellbooks.id("textures/entity/portal/portal_square.png");
   private static final ResourceLocation SQUARE_COLOR_PORTAL = IronsSpellbooks.id("textures/entity/portal/portal_square_color.png");
   static int frameCount = 10;
   static int ticksPerFrame = 2;

   public PortalRenderer(Context context) {
      super(context);
   }

   public void render(PortalEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_146908_()));
      renderPortal(poseStack, bufferSource, entity.f_19797_, partialTicks, true, -1);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public static void renderPortal(PoseStack poseStack, MultiBufferSource buffer, int animationTick, float partialTicks, boolean round, int color) {
      renderPortal(poseStack, buffer, animationTick, partialTicks, round, false, color);
   }

   public static void renderPortal(
      PoseStack poseStack, MultiBufferSource buffer, int animationTick, float partialTicks, boolean round, boolean eldritch, int color
   ) {
      poseStack.m_85836_();
      poseStack.m_85841_(0.0625F, 0.0625F, 0.0625F);
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      ResourceLocation texture = round
         ? (eldritch ? ELDRITCH_ROUND_PORTAL : ROUND_PORTAL)
         : (eldritch ? ELDRITCH_SQUARE_PORTAL : (color == -1 ? SQUARE_PORTAL : SQUARE_COLOR_PORTAL));
      VertexConsumer consumer = buffer.m_6299_(RenderHelper.CustomerRenderType.darkGlow(texture));
      int anim = animationTick / ticksPerFrame % frameCount;
      float uvMin = (float)anim / frameCount;
      float uvMax = (float)(anim + 1) / frameCount;
      vertex(poseMatrix, normalMatrix, consumer, -8.0F, 0.0F, 0.0F, uvMin, 0.0F, color);
      vertex(poseMatrix, normalMatrix, consumer, 8.0F, 0.0F, 0.0F, uvMax, 0.0F, color);
      vertex(poseMatrix, normalMatrix, consumer, 8.0F, 32.0F, 0.0F, uvMax, 1.0F, color);
      vertex(poseMatrix, normalMatrix, consumer, -8.0F, 32.0F, 0.0F, uvMin, 1.0F, color);
      poseStack.m_85849_();
   }

   public static void renderPortal(PoseStack poseStack, MultiBufferSource buffer, int animationTick, float partialTicks, boolean round) {
      renderPortal(poseStack, buffer, animationTick, partialTicks, round, -1);
   }

   public static void vertex(
      Matrix4f pMatrix,
      Matrix3f pNormals,
      VertexConsumer pVertexBuilder,
      float pOffsetX,
      float pOffsetY,
      float pOffsetZ,
      float pTextureX,
      float pTextureY,
      int color
   ) {
      int r = 255;
      int g = 255;
      int b = 255;
      if (color != -1) {
         r = (color & 0xFF0000) >> 16;
         g = (color & 0xFF00) >> 8;
         b = color & 0xFF;
      }

      pVertexBuilder.m_252986_(pMatrix, pOffsetX, pOffsetY, pOffsetZ)
         .m_6122_(r, g, b, 100)
         .m_7421_(pTextureX, pTextureY)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, 1.0F)
         .m_5752_();
   }

   public ResourceLocation getTextureLocation(PortalEntity entity) {
      return ROUND_PORTAL;
   }
}
