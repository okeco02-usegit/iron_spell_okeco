package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class MomentHurtByTargetGoal extends HurtByTargetGoal {
   int forcedAggroTime;
   float intensity;
   boolean isOutnumbered;

   public MomentHurtByTargetGoal(PathfinderMob pMob, Class<?>... pToIgnoreDamage) {
      super(pMob, pToIgnoreDamage);
   }

   public void m_8037_() {
      super.m_8037_();
      if (this.f_26034_ != this.f_26135_.m_21213_()) {
         this.f_26034_ = this.f_26135_.m_21213_();
         if (this.f_26135_.m_21188_() != null && this.f_26135_.m_21188_() != this.f_26137_) {
            this.isOutnumbered = true;
            this.forcedAggroTime -= 20;
            this.intensity *= 0.8F;
         } else if (this.isOutnumbered) {
            this.forcedAggroTime = this.forcedAggroTime + (int)(20.0F * this.intensity);
         }
      }
   }

   public void m_8056_() {
      super.m_8056_();
      this.forcedAggroTime = 40 + this.f_26135_.m_217043_().m_188503_(80) + this.f_26135_.m_217043_().m_188503_(80);
      this.intensity = 1.0F;
      this.isOutnumbered = false;
   }

   public boolean m_8045_() {
      return (!this.isOutnumbered || --this.forcedAggroTime > 0) && super.m_8045_();
   }
}
