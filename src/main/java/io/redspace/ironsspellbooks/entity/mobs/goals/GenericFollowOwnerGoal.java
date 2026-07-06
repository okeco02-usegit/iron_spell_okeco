package io.redspace.ironsspellbooks.entity.mobs.goals;

import java.util.EnumSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class GenericFollowOwnerGoal extends Goal {
   private final PathfinderMob entity;
   @Nullable
   private Entity owner;
   private Supplier<Entity> ownerGetter;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private final float startDistance;
   private float oldWaterCost;
   private float teleportDistance;
   private boolean canFly;

   public GenericFollowOwnerGoal(
      PathfinderMob pTamable,
      Supplier<Entity> ownerGetter,
      double pSpeedModifier,
      float pStartDistance,
      float pStopDistance,
      boolean canFly,
      float teleportDistance
   ) {
      this.entity = pTamable;
      this.ownerGetter = ownerGetter;
      this.speedModifier = pSpeedModifier;
      this.navigation = pTamable.m_21573_();
      this.startDistance = pStartDistance;
      this.stopDistance = pStopDistance;
      this.teleportDistance = teleportDistance;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.canFly = canFly;
   }

   public boolean m_8036_() {
      Entity livingentity = this.ownerGetter.get();
      if (livingentity == null) {
         return false;
      }

      if (this.entity.m_20280_(livingentity) < this.startDistance * this.startDistance) {
         return false;
      }

      this.owner = livingentity;
      return true;
   }

   public boolean m_8045_() {
      return this.navigation.m_26571_() ? false : !(this.entity.m_20280_(this.owner) <= this.stopDistance * this.stopDistance);
   }

   public void m_8056_() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.entity.m_21439_(BlockPathTypes.WATER);
      this.entity.m_21441_(BlockPathTypes.WATER, 0.0F);
   }

   public void m_8041_() {
      this.owner = null;
      this.navigation.m_26573_();
      this.entity.m_21441_(BlockPathTypes.WATER, this.oldWaterCost);
   }

   public void m_8037_() {
      boolean flag = this.shouldTryTeleportToOwner();
      if (!flag) {
         this.entity.m_21563_().m_24960_(this.owner, 10.0F, this.entity.m_8132_());
      }

      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.m_183277_(10);
         if (flag) {
            this.tryToTeleportToOwner();
         } else {
            this.navigation.m_5624_(this.owner, this.speedModifier);
         }
      }
   }

   public void tryToTeleportToOwner() {
      Entity livingentity = this.ownerGetter.get();
      if (livingentity != null) {
         this.teleportToAroundBlockPos(livingentity.m_20183_());
      }
   }

   public boolean shouldTryTeleportToOwner() {
      Entity livingentity = this.ownerGetter.get();
      return livingentity != null && this.entity.m_20280_(livingentity) >= this.teleportDistance * this.teleportDistance;
   }

   private void teleportToAroundBlockPos(BlockPos pPos) {
      for (int i = 0; i < 10; i++) {
         int j = this.entity.m_217043_().m_216332_(-3, 3);
         int k = this.entity.m_217043_().m_216332_(-3, 3);
         if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
            int l = this.entity.m_217043_().m_216332_(-1, 1);
            if (this.maybeTeleportTo(pPos.m_123341_() + j, pPos.m_123342_() + l, pPos.m_123343_() + k)) {
               return;
            }
         }
      }
   }

   private boolean maybeTeleportTo(int pX, int pY, int pZ) {
      if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
         return false;
      }

      this.entity.m_7678_(pX + 0.5, pY, pZ + 0.5, this.entity.m_146908_(), this.entity.m_146909_());
      this.navigation.m_26573_();
      return true;
   }

   private boolean canTeleportTo(BlockPos pPos) {
      BlockPathTypes blockpathtypes = WalkNodeEvaluator.m_77604_(this.entity.f_19853_, pPos.m_122032_());
      if (blockpathtypes != BlockPathTypes.WALKABLE) {
         return false;
      }

      BlockState blockstate = this.entity.f_19853_.m_8055_(pPos.m_7495_());
      if (!this.canFly && blockstate.m_60734_() instanceof LeavesBlock) {
         return false;
      }

      BlockPos blockpos = pPos.m_121996_(this.entity.m_20183_());
      return this.entity.f_19853_.m_45756_(this.entity, this.entity.m_20191_().m_82338_(blockpos));
   }
}
