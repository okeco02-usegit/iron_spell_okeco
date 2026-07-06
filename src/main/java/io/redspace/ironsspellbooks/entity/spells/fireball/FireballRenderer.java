package io.redspace.ironsspellbooks.entity.spells.fireball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
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
import net.minecraft.world.entity.projectile.Projectile;

public class FireballRenderer extends EntityRenderer<Projectile> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fireball_model"), "main"
   );
   public static final ResourceLocation BASE_TEXTURE = IronsSpellbooks.id("textures/entity/fireball/fireball_core.png");
   public static final ResourceLocation[] FIRE_TEXTURES = new ResourceLocation[]{
      IronsSpellbooks.id("textures/entity/fireball/fire_0.png"),
      IronsSpellbooks.id("textures/entity/fireball/fire_1.png"),
      IronsSpellbooks.id("textures/entity/fireball/fire_2.png"),
      IronsSpellbooks.id("textures/entity/fireball/fire_3.png")
   };
   protected final ModelPart body;
   protected final ModelPart outline;
   protected final float scale;

   public FireballRenderer(Context context, float scale) {
      super(context);
      ModelPart modelpart = context.m_174023_(MODEL_LAYER_LOCATION);
      this.body = modelpart.m_171324_("body");
      this.outline = modelpart.m_171324_("outline");
      this.scale = scale;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.f_171404_);
      partdefinition.m_171599_("outline", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 16.0F), PartPose.f_171404_);
      return LayerDefinition.m_171565_(meshdefinition, 48, 24);
   }

   public void render(Projectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      poseStack.m_85841_(this.scale, this.scale, this.scale);
      if (entity instanceof AbstractMagicProjectile) {
         poseStack.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entity.f_19859_, entity.m_146908_()) + 180.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entity.f_19860_, entity.m_146909_())));
      } else {
         poseStack.m_252781_(Axis.f_252436_.m_252977_(-Mth.m_14179_(partialTicks, entity.f_19859_, entity.m_146908_())));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(-Mth.m_14179_(partialTicks, entity.f_19860_, entity.m_146909_())));
      }

      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
      this.body.m_104301_(poseStack, consumer, 15728880, OverlayTexture.f_118083_);
      consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getFireTextureLocation(entity)));
      poseStack.m_85841_(1.15F, 1.15F, 1.15F);
      this.outline.m_104301_(poseStack, consumer, 15728880, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(Projectile entity) {
      return BASE_TEXTURE;
   }

   public ResourceLocation getFireTextureLocation(Projectile entity) {
      int frame = entity.f_19797_ / 2 % FIRE_TEXTURES.length;
      return FIRE_TEXTURES[frame];
   }
}
