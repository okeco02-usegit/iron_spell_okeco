package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class SpellBarrageGoal extends Goal {
   protected static final int interval = 5;
   protected final PathfinderMob mob;
   protected final IMagicEntity spellCastingMob;
   protected LivingEntity target;
   protected final int attackIntervalMin;
   protected final int attackIntervalMax;
   protected final float attackRadius;
   protected final float attackRadiusSqr;
   protected final int projectileCount;
   protected final AbstractSpell spell;
   protected int attackTime;
   protected final int minSpellLevel;
   protected final int maxSpellLevel;

   public SpellBarrageGoal(
      IMagicEntity abstractSpellCastingMob,
      AbstractSpell spell,
      int minLevel,
      int maxLevel,
      int pAttackIntervalMin,
      int pAttackIntervalMax,
      int projectileCount
   ) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.spellCastingMob = abstractSpellCastingMob;
      if (abstractSpellCastingMob instanceof PathfinderMob m) {
         this.mob = m;
         this.attackIntervalMin = pAttackIntervalMin;
         this.attackIntervalMax = pAttackIntervalMax;
         this.attackRadius = 20.0F;
         this.attackRadiusSqr = this.attackRadius * this.attackRadius;
         this.minSpellLevel = minLevel;
         this.maxSpellLevel = maxLevel;
         this.projectileCount = projectileCount;
         this.spell = spell;
         this.resetAttackTimer();
      } else {
         throw new IllegalStateException("Unable to add " + this.getClass().getSimpleName() + "to entity, must extend PathfinderMob.");
      }
   }

   public boolean m_8036_() {
      this.target = this.mob.m_5448_();
      if (this.target != null && !this.spellCastingMob.isCasting()) {
         if (this.attackTime <= -5 * (this.projectileCount - 1)) {
            this.resetAttackTimer();
         }

         this.attackTime--;
         return this.attackTime <= 0 && this.attackTime % 5 == 0;
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return false;
   }

   public void m_8041_() {
      this.target = null;
      if (this.attackTime > 0) {
         this.attackTime = -this.projectileCount * 5 - 1;
      }
   }

   public boolean m_183429_() {
      return true;
   }

   public void m_8037_() {
      if (this.target != null) {
         double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
         if (distanceSquared < this.attackRadiusSqr) {
            this.mob.m_21563_().m_24960_(this.target, 45.0F, 45.0F);
            this.spellCastingMob.initiateCastSpell(this.spell, this.mob.m_217043_().m_216332_(this.minSpellLevel, this.maxSpellLevel));
            this.m_8041_();
         }
      }
   }

   protected void resetAttackTimer() {
      this.attackTime = this.mob.m_217043_().m_216332_(this.attackIntervalMin, this.attackIntervalMax);
   }
}
