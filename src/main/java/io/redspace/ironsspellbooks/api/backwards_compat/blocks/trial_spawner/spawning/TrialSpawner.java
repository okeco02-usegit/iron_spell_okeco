package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.TrialSpawnerBlock;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.SpawnData.CustomSpawnRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;

public final class TrialSpawner {
   public static final String NORMAL_CONFIG_TAG_NAME = "normal_config";
   public static final String OMINOUS_CONFIG_TAG_NAME = "ominous_config";
   public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
   private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
   private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
   private static final int MAX_MOB_TRACKING_DISTANCE = 47;
   private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.m_144944_(47);
   private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
   private final TrialSpawnerConfig normalConfig;
   private final TrialSpawnerConfig ominousConfig;
   private final TrialSpawnerData data;
   private final int requiredPlayerRange;
   private final int targetCooldownLength;
   private final TrialSpawner.StateAccessor stateAccessor;
   private PlayerDetector playerDetector;
   private final PlayerDetector.EntitySelector entitySelector;
   private boolean overridePeacefulAndMobSpawnRule;
   private boolean isOminous;

   public Codec<TrialSpawner> codec() {
      return RecordCodecBuilder.create(
         p_338040_ -> p_338040_.group(
               TrialSpawnerConfig.CODEC.optionalFieldOf("normal_config", TrialSpawnerConfig.DEFAULT).forGetter(TrialSpawner::getNormalConfig),
               TrialSpawnerConfig.CODEC.optionalFieldOf("ominous_config", TrialSpawnerConfig.DEFAULT).forGetter(TrialSpawner::getOminousConfigForSerialization),
               TrialSpawnerData.MAP_CODEC.forGetter(TrialSpawner::getData),
               Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("target_cooldown_length", 36000).forGetter(TrialSpawner::getTargetCooldownLength),
               Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(TrialSpawner::getRequiredPlayerRange)
            )
            .apply(
               p_338040_,
               (p_338035_, p_338036_, p_338037_, p_338038_, p_338039_) -> new TrialSpawner(
                  p_338035_, p_338036_, p_338037_, p_338038_, p_338039_, this.stateAccessor, this.playerDetector, this.entitySelector
               )
            )
      );
   }

   public TrialSpawner(TrialSpawner.StateAccessor stateAccessor, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
      this(TrialSpawnerConfig.DEFAULT, TrialSpawnerConfig.DEFAULT, new TrialSpawnerData(), 36000, 14, stateAccessor, playerDetector, entitySelector);
   }

   public TrialSpawner(
      TrialSpawnerConfig normalConfig,
      TrialSpawnerConfig ominousConfig,
      TrialSpawnerData data,
      int targetCooldownLength,
      int requiredPlayerRange,
      TrialSpawner.StateAccessor stateAccessor,
      PlayerDetector playerDetector,
      PlayerDetector.EntitySelector entitySelector
   ) {
      this.normalConfig = normalConfig;
      this.ominousConfig = ominousConfig;
      this.data = data;
      this.targetCooldownLength = targetCooldownLength;
      this.requiredPlayerRange = requiredPlayerRange;
      this.stateAccessor = stateAccessor;
      this.playerDetector = playerDetector;
      this.entitySelector = entitySelector;
   }

   public TrialSpawnerConfig getConfig() {
      return this.isOminous ? this.ominousConfig : this.normalConfig;
   }

   @VisibleForTesting
   public TrialSpawnerConfig getNormalConfig() {
      return this.normalConfig;
   }

   @VisibleForTesting
   public TrialSpawnerConfig getOminousConfig() {
      return this.ominousConfig;
   }

   private TrialSpawnerConfig getOminousConfigForSerialization() {
      return !this.ominousConfig.equals(this.normalConfig) ? this.ominousConfig : TrialSpawnerConfig.DEFAULT;
   }

   public void applyOminous(ServerLevel level, BlockPos pos) {
      level.m_7731_(pos, (BlockState)level.m_8055_(pos).m_61124_(TrialSpawnerBlock.OMINOUS, true), 3);
      level.m_46796_(3020, pos, 1);
      this.isOminous = true;
      this.data.resetAfterBecomingOminous(this, level);
   }

   public void removeOminous(ServerLevel level, BlockPos pos) {
      level.m_7731_(pos, (BlockState)level.m_8055_(pos).m_61124_(TrialSpawnerBlock.OMINOUS, false), 3);
      this.isOminous = false;
   }

   public boolean isOminous() {
      return this.isOminous;
   }

   public TrialSpawnerData getData() {
      return this.data;
   }

   public int getTargetCooldownLength() {
      return this.targetCooldownLength;
   }

   public int getRequiredPlayerRange() {
      return this.requiredPlayerRange;
   }

   public TrialSpawnerState getState() {
      return this.stateAccessor.getState();
   }

   public void setState(Level level, TrialSpawnerState state) {
      this.stateAccessor.setState(level, state);
   }

   public void markUpdated() {
      this.stateAccessor.markUpdated();
   }

   public PlayerDetector getPlayerDetector() {
      return this.playerDetector;
   }

   public PlayerDetector.EntitySelector getEntitySelector() {
      return this.entitySelector;
   }

   public boolean canSpawnInLevel(Level level) {
      if (this.overridePeacefulAndMobSpawnRule) {
         return true;
      } else {
         return level.m_46791_() == Difficulty.PEACEFUL ? false : level.m_46469_().m_46207_(GameRules.f_46134_);
      }
   }

   public Optional<UUID> spawnMob(ServerLevel level, BlockPos pos) {
      RandomSource randomsource = level.m_213780_();
      SpawnData spawndata = this.data.getOrCreateNextSpawnData(this, level.m_213780_());
      CompoundTag compoundtag = spawndata.f_186561_();
      ListTag listtag = compoundtag.m_128437_("Pos", 6);
      Optional<EntityType<?>> optional = EntityType.m_20637_(compoundtag);
      if (optional.isEmpty()) {
         return Optional.empty();
      }

      int i = listtag.size();
      double d0 = i >= 1 ? listtag.m_128772_(0) : pos.m_123341_() + (randomsource.m_188500_() - randomsource.m_188500_()) * this.getConfig().spawnRange() + 0.5;
      double d1 = i >= 2 ? listtag.m_128772_(1) : pos.m_123342_() + randomsource.m_188503_(3) - 1;
      double d2 = i >= 3 ? listtag.m_128772_(2) : pos.m_123343_() + (randomsource.m_188500_() - randomsource.m_188500_()) * this.getConfig().spawnRange() + 0.5;
      if (!level.m_45772_(optional.get().m_20585_(d0, d1, d2))) {
         return Optional.empty();
      }

      Vec3 vec3 = new Vec3(d0, d1, d2);
      if (!inLineOfSight(level, pos.m_252807_(), vec3)) {
         return Optional.empty();
      }

      BlockPos blockpos = BlockPos.m_274446_(vec3);
      if (!SpawnPlacements.m_217074_(optional.get(), level, MobSpawnType.MOB_SUMMONED, blockpos, level.m_213780_())) {
         return Optional.empty();
      }

      if (spawndata.m_186574_().isPresent()) {
         CustomSpawnRules spawndata$customspawnrules = (CustomSpawnRules)spawndata.m_186574_().get();
         if (!spawndata$customspawnrules.f_186584_().m_184578_(level.m_45517_(LightLayer.BLOCK, pos))
            || !spawndata$customspawnrules.f_186585_().m_184578_(level.m_45517_(LightLayer.SKY, pos))) {
            return Optional.empty();
         }
      }

      Entity entity = EntityType.m_20645_(compoundtag, level, p_312375_ -> {
         p_312375_.m_7678_(d0, d1, d2, randomsource.m_188501_() * 360.0F, 0.0F);
         return p_312375_;
      });
      if (entity == null) {
         return Optional.empty();
      }

      if (entity instanceof Mob mob) {
         if (!mob.m_6914_(level)) {
            return Optional.empty();
         }

         if (spawndata.m_186567_().m_128440_() == 1 && spawndata.m_186567_().m_128425_("id", 8)) {
            boolean flag = true;
         } else {
            boolean flag = false;
         }

         FinalizeSpawn event = new FinalizeSpawn(
            mob, level, mob.m_20185_(), mob.m_20186_(), mob.m_20189_(), level.m_6436_(mob.m_20183_()), MobSpawnType.SPAWNER, null, null, null
         );
         MinecraftForge.EVENT_BUS.post(event);
         mob.m_6518_(level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
         mob.m_21530_();
      }

      if (!level.m_8860_(entity)) {
         return Optional.empty();
      }

      TrialSpawner.FlameParticle trialspawner$flameparticle = this.isOminous ? TrialSpawner.FlameParticle.OMINOUS : TrialSpawner.FlameParticle.NORMAL;
      level.m_247517_(null, pos, (SoundEvent)SoundRegistry.TRIAL_SPAWNER_SPAWN_MOB.get(), SoundSource.BLOCKS);
      MagicManager.spawnParticles(
         level,
         trialspawner$flameparticle.particleType,
         entity.m_20191_().m_82399_().f_82479_,
         entity.m_20191_().m_82399_().f_82480_,
         entity.m_20191_().m_82399_().f_82481_,
         25,
         0.5,
         1.0,
         0.5,
         0.08,
         false
      );
      level.m_142346_(entity, GameEvent.f_157810_, blockpos);
      return Optional.of(entity.m_20148_());
   }

   public void ejectReward(ServerLevel level, BlockPos pos, ResourceLocation lootTable) {
      LootTable loottable = level.m_7654_().m_278653_().m_278676_(lootTable);
      LootParams lootparams = new Builder(level).m_287235_(LootContextParamSets.f_81410_);
      ObjectArrayList<ItemStack> objectarraylist = loottable.m_287195_(lootparams);
      if (!objectarraylist.isEmpty()) {
         ObjectListIterator var7 = objectarraylist.iterator();

         while (var7.hasNext()) {
            ItemStack itemstack = (ItemStack)var7.next();
            DefaultDispenseItemBehavior.m_123378_(level, itemstack, 2, Direction.UP, Vec3.m_82539_(pos).m_231075_(Direction.UP, 1.2));
         }

         level.m_247517_(null, pos, (SoundEvent)SoundRegistry.TRIAL_SPAWNER_EJECT_ITEM.get(), SoundSource.BLOCKS);
      }
   }

   public void tickClient(Level level, BlockPos pos, boolean isOminous) {
      TrialSpawnerState trialspawnerstate = this.getState();
      trialspawnerstate.emitParticles(level, pos, isOminous);
      if (trialspawnerstate.hasSpinningMob()) {
         double d0 = Math.max(0L, this.data.nextMobSpawnsAt - level.m_46467_());
         this.data.oSpin = this.data.spin;
         this.data.spin = (this.data.spin + trialspawnerstate.spinningMobSpeed() / (d0 + 200.0)) % 360.0;
      }

      if (trialspawnerstate.isCapableOfSpawning()) {
         RandomSource randomsource = level.m_213780_();
         if (randomsource.m_188501_() <= 0.02F) {
            SoundEvent soundevent = isOminous
               ? (SoundEvent)SoundRegistry.TRIAL_SPAWNER_AMBIENT_OMINOUS.get()
               : (SoundEvent)SoundRegistry.TRIAL_SPAWNER_AMBIENT.get();
            level.m_245747_(pos, soundevent, SoundSource.BLOCKS, randomsource.m_188501_() * 0.25F + 0.75F, randomsource.m_188501_() + 0.5F, false);
         }
      }
   }

   public void tickServer(ServerLevel level, BlockPos pos, boolean isOminous) {
      this.isOminous = isOminous;
      TrialSpawnerState trialspawnerstate = this.getState();
      if (this.data.currentMobs.removeIf(p_312870_ -> shouldMobBeUntracked(level, pos, p_312870_))) {
         this.data.nextMobSpawnsAt = level.m_46467_() + this.getConfig().ticksBetweenSpawn();
      }

      TrialSpawnerState trialspawnerstate1 = trialspawnerstate.tickAndGetNext(pos, this, level);
      if (trialspawnerstate1 != trialspawnerstate) {
         this.setState(level, trialspawnerstate1);
      }
   }

   private static boolean shouldMobBeUntracked(ServerLevel level, BlockPos pos, UUID uuid) {
      Entity entity = level.m_8791_(uuid);
      return entity == null
         || !entity.m_6084_()
         || !entity.m_9236_().m_46472_().equals(level.m_46472_())
         || entity.m_20183_().m_123331_(pos) > MAX_MOB_TRACKING_DISTANCE_SQR;
   }

   private static boolean inLineOfSight(Level level, Vec3 spawnerPos, Vec3 mobPos) {
      BlockHitResult blockhitresult = level.m_45547_(new ClipContext(mobPos, spawnerPos, Block.VISUAL, Fluid.NONE, null));
      return blockhitresult.m_82425_().equals(BlockPos.m_274446_(spawnerPos)) || blockhitresult.m_6662_() == Type.MISS;
   }

   public static void addSpawnParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particleType) {
      for (int i = 0; i < 20; i++) {
         double d0 = pos.m_123341_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         double d1 = pos.m_123342_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         double d2 = pos.m_123343_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         level.m_7106_(ParticleTypes.f_123762_, d0, d1, d2, 0.0, 0.0, 0.0);
         level.m_7106_(particleType, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }

   public static void addBecomeOminousParticles(Level level, BlockPos pos, RandomSource random) {
      for (int i = 0; i < 20; i++) {
         double d0 = pos.m_123341_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         double d1 = pos.m_123342_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         double d2 = pos.m_123343_() + 0.5 + (random.m_188500_() - 0.5) * 2.0;
         double d3 = random.m_188583_() * 0.02;
         double d4 = random.m_188583_() * 0.02;
         double d5 = random.m_188583_() * 0.02;
         level.m_7106_(ParticleTypes.f_123745_, d0, d1, d2, d3, d4, d5);
      }
   }

   public static void addDetectPlayerParticles(Level level, BlockPos pos, RandomSource random, int type, ParticleOptions particle) {
      for (int i = 0; i < 30 + Math.min(type, 10) * 5; i++) {
         double d0 = (2.0F * random.m_188501_() - 1.0F) * 0.65;
         double d1 = (2.0F * random.m_188501_() - 1.0F) * 0.65;
         double d2 = pos.m_123341_() + 0.5 + d0;
         double d3 = pos.m_123342_() + 0.1 + random.m_188501_() * 0.8;
         double d4 = pos.m_123343_() + 0.5 + d1;
         level.m_7106_(particle, d2, d3, d4, 0.0, 0.0, 0.0);
      }
   }

   public static void addEjectItemParticles(Level level, BlockPos pos, RandomSource random) {
      for (int i = 0; i < 20; i++) {
         double d0 = pos.m_123341_() + 0.4 + random.m_188500_() * 0.2;
         double d1 = pos.m_123342_() + 0.4 + random.m_188500_() * 0.2;
         double d2 = pos.m_123343_() + 0.4 + random.m_188500_() * 0.2;
         double d3 = random.m_188583_() * 0.02;
         double d4 = random.m_188583_() * 0.02;
         double d5 = random.m_188583_() * 0.02;
         level.m_7106_(ParticleTypes.f_175834_, d0, d1, d2, d3, d4, d5 * 0.25);
         level.m_7106_(ParticleTypes.f_123762_, d0, d1, d2, d3, d4, d5);
      }
   }

   @Deprecated(forRemoval = true)
   @VisibleForTesting
   public void setPlayerDetector(PlayerDetector playerDetector) {
      this.playerDetector = playerDetector;
   }

   @Deprecated(forRemoval = true)
   @VisibleForTesting
   public void overridePeacefulAndMobSpawnRule() {
      this.overridePeacefulAndMobSpawnRule = true;
   }

   public enum FlameParticle {
      NORMAL(ParticleTypes.f_123744_),
      OMINOUS(ParticleTypes.f_123745_);

      public final SimpleParticleType particleType;

      FlameParticle(SimpleParticleType particleType) {
         this.particleType = particleType;
      }

      public static TrialSpawner.FlameParticle decode(int id) {
         TrialSpawner.FlameParticle[] atrialspawner$flameparticle = values();
         return id <= atrialspawner$flameparticle.length && id >= 0 ? atrialspawner$flameparticle[id] : NORMAL;
      }

      public int encode() {
         return this.ordinal();
      }
   }

   public interface StateAccessor {
      void setState(Level var1, TrialSpawnerState var2);

      TrialSpawnerState getState();

      void markUpdated();
   }
}
