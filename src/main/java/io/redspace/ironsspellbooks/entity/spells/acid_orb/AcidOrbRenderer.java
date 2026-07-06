package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AcidOrbRenderer extends EntityRenderer<AcidOrb> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "acid_orb_model"), "main"
   );
   private static ResourceLocation ORB_TEXTURE = IronsSpellbooks.id("textures/entity/acid_orb/acid_orb.png");
   private static ResourceLocation[] SWIRL_TEXTURES = new ResourceLocation[]{
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_0.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_1.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_2.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_3.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_4.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_5.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_6.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_7.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_8.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_9.png"),
      IronsSpellbooks.id("textures/entity/acid_orb/swirl_10.png")
   };
   private final ModelPart orb;
   private final ModelPart swirl;

   public AcidOrbRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.orb = modelpart.m_171324_("orb");
      this.swirl = modelpart.m_171324_("swirl");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("orb", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.f_171404_);
      partdefinition.m_171599_("swirl", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 8, 8);
   }

   public void render(AcidOrb entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
      this.orb.m_104301_(poseStack, consumer, light, OverlayTexture.f_118083_);
      float f = entity.f_19797_ + partialTicks;
      float swirlX = Mth.m_14089_(0.08F * f) * 180.0F;
      float swirlY = Mth.m_14031_(0.08F * f) * 180.0F;
      float swirlZ = Mth.m_14089_(0.08F * f + 5464.0F) * 180.0F;
      poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlX));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlY));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlZ));
      consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getSwirlTextureLocation(entity)));
      poseStack.m_85841_(1.15F, 1.15F, 1.15F);
      this.swirl.m_104301_(poseStack, consumer, light, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(AcidOrb entity) {
      return ORB_TEXTURE;
   }

   private ResourceLocation getSwirlTextureLocation(AcidOrb entity) {
      int frame = entity.f_19797_ / 2 % SWIRL_TEXTURES.length;
      return SWIRL_TEXTURES[frame];
   }
}
