package io.redspace.ironsspellbooks.entity.spells.ice_spike;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class IceSpikeRenderer extends EntityRenderer<IceSpikeEntity> {
   private final IceSpikeRenderer.IceSpikeModel model;

   public IceSpikeRenderer(Context pContext) {
      super(pContext);
      this.model = new IceSpikeRenderer.IceSpikeModel(pContext.m_174023_(IceSpikeRenderer.IceSpikeModel.LAYER_LOCATION));
   }

   public void render(IceSpikeEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
      if (entity.f_19797_ >= entity.getWaitTime()) {
         float f = entity.f_19797_ + partialTicks;
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_146908_()));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(entity.m_146909_()));
         float anim = entity.getPositionOffset(partialTicks);
         poseStack.m_85841_(1.0F, -1.0F, 1.0F);
         float scale = entity.getSpikeSize();
         scale = (scale - 1.0F) * 0.25F + 1.0F;
         poseStack.m_85841_(scale, scale, scale);
         poseStack.m_252880_(0.0F, -anim * 68.0F / 16.0F, 0.0F);
         this.model.setupAnim(entity, partialTicks, 0.0F, 0.0F, entity.m_146908_(), entity.m_146909_());
         VertexConsumer vertexconsumer = multiBufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
         this.model.m_7695_(poseStack, vertexconsumer, light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         poseStack.m_85849_();
      }
   }

   public ResourceLocation getTextureLocation(IceSpikeEntity pEntity) {
      return IronsSpellbooks.id("textures/entity/ice_spike.png");
   }

   public static class IceSpikeModel extends EntityModel<IceSpikeEntity> {
      public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
         ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_spike"), "main"
      );
      private final ModelPart bottom;
      private final ModelPart middle;
      private final ModelPart top;

      public IceSpikeModel(ModelPart root) {
         this.bottom = root.m_171324_("bottom");
         this.middle = root.m_171324_("middle");
         this.top = root.m_171324_("top");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = new MeshDefinition();
         PartDefinition partdefinition = meshdefinition.m_171576_();
         PartDefinition bottom = partdefinition.m_171599_(
            "bottom",
            CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-1.0F, -25.0F, -9.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.m_171419_(-4.0F, 25.0F, 4.0F)
         );
         PartDefinition cube_r1 = bottom.m_171599_(
            "cube_r1",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(8.0F, 0.0F, -8.0F, 0.3295F, -0.1172F, 0.3295F)
         );
         PartDefinition cube_r2 = bottom.m_171599_(
            "cube_r2",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(0.0F, 0.0F, 0.0F, -2.812F, 0.1172F, 2.812F)
         );
         PartDefinition middle = partdefinition.m_171599_(
            "middle",
            CubeListBuilder.m_171558_().m_171514_(0, 34).m_171488_(-1.0F, -25.0F, -1.0F, 8.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.m_171419_(-3.0F, 3.0F, -3.0F)
         );
         PartDefinition cube_r3 = middle.m_171599_(
            "cube_r3",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(6.0F, 0.0F, 6.0F, -1.3526F, -1.3526F, 1.5708F)
         );
         PartDefinition cube_r4 = middle.m_171599_(
            "cube_r4",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(0.0F, 0.0F, 0.0F, 1.789F, 1.3526F, 1.5708F)
         );
         PartDefinition top = partdefinition.m_171599_(
            "top",
            CubeListBuilder.m_171558_().m_171514_(39, 38).m_171488_(-1.0F, -25.0F, -3.0F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.m_171419_(-1.0F, -19.0F, 1.0F)
         );
         PartDefinition cube_r5 = top.m_171599_(
            "cube_r5",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(2.0F, 0.0F, -2.0F, 0.1719F, -0.0302F, 0.1719F)
         );
         PartDefinition cube_r6 = top.m_171599_(
            "cube_r6",
            CubeListBuilder.m_171558_().m_171514_(40, 3).m_171488_(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.m_171423_(0.0F, 0.0F, 0.0F, -2.9697F, 0.0302F, 2.9697F)
         );
         return LayerDefinition.m_171565_(meshdefinition, 64, 64);
      }

      public void setupAnim(IceSpikeEntity entity, float partialTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
         float scale = entity.getSpikeSize();
         this.top.f_104207_ = false;
         this.bottom.f_104207_ = false;
         int ypos = 26;
         if (scale >= 3.0F) {
            this.bottom.f_104207_ = true;
            ypos -= 26;
            this.bottom.f_104201_ = ypos;
         }

         ypos -= 22;
         this.middle.f_104201_ = ypos;
         if (scale >= 2.0F) {
            this.top.f_104207_ = true;
            ypos -= 22;
            this.top.f_104201_ = ypos;
         }
      }

      public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
         this.bottom.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
         this.middle.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
         this.top.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
      }
   }
}
