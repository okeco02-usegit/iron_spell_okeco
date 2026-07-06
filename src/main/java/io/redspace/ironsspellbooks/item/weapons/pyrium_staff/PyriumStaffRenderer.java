package io.redspace.ironsspellbooks.item.weapons.pyrium_staff;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class PyriumStaffRenderer extends BlockEntityWithoutLevelRenderer {
   private final ItemRenderer renderer;
   public final BakedModel haftModel;
   public final PyriumStaffHeadModel headModel;
   public final PyriumStaffOrbModel orbModel;

   public PyriumStaffRenderer(ItemRenderer renderDispatcher, EntityModelSet modelSet) {
      super(Minecraft.m_91087_().m_167982_(), modelSet);
      this.renderer = renderDispatcher;
      this.haftModel = this.renderer.m_115103_().m_109393_().getModel(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "item/pyrium_staff_haft"));
      this.headModel = new PyriumStaffHeadModel(modelSet.m_171103_(PyriumStaffHeadModel.LAYER_LOCATION));
      this.orbModel = new PyriumStaffOrbModel(modelSet.m_171103_(PyriumStaffOrbModel.LAYER_LOCATION));
   }

   public void m_108829_(
      ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn
   ) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.5F, 0.5F, 0.5F);
      if (transformType == ItemDisplayContext.GUI) {
         Lighting.m_84931_();
         this.render(poseStack, bufferSource, itemStack, transformType, 15728880, OverlayTexture.f_118083_, false);
         Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
      } else {
         boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         this.render(poseStack, bufferSource, itemStack, transformType, combinedLightIn, combinedOverlayIn, leftHand);
      }

      poseStack.m_85849_();
   }

   private void render(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      ItemStack itemStack,
      ItemDisplayContext transformType,
      int combinedLightIn,
      int combinedOverlayIn,
      boolean leftHanded
   ) {
      this.renderer.m_115143_(itemStack, transformType, leftHanded, poseStack, bufferSource, combinedLightIn, combinedOverlayIn, this.haftModel);
      poseStack.m_85836_();
      ItemTransform transform = this.haftModel.m_7442_().m_269404_(transformType);
      this.applyTransform(transform, leftHanded, poseStack);
      poseStack.m_252781_(Axis.f_252403_.m_252977_(135.0F));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-90.0F));
      poseStack.m_85837_(0.0, -0.3984375, 0.0);
      poseStack.m_85841_(0.5F, 0.5F, 0.5F);
      this.headModel
         .m_7695_(
            poseStack,
            ItemRenderer.m_115222_(bufferSource, this.headModel.renderType(), false, itemStack.m_41790_()),
            combinedLightIn,
            combinedOverlayIn,
            1.0F,
            1.0F,
            1.0F,
            1.0F
         );
      poseStack.m_85837_(0.0, -0.296875, 0.0);
      float f = MinecraftInstanceHelper.getPlayer() == null ? 0.0F : (MinecraftInstanceHelper.getPlayer().f_19797_ + Minecraft.m_91087_().m_91297_()) * 0.75F;
      float scale = (Mth.m_14031_(f * 0.5F) + Mth.m_14031_(3.0F * f)) / 2.0F * 0.04F + 1.0F;
      poseStack.m_252880_(0.0F, Mth.m_14031_(f * 0.3F) / 32.0F, 0.0F);
      poseStack.m_85841_(scale, scale, scale);
      this.orbModel.m_7695_(poseStack, bufferSource.m_6299_(this.orbModel.renderType()), 15728880, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.m_85849_();
   }

   public void applyTransform(ItemTransform transform, boolean pLeftHand, PoseStack pPoseStack) {
      if (transform != ItemTransform.f_111754_) {
         float f = transform.f_111755_.x();
         float f1 = transform.f_111755_.y();
         float f2 = transform.f_111755_.z();
         if (pLeftHand) {
            f1 = -f1;
            f2 = -f2;
         }

         int i = pLeftHand ? -1 : 1;
         pPoseStack.m_252880_(i * transform.f_111756_.x(), transform.f_111756_.y(), transform.f_111756_.z());
         pPoseStack.m_252781_(new Quaternionf().rotationXYZ(f * (float) (Math.PI / 180.0), f1 * (float) (Math.PI / 180.0), f2 * (float) (Math.PI / 180.0)));
         pPoseStack.m_85841_(transform.f_111757_.x(), transform.f_111757_.y(), transform.f_111757_.x());
      }
   }
}
