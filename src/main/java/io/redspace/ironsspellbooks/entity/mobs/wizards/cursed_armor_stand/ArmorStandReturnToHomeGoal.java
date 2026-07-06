package io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ArmorStandReturnToHomeGoal extends WaterAvoidingRandomStrollGoal {
   CursedArmorStandEntity mob;
   int stuckTimer;
   private static final int MAX_INTERVAL = 10;
   private static final float CLOSE_DISTANCE = 2.0F;
   boolean closingFinalDistance;
   Vec3 lastStuckPos = Vec3.f_82478_;
   int stuckCounter;
   private static final double ARRIVED_THRESHOLD = 0.1;
   private static final double ATHRESHOLD_SQR = 0.010000000000000002;

   public ArmorStandReturnToHomeGoal(CursedArmorStandEntity pMob, double pSpeedModifier) {
      super(pMob, pSpeedModifier);
      this.mob = pMob;
      this.f_25730_ = 10;
   }

   public boolean m_8036_() {
      if (this.mob.m_217005_()) {
         return false;
      }

      if (this.mob.isArmorStandFrozen()) {
         return false;
      }

      Vec3 vec3 = this.m_7037_();
      if (vec3 == null) {
         return false;
      }

      this.f_25726_ = vec3.f_82479_;
      this.f_25727_ = vec3.f_82480_;
      this.f_25728_ = vec3.f_82481_;
      this.f_25731_ = false;
      return true;
   }

   public boolean m_8045_() {
      if (this.mob.m_217005_()) {
         return false;
      }

      if (this.mob.m_21573_().m_26571_()) {
         double distance = this.homeDistanceSqr();
         if (distance <= 0.010000000000000002) {
            return false;
         } else if (distance <= 4.0) {
            this.closingFinalDistance = true;
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void m_8037_() {
      if (this.mob.f_19797_ % 200 == 0) {
         Vec3 currpos = this.mob.m_20182_();
         double distance = this.lastStuckPos.m_82557_(currpos);
         if (distance < 25.0) {
            this.stuckCounter++;
         } else {
            this.lastStuckPos = currpos;
            this.stuckCounter = 0;
         }

         if (this.stuckCounter > 2) {
            this.mob.spawn = this.mob.m_20182_();
            this.m_8041_();
            return;
         }
      }

      if (this.closingFinalDistance && this.mob.m_21573_().m_26571_() && this.mob.spawn != null) {
         Vec3 delta = this.mob.spawn.m_82546_(this.mob.m_20182_());
         double currDistance = delta.m_82556_();
         double d0 = this.mob.spawn.f_82479_ - this.mob.m_20185_();
         double d1 = this.mob.spawn.f_82481_ - this.mob.m_20189_();
         float f = (float)(Mth.m_14136_(d1, d0) * 180.0 / (float) Math.PI) - 90.0F;
         this.mob.m_146922_(f);
         this.mob.m_5618_(f);
         this.mob.m_21566_().m_24988_((float)this.f_25729_, 0.0F);
         if (currDistance > 4.0) {
            this.closingFinalDistance = false;
         } else if (currDistance < 0.010000000000000002) {
            this.m_8041_();
         }
      } else {
         super.m_8037_();
      }
   }

   public void m_8056_() {
      this.mob.m_21570_(0.0F);
      super.m_8056_();
      this.stuckTimer = 0;
      this.f_25730_ = 10;
   }

   public void m_8041_() {
      super.m_8041_();
      this.closingFinalDistance = false;
      if (this.homeDistanceSqr() <= 0.010000000000000002) {
         this.mob.setArmorStandFrozen(true);
      }
   }

   private double homeDistanceSqr() {
      return this.mob.spawn == null ? 0.0 : this.mob.m_20238_(this.mob.spawn);
   }

   @Nullable
   protected Vec3 m_7037_() {
      return this.mob.spawn;
   }
}
