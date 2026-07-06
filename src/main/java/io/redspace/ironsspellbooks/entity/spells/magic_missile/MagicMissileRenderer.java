package io.redspace.ironsspellbooks.entity.spells.magic_missile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MagicMissileRenderer extends EntityRenderer<MagicMissileProjectile> {
   private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile/magic_missile.png");
   private static final ResourceLocation FLARE = IronsSpellbooks.id("textures/entity/lens_flare.png");
   private final ModelPart body;

   public MagicMissileRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.m_174023_(FireballRenderer.MODEL_LAYER_LOCATION);
      this.body = modelpart.m_171324_("body");
   }

   public void render(MagicMissileProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.m_85836_();
      Vec3 motion = entity.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
      poseStack.m_85841_(0.35F, 0.35F, 0.35F);
      VertexConsumer consumer = bufferSource.m_6299_(this.renderType(this.getTextureLocation(entity)));
      this.body.m_104306_(poseStack, consumer, 15728880, OverlayTexture.f_118083_, 0.8F, 0.8F, 0.8F, 1.0F);
      poseStack.m_85849_();
      poseStack.m_85836_();
      Pose pose = poseStack.m_85850_();
      Matrix4f poseMatrix = pose.m_252922_();
      float f = entity.f_19797_ + partialTicks;
      float scale = 0.5F + Mth.m_14031_(f) * 0.125F;
      poseStack.m_85841_(scale, scale, scale);
      poseStack.m_252781_(this.f_114476_.m_253208_());
      poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      poseStack.m_252781_(Axis.f_252529_.m_252977_((entity.f_19797_ + partialTicks) * 15.0F));
      consumer = bufferSource.m_6299_(RenderType.m_110473_(FLARE));
      consumer.m_252986_(poseMatrix, 0.0F, -1.0F, -1.0F)
         .m_6122_(255, 180, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, 1.0F, -1.0F)
         .m_6122_(255, 180, 255, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, 1.0F, 1.0F)
         .m_6122_(255, 180, 255, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, 0.0F, -1.0F, 1.0F)
         .m_6122_(255, 180, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 1.0F, 0.0F)
         .m_5752_();
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public RenderType renderType(ResourceLocation TEXTURE) {
      return RenderType.m_110436_(TEXTURE, 0.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(MagicMissileProjectile entity) {
      return TEXTURE;
   }
}
