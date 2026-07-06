package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner;

import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.TrialSpawnerState;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;

public class TrialSpawnerBlock extends BaseEntityBlock {
   public static final EnumProperty<TrialSpawnerState> STATE = EnumProperty.m_61587_("trial_spawner_state", TrialSpawnerState.class);
   public static final BooleanProperty OMINOUS = BooleanProperty.m_61465_("ominous");

   public TrialSpawnerBlock() {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283947_)
            .m_280658_(NoteBlockInstrument.BASEDRUM)
            .m_60953_(p_311743_ -> ((TrialSpawnerState)p_311743_.m_61143_(STATE)).lightLevel())
            .m_60978_(50.0F)
            .m_60918_(
               new SoundType(
                  1.0F,
                  1.0F,
                  (SoundEvent)SoundRegistry.TRIAL_SPAWNER_BREAK.get(),
                  (SoundEvent)SoundRegistry.TRIAL_SPAWNER_STEP.get(),
                  (SoundEvent)SoundRegistry.TRIAL_SPAWNER_PLACE.get(),
                  (SoundEvent)SoundRegistry.TRIAL_SPAWNER_HIT.get(),
                  (SoundEvent)SoundRegistry.TRIAL_SPAWNER_FALL.get()
               )
            )
            .m_60955_()
      );
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(STATE, TrialSpawnerState.INACTIVE)).m_61124_(OMINOUS, false));
   }

   protected void m_7926_(Builder<Block, BlockState> p_312785_) {
      p_312785_.m_61104_(new Property[]{STATE, OMINOUS});
   }

   public RenderShape m_7514_(BlockState p_312710_) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos p_311941_, BlockState p_312821_) {
      return new TrialSpawnerBlockEntity(p_311941_, p_312821_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_311756_, BlockState p_312797_, BlockEntityType<T> p_312122_) {
      return p_311756_ instanceof ServerLevel serverlevel
         ? m_152132_(
            p_312122_,
            (BlockEntityType)BlockRegistry.TRIAL_SPAWNER_BLOCK_ENTITY.get(),
            (p_337976_, p_337977_, p_337978_, p_337979_) -> p_337979_.getTrialSpawner()
               .tickServer(serverlevel, p_337977_, p_337978_.m_61145_(OMINOUS).orElse(false))
         )
         : m_152132_(
            p_312122_,
            (BlockEntityType)BlockRegistry.TRIAL_SPAWNER_BLOCK_ENTITY.get(),
            (p_337980_, p_337981_, p_337982_, p_337983_) -> p_337983_.getTrialSpawner()
               .tickClient(p_337980_, p_337981_, p_337982_.m_61145_(OMINOUS).orElse(false))
         );
   }
}
