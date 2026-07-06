package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.FluidRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class AlchemistCauldronBlock extends BaseEntityBlock {
   private static final VoxelShape SHAPE = Shapes.m_83110_(
      Shapes.m_83124_(
         m_49796_(0.0, 0.0, 4.0, 16.0, 2.0, 6.0),
         new VoxelShape[]{m_49796_(0.0, 0.0, 10.0, 16.0, 2.0, 12.0), m_49796_(4.0, 0.0, 0.0, 6.0, 2.0, 16.0), m_49796_(10.0, 0.0, 0.0, 12.0, 2.0, 16.0)}
      ),
      Shapes.m_83113_(
         Shapes.m_83110_(
            Shapes.m_83113_(m_49796_(0.0, 2.0, 0.0, 16.0, 16.0, 16.0), m_49796_(0.0, 12.0, 0.0, 16.0, 14.0, 16.0), BooleanOp.f_82685_),
            m_49796_(1.0, 12.0, 1.0, 15.0, 14.0, 15.0)
         ),
         m_49796_(2.0, 4.0, 2.0, 14.0, 16.0, 14.0),
         BooleanOp.f_82685_
      )
   );

   public AlchemistCauldronBlock() {
      super(Properties.m_60926_(Blocks.f_50256_).m_60953_(blockState -> 3));
   }

   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return createTicker(pLevel, pBlockEntityType, (BlockEntityType<? extends AlchemistCauldronTile>)BlockRegistry.ALCHEMIST_CAULDRON_TILE.get());
   }

   @Nullable
   protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
      Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends AlchemistCauldronTile> pClientType
   ) {
      return pLevel.f_46443_ ? null : m_152132_(pServerType, pClientType, AlchemistCauldronTile::serverTick);
   }

   public VoxelShape m_5940_(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult m_6227_(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit) {
      return level.m_7702_(pos) instanceof AlchemistCauldronTile tile
         ? tile.handleUse(level.m_8055_(pos), level, pos, player, hand)
         : super.m_6227_(pState, level, pos, player, hand, pHit);
   }

   public void m_7892_(BlockState blockState, Level level, BlockPos pos, Entity entity) {
      if (entity.f_19797_ % 20 == 0
         && level.m_7702_(pos) instanceof AlchemistCauldronTile cauldronTile
         && entity instanceof LivingEntity livingEntity
         && livingEntity.m_6469_(DamageSources.get(level, ISSDamageTypes.CAULDRON), 2.0F)) {
         MagicManager.spawnParticles(
            level, ParticleHelper.BLOOD, entity.m_20185_(), entity.m_20186_() + entity.m_20206_() / 2.0F, entity.m_20189_(), 20, 0.05, 0.05, 0.05, 0.1, false
         );
         cauldronTile.fluidInventory.fill(new FluidStack((Fluid)FluidRegistry.BLOOD.get(), 250), FluidAction.EXECUTE);
      }

      super.m_7892_(blockState, level, pos, entity);
   }

   @org.jetbrains.annotations.Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new AlchemistCauldronTile(pos, state);
   }

   public void m_6810_(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.m_60734_() != pNewState.m_60734_() && pLevel.m_7702_(pPos) instanceof AlchemistCauldronTile cauldronTile) {
         cauldronTile.drops();
      }

      super.m_6810_(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @NotNull
   public RenderShape m_7514_(@NotNull BlockState blockState) {
      return RenderShape.MODEL;
   }
}
