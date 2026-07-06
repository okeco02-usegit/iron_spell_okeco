package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class EnergySwirlLayer {
   public static final ResourceLocation EVASION_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/evasion.png");
   public static final ResourceLocation CHARGE_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/charged.png");
   private static final int COLOR = RenderHelper.colorf(0.8F, 0.8F, 0.8F);

   private static RenderType getRenderType(ResourceLocation texture, float f) {
      return RenderType.m_110436_(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
   }

   private static boolean shouldRender(LivingEntity entity, Predicate<LivingEntity> shouldRenderFlag) {
      return shouldRenderFlag.test(entity);
   }

   public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob> {
      private final ResourceLocation TEXTURE;
      private final Predicate<LivingEntity> shouldRenderFlag;

      public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture, Supplier<MobEffect> shouldRenderFlag) {
         this(entityRendererIn, texture, living -> living.m_21023_(shouldRenderFlag.get()));
      }

      public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture, Predicate<LivingEntity> shouldRenderFlag) {
         super(entityRendererIn);
         this.TEXTURE = texture;
         this.shouldRenderFlag = shouldRenderFlag;
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
         if (EnergySwirlLayer.shouldRender(animatable, this.shouldRenderFlag)) {
            float f = animatable.f_19797_ + partialTick;
            RenderType renderType = EnergySwirlLayer.getRenderType(this.TEXTURE, f);
            VertexConsumer vertexconsumer = bufferSource.m_6299_(renderType);
            poseStack.m_85836_();
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.1F, 1.1F, 1.1F)));
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
                  packedLight,
                  OverlayTexture.f_118083_,
                  0.8F,
                  0.8F,
                  0.8F,
                  1.0F
               );
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.0F, 1.0F, 1.0F)));
            poseStack.m_85849_();
         }
      }
   }

   public static class Vanilla extends RenderLayer<Player, HumanoidModel<Player>> {
      public static ModelLayerLocation ENERGY_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "energy_layer"), "main");
      private final HumanoidModel<Player> model = new HumanoidModel(Minecraft.m_91087_().m_167973_().m_171103_(ENERGY_LAYER));
      private final ResourceLocation TEXTURE;
      private final Predicate<LivingEntity> shouldRender;

      public Vanilla(RenderLayerParent pRenderer, ResourceLocation texture, Predicate<LivingEntity> shouldRender) {
         super(pRenderer);
         this.TEXTURE = texture;
         this.shouldRender = shouldRender;
      }

      public Vanilla(RenderLayerParent pRenderer, ResourceLocation texture, Supplier<MobEffect> shouldRenderFlag) {
         this(pRenderer, texture, living -> living.m_21023_(shouldRenderFlag.get()));
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         Player pLivingEntity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float pPartialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (EnergySwirlLayer.shouldRender(pLivingEntity, this.shouldRender)) {
            float f = pLivingEntity.f_19797_ + pPartialTicks;
            HumanoidModel<Player> entitymodel = this.model();
            VertexConsumer vertexconsumer = pBuffer.m_6299_(EnergySwirlLayer.getRenderType(this.TEXTURE, f));
            ((HumanoidModel)this.m_117386_()).m_102872_(entitymodel);
            entitymodel.m_7695_(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.f_118083_, 0.8F, 0.8F, 0.8F, 1.0F);
         }
      }

      protected HumanoidModel<Player> model() {
         return this.model;
      }

      protected boolean shouldRender(Player entity) {
         return true;
      }
   }
}
