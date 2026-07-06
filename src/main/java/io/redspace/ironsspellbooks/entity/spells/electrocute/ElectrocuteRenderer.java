package io.redspace.ironsspellbooks.entity.spells.electrocute;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ElectrocuteRenderer extends EntityRenderer<ElectrocuteProjectile> {
   private static ResourceLocation[] TEXTURES = new ResourceLocation[]{
      IronsSpellbooks.id("textures/entity/electric_beams/beam_1.png"),
      IronsSpellbooks.id("textures/entity/electric_beams/beam_2.png"),
      IronsSpellbooks.id("textures/entity/electric_beams/beam_3.png"),
      IronsSpellbooks.id("textures/entity/electric_beams/beam_4.png")
   };
   private static ResourceLocation SOLID = IronsSpellbooks.id("textures/entity/electric_beams/solid.png");

   public ElectrocuteRenderer(Context context) {
      super(context);
   }

   public void render(ElectrocuteProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      if (entity.m_19749_() != null) {
         poseStack.m_85836_();
         Pose pose = poseStack.m_85850_();
         Matrix4f poseMatrix = pose.m_252922_();
         Matrix3f normalMatrix = pose.m_252943_();
         poseStack.m_252880_(0.0F, entity.m_20192_() * 0.5F, 0.0F);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_19749_().m_146908_()));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(entity.m_19749_().m_146909_()));
         poseStack.m_85837_(0.0, 0.0, 0.1);
         List<Vec3> segments = entity.getBeamCache();
         VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_234338_(this.getTextureLocation(entity)));
         float width = 0.3F;
         float height = width;
         Vec3 start = Vec3.f_82478_;

         for (int i = 0; i < segments.size() - 1; i += 2) {
            Vec3 from = segments.get(i).m_82549_(start);
            Vec3 to = segments.get(i + 1).m_82549_(start);
            this.drawHull(from, to, width, height, pose, consumer, 0, 156, 255, 30);
            this.drawHull(from, to, width * 0.55F, height * 0.55F, pose, consumer, 63, 178, 255, 30);
         }

         consumer = bufferSource.m_6299_(RenderHelper.CustomerRenderType.magicNoCull(this.getTextureLocation(entity)));

         for (int i = 0; i < segments.size() - 1; i += 2) {
            Vec3 from = segments.get(i).m_82549_(start);
            Vec3 to = segments.get(i + 1).m_82549_(start);
            this.drawHull(from, to, width * 0.2F, height * 0.2F, pose, consumer, 255, 255, 255, 255);
         }

         poseStack.m_85849_();
         super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
      }
   }

   public void drawHull(Vec3 from, Vec3 to, float width, float height, Pose pose, VertexConsumer consumer, int r, int g, int b, int a) {
      this.drawQuad(from.m_82492_(0.0, height * 0.5F, 0.0), to.m_82492_(0.0, height * 0.5F, 0.0), width, 0.0F, pose, consumer, r, g, b, a);
      this.drawQuad(from.m_82520_(0.0, height * 0.5F, 0.0), to.m_82520_(0.0, height * 0.5F, 0.0), width, 0.0F, pose, consumer, r, g, b, a);
      this.drawQuad(from.m_82492_(width * 0.5F, 0.0, 0.0), to.m_82492_(width * 0.5F, 0.0, 0.0), 0.0F, height, pose, consumer, r, g, b, a);
      this.drawQuad(from.m_82520_(width * 0.5F, 0.0, 0.0), to.m_82520_(width * 0.5F, 0.0, 0.0), 0.0F, height, pose, consumer, r, g, b, a);
   }

   public void drawQuad(Vec3 from, Vec3 to, float width, float height, Pose pose, VertexConsumer consumer, int r, int g, int b, int a) {
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      float halfWidth = width * 0.5F;
      float halfHeight = height * 0.5F;
      consumer.m_252986_(poseMatrix, (float)from.f_82479_ - halfWidth, (float)from.f_82480_ - halfHeight, (float)from.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)from.f_82479_ + halfWidth, (float)from.f_82480_ + halfHeight, (float)from.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)to.f_82479_ + halfWidth, (float)to.f_82480_ + halfHeight, (float)to.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)to.f_82479_ - halfWidth, (float)to.f_82480_ - halfHeight, (float)to.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }

   public ResourceLocation getTextureLocation(ElectrocuteProjectile p_115264_) {
      return SOLID;
   }
}
