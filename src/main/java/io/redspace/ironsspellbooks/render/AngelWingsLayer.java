package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AngelWingsLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation WINGS_LOCATION = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/angel_wings.png");
   private final AngelWingsModel<T> angelWingsModel = new AngelWingsModel<>(Minecraft.m_91087_().m_167973_().m_171103_(AngelWingsModel.ANGEL_WINGS_LAYER));

   public AngelWingsLayer(RenderLayerParent<T, M> pRenderer) {
      super(pRenderer);
   }

   public void render(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      T pLivingEntity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (this.shouldRender(pLivingEntity)) {
         ResourceLocation resourcelocation;
         if (pLivingEntity instanceof AbstractClientPlayer abstractclientplayer) {
            if (abstractclientplayer.m_108562_() && abstractclientplayer.m_108563_() != null) {
               resourcelocation = abstractclientplayer.m_108563_();
            } else if (abstractclientplayer.m_108555_() && abstractclientplayer.m_108561_() != null && abstractclientplayer.m_36170_(PlayerModelPart.CAPE)) {
               resourcelocation = abstractclientplayer.m_108561_();
            } else {
               resourcelocation = this.getAngelWingsTexture(pLivingEntity);
            }
         } else {
            resourcelocation = this.getAngelWingsTexture(pLivingEntity);
         }

         pMatrixStack.m_85836_();
         pMatrixStack.m_85837_(0.0, 0.0, 0.125);
         this.m_117386_().m_102624_(this.angelWingsModel);
         this.angelWingsModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         VertexConsumer vertexconsumer = ItemRenderer.m_115184_(pBuffer, RenderType.m_110436_(resourcelocation, 0.0F, 0.0F), false, false);
         this.angelWingsModel.m_7695_(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         pMatrixStack.m_85849_();
      }
   }

   public boolean shouldRender(T entity) {
      return !entity.m_6844_(EquipmentSlot.CHEST).m_150930_(Items.f_42741_) && entity.m_21023_((MobEffect)MobEffectRegistry.ANGEL_WINGS.get());
   }

   public ResourceLocation getAngelWingsTexture(T entity) {
      return WINGS_LOCATION;
   }
}
