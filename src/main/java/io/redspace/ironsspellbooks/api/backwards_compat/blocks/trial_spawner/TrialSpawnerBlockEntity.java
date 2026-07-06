package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.PlayerDetector;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.TrialSpawner;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.TrialSpawnerState;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class TrialSpawnerBlockEntity extends BlockEntity implements TrialSpawner.StateAccessor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private TrialSpawner trialSpawner;

   public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)BlockRegistry.TRIAL_SPAWNER_BLOCK_ENTITY.get(), pos, state);
      PlayerDetector playerdetector = PlayerDetector.NO_CREATIVE_PLAYERS;
      PlayerDetector.EntitySelector playerdetector$entityselector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
      this.trialSpawner = new TrialSpawner(this, playerdetector, playerdetector$entityselector);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      if (tag.m_128441_("normal_config")) {
         CompoundTag compoundtag = tag.m_128469_("normal_config").m_6426_();
         tag.m_128365_("ominous_config", compoundtag.m_128391_(tag.m_128469_("ominous_config")));
      }

      this.trialSpawner.codec().parse(NbtOps.f_128958_, tag).resultOrPartial(LOGGER::error).ifPresent(p_311911_ -> this.trialSpawner = p_311911_);
      if (this.f_58857_ != null) {
         this.markUpdated();
      }
   }

   public void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      this.trialSpawner
         .codec()
         .encodeStart(NbtOps.f_128958_, this.trialSpawner)
         .get()
         .ifLeft(p_312175_ -> tag.m_128391_((CompoundTag)p_312175_))
         .ifRight(p_338001_ -> LOGGER.warn("Failed to encode TrialSpawner {}", p_338001_.message()));
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public CompoundTag m_5995_() {
      return this.trialSpawner.getData().getUpdateTag((TrialSpawnerState)this.m_58900_().m_61143_(TrialSpawnerBlock.STATE));
   }

   public boolean m_6326_() {
      return true;
   }

   public TrialSpawner getTrialSpawner() {
      return this.trialSpawner;
   }

   @Override
   public TrialSpawnerState getState() {
      return !this.m_58900_().m_61138_(TrialSpawnerBlock.STATE)
         ? TrialSpawnerState.INACTIVE
         : (TrialSpawnerState)this.m_58900_().m_61143_(TrialSpawnerBlock.STATE);
   }

   @Override
   public void setState(Level level, TrialSpawnerState state) {
      this.m_6596_();
      level.m_46597_(this.f_58858_, (BlockState)this.m_58900_().m_61124_(TrialSpawnerBlock.STATE, state));
   }

   @Override
   public void markUpdated() {
      this.m_6596_();
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
      }
   }
}
