package io.redspace.ironsspellbooks.entity.spells.black_hole;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleRenderer extends EntityRenderer<BlackHole> {
   private static final ResourceLocation CENTER_TEXTURE = IronsSpellbooks.id("textures/entity/black_hole/black_hole.png");
   private static final ResourceLocation BEAM_TEXTURE = IronsSpellbooks.id("textures/entity/black_hole/beam.png");
   private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

   public BlackHoleRenderer(Context pContext) {
      super(pContext);
   }

   public void render(BlackHole entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() / 2.0, 0.0);
      float entityScale = entity.m_20205_() * 0.025F;
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      Vec3 normalToCamera = this.f_114476_.f_114358_.m_90583_().m_82546_(entity.m_20191_().m_82399_()).m_82541_().m_82490_(2.0);
      poseStack.m_85837_(normalToCamera.f_82479_, normalToCamera.f_82480_, normalToCamera.f_82481_);
      poseStack.m_85841_(0.5F * entityScale, 0.5F * entityScale, 0.5F * entityScale);
      poseStack.m_252781_(this.f_114476_.m_253208_());
      poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110473_(CENTER_TEXTURE));
      float centerScale = 3.0F;
      consumer.m_252986_(poseMatrix, 0.0F, -centerScale, -centerScale)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, centerScale, -centerScale)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, centerScale, centerScale)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, -centerScale, centerScale)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      poseStack.m_85849_();
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() / 2.0, 0.0);
      float animationProgress = (entity.f_19797_ + partialTicks) / 200.0F;
      float fadeProgress = 0.5F;
      RandomSource randomSource = RandomSource.m_216335_(432L);
      VertexConsumer vertexConsumer = bufferSource.m_6299_(RenderType.m_110436_(BEAM_TEXTURE, 0.0F, 0.0F));
      float segments = Math.min(animationProgress, 0.8F);

      for (int i = 0; i < (segments + segments * segments) / 2.0F * 40.0F; i++) {
         poseStack.m_252781_(Axis.f_252529_.m_252977_(randomSource.m_188501_() * 360.0F));
         poseStack.m_252781_(Axis.f_252436_.m_252977_(randomSource.m_188501_() * 360.0F));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(randomSource.m_188501_() * 360.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(randomSource.m_188501_() * 360.0F));
         poseStack.m_252781_(Axis.f_252436_.m_252977_(randomSource.m_188501_() * 360.0F));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(randomSource.m_188501_() * 360.0F + animationProgress * 90.0F));
         float size1 = (randomSource.m_188501_() * 10.0F + 5.0F + fadeProgress * 5.0F) * entityScale * 0.4F;
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         Matrix3f normalMatrix2 = poseStack.m_85850_().m_252943_();
         drawTriangle(vertexConsumer, matrix, normalMatrix2, size1);
      }

      poseStack.m_85849_();
      super.m_7392_(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
   }

   public ResourceLocation getTextureLocation(BlackHole pEntity) {
      return IcicleRenderer.TEXTURE;
   }

   private static void drawTriangle(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
      consumer.m_252986_(poseMatrix, 0.0F, 0.0F, 0.0F)
         .m_6122_(255, 0, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, 3.0F * size, -1.0F * size)
         .m_6122_(0, 0, 0, 0)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, 3.0F * size, 1.0F * size)
         .m_6122_(0, 0, 0, 0)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, 0.0F, 0.0F)
         .m_6122_(255, 0, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
   }
}
