package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class WarlockAttackGoal extends WizardAttackGoal {
   protected boolean wantsToMelee;
   protected int meleeTime;
   protected int meleeDecisionTime;
   protected float meleeBiasMin;
   protected float meleeBiasMax;
   protected float meleeMoveSpeedModifier;
   protected int meleeAttackIntervalMin;
   protected int meleeAttackIntervalMax;
   protected int meleeAttackDelay = -1;
   @Deprecated(forRemoval = true)
   protected float meleeRange;

   @Deprecated(forRemoval = true)
   public WarlockAttackGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval, float range) {
      this(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.meleeRange = range;
   }

   public WarlockAttackGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.meleeDecisionTime = this.mob.m_217043_().m_216332_(80, 200);
      this.meleeBiasMin = 0.25F;
      this.meleeBiasMax = 0.75F;
      this.allowFleeing = false;
      this.meleeMoveSpeedModifier = (float)pSpeedModifier;
      this.meleeAttackIntervalMin = minAttackInterval;
      this.meleeAttackIntervalMax = maxAttackInterval;
   }

   @Override
   public void m_8037_() {
      super.m_8037_();
      if (++this.meleeTime > this.meleeDecisionTime) {
         this.meleeTime = 0;
         this.wantsToMelee = this.mob.m_217043_().m_188501_() <= this.meleeBias();
         this.meleeDecisionTime = this.mob.m_217043_().m_216332_(60, 120);
      }
   }

   public float meleeRange() {
      return (float)(this.mob.m_21133_((Attribute)ForgeMod.ENTITY_REACH.get()) * this.mob.m_6134_());
   }

   protected float meleeBias() {
      return Mth.m_144920_(this.meleeBiasMin, this.meleeBiasMax, this.mob.m_21223_() / this.mob.m_21233_());
   }

   @Override
   protected void doMovement(double distanceSquared) {
      if (!this.wantsToMelee) {
         super.doMovement(distanceSquared);
      } else {
         if (this.target.m_21224_()) {
            this.mob.m_21573_().m_26573_();
         } else {
            float meleeRange = this.meleeRange();
            this.mob.m_21391_(this.target, 30.0F, 30.0F);
            float speed = (float)this.movementSpeed();
            if (distanceSquared > meleeRange * meleeRange) {
               this.mob.m_21570_(0.0F);
               if (this.mob.f_19797_ % 5 == 0) {
                  this.mob.m_21573_().m_5624_(this.target, this.meleeMoveSpeedModifier);
               }
            } else {
               this.mob.m_21573_().m_26573_();
               float strafeForwards = 0.5F * this.meleeMoveSpeedModifier * (4.0 * distanceSquared > meleeRange * meleeRange ? 1.5F : -1.0F);
               if (++this.strafeTime > 25 && this.mob.m_217043_().m_188500_() < 0.1) {
                  this.strafingClockwise = !this.strafingClockwise;
                  this.strafeTime = 0;
               }

               float strafeDir = this.strafingClockwise ? 1.0F : -1.0F;
               this.mob.m_21566_().m_24988_(strafeForwards, speed * strafeDir);
            }

            this.mob.m_21563_().m_148051_(this.target);
         }
      }
   }

   @Override
   public void m_8041_() {
      super.m_8041_();
      this.meleeAttackDelay = -1;
   }

   @Override
   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      if (this.wantsToMelee && !(distanceSquared > meleeRange * meleeRange) && !this.spellCastingMob.isCasting()) {
         if (--this.meleeAttackDelay <= 0) {
            this.mob.m_6674_(InteractionHand.MAIN_HAND);
            this.doMeleeAction();
         }
      } else {
         super.handleAttackLogic(distanceSquared);
      }
   }

   protected void doMeleeAction() {
      double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
      this.mob.m_7327_(this.target);
      this.resetMeleeAttackInterval(distanceSquared);
   }

   public WarlockAttackGoal setMeleeBias(float meleeBiasMin, float meleeBiasMax) {
      this.meleeBiasMin = meleeBiasMin;
      this.meleeBiasMax = meleeBiasMax;
      return this;
   }

   public WarlockAttackGoal setSpells(
      List<AbstractSpell> attackSpells, List<AbstractSpell> defenseSpells, List<AbstractSpell> movementSpells, List<AbstractSpell> supportSpells
   ) {
      return (WarlockAttackGoal)super.setSpells(attackSpells, defenseSpells, movementSpells, supportSpells);
   }

   public WarlockAttackGoal setSpellQuality(float minSpellQuality, float maxSpellQuality) {
      return (WarlockAttackGoal)super.setSpellQuality(minSpellQuality, maxSpellQuality);
   }

   public WarlockAttackGoal setSingleUseSpell(AbstractSpell spellType, int minDelay, int maxDelay, int minLevel, int maxLevel) {
      return (WarlockAttackGoal)super.setSingleUseSpell(spellType, minDelay, maxDelay, minLevel, maxLevel);
   }

   public WarlockAttackGoal setIsFlying() {
      return (WarlockAttackGoal)super.setIsFlying();
   }

   public WarlockAttackGoal setMeleeMovespeedModifier(float meleeMovespeedModifier) {
      this.meleeMoveSpeedModifier = meleeMovespeedModifier;
      return this;
   }

   public WarlockAttackGoal setMeleeAttackInverval(int min, int max) {
      this.meleeAttackIntervalMax = max;
      this.meleeAttackIntervalMin = min;
      return this;
   }

   @Override
   protected double movementSpeed() {
      return this.wantsToMelee ? this.meleeMoveSpeedModifier * this.mob.m_21133_(Attributes.f_22279_) * 2.0 : super.movementSpeed();
   }

   protected void resetMeleeAttackInterval(double distanceSquared) {
      float f = (float)Math.sqrt(distanceSquared) / this.spellcastingRange;
      this.meleeAttackDelay = Math.max(1, Mth.m_14143_(f * (this.meleeAttackIntervalMax - this.meleeAttackIntervalMin) + this.meleeAttackIntervalMin));
   }
}
