package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class BrazierBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty HANGING = BooleanProperty.m_61465_("hanging");
   private final boolean soul;
   public static final VoxelShape COLLISION_SHAPE = Block.m_49796_(1.0, 0.0, 1.0, 15.0, 10.0, 15.0);
   public static final VoxelShape RENDER_SHAPE = Block.m_49796_(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

   public BrazierBlock(boolean soul) {
      super(Properties.m_60926_(Blocks.f_50184_).m_60953_(blockState -> blockState.m_61143_(BlockStateProperties.f_61443_) ? 15 : 0));
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, false)).m_61124_(BlockStateProperties.f_61443_, true))
            .m_61124_(HANGING, false)
      );
      this.soul = soul;
   }

   public BrazierBlock() {
      this(false);
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return RENDER_SHAPE;
   }

   public VoxelShape m_5939_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return COLLISION_SHAPE;
   }

   public void m_7892_(BlockState pState, Level pLevel, BlockPos blockpos, Entity entity) {
      if ((Boolean)pState.m_61143_(BlockStateProperties.f_61443_) && entity instanceof LivingEntity) {
         float margin = 0.0625F;
         AABB bb = entity.m_20191_();
         if (bb.f_82291_ > blockpos.m_123341_() + margin
            && bb.f_82293_ > blockpos.m_123343_() + margin
            && bb.f_82288_ < blockpos.m_123341_() + 1 - margin
            && bb.f_82290_ < blockpos.m_123343_() + 1 - margin) {
            entity.m_6469_(pLevel.m_269111_().m_269387_(), 1.0F);
         }
      }

      super.m_7892_(pState, pLevel, blockpos, entity);
   }

   public BlockState m_7417_(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
      BlockState newState = super.m_7417_(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
      if ((Boolean)pState.m_61143_(BlockStateProperties.f_61362_)) {
         pLevel.m_186469_(pPos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(pLevel));
      }

      if (pDirection == Direction.UP) {
         boolean chain = pNeighborState.m_60713_(Blocks.f_50184_) && ((Axis)pNeighborState.m_61143_(RotatedPillarBlock.f_55923_)).m_122478_();
         if (chain != (Boolean)pState.m_61143_(HANGING)) {
            newState = (BlockState)newState.m_61124_(HANGING, chain);
         }
      }

      return newState;
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction itemAbility, boolean simulate) {
      if (ToolActions.SHOVEL_FLATTEN == itemAbility && state.m_60734_() instanceof BrazierBlock && (Boolean)state.m_61143_(BlockStateProperties.f_61443_)) {
         BlockState newState = (BlockState)state.m_61124_(BlockStateProperties.f_61443_, false);
         if (!simulate) {
            context.m_43725_().m_5594_(null, context.m_8083_(), SoundEvents.f_11914_, SoundSource.BLOCKS, 1.0F, 1.0F);
            context.m_43725_().m_7731_(context.m_8083_(), newState, 11);
            if (context.m_43723_() != null) {
               context.m_43723_().m_6674_(context.m_43724_());
            }
         }

         return newState;
      } else {
         return super.getToolModifiedState(state, context, itemAbility, simulate);
      }
   }

   public boolean m_7361_(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
      if (!(Boolean)pState.m_61143_(BlockStateProperties.f_61362_) && pFluidState.m_76152_() == Fluids.f_76193_) {
         boolean flag = (Boolean)pState.m_61143_(BlockStateProperties.f_61443_);
         if (flag && !pLevel.m_5776_()) {
            pLevel.m_5594_(null, pPos, SoundEvents.f_11914_, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         pLevel.m_7731_(pPos, (BlockState)((BlockState)pState.m_61124_(BlockStateProperties.f_61362_, true)).m_61124_(BlockStateProperties.f_61443_, false), 3);
         pLevel.m_186469_(pPos, pFluidState.m_76152_(), pFluidState.m_76152_().m_6718_(pLevel));
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext pContext) {
      boolean water = pContext.m_43725_().m_6425_(pContext.m_8083_()).m_76152_() == Fluids.f_76193_;
      boolean hanging = pContext.m_43725_().m_8055_(pContext.m_8083_().m_7494_()).m_60713_(Blocks.f_50184_);
      return (BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, water))
            .m_61124_(BlockStateProperties.f_61443_, !water))
         .m_61124_(HANGING, hanging);
   }

   public FluidState m_5888_(BlockState pState) {
      return pState.m_61143_(BlockStateProperties.f_61362_) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(pState);
   }

   protected void m_7926_(Builder<Block, BlockState> pBuilder) {
      pBuilder.m_61104_(new Property[]{BlockStateProperties.f_61362_, BlockStateProperties.f_61443_, HANGING});
   }

   public void m_214162_(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      if ((Boolean)pState.m_61143_(BlockStateProperties.f_61443_) && (!this.soul || pRandom.m_188501_() < 0.085F)) {
         float scale = 0.25F;
         double d0 = pPos.m_123341_() + 0.5;
         double d1 = pPos.m_123342_() + 0.7 + scale;
         double d2 = pPos.m_123343_() + 0.5;
         double d3 = Utils.getRandomScaled(scale);
         double d4 = Utils.getRandomScaled(scale);
         double d6 = Utils.getRandomScaled(scale);
         double d7 = pRandom.m_188500_() * 0.17;
         pLevel.m_7106_((ParticleOptions)(this.soul ? ParticleTypes.f_123746_ : ParticleHelper.EMBERS), d0 + d3, d1 + d4, d2 + d6, 0.0, d7, 0.0);
      }
   }
}
