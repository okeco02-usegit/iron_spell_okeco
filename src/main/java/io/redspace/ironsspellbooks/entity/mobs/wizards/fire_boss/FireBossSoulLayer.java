package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class FireBossSoulLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/keeper/keeper_ghost.png");

   public FireBossSoulLayer(GeoEntityRenderer entityRendererIn) {
      super(entityRendererIn);
   }

   public void render(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel bakedModel,
      RenderType renderType2,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      int i = animatable.f_20916_ + animatable.f_20919_;
      if (i > 0) {
         float alpha = animatable.f_20919_ > 0 ? i / 120.0F : (float)i / animatable.f_20917_ * 2.0F;
         float f = animatable.f_19797_ + partialTick;
         RenderType renderType = RenderType.m_110436_(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
         VertexConsumer vertexconsumer = bufferSource.m_6299_(renderType);
         poseStack.m_85836_();
         List<GeoBone> bones = bakedModel.topLevelBones();
         this.setArmorVisible(bones, false);
         this.getRenderer()
            .actuallyRender(
               poseStack,
               animatable,
               bakedModel,
               renderType,
               bufferSource,
               vertexconsumer,
               true,
               partialTick,
               15728880,
               OverlayTexture.f_118083_,
               0.15F * alpha,
               0.02F * alpha,
               0.0F * alpha,
               1.0F
            );
         this.setArmorVisible(bones, true);
         poseStack.m_85849_();
      }
   }

   private void setArmorVisible(List<GeoBone> bones, boolean visible) {
      for (GeoBone bone : bones) {
         if (bone != null) {
            if (bone.getName().startsWith("armor")) {
               bone.setHidden(!visible);
            }

            this.setArmorVisible(bone.getChildBones(), visible);
         }
      }
   }
}
