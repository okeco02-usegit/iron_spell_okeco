package io.redspace.ironsspellbooks.entity.spells.lightning_lance;

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

public class LightningLanceRenderer extends EntityRenderer<LightningLanceProjectile> {
   private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance.png");

   public LightningLanceRenderer(Context context) {
      super(context);
   }

   public void render(LightningLanceProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      renderModel(poseStack, bufferSource, entity.getAge());
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110436_(TEXTURE, 0.0F, 0.0F));
      int framecout = 7;
      int anim = animOffset % framecout;
      float uvMin = (float)anim / framecout;
      float uvMax = (float)(anim + 1) / framecout;
      float halfWidth = 2.0F;
      float halfHeight = 1.0F;
      float angleCorrection = 50.0F;
      float texturefix = -0.2F;
      poseStack.m_252781_(Axis.f_252529_.m_252977_(angleCorrection));
      consumer.m_252986_(poseMatrix, 0.0F, -halfWidth, -halfHeight + texturefix)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMin, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, halfWidth, -halfHeight + texturefix)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMin, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, halfWidth, halfHeight + texturefix)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMax, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, -halfWidth, halfHeight + texturefix)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMax, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-angleCorrection));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-angleCorrection));
      consumer.m_252986_(poseMatrix, -halfWidth - texturefix, 0.0F, -halfHeight)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMin, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, halfWidth - texturefix, 0.0F, -halfHeight)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMin, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, halfWidth - texturefix, 0.0F, halfHeight)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMax, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, -halfWidth - texturefix, 0.0F, halfHeight)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(uvMax, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      poseStack.m_252781_(Axis.f_252436_.m_252977_(angleCorrection));
   }

   public ResourceLocation getTextureLocation(LightningLanceProjectile entity) {
      return TEXTURE;
   }
}
