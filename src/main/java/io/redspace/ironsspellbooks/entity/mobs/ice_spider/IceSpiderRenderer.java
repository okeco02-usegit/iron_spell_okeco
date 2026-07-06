package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceSpiderRenderer extends GeoEntityRenderer<IceSpiderEntity> {
   public IceSpiderRenderer(Context renderManager) {
      super(renderManager, new IceSpiderModel());
   }

   public void preRender(
      PoseStack poseStack,
      IceSpiderEntity entity,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float r,
      float g,
      float b,
      float a
   ) {
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, r, g, b, a);
      Vec3 normal = Utils.lerp(partialTick, entity.lastNormal, entity.normal);
      poseStack.m_252781_(Utils.rotationBetweenVectors(new Vector3f(0.0F, 1.0F, 0.0F), this.cast(normal)));
   }

   protected float getDeathMaxRotation(IceSpiderEntity animatable) {
      return 180.0F;
   }

   private Vector3f cast(Vec3 vec3) {
      return new Vector3f((float)vec3.f_82479_, (float)vec3.f_82480_, (float)vec3.f_82481_);
   }
}
