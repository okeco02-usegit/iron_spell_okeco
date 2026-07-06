package io.redspace.ironsspellbooks.entity.spells.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class ShieldTrimLayer extends RenderLayer<ShieldEntity, ShieldModel> {
   private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/shield/shield_trim.png");
   private final ShieldTrimModel model;

   public ShieldTrimLayer(RenderLayerParent<ShieldEntity, ShieldModel> renderer, Context context) {
      super(renderer);
      this.model = new ShieldTrimModel(context.m_174023_(ShieldTrimModel.LAYER_LOCATION));
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      ShieldEntity entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float partialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      Vec2 offset = ShieldRenderer.getEnergySwirlOffset(entity, partialTicks, 3456);
      VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110436_(TEXTURE, 0.0F, 0.0F));
      this.model.m_7695_(poseStack, consumer, 15728880, OverlayTexture.f_118083_, 0.65F, 0.65F, 0.65F, 1.0F);
   }
}
