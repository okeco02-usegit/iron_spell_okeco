package io.redspace.ironsspellbooks.block.portal_frame;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

public class PocketDimensionPortalFrameBlock extends PortalFrameBlock {
   public PocketDimensionPortalFrameBlock() {
      super(
         Properties.m_284310_()
            .m_60913_(-1.0F, 3600000.8F)
            .m_284180_(MapColor.f_283808_)
            .m_222994_()
            .m_60955_()
            .m_60922_(PocketDimensionPortalFrameBlock::never)
            .m_246721_()
            .m_278166_(PushReaction.BLOCK)
            .m_60918_(SoundType.f_154663_)
      );
   }

   private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
      return false;
   }

   @Override
   public InteractionResult m_6227_(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit) {
      return InteractionResult.PASS;
   }

   @Override
   public boolean canTeleport(Entity entity) {
      return entity instanceof Player;
   }
}
