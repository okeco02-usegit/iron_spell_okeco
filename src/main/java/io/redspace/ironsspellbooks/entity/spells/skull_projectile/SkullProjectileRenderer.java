package io.redspace.ironsspellbooks.entity.spells.skull_projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
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
import net.minecraft.world.phys.Vec3;

public class SkullProjectileRenderer extends EntityRenderer<AbstractMagicProjectile> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "skull_model"), "main"
   );
   private final ModelPart model;
   private final ResourceLocation textureLocation;

   public SkullProjectileRenderer(Context context, ResourceLocation textureLocation) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.model = modelpart.m_171324_("head");
      this.textureLocation = textureLocation;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 32, 32);
   }

   public void render(AbstractMagicProjectile entity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
      poseStack.m_85836_();
      Vec3 motion = entity.deltaMovementOld.m_82549_(entity.m_20184_().m_82546_(entity.deltaMovementOld).m_82490_(pPartialTicks));
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      poseStack.m_85841_(-1.0F, -1.0F, 1.0F);
      VertexConsumer vertexconsumer = pBuffer.m_6299_(RenderType.m_110452_(this.getTextureLocation(entity)));
      this.model.m_104301_(poseStack, vertexconsumer, pPackedLight, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
   }

   public ResourceLocation getTextureLocation(AbstractMagicProjectile entity) {
      return this.textureLocation;
   }
}
