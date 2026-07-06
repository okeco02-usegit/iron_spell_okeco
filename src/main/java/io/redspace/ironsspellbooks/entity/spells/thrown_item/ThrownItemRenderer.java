package io.redspace.ironsspellbooks.entity.spells.thrown_item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ThrownItemRenderer extends EntityRenderer<ThrownItemProjectile> {
   public ThrownItemRenderer(Context context) {
      super(context);
   }

   public void render(ThrownItemProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      ItemStack item = entity.getThrownItem();
      if (item.m_41619_()) {
         item = Items.f_41905_.m_7968_();
      }

      poseStack.m_85836_();
      Vec3 motion = entity.deltaMovementOld.m_82549_(entity.m_20184_().m_82546_(entity.deltaMovementOld).m_82490_(partialTick));
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot - (entity.f_19797_ + partialTick) * 36.0F));
      float scale = entity.getScale();
      poseStack.m_85841_(scale, scale, scale);
      Minecraft.m_91087_()
         .m_91291_()
         .m_269128_(item, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.f_118083_, poseStack, bufferSource, entity.f_19853_, 0);
      poseStack.m_85849_();
   }

   public ResourceLocation getTextureLocation(ThrownItemProjectile entity) {
      return IronsSpellbooks.id("empty");
   }
}
