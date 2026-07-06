package io.redspace.ironsspellbooks.block.scroll_forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScrollForgeBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.f_61374_;
   private static final Component CONTAINER_TITLE = Component.m_237115_("container.crafting");
   public static final VoxelShape SHAPE_TABLETOP = Block.m_49796_(0.0, 10.0, 0.0, 16.0, 14.0, 16.0);
   public static final VoxelShape SHAPE_LEG_1 = Block.m_49796_(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
   public static final VoxelShape SHAPE_LEG_2 = Block.m_49796_(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
   public static final VoxelShape SHAPE = Shapes.m_83124_(SHAPE_LEG_1, new VoxelShape[]{SHAPE_LEG_2, SHAPE_TABLETOP});

   public ScrollForgeBlock() {
      super(Properties.m_60926_(Blocks.f_50201_).m_60955_().m_60918_(SoundType.f_56725_));
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_().m_122424_());
   }

   public BlockState m_6843_(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.m_61124_(FACING, pRotation.m_55954_((Direction)pState.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState pState, Mirror pMirror) {
      return pState.m_60717_(pMirror.m_54846_((Direction)pState.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING});
   }

   public InteractionResult m_6227_(BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!pLevel.m_5776_()) {
         BlockEntity entity = pLevel.m_7702_(pos);
         if (!(entity instanceof ScrollForgeTile)) {
            throw new IllegalStateException("Our Container provider is missing!");
         }

         NetworkHooks.openScreen((ServerPlayer)player, (ScrollForgeTile)entity, pos);
      }

      return InteractionResult.m_19078_(pLevel.m_5776_());
   }

   public void m_6810_(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.m_60734_() != pNewState.m_60734_()) {
         BlockEntity blockEntity = pLevel.m_7702_(pPos);
         if (blockEntity instanceof ScrollForgeTile) {
            ((ScrollForgeTile)blockEntity).drops();
         }
      }

      super.m_6810_(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new ScrollForgeTile(pos, state);
   }

   @NotNull
   public RenderShape m_7514_(@NotNull BlockState blockState) {
      return RenderShape.MODEL;
   }
}
