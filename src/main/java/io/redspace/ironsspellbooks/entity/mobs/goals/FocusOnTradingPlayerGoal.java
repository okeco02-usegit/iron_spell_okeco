package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;

public class FocusOnTradingPlayerGoal<T extends PathfinderMob & IMerchantWizard> extends Goal {
   final T mob;
   int hurtTracker;

   public FocusOnTradingPlayerGoal(T mob) {
      this.m_7021_(EnumSet.of(Flag.LOOK, Flag.MOVE));
      this.mob = mob;
   }

   public boolean m_8036_() {
      return this.mob.m_7962_() != null;
   }

   public void m_8056_() {
      super.m_8056_();
      this.mob.m_21573_().m_26573_();
      this.hurtTracker = this.mob.m_21213_();
   }

   public void m_8037_() {
      Player player = this.mob.m_7962_();
      if (player != null && !player.m_21224_() && !player.m_213877_()) {
         this.mob.m_21563_().m_148051_(player);
         if (this.mob.m_21213_() != this.hurtTracker) {
            this.mob.stopTrading();
            this.m_8041_();
         }
      } else {
         this.m_8041_();
      }
   }
}
