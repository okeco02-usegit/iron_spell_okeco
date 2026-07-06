package io.redspace.ironsspellbooks.entity.spells.fiery_dagger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FieryDaggerRenderer extends GeoEntityRenderer<FieryDaggerEntity> {
   public FieryDaggerRenderer(Context renderManager) {
      super(renderManager, new FieryDaggerModel());
   }

   public void preRender(
      PoseStack poseStack,
      FieryDaggerEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
      Vec3 motion = entity.deltaMovementOld.m_82549_(entity.m_20184_().m_82546_(entity.deltaMovementOld).m_82490_(partialTick));
      float xRot = (float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F;
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) - 90.0F);
      poseStack.m_252880_(0.0F, entity.m_20206_() * 0.5F, 0.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
   }

   public Color getRenderColor(FieryDaggerEntity animatable, float partialTick, int packedLight) {
      return Color.LIGHT_GRAY;
   }
}
