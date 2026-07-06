package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FireflyJar extends Block implements SimpleWaterloggedBlock {
   public static final VoxelShape SHAPE = Shapes.m_83110_(Block.m_49796_(4.0, 0.0, 4.0, 12.0, 13.0, 12.0), Block.m_49796_(6.0, 13.0, 6.0, 10.0, 16.0, 10.0));

   public FireflyJar() {
      super(Properties.m_60926_(Blocks.f_50058_).m_60953_(x -> 8));
      this.m_49959_((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, false));
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
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

   public void m_214162_(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      double d0 = pPos.m_123341_() + 0.5;
      double d1 = pPos.m_123342_();
      double d2 = pPos.m_123343_() + 0.5;
      double d3 = pRandom.m_188500_() * 0.6 - 0.3;
      double d4 = pRandom.m_188500_() * 0.6;
      double d6 = pRandom.m_188500_() * 0.6 - 0.3;
      pLevel.m_7106_(ParticleHelper.FIREFLY, d0 + d3, d1 + d4, d2 + d6, 0.0, 0.0, 0.0);
      pLevel.m_7106_(ParticleHelper.FIREFLY, d0 + d3 * 2.0, d1 + d4 * 2.0, d2 + d6 * 2.0, 0.0, 0.0, 0.0);
   }
}
