package io.redspace.ironsspellbooks.entity.spells.thunderstep;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ThunderstepProjectileRenderer extends EntityRenderer<ThunderstepProjectile> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "ball_lightning_model"), "main"
   );
   private static final ResourceLocation[] SWIRL_TEXTURES = new ResourceLocation[]{
      IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_0.png"),
      IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_1.png"),
      IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_2.png"),
      IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_3.png"),
      IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_4.png")
   };
   private final ModelPart orb;

   public ThunderstepProjectileRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.orb = modelpart.m_171324_("orb");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("orb", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 8, 8);
   }

   public void render(ThunderstepProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
      poseStack.m_85841_(0.2F, 0.2F, 0.2F);

      for (int i = 0; i < 3; i++) {
         poseStack.m_85836_();
         float r = 0.25F;
         float g = 0.8F;
         float b = 1.0F;
         r = Mth.m_14036_(r + r * i, 0.0F, 1.0F);
         g = Mth.m_14036_(g + g * i, 0.0F, 1.0F);
         b = Mth.m_14036_(b + b * i, 0.0F, 1.0F);
         float f = entity.f_19797_ + partialTicks + i * 777;
         float swirlX = Mth.m_14089_(0.065F * f) * 180.0F;
         float swirlY = Mth.m_14031_(0.065F * f) * 180.0F;
         float swirlZ = Mth.m_14089_(0.065F * f + 5464.0F) * 180.0F;
         float scalePerLayer = 0.2F;
         poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlX * (int)Math.pow(-1.0, i)));
         poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlY * (int)Math.pow(-1.0, i)));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlZ * (int)Math.pow(-1.0, i)));
         consumer = bufferSource.m_6299_(RenderHelper.CustomerRenderType.magic(this.getSwirlTextureLocation(entity, i * i)));
         float scale = 2.0F - i * scalePerLayer;
         poseStack.m_85841_(scale, scale, scale);
         this.orb.m_104306_(poseStack, consumer, 15728880, OverlayTexture.f_118083_, r, g, b, 1.0F);
         poseStack.m_85849_();
      }

      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(ThunderstepProjectile entity) {
      return SWIRL_TEXTURES[0];
   }

   private ResourceLocation getSwirlTextureLocation(ThunderstepProjectile entity, int offset) {
      int frame = (entity.f_19797_ + offset) % SWIRL_TEXTURES.length;
      return SWIRL_TEXTURES[frame];
   }
}
