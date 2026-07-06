package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class SpellBookCurioRenderer implements ICurioRenderer {
   ItemRenderer itemRenderer = Minecraft.m_91087_().m_91291_();

   public <T extends LivingEntity, M extends EntityModel<T>> void render(
      ItemStack itemStack,
      SlotContext slotContext,
      PoseStack poseStack,
      RenderLayerParent<T, M> renderLayerParent,
      MultiBufferSource renderTypeBuffer,
      int light,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (renderLayerParent.m_7200_() instanceof HumanoidModel) {
         HumanoidModel<LivingEntity> humanoidModel = (HumanoidModel<LivingEntity>)renderLayerParent.m_7200_();
         poseStack.m_85836_();
         humanoidModel.f_102810_.m_104299_(poseStack);
         poseStack.m_85837_((slotContext.entity() != null && !slotContext.entity().m_6844_(EquipmentSlot.CHEST).m_41619_() ? -5.5 : -4.5) * 0.0625, 0.5625, 0.0);
         poseStack.m_252781_(Axis.f_252436_.m_252961_((float) Math.PI));
         poseStack.m_252781_(Axis.f_252403_.m_252961_(3.0543263F));
         poseStack.m_85841_(0.625F, 0.625F, 0.625F);
         this.itemRenderer.m_269128_(itemStack, ItemDisplayContext.FIXED, light, OverlayTexture.f_118083_, poseStack, renderTypeBuffer, null, 0);
         poseStack.m_85849_();
      }
   }
}
