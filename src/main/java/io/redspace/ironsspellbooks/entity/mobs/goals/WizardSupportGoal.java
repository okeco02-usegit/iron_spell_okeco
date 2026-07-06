package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class WizardSupportGoal<T extends PathfinderMob & SupportMob & IMagicEntity> extends Goal {
   protected final T mob;
   protected LivingEntity target;
   protected final double speedModifier;
   protected final int attackIntervalMin;
   protected final int attackIntervalMax;
   protected final float attackRadius;
   protected final float attackRadiusSqr;
   protected boolean shortCircuitTemp = false;
   protected boolean hasLineOfSight;
   protected int seeTime = 0;
   protected int attackTime = 0;
   protected boolean isFlying;
   protected final ArrayList<AbstractSpell> healingSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> buffSpells = new ArrayList<>();
   protected float minSpellQuality = 0.1F;
   protected float maxSpellQuality = 0.3F;

   public WizardSupportGoal(T abstractSpellCastingMob, double pSpeedModifier, int pAttackInterval) {
      this(abstractSpellCastingMob, pSpeedModifier, pAttackInterval, pAttackInterval);
   }

   public WizardSupportGoal(T abstractSpellCastingMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax) {
      this.mob = abstractSpellCastingMob;
      this.speedModifier = pSpeedModifier;
      this.attackIntervalMin = pAttackIntervalMin;
      this.attackIntervalMax = pAttackIntervalMax;
      this.attackRadius = 20.0F;
      this.attackRadiusSqr = this.attackRadius * this.attackRadius;
   }

   public WizardSupportGoal<T> setSpells(List<AbstractSpell> healingSpells, List<AbstractSpell> buffSpells) {
      this.healingSpells.clear();
      this.buffSpells.clear();
      this.healingSpells.addAll(healingSpells);
      this.buffSpells.addAll(buffSpells);
      return this;
   }

   public WizardSupportGoal<T> setSpellQuality(float minSpellQuality, float maxSpellQuality) {
      this.minSpellQuality = minSpellQuality;
      this.maxSpellQuality = maxSpellQuality;
      return this;
   }

   public WizardSupportGoal<T> setIsFlying() {
      this.isFlying = true;
      return this;
   }

   public boolean m_8036_() {
      LivingEntity livingentity = this.mob.getSupportTarget();
      if (livingentity != null && livingentity.m_6084_() && Utils.shouldHealEntity(this.mob, livingentity)) {
         this.target = livingentity;
         return true;
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return this.m_8036_() || this.target.m_6084_() && !this.mob.m_21573_().m_26571_() && Utils.shouldHealEntity(this.mob, this.target);
   }

   public void m_8041_() {
      this.target = null;
      this.seeTime = 0;
      this.attackTime = -1;
   }

   public boolean m_183429_() {
      return true;
   }

   public void m_8037_() {
      if (this.target != null) {
         double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
         this.hasLineOfSight = this.mob.m_21574_().m_148306_(this.target);
         if (this.hasLineOfSight) {
            this.seeTime++;
         } else {
            this.seeTime = 0;
         }

         this.doMovement(distanceSquared);
         this.handleAttackLogic(distanceSquared);
      }
   }

   protected void handleAttackLogic(double distanceSquared) {
      if (--this.attackTime == 0) {
         if (!this.mob.isCasting()) {
            this.mob.m_21391_(this.target, 180.0F, 180.0F);
            this.doSpellAction();
         }

         this.resetAttackTimer(distanceSquared);
      }

      if (this.mob.isCasting()) {
         SpellData spellData = MagicData.getPlayerMagicData(this.mob).getCastingSpell();
         if (this.target.m_21224_() || spellData.getSpell().shouldAIStopCasting(spellData.getLevel(), this.mob, this.target)) {
            this.mob.cancelCast();
         }
      }
   }

   protected void resetAttackTimer(double distanceSquared) {
      float f = (float)Math.sqrt(distanceSquared) / this.attackRadius;
      this.attackTime = (int)(f * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin);
   }

   protected void doMovement(double distanceSquared) {
      float movementDebuff = this.mob.isCasting() ? 0.2F : 1.0F;
      double effectiveSpeed = movementDebuff * this.speedModifier;
      if (distanceSquared < this.attackRadiusSqr && this.seeTime >= 5) {
         this.mob.m_21573_().m_26573_();
         this.mob.m_21391_(this.target, 30.0F, 30.0F);
      } else if (this.isFlying) {
         this.mob.m_21566_().m_6849_(this.target.m_20185_(), this.target.m_20186_() + 2.0, this.target.m_20189_(), this.speedModifier);
      } else {
         this.mob.m_21573_().m_5624_(this.target, effectiveSpeed);
      }
   }

   protected void doSpellAction() {
      int spellLevel = (int)(this.getNextSpellType().getMaxLevel() * Mth.m_14179_(this.mob.m_217043_().m_188501_(), this.minSpellQuality, this.maxSpellQuality));
      spellLevel = Math.max(spellLevel, 1);
      AbstractSpell abstractSpell = this.getNextSpellType();
      if (!abstractSpell.shouldAIStopCasting(spellLevel, this.mob, this.target)) {
         this.mob.initiateCastSpell(abstractSpell, spellLevel);
      }

      this.mob.setSupportTarget(null);
   }

   protected AbstractSpell getNextSpellType() {
      float shouldBuff = 0.0F;
      if (!this.buffSpells.isEmpty() && this.target instanceof Mob mob && mob.m_5912_()) {
         shouldBuff = this.target.m_21223_() / this.target.m_21233_();
      }

      return this.getSpell(this.mob.m_217043_().m_188501_() > shouldBuff ? this.healingSpells : this.buffSpells);
   }

   protected AbstractSpell getSpell(List<AbstractSpell> spells) {
      return spells.isEmpty() ? SpellRegistry.none() : spells.get(this.mob.m_217043_().m_188503_(spells.size()));
   }

   public void m_8056_() {
      super.m_8056_();
   }
}
