package io.redspace.ironsspellbooks.entity.spells.ice_tomb;

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

public class IceTombRenderer extends EntityRenderer<IceTombEntity> {
   public static final ResourceLocation NOCULL = IronsSpellbooks.id("textures/entity/ice_tomb/ice_tomb.png");
   public static final ResourceLocation CULL = IronsSpellbooks.id("textures/entity/ice_tomb/ice_tomb_cull.png");
   private final IceTombRenderer.IceTombModel model;

   public IceTombRenderer(Context pContext) {
      super(pContext);
      this.model = new IceTombRenderer.IceTombModel(pContext.m_174023_(IceTombRenderer.IceTombModel.LAYER_LOCATION));
   }

   public void render(IceTombEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_146908_()));
      float xScaleFactor = entity.m_20205_() / entity.m_6095_().m_20680_().f_20377_;
      float yScaleFactor = entity.m_20206_() / entity.m_6095_().m_20680_().f_20378_;
      poseStack.m_85841_(xScaleFactor, -yScaleFactor, -xScaleFactor);
      poseStack.m_85837_(0.0, -1.501, 0.0);
      this.model.setupAnim(entity, partialTicks, 0.0F, 0.0F, entity.m_146908_(), entity.m_146909_());
      VertexConsumer vertexconsumer = multiBufferSource.m_6299_(RenderType.m_110473_(NOCULL));
      this.model.m_7695_(poseStack, vertexconsumer, light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      vertexconsumer = multiBufferSource.m_6299_(RenderType.m_110470_(CULL));
      this.model.m_7695_(poseStack, vertexconsumer, light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.m_85849_();
   }

   public ResourceLocation getTextureLocation(IceTombEntity pEntity) {
      return NOCULL;
   }

   public static class IceTombModel extends EntityModel<IceTombEntity> {
      public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
         ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ice_tomb"), "main"
      );
      private final ModelPart model;

      public IceTombModel(ModelPart root) {
         this.model = root.m_171324_("model");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = new MeshDefinition();
         PartDefinition partdefinition = meshdefinition.m_171576_();
         PartDefinition bb_main = partdefinition.m_171599_(
            "model",
            CubeListBuilder.m_171558_()
               .m_171514_(0, 0)
               .m_171488_(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(0.0F))
               .m_171514_(40, 67)
               .m_171488_(4.0F, -9.0F, 4.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
               .m_171514_(0, 52)
               .m_171488_(1.0F, -24.0F, -11.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
               .m_171514_(64, 0)
               .m_171488_(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(-0.01F))
               .m_171514_(40, 52)
               .m_171488_(-10.0F, -9.0F, -10.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
               .m_171514_(0, 86)
               .m_171488_(-11.0F, -24.0F, 1.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.m_171419_(0.0F, 24.0F, 0.0F)
         );
         return LayerDefinition.m_171565_(meshdefinition, 128, 128);
      }

      public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
         this.model.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
      }

      public void setupAnim(IceTombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      }
   }
}
