package io.redspace.ironsspellbooks.entity.spells.wall_of_fire;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WallOfFireRenderer extends EntityRenderer<WallOfFireEntity> {
   private static ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/fire_0.png");

   public WallOfFireRenderer(Context context) {
      super(context);
   }

   public void render(WallOfFireEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110452_(TEXTURE));
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      float height = 3.0F;
      Vec3 origin = entity.m_20182_();

      for (int i = 0; i < entity.subEntities.length - 1; i++) {
         Vec3 start = entity.subEntities[i].m_20182_().m_82546_(origin);
         Vec3 end = entity.subEntities[i + 1].m_20182_().m_82546_(origin);
         int frameCount = 32;
         int frame = (entity.f_19797_ + i * 87) % frameCount;
         float uvPerFrame = 1.0F / frameCount;
         float uvY = frame * uvPerFrame;
         poseStack.m_85836_();
         consumer.m_252986_(poseMatrix, (float)start.f_82479_, (float)start.f_82480_, (float)start.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(0.0F, uvY + uvPerFrame)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)start.f_82479_, (float)start.f_82480_ + height, (float)start.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(0.0F, uvY)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)end.f_82479_, (float)end.f_82480_ + height, (float)end.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(1.0F, uvY)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)end.f_82479_, (float)end.f_82480_, (float)end.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(1.0F, uvY + uvPerFrame)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         poseStack.m_252781_(Axis.f_252403_.m_252977_(180.0F));
         consumer.m_252986_(poseMatrix, (float)start.f_82479_, (float)start.f_82480_, (float)start.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(0.0F, uvY + uvPerFrame)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)start.f_82479_, (float)start.f_82480_ + height, (float)start.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(0.0F, uvY)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)end.f_82479_, (float)end.f_82480_ + height, (float)end.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(1.0F, uvY)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         consumer.m_252986_(poseMatrix, (float)end.f_82479_, (float)end.f_82480_, (float)end.f_82481_)
            .m_6122_(255, 255, 255, 255)
            .m_7421_(1.0F, uvY + uvPerFrame)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(15728880)
            .m_5601_(0.0F, 1.0F, 0.0F);
         poseStack.m_85849_();
      }

      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(WallOfFireEntity entity) {
      return TEXTURE;
   }
}
