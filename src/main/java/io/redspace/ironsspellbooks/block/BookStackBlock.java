package io.redspace.ironsspellbooks.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BookStackBlock extends HorizontalDirectionalBlock {
   public static final VoxelShape SHAPE = Block.m_49796_(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

   public BookStackBlock() {
      super(
         Properties.m_284310_()
            .m_284268_(DyeColor.WHITE)
            .m_60955_()
            .m_278166_(PushReaction.DESTROY)
            .m_60918_(new SoundType(1.0F, 1.0F, SoundEvents.f_11714_, SoundEvents.f_12591_, SoundEvents.f_11714_, SoundEvents.f_12641_, SoundEvents.f_11713_))
            .m_60978_(0.2F)
      );
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public boolean m_7898_(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return m_49863_(pLevel, pPos.m_7495_(), Direction.UP);
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext pContext) {
      Direction direction = pContext.m_8125_();
      return (BlockState)this.m_49966_().m_61124_(f_54117_, direction.m_122424_());
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{f_54117_});
   }
}
