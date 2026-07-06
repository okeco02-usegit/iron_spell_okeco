package io.redspace.ironsspellbooks.entity.spells.target_area;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TargetAreaRenderer extends EntityRenderer<TargetedAreaEntity> {
   int fadeTick = -1;

   public TargetAreaRenderer(Context pContext) {
      super(pContext);
   }

   public ResourceLocation getTextureLocation(TargetedAreaEntity pEntity) {
      return null;
   }

   public void render(TargetedAreaEntity entity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110436_(SpellRenderingHelper.SOLID, 0.0F, 0.0F));
      Vector3f color = entity.getColor();
      poseStack.m_85836_();
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      float radius = entity.getRadius();
      int segments = (int)(5.0F * radius + 9.0F);
      float angle = (float) (Math.PI * 2) / segments;
      float entityY = (float)Mth.m_14139_(pPartialTick, entity.f_19791_, entity.m_20186_());
      float[] heights = new float[6];

      for (int i = 0; i < 6; i++) {
         int degrees = i * 60;
         float x = radius * Mth.m_14089_(degrees * (float) (Math.PI / 180.0));
         float z = radius * Mth.m_14031_(degrees * (float) (Math.PI / 180.0));
         float y = Utils.findRelativeGroundLevel(entity.f_19853_, entity.m_20182_().m_82520_(x, entity.m_20206_(), z), (int)(entity.m_20206_() * 4.0F));
         heights[i] = y - entityY;
         if (entity.f_19853_.m_186437_(null, AABB.m_165882_(new Vec3(x, y, z), 0.1, 0.1, 0.1))) {
            heights[i] = 0.0F;
         }
      }

      for (int i = 0; i < segments; i++) {
         float theta = angle * i;
         float theta2 = angle * (i + 1);
         float x1 = radius * Mth.m_14089_(theta);
         float x2 = radius * Mth.m_14089_(theta2);
         float z1 = radius * Mth.m_14031_(theta);
         float z2 = radius * Mth.m_14031_(theta2);
         int degrees = (int)(theta * (180.0F / (float)Math.PI));
         int degrees2 = (int)(theta2 * (180.0F / (float)Math.PI));
         int j = degrees / 60 % 6;
         float heightMin = heights[j];
         float heightMax = heights[(j + 1) % 6];
         float f = theta * (180.0F / (float)Math.PI) % 60.0F / 60.0F;
         float f2 = theta2 * (180.0F / (float)Math.PI) % 60.0F / 60.0F;
         float y1 = Mth.m_14179_(f, heightMin, heightMax);
         if (f2 < f) {
            heightMin = heightMax;
            heightMax = heights[(j + 2) % 6];
         }

         float y2 = Mth.m_14179_(f2, heightMin, heightMax);
         float alpha = 1.0F;
         if (entity.isFading()) {
            if (this.fadeTick < 0) {
               this.fadeTick = entity.f_19797_;
            }

            alpha = Mth.m_144920_(1.0F, 0.0F, (entity.f_19797_ + pPartialTick - this.fadeTick) / 10.0F);
         }

         consumer.m_252986_(poseMatrix, x2, y2 - 0.6F, z2)
            .m_85950_(color.x() * alpha, color.y() * alpha, color.z() * alpha, 1.0F)
            .m_7421_(0.0F, 1.0F)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(light * 4)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(poseMatrix, x2, y2 + 0.6F, z2)
            .m_6122_(0, 0, 0, 1)
            .m_7421_(0.0F, 0.0F)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(light * 4)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(poseMatrix, x1, y1 + 0.6F, z1)
            .m_6122_(0, 0, 0, 1)
            .m_7421_(1.0F, 0.0F)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(light * 4)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(poseMatrix, x1, y1 - 0.6F, z1)
            .m_85950_(color.x() * alpha, color.y() * alpha, color.z() * alpha, 1.0F)
            .m_7421_(1.0F, 1.0F)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(light * 4)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
      }

      poseStack.m_85849_();
   }

   private ParticleOptions particle(int i) {
      return switch (i) {
         case 0 -> ParticleHelper.SNOWFLAKE;
         case 1 -> ParticleHelper.UNSTABLE_ENDER;
         case 2 -> ParticleHelper.ACID_BUBBLE;
         case 3 -> ParticleHelper.BLOOD;
         case 4 -> ParticleHelper.WISP;
         case 5 -> ParticleHelper.ELECTRIC_SPARKS;
         default -> throw new IllegalStateException("Unexpected value: " + i);
      };
   }
}
