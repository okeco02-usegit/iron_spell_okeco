package io.redspace.ironsspellbooks.block.pedestal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final VoxelShape SHAPE_COLUMN = Block.m_49796_(3.0, 4.0, 3.0, 13.0, 12.0, 13.0);
   public static final VoxelShape SHAPE_BOTTOM = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
   public static final VoxelShape SHAPE_TOP = Block.m_49796_(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape SHAPE = Shapes.m_83124_(SHAPE_BOTTOM, new VoxelShape[]{SHAPE_TOP, SHAPE_COLUMN});

   public PedestalBlock() {
      super(Properties.m_60926_(Blocks.f_50729_).m_60955_());
      this.m_49959_((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, false));
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult m_6227_(BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!pLevel.m_5776_() && pLevel.m_7702_(pos) instanceof PedestalTile pedestalTile) {
         ItemStack currentPedestalItem = pedestalTile.getHeldItem();
         ItemStack handItem = player.m_21120_(hand);
         ItemStack playerItem = currentPedestalItem.m_41777_();
         if (!handItem.m_41619_() && handItem.m_41613_() != 1) {
            this.dropItem(playerItem, player);
         } else {
            player.m_21008_(hand, playerItem);
         }

         pedestalTile.setHeldItem(ItemStack.f_41583_);
         currentPedestalItem = handItem.m_41777_();
         if (!currentPedestalItem.m_41619_()) {
            currentPedestalItem.m_41764_(1);
            pedestalTile.setHeldItem(currentPedestalItem);
            handItem.m_41774_(1);
         }

         pLevel.m_7260_(pos, state, state, 2);
      }

      return InteractionResult.m_19078_(pLevel.m_5776_());
   }

   private void dropItem(ItemStack itemstack, Player owner) {
      if (owner instanceof ServerPlayer serverplayer) {
         ItemEntity itementity = serverplayer.m_36176_(itemstack, false);
         if (itementity != null) {
            itementity.m_32061_();
            itementity.m_32052_(serverplayer.m_20148_());
         }
      }
   }

   public void m_6810_(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.m_60734_() != pNewState.m_60734_()) {
         BlockEntity blockEntity = pLevel.m_7702_(pPos);
         if (blockEntity instanceof PedestalTile) {
            ((PedestalTile)blockEntity).drops();
         }
      }

      super.m_6810_(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   public BlockState m_7417_(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
      if ((Boolean)pState.m_61143_(BlockStateProperties.f_61362_)) {
         pLevel.m_186469_(pPos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(pLevel));
      }

      return super.m_7417_(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext pContext) {
      return (BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, pContext.m_43725_().m_6425_(pContext.m_8083_()).m_76152_() == Fluids.f_76193_);
   }

   public FluidState m_5888_(BlockState pState) {
      return pState.m_61143_(BlockStateProperties.f_61362_) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(pState);
   }

   protected void m_7926_(Builder<Block, BlockState> pBuilder) {
      pBuilder.m_61104_(new Property[]{BlockStateProperties.f_61362_});
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new PedestalTile(pos, state);
   }

   public RenderShape m_7514_(BlockState blockState) {
      return RenderShape.MODEL;
   }
}
