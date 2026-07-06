package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class VoidstoneBlock extends Block {
   public static final BooleanProperty NORTH = PipeBlock.f_55148_;
   public static final BooleanProperty EAST = PipeBlock.f_55149_;
   public static final BooleanProperty SOUTH = PipeBlock.f_55150_;
   public static final BooleanProperty WEST = PipeBlock.f_55151_;

   public VoidstoneBlock() {
      super(
         Properties.m_284310_()
            .m_60913_(-1.0F, 3600000.8F)
            .m_284180_(MapColor.f_283808_)
            .m_222994_()
            .m_60922_(VoidstoneBlock::never)
            .m_278166_(PushReaction.BLOCK)
            .m_60918_(SoundType.f_154663_)
            .m_60953_(state -> 9)
      );
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(NORTH, false)).m_61124_(EAST, false))
               .m_61124_(SOUTH, false))
            .m_61124_(WEST, false)
      );
   }

   public boolean connectsTo(BlockState state, Direction direction) {
      return state.m_60713_((Block)BlockRegistry.VOIDSTONE.get());
   }

   private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
      return false;
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      BlockGetter blockgetter = context.m_43725_();
      BlockPos blockpos = context.m_8083_();
      BlockPos blockpos1 = blockpos.m_122012_();
      BlockPos blockpos2 = blockpos.m_122029_();
      BlockPos blockpos3 = blockpos.m_122019_();
      BlockPos blockpos4 = blockpos.m_122024_();
      BlockState blockstate = blockgetter.m_8055_(blockpos1);
      BlockState blockstate1 = blockgetter.m_8055_(blockpos2);
      BlockState blockstate2 = blockgetter.m_8055_(blockpos3);
      BlockState blockstate3 = blockgetter.m_8055_(blockpos4);
      return (BlockState)((BlockState)((BlockState)((BlockState)super.m_5573_(context).m_61124_(NORTH, this.connectsTo(blockstate, Direction.SOUTH)))
               .m_61124_(EAST, this.connectsTo(blockstate1, Direction.WEST)))
            .m_61124_(SOUTH, this.connectsTo(blockstate2, Direction.NORTH)))
         .m_61124_(WEST, this.connectsTo(blockstate3, Direction.EAST));
   }

   public BlockState m_7417_(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      return facing.m_122434_().m_122480_() == Plane.HORIZONTAL
         ? (BlockState)state.m_61124_((Property)PipeBlock.f_55154_.get(facing), this.connectsTo(facingState, facing.m_122424_()))
         : super.m_7417_(state, facing, facingState, level, currentPos, facingPos);
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{NORTH, EAST, WEST, SOUTH});
   }
}
