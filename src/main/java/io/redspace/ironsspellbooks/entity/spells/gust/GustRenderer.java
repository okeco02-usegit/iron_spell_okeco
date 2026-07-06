package io.redspace.ironsspellbooks.entity.spells.gust;

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
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GustRenderer extends EntityRenderer<GustCollider> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "gust_model"), "main"
   );
   private static ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/trident_riptide.png");
   private final ModelPart body;

   public GustRenderer(Context context) {
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

   public void render(GustCollider entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-entity.m_146908_() - 180.0F));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-entity.m_146909_() - 90.0F));
      poseStack.m_85841_(0.25F, 0.25F, 0.25F);
      float f = entity.f_19797_ + partialTicks;
      float scale = Mth.m_14179_(Mth.m_14036_(f / 6.0F, 0.0F, 1.0F), 1.0F, 2.3F);
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110473_(this.getTextureLocation(entity)));
      float alpha = 1.0F - f / 10.0F;

      for (int i = 0; i < 3; i++) {
         poseStack.m_252781_(Axis.f_252436_.m_252977_(f * 10.0F));
         poseStack.m_85841_(scale, scale, scale);
         poseStack.m_252880_(0.0F, scale - 1.0F, 0.0F);
         this.body.m_104306_(poseStack, consumer, light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, alpha);
      }

      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(GustCollider entity) {
      return TEXTURE;
   }
}
