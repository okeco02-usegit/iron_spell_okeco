package io.redspace.ironsspellbooks.block.arcane_anvil;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArcaneAnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   private static final VoxelShape BASE = Block.m_49796_(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
   private static final VoxelShape X_LEG1 = Block.m_49796_(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
   private static final VoxelShape X_LEG2 = Block.m_49796_(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
   private static final VoxelShape X_TOP = Block.m_49796_(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
   private static final VoxelShape Z_LEG1 = Block.m_49796_(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
   private static final VoxelShape Z_LEG2 = Block.m_49796_(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
   private static final VoxelShape Z_TOP = Block.m_49796_(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
   private static final VoxelShape X_AXIS_AABB = Shapes.m_83124_(BASE, new VoxelShape[]{X_LEG1, X_LEG2, X_TOP});
   private static final VoxelShape Z_AXIS_AABB = Shapes.m_83124_(BASE, new VoxelShape[]{Z_LEG1, Z_LEG2, Z_TOP});
   private static final Component CONTAINER_TITLE = Component.m_237115_("ui.irons_spellbooks.arcane_anvil_title");

   public ArcaneAnvilBlock() {
      super(Properties.m_60926_(Blocks.f_50201_).m_60955_().m_60918_(SoundType.f_154654_));
   }

   protected void m_6788_(FallingBlockEntity pFallingEntity) {
      pFallingEntity.m_149656_(2.0F, 40);
   }

   public void m_48792_(Level pLevel, BlockPos pPos, BlockState pState, BlockState pReplaceableState, FallingBlockEntity pFallingBlock) {
      if (!pFallingBlock.m_20067_()) {
         pLevel.m_5594_(null, pPos, SoundEvents.f_11668_, SoundSource.BLOCKS, 0.3F, Utils.random.m_188501_() * 0.1F + 0.9F);
      }
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = (Direction)pState.m_61143_(FACING);
      return direction.m_122434_() == Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   public BlockState m_5573_(BlockPlaceContext pContext) {
      return (BlockState)this.m_49966_().m_61124_(FACING, pContext.m_8125_().m_122427_());
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

   public InteractionResult m_6227_(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.f_46443_) {
         return InteractionResult.SUCCESS;
      }

      pPlayer.m_5893_(pState.m_60750_(pLevel, pPos));
      pPlayer.m_36220_(Stats.f_12951_);
      return InteractionResult.CONSUME;
   }

   @Nullable
   public MenuProvider m_7246_(BlockState pState, Level pLevel, BlockPos pPos) {
      return new SimpleMenuProvider((i, inventory, player) -> new ArcaneAnvilMenu(i, inventory, ContainerLevelAccess.m_39289_(pLevel, pPos)), CONTAINER_TITLE);
   }

   public RenderShape m_7514_(BlockState blockState) {
      return RenderShape.MODEL;
   }
}
