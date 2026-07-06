package io.redspace.ironsspellbooks.entity.spells.ray_of_frost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RayOfFrostRenderer extends EntityRenderer<RayOfFrostVisualEntity> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ray_of_frost_model"), "main"
   );
   private static final ResourceLocation TEXTURE_CORE = IronsSpellbooks.id("textures/entity/ray_of_frost/core.png");
   private static final ResourceLocation TEXTURE_OVERLAY = IronsSpellbooks.id("textures/entity/ray_of_frost/overlay.png");
   private final ModelPart body;

   public RayOfFrostRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.body = modelpart.m_171324_("body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public boolean shouldRender(RayOfFrostVisualEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
      return true;
   }

   public void render(RayOfFrostVisualEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      float lifetime = 15.0F;
      float scalar = 0.25F;
      float length = 32.0F * scalar * scalar;
      float f = entity.f_19797_ + partialTicks;
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_146908_() - 180.0F));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-entity.m_146909_() - 90.0F));
      poseStack.m_85841_(scalar, scalar, scalar);
      float alpha = Mth.m_14036_(1.0F - f / lifetime, 0.0F, 1.0F);

      for (float i = 0.0F; i < entity.distance * 4.0F; i += length) {
         poseStack.m_252880_(0.0F, length, 0.0F);
         VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110436_(TEXTURE_OVERLAY, 0.0F, 0.0F));
         poseStack.m_85836_();
         float expansion = Mth.m_144920_(1.2F, 0.0F, f / lifetime);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(f * 5.0F));
         poseStack.m_85841_(expansion, 1.0F, expansion);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(45.0F));
         this.body.m_104306_(poseStack, consumer, 15728880, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, alpha);
         poseStack.m_85849_();
         consumer = bufferSource.m_6299_(RenderType.m_110436_(TEXTURE_CORE, 0.0F, 0.0F));
         poseStack.m_85836_();
         expansion = Mth.m_144920_(1.0F, 0.0F, f / (lifetime - 8.0F));
         poseStack.m_85841_(expansion, 1.0F, expansion);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(f * -10.0F));
         this.body.m_104306_(poseStack, consumer, 15728880, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         poseStack.m_85849_();
      }

      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(RayOfFrostVisualEntity entity) {
      return TEXTURE_CORE;
   }
}
