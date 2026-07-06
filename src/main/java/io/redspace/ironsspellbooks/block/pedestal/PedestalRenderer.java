package io.redspace.ironsspellbooks.block.pedestal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;

public class PedestalRenderer implements BlockEntityRenderer<PedestalTile> {
   ItemRenderer itemRenderer;
   private static final Vec3 ITEM_POS = new Vec3(0.5, 1.5, 0.5);

   public PedestalRenderer(Context context) {
      this.itemRenderer = context.m_234447_();
   }

   public void render(PedestalTile pedestalTile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      ItemStack heldItem = pedestalTile.getHeldItem();
      if (!heldItem.m_41619_()) {
         Player player = Minecraft.m_91087_().f_91074_;
         float bob = 0.0F;
         float rotation = player.f_19797_ * 2 + partialTick;
         this.renderItem(heldItem, ITEM_POS.m_82520_(0.0, bob, 0.0), rotation, pedestalTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      }
   }

   private void renderItem(
      ItemStack itemStack,
      Vec3 offset,
      float yRot,
      PedestalTile pedestalTile,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      poseStack.m_85836_();
      int renderId = (int)pedestalTile.m_58899_().m_121878_();
      poseStack.m_85837_(offset.f_82479_, offset.f_82480_, offset.f_82481_);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      if (itemStack.m_41720_() instanceof SwordItem || itemStack.m_41720_() instanceof DiggerItem) {
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
      }

      poseStack.m_85841_(0.65F, 0.65F, 0.65F);
      this.itemRenderer
         .m_269128_(
            itemStack,
            ItemDisplayContext.FIXED,
            LevelRenderer.m_109541_(pedestalTile.m_58904_(), pedestalTile.m_58899_()),
            packedOverlay,
            poseStack,
            bufferSource,
            pedestalTile.m_58904_(),
            renderId
         );
      poseStack.m_85849_();
   }
}
