package io.redspace.ironsspellbooks.entity.spells.snowball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SnowballRenderer extends EntityRenderer<AbstractMagicProjectile> {
   private final ModelPart orb;

   public SnowballRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(AcidOrbRenderer.MODEL_LAYER_LOCATION);
      this.orb = modelpart.m_171324_("orb");
   }

   public void render(AbstractMagicProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      poseStack.m_85837_(0.0, entity.m_20191_().m_82376_() * 0.5, 0.0);
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      float f = entity.f_19797_ + partialTicks;
      float swirlX = Mth.m_14089_(0.08F * f) * 130.0F;
      float swirlY = Mth.m_14031_(0.08F * f) * 130.0F;
      float swirlZ = Mth.m_14089_(0.08F * f + 5464.0F) * 130.0F;
      poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlX));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlY));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlZ));
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(this.getTextureLocation(entity)));
      this.orb.m_104301_(poseStack, consumer, light, OverlayTexture.f_118083_);
      poseStack.m_85849_();
   }

   public ResourceLocation getTextureLocation(AbstractMagicProjectile entity) {
      return ResourceLocation.withDefaultNamespace("textures/block/snow.png");
   }
}
