package io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.IExtendedSimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FrozenHumanoidRenderer extends LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/frozen_humanoid.png");
   private static final ResourceLocation TEXTURE_ALT = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/frozen_humanoid_alt.png");
   final EntityModel<LivingEntity> originalModel;
   boolean rectangular = false;

   public FrozenHumanoidRenderer(Context context) {
      super(context, new HumanoidModel(context.m_174023_(ModelLayers.f_171162_)), 0.36F);
      this.originalModel = new HumanoidModel(context.m_174023_(ModelLayers.f_171162_));
   }

   public void m_7392_(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      LivingEntity entityToRender = entity;
      this.rectangular = false;
      if (entity instanceof FrozenHumanoid frozenHumanoid && frozenHumanoid.entityToCopy != null) {
         EntityRenderer<?> entityRenderer = (EntityRenderer<?>)Minecraft.m_91087_().m_91290_().f_114362_.get(frozenHumanoid.entityToCopy);
         Entity fakeEntity = frozenHumanoid.entityToCopy.m_20615_(Minecraft.m_91087_().f_91073_);
         if (fakeEntity instanceof LivingEntity livingFakeEntity) {
            FrozenHumanoid.copyEntityVisualProperties(livingFakeEntity, entity);
            entityToRender = livingFakeEntity;
         }

         if (entityRenderer instanceof LivingEntityRenderer<?, ?> renderer) {
            this.f_115290_ = renderer.m_7200_();
            ResourceLocation texturelocation = renderer.m_5478_(fakeEntity);
            AbstractTexture texture = Minecraft.m_91087_().m_91097_().m_118506_(texturelocation);
            if (texture instanceof SimpleTexture) {
               this.rectangular = ((IExtendedSimpleTexture)texture).irons_spellbooks$isRectangular();
            }
         }
      }

      try {
         super.m_7392_(entityToRender, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.error("Failed to render Ice Shadow of {}: {}", ((FrozenHumanoid)entity).entityToCopy, e.getMessage());
         ((FrozenHumanoid)entity).entityToCopy = null;
      }

      this.rectangular = false;
      this.f_115290_ = this.originalModel;
   }

   protected boolean m_6512_(LivingEntity entity) {
      double d0 = this.f_114476_.m_114471_(entity);
      float f = entity.m_6047_() ? 32.0F : 64.0F;
      return d0 >= f * f ? false : entity.m_20151_();
   }

   public ResourceLocation getTextureLocation(LivingEntity pEntity) {
      return this.rectangular ? TEXTURE_ALT : TEXTURE;
   }

   protected float m_6930_(LivingEntity pLivingBase, float pPartialTick) {
      return 0.0F;
   }
}
