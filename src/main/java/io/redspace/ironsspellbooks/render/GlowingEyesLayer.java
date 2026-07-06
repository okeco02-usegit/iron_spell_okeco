package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GlowingEyesLayer {
   public static final ResourceLocation EYE_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/purple_eyes.png");
   public static final RenderType EYES = RenderType.m_110488_(EYE_TEXTURE);

   public static GlowingEyesLayer.EyeType getEyeType(LivingEntity entity) {
      if (entity.m_21023_((MobEffect)MobEffectRegistry.ABYSSAL_SHROUD.get())) {
         return GlowingEyesLayer.EyeType.Abyssal;
      } else {
         return entity.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get()) ? GlowingEyesLayer.EyeType.Planar_Sight : GlowingEyesLayer.EyeType.None;
      }
   }

   public static float getEyeScale(LivingEntity entity) {
      if (entity.m_6844_(EquipmentSlot.HEAD).m_150930_((Item)ItemRegistry.SHADOWWALKER_HELMET.get())) {
         return GlowingEyesLayer.EyeType.Ender_Armor.scale;
      } else if (entity.m_21023_((MobEffect)MobEffectRegistry.ABYSSAL_SHROUD.get())) {
         return GlowingEyesLayer.EyeType.Abyssal.scale;
      } else {
         return entity.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get())
            ? GlowingEyesLayer.EyeType.Planar_Sight.scale
            : GlowingEyesLayer.EyeType.None.scale;
      }
   }

   public enum EyeType {
      None(0.0F, 0.0F, 0.0F, 0.0F),
      Abyssal(1.0F, 1.0F, 1.0F, 1.0F),
      Planar_Sight(0.42F, 0.258F, 0.96F, 1.0F),
      Ender_Armor(0.816F, 0.0F, 1.0F, 1.15F);

      public final float r;
      public final float g;
      public final float b;
      public final float scale;

      EyeType(float r, float g, float b, float scale) {
         this.r = r;
         this.g = g;
         this.b = b;
         this.scale = scale;
      }
   }

   public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob> {
      public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn) {
         super(entityRendererIn);
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
         GlowingEyesLayer.EyeType eye = GlowingEyesLayer.getEyeType(animatable);
         if (eye != GlowingEyesLayer.EyeType.None) {
            bakedModel.getBone("head")
               .ifPresent(
                  headBone -> {
                     float scale = GlowingEyesLayer.getEyeScale(animatable);
                     headBone.updateScale(scale, scale, scale);
                     this.getRenderer()
                        .renderChildBones(
                           poseStack,
                           animatable,
                           headBone,
                           GlowingEyesLayer.EYES,
                           bufferSource,
                           buffer,
                           true,
                           partialTick,
                           packedLight,
                           packedOverlay,
                           eye.r,
                           eye.g,
                           eye.b,
                           1.0F
                        );
                  }
               );
         }
      }
   }

   public static class Vanilla<T extends LivingEntity, M extends HumanoidModel<T>> extends EyesLayer<T, M> {
      public Vanilla(RenderLayerParent pRenderer) {
         super(pRenderer);
      }

      public RenderType m_5708_() {
         return GlowingEyesLayer.EYES;
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource multiBufferSource,
         int pPackedLight,
         T livingEntity,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         GlowingEyesLayer.EyeType eye = GlowingEyesLayer.getEyeType(livingEntity);
         if (eye != GlowingEyesLayer.EyeType.None) {
            VertexConsumer vertexconsumer = multiBufferSource.m_6299_(this.m_5708_());
            float scale = GlowingEyesLayer.getEyeScale(livingEntity);
            poseStack.m_85841_(scale, scale, scale);
            ((HumanoidModel)this.m_117386_()).m_7695_(poseStack, vertexconsumer, 15728640, OverlayTexture.f_118083_, eye.r, eye.g, eye.b, 1.0F);
         }
      }
   }
}
