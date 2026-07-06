package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArmorPileBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   private static final VoxelShape BASE = Block.m_49796_(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

   public ArmorPileBlock() {
      super(Properties.m_284310_().m_60913_(5.0F, 8.0F).m_60955_().m_60918_(SoundType.f_56728_).m_284180_(MapColor.f_283927_));
      this.m_49959_((BlockState)this.m_49966_().m_61124_(BlockStateProperties.f_61362_, false));
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return BASE;
   }

   public BlockState m_7417_(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
      if ((Boolean)pState.m_61143_(BlockStateProperties.f_61362_)) {
         pLevel.m_186469_(pPos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(pLevel));
      }

      return super.m_7417_(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
   }

   public FluidState m_5888_(BlockState pState) {
      return pState.m_61143_(BlockStateProperties.f_61362_) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(pState);
   }

   public BlockState m_5573_(BlockPlaceContext pContext) {
      return (BlockState)((BlockState)this.m_49966_().m_61124_(FACING, pContext.m_8125_().m_122424_()))
         .m_61124_(BlockStateProperties.f_61362_, pContext.m_43725_().m_6425_(pContext.m_8083_()).m_76152_() == Fluids.f_76193_);
   }

   public void m_6786_(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
      super.m_6786_(pLevel, pPos, pState);
   }

   public void m_213646_(BlockState pState, ServerLevel level, BlockPos pos, ItemStack pStack, boolean pDropExperience) {
      super.m_213646_(pState, level, pos, pStack, pDropExperience);
      KeeperEntity keeper = new KeeperEntity(level);
      keeper.m_20219_(Vec3.m_82512_(pos));
      keeper.m_6518_(level, level.m_6436_(pos), MobSpawnType.TRIGGERED, null, null);
      level.m_7967_(keeper);
      MagicManager.spawnParticles(level, ParticleTypes.f_123746_, pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), 20, 0.1, 0.1, 0.1, 0.05, false);
      level.m_5594_(null, pos, SoundEvents.f_12404_, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public BlockState m_6843_(BlockState pState, Rotation pRotation) {
      return (BlockState)pState.m_61124_(FACING, pRotation.m_55954_((Direction)pState.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState pState, Mirror pMirror) {
      return pState.m_60717_(pMirror.m_54846_((Direction)pState.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING, BlockStateProperties.f_61362_});
   }

   public RenderShape m_7514_(BlockState blockState) {
      return RenderShape.MODEL;
   }
}
