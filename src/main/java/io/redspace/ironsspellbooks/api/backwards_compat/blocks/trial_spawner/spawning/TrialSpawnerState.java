package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning;

import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public enum TrialSpawnerState implements StringRepresentable {
   INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmission.NONE, -1.0, false),
   WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, 200.0, true),
   ACTIVE("active", 8, TrialSpawnerState.ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true),
   WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
   EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
   COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);

   private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
   private static final int TIME_BETWEEN_EACH_EJECTION = Mth.m_14143_(30.0F);
   private final String name;
   private final int lightLevel;
   private final double spinningMobSpeed;
   private final TrialSpawnerState.ParticleEmission particleEmission;
   private final boolean isCapableOfSpawning;

   TrialSpawnerState(String name, int lightLevel, TrialSpawnerState.ParticleEmission particleEmission, double spinningMobSpeed, boolean isCapableOfSpawning) {
      this.name = name;
      this.lightLevel = lightLevel;
      this.particleEmission = particleEmission;
      this.spinningMobSpeed = spinningMobSpeed;
      this.isCapableOfSpawning = isCapableOfSpawning;
   }

   TrialSpawnerState tickAndGetNext(BlockPos pos, TrialSpawner spawner, ServerLevel level) {
      TrialSpawnerData trialspawnerdata = spawner.getData();
      TrialSpawnerConfig trialspawnerconfig = spawner.getConfig();

      return switch (this) {
         case INACTIVE -> trialspawnerdata.getOrCreateDisplayEntity(spawner, level, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
         case WAITING_FOR_PLAYERS -> {
            if (!spawner.canSpawnInLevel(level)) {
               trialspawnerdata.reset();
               yield this;
            } else if (!trialspawnerdata.hasMobToSpawn(spawner, level.f_46441_)) {
               yield INACTIVE;
            } else {
               trialspawnerdata.tryDetectPlayers(level, pos, spawner);
               yield trialspawnerdata.detectedPlayers.isEmpty() ? this : ACTIVE;
            }
         }
         case ACTIVE -> {
            if (!spawner.canSpawnInLevel(level)) {
               trialspawnerdata.reset();
               yield WAITING_FOR_PLAYERS;
            } else if (!trialspawnerdata.hasMobToSpawn(spawner, level.f_46441_)) {
               yield INACTIVE;
            } else {
               int i = trialspawnerdata.countAdditionalPlayers(pos);
               trialspawnerdata.tryDetectPlayers(level, pos, spawner);
               if (spawner.isOminous()) {
                  this.spawnOminousOminousItemSpawner(level, pos, spawner);
               }

               if (trialspawnerdata.hasFinishedSpawningAllMobs(trialspawnerconfig, i)) {
                  if (trialspawnerdata.haveAllCurrentMobsDied()) {
                     trialspawnerdata.cooldownEndsAt = level.m_46467_() + spawner.getTargetCooldownLength();
                     trialspawnerdata.totalMobsSpawned = 0;
                     trialspawnerdata.nextMobSpawnsAt = 0L;
                     yield WAITING_FOR_REWARD_EJECTION;
                  }
               } else if (trialspawnerdata.isReadyToSpawnNextMob(level, trialspawnerconfig, i)) {
                  spawner.spawnMob(level, pos).ifPresent(p_340800_ -> {
                     trialspawnerdata.currentMobs.add(p_340800_);
                     trialspawnerdata.totalMobsSpawned++;
                     trialspawnerdata.nextMobSpawnsAt = level.m_46467_() + trialspawnerconfig.ticksBetweenSpawn();
                     trialspawnerconfig.spawnPotentialsDefinition().m_216829_(level.m_213780_()).ifPresent(p_338048_ -> {
                        trialspawnerdata.nextSpawnData = Optional.of((SpawnData)p_338048_.m_146310_());
                        spawner.markUpdated();
                     });
                  });
               }

               yield this;
            }
         }
         case WAITING_FOR_REWARD_EJECTION -> {
            if (trialspawnerdata.isReadyToOpenShutter(level, 40.0F, spawner.getTargetCooldownLength())) {
               level.m_247517_(null, pos, (SoundEvent)SoundRegistry.TRIAL_SPAWNER_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
               yield EJECTING_REWARD;
            } else {
               yield this;
            }
         }
         case EJECTING_REWARD -> {
            if (!trialspawnerdata.isReadyToEjectItems(level, TIME_BETWEEN_EACH_EJECTION, spawner.getTargetCooldownLength())) {
               yield this;
            } else if (trialspawnerdata.detectedPlayers.isEmpty()) {
               level.m_247517_(null, pos, (SoundEvent)SoundRegistry.TRIAL_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
               trialspawnerdata.ejectingLootTable = Optional.empty();
               yield COOLDOWN;
            } else {
               if (trialspawnerdata.ejectingLootTable.isEmpty()) {
                  trialspawnerdata.ejectingLootTable = trialspawnerconfig.lootTablesToEject().m_216820_(level.m_213780_());
               }

               trialspawnerdata.ejectingLootTable.ifPresent(p_335304_ -> spawner.ejectReward(level, pos, p_335304_));
               trialspawnerdata.detectedPlayers.remove(trialspawnerdata.detectedPlayers.iterator().next());
               yield this;
            }
         }
         case COOLDOWN -> {
            trialspawnerdata.tryDetectPlayers(level, pos, spawner);
            if (!trialspawnerdata.detectedPlayers.isEmpty()) {
               trialspawnerdata.totalMobsSpawned = 0;
               trialspawnerdata.nextMobSpawnsAt = 0L;
               yield ACTIVE;
            } else if (trialspawnerdata.isCooldownFinished(level)) {
               spawner.removeOminous(level, pos);
               trialspawnerdata.reset();
               yield WAITING_FOR_PLAYERS;
            } else {
               yield this;
            }
         }
      };
   }

   private void spawnOminousOminousItemSpawner(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
      TrialSpawnerData trialspawnerdata = spawner.getData();
      TrialSpawnerConfig trialspawnerconfig = spawner.getConfig();
      ItemStack itemstack = trialspawnerdata.getDispensingItems(level, trialspawnerconfig, pos).m_216820_(level.f_46441_).orElse(ItemStack.f_41583_);
      if (!itemstack.m_41619_() && this.timeToSpawnItemSpawner(level, trialspawnerdata)) {
      }
   }

   private static Optional<Vec3> calculatePositionToSpawnSpawner(ServerLevel level, BlockPos pos, TrialSpawner spawner, TrialSpawnerData spawnerData) {
      List<Player> list = spawnerData.detectedPlayers
         .stream()
         .<Player>map(level::m_46003_)
         .filter(Objects::nonNull)
         .filter(
            p_350236_ -> !p_350236_.m_7500_()
               && !p_350236_.m_5833_()
               && p_350236_.m_6084_()
               && p_350236_.m_20238_(pos.m_252807_()) <= Mth.m_144944_(spawner.getRequiredPlayerRange())
         )
         .toList();
      if (list.isEmpty()) {
         return Optional.empty();
      }

      Entity entity = selectEntityToSpawnItemAbove(list, spawnerData.currentMobs, spawner, pos, level);
      return entity == null ? Optional.empty() : calculatePositionAbove(entity, level);
   }

   private static Optional<Vec3> calculatePositionAbove(Entity entity, ServerLevel level) {
      Vec3 vec3 = entity.m_20182_();
      Vec3 vec31 = vec3.m_231075_(Direction.UP, entity.m_20206_() + 2.0F + level.f_46441_.m_188503_(4));
      BlockHitResult blockhitresult = level.m_45547_(new ClipContext(vec3, vec31, Block.VISUAL, Fluid.NONE, null));
      Vec3 vec32 = blockhitresult.m_82425_().m_252807_().m_231075_(Direction.DOWN, 1.0);
      BlockPos blockpos = BlockPos.m_274446_(vec32);
      return !level.m_8055_(blockpos).m_60812_(level, blockpos).m_83281_() ? Optional.empty() : Optional.of(vec32);
   }

   @Nullable
   private static Entity selectEntityToSpawnItemAbove(List<Player> player, Set<UUID> currentMobs, TrialSpawner spawner, BlockPos pos, ServerLevel level) {
      Stream<Entity> stream = currentMobs.stream()
         .<Entity>map(level::m_8791_)
         .filter(Objects::nonNull)
         .filter(p_338051_ -> p_338051_.m_6084_() && p_338051_.m_20238_(pos.m_252807_()) <= Mth.m_144944_(spawner.getRequiredPlayerRange()));
      List<? extends Entity> list = level.f_46441_.m_188499_() ? stream.toList() : player;
      if (list.isEmpty()) {
         return null;
      } else {
         return list.size() == 1 ? list.get(0) : (Entity)Util.m_214621_(list, level.f_46441_);
      }
   }

   private boolean timeToSpawnItemSpawner(ServerLevel level, TrialSpawnerData spawnerData) {
      return level.m_46467_() >= spawnerData.cooldownEndsAt;
   }

   public int lightLevel() {
      return this.lightLevel;
   }

   public double spinningMobSpeed() {
      return this.spinningMobSpeed;
   }

   public boolean hasSpinningMob() {
      return this.spinningMobSpeed >= 0.0;
   }

   public boolean isCapableOfSpawning() {
      return this.isCapableOfSpawning;
   }

   public void emitParticles(Level level, BlockPos pos, boolean isOminous) {
      this.particleEmission.emit(level, level.m_213780_(), pos, isOminous);
   }

   public String m_7912_() {
      return this.name;
   }

   static class LightLevel {
      private static final int UNLIT = 0;
      private static final int HALF_LIT = 4;
      private static final int LIT = 8;

      private LightLevel() {
      }
   }

   interface ParticleEmission {
      TrialSpawnerState.ParticleEmission NONE = (p_311998_, p_311983_, p_312351_, p_338371_) -> {};
      TrialSpawnerState.ParticleEmission SMALL_FLAMES = (p_338069_, p_338070_, p_338071_, p_338072_) -> {
         if (p_338070_.m_188503_(2) == 0) {
            Vec3 vec3 = p_338071_.m_252807_().m_272010_(p_338070_, 0.9F);
            addParticle(p_338072_ ? ParticleTypes.f_123745_ : ParticleTypes.f_175834_, vec3, p_338069_);
         }
      };
      TrialSpawnerState.ParticleEmission FLAMES_AND_SMOKE = (p_338065_, p_338066_, p_338067_, p_338068_) -> {
         Vec3 vec3 = p_338067_.m_252807_().m_272010_(p_338066_, 1.0F);
         addParticle(ParticleTypes.f_123762_, vec3, p_338065_);
         addParticle(p_338068_ ? ParticleTypes.f_123745_ : ParticleTypes.f_123744_, vec3, p_338065_);
      };
      TrialSpawnerState.ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (p_311899_, p_311762_, p_312096_, p_338301_) -> {
         Vec3 vec3 = p_312096_.m_252807_().m_272010_(p_311762_, 0.9F);
         if (p_311762_.m_188503_(3) == 0) {
            addParticle(ParticleTypes.f_123762_, vec3, p_311899_);
         }

         if (p_311899_.m_46467_() % 20L == 0L) {
            Vec3 vec31 = p_312096_.m_252807_().m_82520_(0.0, 0.5, 0.0);
            int i = p_311899_.m_213780_().m_188503_(4) + 20;

            for (int j = 0; j < i; j++) {
               addParticle(ParticleTypes.f_123762_, vec31, p_311899_);
            }
         }
      };

      private static void addParticle(SimpleParticleType particleType, Vec3 pos, Level level) {
         level.m_7106_(particleType, pos.m_7096_(), pos.m_7098_(), pos.m_7094_(), 0.0, 0.0, 0.0);
      }

      void emit(Level var1, RandomSource var2, BlockPos var3, boolean var4);
   }

   static class SpinningMob {
      private static final double NONE = -1.0;
      private static final double SLOW = 200.0;
      private static final double FAST = 1000.0;

      private SpinningMob() {
      }
   }
}
