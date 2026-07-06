package io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault;

import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.TrialSpawnerBlock;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data.VaultBlockEntity;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data.VaultState;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class VaultBlock extends BaseEntityBlock {
   public static final Property<VaultState> STATE = EnumProperty.m_61587_("vault_state", VaultState.class);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   public static final BooleanProperty OMINOUS = TrialSpawnerBlock.OMINOUS;

   public VaultBlock() {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283947_)
            .m_280658_(NoteBlockInstrument.BASEDRUM)
            .m_60955_()
            .m_60918_(
               new SoundType(
                  1.0F,
                  1.0F,
                  (SoundEvent)SoundRegistry.VAULT_BREAK.get(),
                  (SoundEvent)SoundRegistry.VAULT_STEP.get(),
                  (SoundEvent)SoundRegistry.VAULT_PLACE.get(),
                  (SoundEvent)SoundRegistry.VAULT_HIT.get(),
                  (SoundEvent)SoundRegistry.VAULT_FALL.get()
               )
            )
            .m_60953_(p_323402_ -> ((VaultState)p_323402_.m_61143_(STATE)).lightLevel())
            .m_60978_(50.0F)
      );
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH)).m_61124_(STATE, VaultState.INACTIVE))
            .m_61124_(OMINOUS, false)
      );
   }

   public InteractionResult m_6227_(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      ItemStack stack = pPlayer.m_21120_(pHand);
      if (stack.m_41619_() || pState.m_61143_(STATE) != VaultState.ACTIVE) {
         return InteractionResult.PASS;
      }

      if (pLevel instanceof ServerLevel serverlevel) {
         if (serverlevel.m_7702_(pPos) instanceof VaultBlockEntity vaultblockentity) {
            VaultBlockEntity.Server.tryInsertKey(
               serverlevel, pPos, pState, vaultblockentity.getConfig(), vaultblockentity.getServerData(), vaultblockentity.getSharedData(), pPlayer, stack
            );
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos p_324543_, BlockState p_323652_) {
      return new VaultBlockEntity(p_324543_, p_323652_);
   }

   protected void m_7926_(Builder<Block, BlockState> p_323673_) {
      p_323673_.m_61104_(new Property[]{FACING, STATE, OMINOUS});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_323525_, BlockState p_324070_, BlockEntityType<T> p_323541_) {
      return p_323525_ instanceof ServerLevel serverlevel
         ? m_152132_(
            p_323541_,
            (BlockEntityType)BlockRegistry.VAULT_BLOCK_ENTITY.get(),
            (p_323957_, p_324322_, p_323828_, p_323769_) -> VaultBlockEntity.Server.tick(
               serverlevel, p_324322_, p_323828_, p_323769_.getConfig(), p_323769_.getServerData(), p_323769_.getSharedData()
            )
         )
         : m_152132_(
            p_323541_,
            (BlockEntityType)BlockRegistry.VAULT_BLOCK_ENTITY.get(),
            (p_324290_, p_323926_, p_323941_, p_323489_) -> VaultBlockEntity.Client.tick(
               p_324290_, p_323926_, p_323941_, p_323489_.getClientData(), p_323489_.getSharedData()
            )
         );
   }

   public BlockState m_5573_(BlockPlaceContext p_324576_) {
      return (BlockState)this.m_49966_().m_61124_(FACING, p_324576_.m_8125_().m_122424_());
   }

   public BlockState m_6843_(BlockState p_324232_, Rotation p_324443_) {
      return (BlockState)p_324232_.m_61124_(FACING, p_324443_.m_55954_((Direction)p_324232_.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState p_323894_, Mirror p_324242_) {
      return p_323894_.m_60717_(p_324242_.m_54846_((Direction)p_323894_.m_61143_(FACING)));
   }

   public RenderShape m_7514_(BlockState p_324584_) {
      return RenderShape.MODEL;
   }
}
