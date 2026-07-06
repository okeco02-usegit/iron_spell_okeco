package io.redspace.ironsspellbooks.entity.spells.thrown_spear;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownSpearRenderer extends EntityRenderer<ThrownSpear> {
   public ThrownSpearRenderer(Context context) {
      super(context);
   }

   public void render(ThrownSpear entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      ItemStack item = entity.getWeaponItem();
      if (!item.m_41619_()) {
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-Mth.m_14179_(partialTicks, entity.f_19859_, entity.m_146908_())));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(-Mth.m_14179_(partialTicks, entity.f_19860_, entity.m_146909_()) + 10.0F));
         poseStack.m_85837_(0.0, -1.5, 0.0);
         Minecraft.m_91087_()
            .m_91291_()
            .m_269128_(item, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.f_118083_, poseStack, buffer, entity.f_19853_, 0);
         if (entity.isChanneled()) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(80.0F));
            LightningLanceRenderer.renderModel(poseStack, buffer, entity.f_19797_);
         }

         poseStack.m_85849_();
         super.m_7392_(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   public ResourceLocation getTextureLocation(ThrownSpear entity) {
      return ThrownTridentRenderer.f_116094_;
   }
}
