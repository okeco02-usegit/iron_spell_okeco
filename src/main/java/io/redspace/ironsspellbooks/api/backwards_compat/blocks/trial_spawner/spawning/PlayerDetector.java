package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public interface PlayerDetector {
   PlayerDetector NO_CREATIVE_PLAYERS = (p_338030_, p_338031_, p_338032_, p_338033_, p_338034_) -> p_338031_.getPlayers(
         p_338030_, p_352883_ -> p_352883_.m_20183_().m_123314_(p_338032_, p_338033_) && !p_352883_.m_7500_() && !p_352883_.m_5833_()
      )
      .stream()
      .filter(p_350221_ -> !p_338034_ || inLineOfSight(p_338030_, p_338032_.m_252807_(), p_350221_.m_146892_()))
      .<UUID>map(Entity::m_20148_)
      .toList();
   PlayerDetector INCLUDING_CREATIVE_PLAYERS = (p_338015_, p_338016_, p_338017_, p_338018_, p_338019_) -> p_338016_.getPlayers(
         p_338015_, p_352886_ -> p_352886_.m_20183_().m_123314_(p_338017_, p_338018_) && !p_352886_.m_5833_()
      )
      .stream()
      .filter(p_350228_ -> !p_338019_ || inLineOfSight(p_338015_, p_338017_.m_252807_(), p_350228_.m_146892_()))
      .<UUID>map(Entity::m_20148_)
      .toList();
   PlayerDetector SHEEP = (p_338002_, p_338003_, p_338004_, p_338005_, p_338006_) -> {
      AABB aabb = new AABB(p_338004_).m_82400_(p_338005_);
      return p_338003_.getEntities(p_338002_, EntityType.f_20520_, aabb, LivingEntity::m_6084_)
         .stream()
         .filter(p_350217_ -> !p_338006_ || inLineOfSight(p_338002_, p_338004_.m_252807_(), p_350217_.m_146892_()))
         .<UUID>map(Entity::m_20148_)
         .toList();
   };

   List<UUID> detect(ServerLevel var1, PlayerDetector.EntitySelector var2, BlockPos var3, double var4, boolean var6);

   private static boolean inLineOfSight(Level level, Vec3 pos, Vec3 targetPos) {
      BlockHitResult blockhitresult = level.m_45547_(new ClipContext(targetPos, pos, Block.VISUAL, Fluid.NONE, null));
      return blockhitresult.m_82425_().equals(BlockPos.m_274446_(pos)) || blockhitresult.m_6662_() == Type.MISS;
   }

   interface EntitySelector {
      PlayerDetector.EntitySelector SELECT_FROM_LEVEL = new PlayerDetector.EntitySelector() {
         @Override
         public List<ServerPlayer> getPlayers(ServerLevel p_323695_, Predicate<? super Player> p_324206_) {
            return p_323695_.m_8795_(p_324206_);
         }

         @Override
         public <T extends Entity> List<T> getEntities(
            ServerLevel p_324491_, EntityTypeTest<Entity, T> p_323728_, AABB p_324572_, Predicate<? super T> p_323881_
         ) {
            return p_324491_.m_142425_(p_323728_, p_324572_, p_323881_);
         }
      };

      List<? extends Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2);

      <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4);

      static PlayerDetector.EntitySelector onlySelectPlayer(Player player) {
         return onlySelectPlayers(List.of(player));
      }

      static PlayerDetector.EntitySelector onlySelectPlayers(final List<Player> players) {
         return new PlayerDetector.EntitySelector() {
            @Override
            public List<Player> getPlayers(ServerLevel p_323585_, Predicate<? super Player> p_323950_) {
               return players.stream().filter(p_323950_).toList();
            }

            @Override
            public <T extends Entity> List<T> getEntities(
               ServerLevel p_324352_, EntityTypeTest<Entity, T> p_323526_, AABB p_324544_, Predicate<? super T> p_323570_
            ) {
               return players.stream().<T>map(p_323526_::m_141992_).filter(Objects::nonNull).filter(p_323570_).toList();
            }
         };
      }
   }
}
