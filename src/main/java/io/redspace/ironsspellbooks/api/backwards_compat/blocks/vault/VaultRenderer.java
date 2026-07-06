package io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data.VaultBlockEntity;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data.VaultClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
   private final ItemRenderer itemRenderer;
   private final RandomSource random = RandomSource.m_216327_();

   public VaultRenderer(Context context) {
      this.itemRenderer = context.m_234447_();
   }

   public void render(VaultBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(blockEntity.getSharedData())) {
         Level level = blockEntity.m_58904_();
         if (level != null) {
            ItemStack itemstack = blockEntity.getSharedData().getDisplayItem();
            if (!itemstack.m_41619_()) {
               this.random.m_188584_(0L);
               VaultClientData vaultclientdata = blockEntity.getClientData();
               renderItemInside(
                  partialTick,
                  level,
                  poseStack,
                  bufferSource,
                  packedLight,
                  itemstack,
                  this.itemRenderer,
                  vaultclientdata.previousSpin(),
                  vaultclientdata.currentSpin(),
                  this.random
               );
            }
         }
      }
   }

   public static void renderItemInside(
      float partialTick,
      Level level,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      ItemStack item,
      ItemRenderer itemRenderer,
      float previousSpin,
      float currentSpin,
      RandomSource random
   ) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.5F, 0.4F, 0.5F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14189_(partialTick, previousSpin, currentSpin)));
      renderMultipleFromCount(itemRenderer, poseStack, buffer, packedLight, item, random, level);
      poseStack.m_85849_();
   }

   public static void renderMultipleFromCount(
      ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack item, RandomSource random, Level level
   ) {
      BakedModel bakedmodel = itemRenderer.m_174264_(item, level, null, 0);
      renderMultipleFromCount(itemRenderer, poseStack, buffer, packedLight, item, bakedmodel, bakedmodel.m_7539_(), random);
   }

   static int getRenderedAmount(int count) {
      if (count <= 1) {
         return 1;
      } else if (count <= 16) {
         return 2;
      } else if (count <= 32) {
         return 3;
      } else {
         return count <= 48 ? 4 : 5;
      }
   }

   public static void renderMultipleFromCount(
      ItemRenderer itemRenderer,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      ItemStack item,
      BakedModel model,
      boolean isGui3d,
      RandomSource random
   ) {
      int i = getRenderedAmount(item.m_41613_());
      float f = model.m_7442_().f_111793_.f_111757_.x();
      float f1 = model.m_7442_().f_111793_.f_111757_.y();
      float f2 = model.m_7442_().f_111793_.f_111757_.z();
      if (!isGui3d) {
         float f3 = -0.0F * (i - 1) * 0.5F * f;
         float f4 = -0.0F * (i - 1) * 0.5F * f1;
         float f5 = -0.09375F * (i - 1) * 0.5F * f2;
         poseStack.m_252880_(f3, f4, f5);
      }

      boolean shouldSpread = false;

      for (int j = 0; j < i; j++) {
         poseStack.m_85836_();
         if (j > 0 && shouldSpread) {
            if (isGui3d) {
               float f7 = (random.m_188501_() * 2.0F - 1.0F) * 0.15F;
               float f9 = (random.m_188501_() * 2.0F - 1.0F) * 0.15F;
               float f6 = (random.m_188501_() * 2.0F - 1.0F) * 0.15F;
               poseStack.m_252880_(f7, f9, f6);
            } else {
               float f8 = (random.m_188501_() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float f10 = (random.m_188501_() * 2.0F - 1.0F) * 0.15F * 0.5F;
               poseStack.m_252880_(f8, f10, 0.0F);
            }
         }

         itemRenderer.m_115143_(item, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, OverlayTexture.f_118083_, model);
         poseStack.m_85849_();
         if (!isGui3d) {
            poseStack.m_252880_(0.0F * f, 0.0F * f1, 0.09375F * f2);
         }
      }
   }
}
