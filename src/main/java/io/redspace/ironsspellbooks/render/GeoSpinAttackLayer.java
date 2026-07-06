package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.entity.spells.SpinAttackModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class GeoSpinAttackLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
   private GeoModel<AbstractSpellCastingMob> modelProvider;

   public GeoSpinAttackLayer(GeoRenderer<AbstractSpellCastingMob> entityRendererIn) {
      super(entityRendererIn);
   }

   public GeoSpinAttackLayer(AbstractSpellCastingMobRenderer entityRendererIn) {
      super(entityRendererIn);
      this.modelProvider = new SpinAttackModel();
   }

   public void render(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (animatable.m_21209_()) {
         poseStack.m_85836_();

         for (int i = 0; i < 3; i++) {
            poseStack.m_85836_();
            float f = animatable.f_19797_ + partialTick + -(45 + i * 5);
            poseStack.m_252781_(Axis.f_252436_.m_252977_(f));
            float f1 = 0.75F * i;
            poseStack.m_85841_(f1, f1, f1);
            poseStack.m_85837_(0.0, -1.0 + (-0.2F + 0.6F * i), 0.0);
            RenderType rendertype = RenderType.m_110458_(this.modelProvider.getTextureResource(animatable));
            this.getRenderer()
               .actuallyRender(
                  poseStack,
                  animatable,
                  this.modelProvider.getBakedModel(this.modelProvider.getModelResource(animatable)),
                  rendertype,
                  bufferSource,
                  bufferSource.m_6299_(rendertype),
                  true,
                  partialTick,
                  15728880,
                  packedOverlay,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F
               );
            poseStack.m_85849_();
         }

         poseStack.m_85849_();
      }
   }
}
