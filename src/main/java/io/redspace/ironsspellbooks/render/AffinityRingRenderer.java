package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
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

public class AffinityRingRenderer extends BlockEntityWithoutLevelRenderer {
   private final ItemRenderer renderer;
   private final ResourceLocation defaultModel = IronsSpellbooks.id("item/affinity_ring_evocation");

   public AffinityRingRenderer(ItemRenderer renderDispatcher, EntityModelSet modelSet) {
      super(Minecraft.m_91087_().m_167982_(), modelSet);
      this.renderer = renderDispatcher;
   }

   public void m_108829_(
      ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn
   ) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.5F, 0.5F, 0.5F);
      BakedModel model;
      if (!AffinityData.hasAffinityData(itemStack)) {
         model = this.renderer.m_115103_().m_109393_().getModel(this.defaultModel);
      } else {
         ResourceLocation modelResource = getAffinityRingModelLocation(AffinityData.getAffinityData(itemStack).getSpell().getSchoolType());
         model = this.renderer.m_115103_().m_109393_().getModel(modelResource);
      }

      if (transformType == ItemDisplayContext.GUI) {
         Lighting.m_84930_();
         this.renderer.m_115143_(itemStack, transformType, false, poseStack, bufferSource, 15728880, OverlayTexture.f_118083_, model);
         Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
         Lighting.m_84931_();
      } else {
         boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         this.renderer.m_115143_(itemStack, transformType, leftHand, poseStack, bufferSource, combinedLightIn, combinedOverlayIn, model);
      }

      poseStack.m_85849_();
   }

   public static ResourceLocation getAffinityRingModelLocation(SchoolType schoolType) {
      return ResourceLocation.fromNamespaceAndPath(schoolType.getId().m_135827_(), String.format("item/affinity_ring_%s", schoolType.getId().m_135815_()));
   }
}
