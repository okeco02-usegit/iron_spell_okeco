package io.redspace.ironsspellbooks.block.inscription_table;

import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InscriptionTableBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final EnumProperty<ChestType> PART = BlockStateProperties.f_61392_;
   public static final VoxelShape SHAPE = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 18.0, 16.0);
   public static final VoxelShape SHAPE_TABLETOP = Block.m_49796_(0.0, 10.0, 0.0, 16.0, 14.0, 16.0);
   public static final VoxelShape SHAPE_LEG_1 = Block.m_49796_(1.0, 0.0, 1.0, 4.0, 10.0, 4.0);
   public static final VoxelShape SHAPE_LEG_2 = Block.m_49796_(12.0, 0.0, 1.0, 15.0, 10.0, 4.0);
   public static final VoxelShape SHAPE_LEG_3 = Block.m_49796_(1.0, 0.0, 12.0, 4.0, 10.0, 15.0);
   public static final VoxelShape SHAPE_LEG_4 = Block.m_49796_(12.0, 0.0, 12.0, 15.0, 10.0, 15.0);
   public static final VoxelShape SHAPE_LEGS_EAST = Shapes.m_83124_(SHAPE_LEG_2, new VoxelShape[]{SHAPE_LEG_4, SHAPE_TABLETOP});
   public static final VoxelShape SHAPE_LEGS_WEST = Shapes.m_83124_(SHAPE_LEG_1, new VoxelShape[]{SHAPE_LEG_3, SHAPE_TABLETOP});
   public static final VoxelShape SHAPE_LEGS_NORTH = Shapes.m_83124_(SHAPE_LEG_3, new VoxelShape[]{SHAPE_LEG_4, SHAPE_TABLETOP});
   public static final VoxelShape SHAPE_LEGS_SOUTH = Shapes.m_83124_(SHAPE_LEG_1, new VoxelShape[]{SHAPE_LEG_2, SHAPE_TABLETOP});

   public InscriptionTableBlock() {
      super(Properties.m_284310_().m_60978_(2.5F).m_60918_(SoundType.f_56736_).m_60955_());
      this.m_49959_((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, false));
   }

   public BlockState m_7417_(BlockState myState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos myPos, BlockPos pFacingPos) {
      ChestType half = (ChestType)myState.m_61143_(PART);
      BlockPos requiredNeighborPos = myPos.m_121945_(getNeighbourDirection(half, (Direction)myState.m_61143_(f_54117_)));
      boolean waterlogged = (Boolean)myState.m_61143_(BlockStateProperties.f_61362_);
      if (waterlogged) {
         pLevel.m_186469_(myPos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(pLevel));
         pLevel.m_186469_(requiredNeighborPos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(pLevel));
      }

      BlockState neighborState = pLevel.m_8055_(requiredNeighborPos);
      if (!neighborState.m_60713_(this)) {
         BlockState air = (waterlogged ? Blocks.f_49990_ : Blocks.f_50016_).m_49966_();
         pLevel.m_7731_(myPos, air, 35);
         pLevel.m_5898_(null, 2001, myPos, Block.m_49956_(air));
         return air;
      } else {
         return super.m_7417_(myState, pFacing, pFacingState, pLevel, myPos, pFacingPos);
      }
   }

   private static Direction getNeighbourDirection(ChestType pPart, Direction pDirection) {
      return pPart == ChestType.LEFT ? pDirection.m_122428_() : pDirection.m_122427_();
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = ((ChestType)pState.m_61143_(PART)).equals(ChestType.RIGHT)
         ? (Direction)pState.m_61143_(f_54117_)
         : ((Direction)pState.m_61143_(f_54117_)).m_122424_();

      return switch (direction) {
         case NORTH -> SHAPE_LEGS_WEST;
         case SOUTH -> SHAPE_LEGS_EAST;
         case WEST -> SHAPE_LEGS_NORTH;
         default -> SHAPE_LEGS_SOUTH;
      };
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext pContext) {
      Direction direction = pContext.m_8125_();
      BlockPos blockpos = pContext.m_8083_();
      BlockPos blockpos1 = blockpos.m_121945_(direction.m_122428_());
      Level level = pContext.m_43725_();
      return level.m_8055_(blockpos1).m_60629_(pContext) && level.m_6857_().m_61937_(blockpos1)
         ? (BlockState)((BlockState)this.m_49966_().m_61124_(f_54117_, direction.m_122424_()))
            .m_61124_(BlockStateProperties.f_61362_, level.m_6425_(blockpos).m_76152_() == Fluids.f_76193_)
         : null;
   }

   public void m_6402_(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.m_6402_(pLevel, pPos, pState, pPlacer, pStack);
      if (!pLevel.f_46443_) {
         BlockPos blockpos = pPos.m_121945_(((Direction)pState.m_61143_(f_54117_)).m_122427_());
         pLevel.m_7731_(
            blockpos,
            (BlockState)((BlockState)pState.m_61124_(PART, ChestType.LEFT))
               .m_61124_(BlockStateProperties.f_61362_, pLevel.m_6425_(blockpos).m_76152_() == Fluids.f_76193_),
            3
         );
         pLevel.m_7731_(pPos, (BlockState)pState.m_61124_(PART, ChestType.RIGHT), 3);
         pLevel.m_6289_(pPos, Blocks.f_50016_);
         pState.m_60701_(pLevel, pPos, 3);
      }
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{f_54117_, PART, BlockStateProperties.f_61362_});
   }

   public RenderShape m_7514_(BlockState blockState) {
      return ((ChestType)blockState.m_61143_(PART)).equals(ChestType.RIGHT) ? RenderShape.MODEL : RenderShape.INVISIBLE;
   }

   public FluidState m_5888_(BlockState pState) {
      return pState.m_61143_(BlockStateProperties.f_61362_) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(pState);
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.BLOCK;
   }

   public InteractionResult m_6227_(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.f_46443_) {
         return InteractionResult.SUCCESS;
      }

      pPlayer.m_5893_(pState.m_60750_(pLevel, pPos));
      return InteractionResult.CONSUME;
   }

   @Nullable
   public MenuProvider m_7246_(BlockState pState, Level pLevel, BlockPos pPos) {
      return new SimpleMenuProvider(
         (i, inventory, player) -> new InscriptionTableMenu(i, inventory, ContainerLevelAccess.m_39289_(pLevel, pPos)),
         Component.m_237115_("block.irons_spellbooks.inscription_table")
      );
   }
}
