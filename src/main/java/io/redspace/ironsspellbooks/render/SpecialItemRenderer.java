package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Deprecated(forRemoval = true)
public class SpecialItemRenderer extends BlockEntityWithoutLevelRenderer {
   private final ItemRenderer renderer;
   public final BakedModel guiModel;
   public final BakedModel normalModel;

   public SpecialItemRenderer(ItemRenderer renderDispatcher, EntityModelSet modelSet, String name) {
      super(Minecraft.m_91087_().m_167982_(), modelSet);
      this.renderer = renderDispatcher;
      this.guiModel = this.renderer.m_115103_().m_109393_().getModel(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "item/" + name + "_gui"));
      this.normalModel = this.renderer.m_115103_().m_109393_().getModel(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "item/" + name + "_normal"));
   }

   public void m_108829_(
      ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn
   ) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.5F, 0.5F, 0.5F);
      if (transformType == ItemDisplayContext.GUI) {
         Lighting.m_84930_();
         BakedModel model = this.guiModel;
         this.renderer.m_115143_(itemStack, transformType, false, poseStack, bufferSource, 15728880, OverlayTexture.f_118083_, model);
         Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
         Lighting.m_84931_();
      } else {
         BakedModel model = this.normalModel;
         boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         this.renderer.m_115143_(itemStack, transformType, leftHand, poseStack, bufferSource, combinedLightIn, combinedOverlayIn, model);
      }

      poseStack.m_85849_();
   }
}
