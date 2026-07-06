package io.redspace.ironsspellbooks.entity.spells.guiding_bolt;

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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class GuidingBoltRenderer extends EntityRenderer<GuidingBoltProjectile> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "guiding_bolt_model"), "main"
   );
   private static final ResourceLocation BASE_TEXTURE = IronsSpellbooks.id("textures/entity/guiding_bolt/guiding_bolt.png");
   private static final ResourceLocation[] FIRE_TEXTURES = new ResourceLocation[]{
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_1.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_2.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_3.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_4.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_5.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_6.png"),
      IronsSpellbooks.id("textures/entity/guiding_bolt/fire_7.png")
   };
   protected final ModelPart body;
   protected final ModelPart outline;

   public GuidingBoltRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.body = modelpart.m_171324_("body");
      this.outline = modelpart.m_171324_("outline");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F), PartPose.f_171404_);
      partdefinition.m_171599_("outline", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 16.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 48, 24);
   }

   public void render(GuidingBoltProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110436_(this.getTextureLocation(entity), 0.0F, 0.0F));
      this.body.m_104301_(poseStack, consumer, 15728880, OverlayTexture.f_118083_);
      consumer = bufferSource.m_6299_(RenderType.m_110436_(this.getFireTextureLocation(entity), 0.0F, 0.0F));
      poseStack.m_85841_(0.4F, 0.4F, 0.4F);
      this.outline.m_104301_(poseStack, consumer, 15728880, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(GuidingBoltProjectile entity) {
      return BASE_TEXTURE;
   }

   public ResourceLocation getFireTextureLocation(Projectile entity) {
      int frame = entity.f_19797_ % FIRE_TEXTURES.length;
      return FIRE_TEXTURES[frame];
   }
}
