package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class WispAttackGoal extends Goal {
   private LivingEntity target;
   private PathfinderMob wisp;
   private double speedModifier;

   public WispAttackGoal(PathfinderMob wisp, double speedModifier) {
      this.wisp = wisp;
      this.speedModifier = speedModifier;
   }

   public boolean m_8036_() {
      LivingEntity livingentity = this.wisp.m_5448_();
      if (livingentity != null && livingentity.m_6084_()) {
         this.target = livingentity;
         return true;
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return this.m_8036_() || this.target.m_6084_() && !this.wisp.m_21573_().m_26571_();
   }

   public void m_8041_() {
      this.target = null;
   }

   public boolean m_183429_() {
      return true;
   }

   public void m_8037_() {
      double distanceSquared = this.target.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
      boolean hasLineOfSight = this.wisp.m_21574_().m_148306_(this.target);
      boolean moveResult = this.wisp.m_21573_().m_26519_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_(), this.speedModifier);
      this.wisp.m_21563_().m_24960_(this.target, 180.0F, 180.0F);
   }

   private void doAction() {
   }
}
