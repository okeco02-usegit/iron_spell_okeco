package io.redspace.ironsspellbooks.entity.spells.icicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class IcicleRenderer extends EntityRenderer<IcicleProjectile> {
   public static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/icicle_projectile.png");
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "icicle_model"), "main"
   );
   private final ModelPart body;

   public IcicleRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.body = modelpart.m_171324_("model");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition bb_main = partdefinition.m_171599_(
         "model",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 8)
            .m_171488_(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 0)
            .m_171488_(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.f_171404_
      );
      return LayerDefinition.m_171565_(meshdefinition, 16, 16);
   }

   public void render(IcicleProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      poseStack.m_85841_(1.0F, -1.0F, 1.0F);
      VertexConsumer consumer2 = bufferSource.m_6299_(RenderType.m_110458_(TEXTURE));
      this.body.m_104301_(poseStack, consumer2, light, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(IcicleProjectile entity) {
      return TEXTURE;
   }
}
