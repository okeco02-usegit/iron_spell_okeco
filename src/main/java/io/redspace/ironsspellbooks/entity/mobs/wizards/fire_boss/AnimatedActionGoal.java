package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public abstract class AnimatedActionGoal<T extends Mob & IMagicEntity & IAnimatedAttacker> extends Goal {
   protected int abilityTimer;
   protected int delay;
   protected boolean isUsing;
   protected final T mob;

   public AnimatedActionGoal(T mob) {
      this.m_7021_(EnumSet.of(Flag.TARGET));
      this.mob = mob;
      this.delay = this.getCooldown();
   }

   public final boolean m_8036_() {
      return this.delay-- <= 0 && this.canStartAction();
   }

   public boolean m_8045_() {
      return this.isUsing;
   }

   public boolean m_183429_() {
      return true;
   }

   protected abstract boolean canStartAction();

   protected abstract int getActionTimestamp();

   protected abstract int getActionDuration();

   protected abstract int getCooldown();

   protected abstract String getAnimationId();

   protected abstract void doAction();

   public void m_8037_() {
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         this.mob.m_21563_().m_148051_(target);
      }

      if (this.abilityTimer == this.getActionTimestamp()) {
         this.doAction();
      }

      if (this.abilityTimer >= this.getActionDuration()) {
         this.m_8041_();
      }

      this.abilityTimer++;
   }

   public void m_8041_() {
      this.isUsing = false;
   }

   public void m_8056_() {
      this.isUsing = true;
      this.abilityTimer = 0;
      this.delay = this.getCooldown();
      this.mob.serverTriggerAnimation(this.getAnimationId());
   }
}
