package io.redspace.ironsspellbooks.worldgen;

import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.event.ForgeEventFactory;

public class IceSpiderPatrolSpawner implements CustomSpawner {
   private static final int DELAY_FIXED = 9600;
   private static final int DELAY_VARIABLE = 3600;
   private int tickDelay;

   public int m_7995_(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
      if (spawnEnemies && (Boolean)ServerConfigs.ICE_SPIDER_PATROLS.get()) {
         int playercount = level.m_6907_().size();
         if (playercount < 1) {
            return 0;
         }

         RandomSource randomsource = level.f_46441_;
         this.tickDelay--;
         if (this.tickDelay > 0) {
            return 0;
         }

         this.tickDelay = 9600 / getGroupedPlayerCount(level) + randomsource.m_188503_(3600);
         if (level.m_46471_() && !randomsource.m_188499_()) {
            Player player = null;

            for (int i = 0; i < playercount; i++) {
               player = (Player)level.m_6907_().get(randomsource.m_188503_(playercount));
               if (!player.m_5833_() && !player.m_7500_()) {
                  break;
               }

               player = null;
            }

            if (player == null) {
               return 0;
            } else {
               return performIceSpiderHuntSpawn(level, player, 4) ? 1 : 0;
            }
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static boolean performIceSpiderHuntSpawn(ServerLevel level, LivingEntity targetEntity, int maxAttempts) {
      for (int i = 0; i < maxAttempts; i++) {
         if (i > 0) {
            Holder<Biome> holder = level.m_204166_(targetEntity.m_20183_());
            if (!holder.m_203656_(ModTags.ICE_SPIDER_PATROLS)) {
               return false;
            }
         }

         RandomSource randomsource = level.f_46441_;
         int k = (24 + randomsource.m_188503_(24)) * (randomsource.m_188499_() ? -1 : 1);
         int l = (24 + randomsource.m_188503_(24)) * (randomsource.m_188499_() ? -1 : 1);
         MutableBlockPos blockpos$mutableblockpos = targetEntity.m_20183_().m_122032_().m_122184_(k, 0, l);
         if (!level.m_151572_(
            blockpos$mutableblockpos.m_123341_() - 10,
            blockpos$mutableblockpos.m_123343_() - 10,
            blockpos$mutableblockpos.m_123341_() + 10,
            blockpos$mutableblockpos.m_123343_() + 10
         )) {
            break;
         }

         Holder<Biome> holder = level.m_204166_(blockpos$mutableblockpos);
         if (!holder.m_203656_(ModTags.ICE_SPIDER_PATROLS)) {
            break;
         }

         blockpos$mutableblockpos.m_142448_(level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).m_123342_());
         if (createSpider(level, blockpos$mutableblockpos, targetEntity)) {
            return true;
         }
      }

      return false;
   }

   private static int getGroupedPlayerCount(ServerLevel serverLevel) {
      List<BlockPos> groupPositions = new ArrayList<>();
      int count = 0;
      int groupRange = 48;

      for (Player player : serverLevel.m_6907_()) {
         if (groupPositions.stream().noneMatch(pos -> pos.m_123331_(player.m_20183_()) < groupRange * groupRange)) {
            count++;
            groupPositions.add(player.m_20183_());
         }
      }

      return count;
   }

   private static boolean createSpider(ServerLevel level, MutableBlockPos pos, LivingEntity targetEntity) {
      BlockState blockstate = level.m_8055_(pos);
      if (!NaturalSpawner.m_47056_(level, pos, blockstate, blockstate.m_60819_(), (EntityType)EntityRegistry.ICE_SPIDER.get())) {
         return false;
      }

      if (!checkPatrollingMonsterSpawnRules((EntityType<? extends Mob>)EntityRegistry.ICE_SPIDER.get(), level, MobSpawnType.PATROL, pos, level.f_46441_)) {
         return false;
      }

      IceSpiderEntity iceSpider = new IceSpiderEntity(level);
      iceSpider.m_20035_(pos.m_7949_(), 0.0F, 0.0F);
      iceSpider.m_6710_(targetEntity);
      level.m_5594_(null, iceSpider.m_20183_(), (SoundEvent)SoundRegistry.ICE_SPIDER_HOWL.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
      iceSpider.setEmergeFromGround();
      if (!ForgeEventFactory.checkSpawnPosition(iceSpider, level, MobSpawnType.PATROL)) {
         return false;
      }

      level.m_7967_(iceSpider);
      iceSpider.m_6518_(level, level.m_6436_(pos), MobSpawnType.PATROL, null, null);
      return true;
   }

   public static boolean checkPatrollingMonsterSpawnRules(
      EntityType<? extends Mob> mob, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
   ) {
      return level.m_45517_(LightLayer.BLOCK, pos) <= 8 && level.m_46791_() != Difficulty.PEACEFUL && Monster.m_217057_(mob, level, spawnType, pos, random);
   }
}
