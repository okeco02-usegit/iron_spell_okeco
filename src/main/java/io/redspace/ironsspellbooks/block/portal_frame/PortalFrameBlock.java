package io.redspace.ironsspellbooks.block.portal_frame;

import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.render.RenderHelper;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortalFrameBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.f_61374_;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.f_61401_;
   protected static final VoxelShape SOUTH_AABB = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
   protected static final VoxelShape NORTH_AABB = Block.m_49796_(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.m_49796_(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape EAST_AABB = Block.m_49796_(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
   protected static final VoxelShape LOWER_SOUTH_COLLIDER_AABB = Shapes.m_83113_(SOUTH_AABB, Block.m_49796_(1.0, 0.0, 0.0, 15.0, 16.0, 3.0), BooleanOp.f_82685_);
   protected static final VoxelShape LOWER_NORTH_COLLIDER_AABB = Shapes.m_83113_(
      NORTH_AABB, Block.m_49796_(1.0, 0.0, 13.0, 15.0, 16.0, 16.0), BooleanOp.f_82685_
   );
   protected static final VoxelShape LOWER_WEST_COLLIDER_AABB = Shapes.m_83113_(WEST_AABB, Block.m_49796_(13.0, 0.0, 1.0, 16.0, 16.0, 15.0), BooleanOp.f_82685_);
   protected static final VoxelShape LOWER_EAST_COLLIDER_AABB = Shapes.m_83113_(EAST_AABB, Block.m_49796_(0.0, 0.0, 1.0, 3.0, 16.0, 15.0), BooleanOp.f_82685_);
   protected static final VoxelShape UPPER_SOUTH_COLLIDER_AABB = Shapes.m_83113_(
      LOWER_SOUTH_COLLIDER_AABB, Block.m_49796_(0.0, 15.0, 0.0, 16.0, 16.0, 3.0), BooleanOp.f_82695_
   );
   protected static final VoxelShape UPPER_NORTH_COLLIDER_AABB = Shapes.m_83113_(
      LOWER_NORTH_COLLIDER_AABB, Block.m_49796_(0.0, 15.0, 13.0, 16.0, 16.0, 16.0), BooleanOp.f_82695_
   );
   protected static final VoxelShape UPPER_WEST_COLLIDER_AABB = Shapes.m_83113_(
      LOWER_WEST_COLLIDER_AABB, Block.m_49796_(13.0, 15.0, 0.0, 16.0, 16.0, 16.0), BooleanOp.f_82695_
   );
   protected static final VoxelShape UPPER_EAST_COLLIDER_AABB = Shapes.m_83113_(
      LOWER_EAST_COLLIDER_AABB, Block.m_49796_(0.0, 15.0, 0.0, 3.0, 16.0, 16.0), BooleanOp.f_82695_
   );

   public PortalFrameBlock() {
      this(Properties.m_284310_().m_60955_().m_60960_((x, y, z) -> false).m_60918_(SoundType.f_154663_).m_60971_((x, y, z) -> false).m_60913_(10.0F, 6.0F));
   }

   public PortalFrameBlock(Properties properties) {
      super(properties);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return createTicker(pLevel, pBlockEntityType, (BlockEntityType<? extends PortalFrameBlockEntity>)BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get());
   }

   @Nullable
   protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
      Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends PortalFrameBlockEntity> pClientType
   ) {
      return pLevel.f_46443_ ? null : m_152132_(pServerType, pClientType, PortalFrameBlockEntity::serverTick);
   }

   public BlockState m_7417_(BlockState myState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos myPos, BlockPos pFacingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)myState.m_61143_(HALF);
      BlockPos requiredNeighborPos = myPos.m_121945_(directionToOther(half));
      BlockState neighborState = pLevel.m_8055_(requiredNeighborPos);
      if (!neighborState.m_60713_(this)) {
         BlockState air = Blocks.f_50016_.m_49966_();
         pLevel.m_7731_(myPos, air, 35);
         pLevel.m_5898_(null, 2001, myPos, Block.m_49956_(air));
         return air;
      } else {
         return super.m_7417_(myState, pFacing, pFacingState, pLevel, myPos, pFacingPos);
      }
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      Direction horizontalDir = context.m_8125_();
      Direction facing = horizontalDir.m_122424_();
      BlockPos blockPos = context.m_8083_();
      boolean bottom = context.m_43719_() != Direction.DOWN;
      BlockPos blockPos2 = bottom ? blockPos.m_7494_() : blockPos.m_7495_();
      Level level = context.m_43725_();
      return level.m_8055_(blockPos2).m_60629_(context) && level.m_6857_().m_61937_(blockPos2)
         ? (BlockState)((BlockState)this.m_49966_().m_61124_(FACING, facing)).m_61124_(HALF, bottom ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER)
         : null;
   }

   public void m_6402_(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.m_6402_(pLevel, pPos, pState, pPlacer, pStack);
      if (!pLevel.f_46443_) {
         DoubleBlockHalf half = (DoubleBlockHalf)pState.m_61143_(HALF);
         Direction facing = (Direction)pState.m_61143_(FACING);
         BlockPos blockpos = pPos.m_121945_(directionToOther(half));
         pLevel.m_7731_(blockpos, (BlockState)((BlockState)pState.m_61124_(HALF, otherHalf(half))).m_61124_(FACING, facing), 3);
         pLevel.m_6289_(pPos, Blocks.f_50016_);
         pState.m_60701_(pLevel, pPos, 3);
         if (pPlacer != null && pLevel.m_7702_(pPos) instanceof PortalFrameBlockEntity portalFrameBlockEntity) {
            portalFrameBlockEntity.setOwnerUUID(pPlacer.m_20148_());
         }
      }
   }

   public static Direction directionToOther(DoubleBlockHalf half) {
      return half == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP;
   }

   public static DoubleBlockHalf otherHalf(DoubleBlockHalf half) {
      return half == DoubleBlockHalf.UPPER ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = (Direction)pState.m_61143_(FACING);

      return switch (direction) {
         case NORTH -> NORTH_AABB;
         case SOUTH -> SOUTH_AABB;
         case WEST -> WEST_AABB;
         default -> EAST_AABB;
      };
   }

   public VoxelShape m_5939_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = (Direction)pState.m_61143_(FACING);
      boolean lower = ((DoubleBlockHalf)pState.m_61143_(HALF)).equals(DoubleBlockHalf.LOWER);

      return switch (direction) {
         case NORTH -> lower ? LOWER_NORTH_COLLIDER_AABB : UPPER_NORTH_COLLIDER_AABB;
         case SOUTH -> lower ? LOWER_SOUTH_COLLIDER_AABB : UPPER_SOUTH_COLLIDER_AABB;
         case WEST -> lower ? LOWER_WEST_COLLIDER_AABB : UPPER_WEST_COLLIDER_AABB;
         default -> lower ? LOWER_EAST_COLLIDER_AABB : UPPER_EAST_COLLIDER_AABB;
      };
   }

   public void m_7892_(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pEntity.f_19853_.f_46443_) {
         VoxelShape voxelshape = pState.m_60651_(pLevel, pPos, CollisionContext.m_82750_(pEntity));
         VoxelShape voxelshape1 = voxelshape.m_83216_(pPos.m_123341_(), pPos.m_123342_(), pPos.m_123343_());
         if (pEntity.m_20191_().m_82381_(voxelshape1.m_83215_())) {
            pLevel.m_141902_(pPos, (BlockEntityType)BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get()).ifPresent(tile -> tile.setActive());
         }
      }
   }

   public boolean canTeleport(Entity entity) {
      return true;
   }

   public InteractionResult m_6227_(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit) {
      ItemStack stack = player.m_21120_(hand);
      if (stack.m_41720_() instanceof DyeItem dyeItem) {
         Optional<PortalFrameBlockEntity> portal = level.m_141902_(pos, (BlockEntityType)BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get());
         if (portal.isPresent()) {
            PortalFrameBlockEntity tile = portal.get();
            int color = RenderHelper.colorf(dyeItem.m_41089_().m_41068_()[0], dyeItem.m_41089_().m_41068_()[1], dyeItem.m_41089_().m_41068_()[2]);
            if (tile.isPortalConnected() && tile.getColor() != color) {
               if (!(Boolean)ServerConfigs.PORTAL_FRAME_RESTRICT_DYE.get() || tile.getOwnerUUID() == null || player.m_20148_().equals(tile.getOwnerUUID())) {
                  if (!player.m_150110_().f_35937_) {
                     stack.m_41774_(1);
                  }

                  tile.setColor(color);
                  return InteractionResult.SUCCESS;
               }

               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.f_8906_
                     .m_9829_(
                        new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.portal_dye_failure").m_130940_(ChatFormatting.RED))
                     );
               }
            }
         }
      }

      return super.m_6227_(pState, level, pos, player, hand, pHit);
   }

   @org.jetbrains.annotations.Nullable
   public BlockEntity m_142194_(BlockPos pPos, BlockState pState) {
      return new PortalFrameBlockEntity(pPos, pState);
   }

   public void m_6810_(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
      if (pLevel.m_7702_(pPos) instanceof PortalFrameBlockEntity portalFrame) {
         portalFrame.breakPortalConnection();
      }

      super.m_6810_(pState, pLevel, pPos, pNewState, pMovedByPiston);
   }

   @org.jetbrains.annotations.Nullable
   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.IGNORE;
   }

   public RenderShape m_7514_(BlockState blockState) {
      return ((DoubleBlockHalf)blockState.m_61143_(HALF)).equals(DoubleBlockHalf.LOWER) ? RenderShape.MODEL : RenderShape.INVISIBLE;
   }

   public BlockState m_6843_(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.m_61124_(FACING, pRotation.m_55954_((Direction)pState.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState pState, Mirror pMirror) {
      return pState.m_60717_(pMirror.m_54846_((Direction)pState.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING, HALF});
   }
}
