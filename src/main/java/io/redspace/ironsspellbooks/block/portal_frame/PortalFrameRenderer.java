package io.redspace.ironsspellbooks.block.portal_frame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

public class PortalFrameRenderer implements BlockEntityRenderer<PortalFrameBlockEntity> {
   public PortalFrameRenderer(Context context) {
   }

   public void render(
      PortalFrameBlockEntity pBlockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      if (pBlockEntity.clientIsConnected && !((DoubleBlockHalf)pBlockEntity.m_58900_().m_61143_(PortalFrameBlock.HALF)).equals(DoubleBlockHalf.UPPER)) {
         poseStack.m_85836_();
         poseStack.m_85837_(0.5, 0.0, 0.5);
         Direction direction = (Direction)pBlockEntity.m_58900_().m_61143_(PortalFrameBlock.FACING);
         Vec3i n = direction.m_122436_();
         Vec3 dir = new Vec3(n.m_123341_(), 0.0, n.m_123343_()).m_82490_(-0.40625);
         poseStack.m_85837_(dir.f_82479_, 0.0, dir.f_82481_);
         if (direction == Direction.EAST || direction == Direction.WEST) {
            poseStack.m_252781_(Axis.f_252436_.m_252961_((float) (Math.PI / 2)));
         }

         PortalRenderer.renderPortal(
            poseStack,
            pBufferSource,
            pBlockEntity.m_58904_() == null ? 0 : (int)pBlockEntity.m_58904_().m_46467_(),
            pPartialTick,
            false,
            pBlockEntity.m_58900_().m_60713_((Block)BlockRegistry.POCKET_PORTAL_FRAME.get()),
            pBlockEntity.getColor()
         );
         poseStack.m_85849_();
      }
   }
}
