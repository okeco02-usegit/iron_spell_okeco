package io.redspace.ironsspellbooks.block.scroll_forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ScrollForgeRenderer implements BlockEntityRenderer<ScrollForgeTile> {
   private static final ResourceLocation PAPER_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/block/scroll_forge_paper.png");
   private static final ResourceLocation SIGIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/block/scroll_forge_sigil.png");
   ItemRenderer itemRenderer;
   private static final Vec3 INK_POS = new Vec3(0.175, 0.876, 0.25);
   private static final Vec3 FOCUS_POS = new Vec3(0.75, 0.876, 0.4);
   private static final Vec3 PAPER_POS = new Vec3(0.5, 0.876, 0.7);

   public ScrollForgeRenderer(Context context) {
      this.itemRenderer = context.m_234447_();
   }

   public void render(
      ScrollForgeTile scrollForgeTile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      ItemStack inkStack = scrollForgeTile.getStackInSlot(0);
      ItemStack paperStack = scrollForgeTile.getStackInSlot(1);
      ItemStack focusStack = scrollForgeTile.getItemHandler().getStackInSlot(2);
      if (!inkStack.m_41619_() && inkStack.m_41720_() instanceof InkItem) {
         this.renderItem(inkStack, INK_POS, 15.0F, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      }

      if (!focusStack.m_41619_() && focusStack.m_204117_(ModTags.SCHOOL_FOCUS)) {
         this.renderItem(focusStack, FOCUS_POS, 5.0F, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      }

      if (!paperStack.m_41619_() && paperStack.m_150930_(Items.f_42516_)) {
         poseStack.m_85836_();
         this.rotatePoseWithBlock(poseStack, scrollForgeTile);
         poseStack.m_85837_(PAPER_POS.f_82479_, PAPER_POS.f_82480_, PAPER_POS.f_82481_);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(85.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(180.0F));
         VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110452_(PAPER_TEXTURE));
         int light = LevelRenderer.m_109541_(scrollForgeTile.m_58904_(), scrollForgeTile.m_58899_());
         this.drawQuad(0.45F, poseStack.m_85850_(), consumer, light);
         poseStack.m_85849_();
      }
   }

   private void renderItem(
      ItemStack itemStack,
      Vec3 offset,
      float yRot,
      ScrollForgeTile scrollForgeTile,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      poseStack.m_85836_();
      int renderId = (int)scrollForgeTile.m_58899_().m_121878_();
      this.rotatePoseWithBlock(poseStack, scrollForgeTile);
      poseStack.m_85837_(offset.f_82479_, offset.f_82480_, offset.f_82481_);
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(-yRot));
      poseStack.m_85841_(0.45F, 0.45F, 0.45F);
      this.itemRenderer
         .m_269128_(
            itemStack,
            ItemDisplayContext.FIXED,
            LevelRenderer.m_109541_(scrollForgeTile.m_58904_(), scrollForgeTile.m_58899_()),
            packedOverlay,
            poseStack,
            bufferSource,
            scrollForgeTile.m_58904_(),
            renderId
         );
      poseStack.m_85849_();
   }

   private void drawQuad(float width, Pose pose, VertexConsumer consumer, int light) {
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      float halfWidth = width * 0.5F;
      consumer.m_252986_(poseMatrix, -halfWidth, 0.0F, -halfWidth)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_252939_(normalMatrix, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, halfWidth, 0.0F, -halfWidth)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_252939_(normalMatrix, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, halfWidth, 0.0F, halfWidth)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_252939_(normalMatrix, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, -halfWidth, 0.0F, halfWidth)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(light)
         .m_252939_(normalMatrix, 0.0F, -1.0F, 0.0F)
         .m_5752_();
   }

   private void rotatePoseWithBlock(PoseStack poseStack, ScrollForgeTile scrollForgeTile) {
      Vec3 center = new Vec3(0.5, 0.5, 0.5);
      poseStack.m_85837_(center.f_82479_, center.f_82480_, center.f_82481_);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(this.getBlockFacingDegrees(scrollForgeTile)));
      poseStack.m_85837_(-center.f_82479_, -center.f_82480_, -center.f_82481_);
   }

   private int getBlockFacingDegrees(ScrollForgeTile tileEntity) {
      BlockState block = tileEntity.m_58904_().m_8055_(tileEntity.m_58899_());
      if (block.m_60734_() instanceof ScrollForgeBlock) {
         Direction facing = (Direction)block.m_61143_(BlockStateProperties.f_61374_);

         return switch (facing) {
            case NORTH -> 180;
            case EAST -> 90;
            case WEST -> -90;
            default -> 0;
         };
      } else {
         return 0;
      }
   }
}
