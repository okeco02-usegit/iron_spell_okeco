package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WizardAttackGoal extends Goal {
   protected LivingEntity target;
   protected final double speedModifier;
   protected final int spellAttackIntervalMin;
   protected final int spellAttackIntervalMax;
   protected float spellcastingRange;
   protected float spellcastingRangeSqr;
   protected boolean shortCircuitTemp = false;
   protected boolean hasLineOfSight;
   protected int seeTime = 0;
   protected int strafeTime;
   protected boolean strafingClockwise;
   protected int spellAttackDelay = -1;
   protected int projectileCount;
   protected AbstractSpell singleUseSpell = SpellRegistry.none();
   protected int singleUseDelay;
   protected int singleUseLevel;
   protected boolean isFlying;
   protected boolean allowFleeing;
   protected int fleeCooldown;
   protected final ArrayList<AbstractSpell> attackSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> defenseSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> movementSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> supportSpells = new ArrayList<>();
   protected ArrayList<AbstractSpell> lastSpellCategory = this.attackSpells;
   protected float minSpellQuality = 0.1F;
   protected float maxSpellQuality = 0.4F;
   protected boolean drinksPotions;
   protected final PathfinderMob mob;
   protected final IMagicEntity spellCastingMob;
   @Deprecated
   protected int attackTime;

   @Deprecated
   public void resetAttackTimer(double d) {
      this.attackTime = this.mob.m_217043_().m_216339_(this.spellAttackIntervalMin, this.spellAttackIntervalMax);
   }

   public WizardAttackGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int pAttackInterval) {
      this(abstractSpellCastingMob, pSpeedModifier, pAttackInterval, pAttackInterval);
   }

   public WizardAttackGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.spellCastingMob = abstractSpellCastingMob;
      if (abstractSpellCastingMob instanceof PathfinderMob m) {
         this.mob = m;
         this.speedModifier = pSpeedModifier;
         this.spellAttackIntervalMin = pAttackIntervalMin;
         this.spellAttackIntervalMax = pAttackIntervalMax;
         this.spellcastingRange = 20.0F;
         this.spellcastingRangeSqr = this.spellcastingRange * this.spellcastingRange;
         this.allowFleeing = true;
      } else {
         throw new IllegalStateException("Unable to add " + this.getClass().getSimpleName() + "to entity, must extend PathfinderMob.");
      }
   }

   public WizardAttackGoal setSpells(
      List<AbstractSpell> attackSpells, List<AbstractSpell> defenseSpells, List<AbstractSpell> movementSpells, List<AbstractSpell> supportSpells
   ) {
      this.attackSpells.clear();
      this.defenseSpells.clear();
      this.movementSpells.clear();
      this.supportSpells.clear();
      this.attackSpells.addAll(attackSpells);
      this.defenseSpells.addAll(defenseSpells);
      this.movementSpells.addAll(movementSpells);
      this.supportSpells.addAll(supportSpells);
      return this;
   }

   public WizardAttackGoal setSpellQuality(float minSpellQuality, float maxSpellQuality) {
      this.minSpellQuality = minSpellQuality;
      this.maxSpellQuality = maxSpellQuality;
      return this;
   }

   public WizardAttackGoal setSingleUseSpell(AbstractSpell abstractSpell, int minDelay, int maxDelay, int minLevel, int maxLevel) {
      this.singleUseSpell = abstractSpell;
      this.singleUseDelay = Utils.random.m_216332_(minDelay, maxDelay);
      this.singleUseLevel = Utils.random.m_216332_(minLevel, maxLevel);
      return this;
   }

   public WizardAttackGoal setIsFlying() {
      this.isFlying = true;
      return this;
   }

   public WizardAttackGoal setDrinksPotions() {
      this.drinksPotions = true;
      return this;
   }

   public WizardAttackGoal setAllowFleeing(boolean allowFleeing) {
      this.allowFleeing = allowFleeing;
      return this;
   }

   public boolean m_8036_() {
      LivingEntity livingentity = this.mob.m_5448_();
      if (livingentity != null && livingentity.m_6084_()) {
         this.target = livingentity;
         return this.mob.m_6779_(this.target);
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return this.m_8036_();
   }

   public void m_8041_() {
      this.target = null;
      this.seeTime = 0;
      this.spellAttackDelay = -1;
      this.mob.m_21561_(false);
      this.mob.m_21566_().m_24988_(0.0F, 0.0F);
      this.mob.m_21573_().m_26573_();
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
            this.seeTime--;
         }

         this.doMovement(distanceSquared);
         if (this.mob.m_21213_() == this.mob.f_19797_ - 1) {
            this.spellAttackDelay = (int)(Mth.m_14179_(0.6F, this.spellAttackDelay, 0.0F) + 1.0F);
         }

         this.handleAttackLogic(distanceSquared);
         this.singleUseDelay--;
      }
   }

   protected void handleAttackLogic(double distanceSquared) {
      if (this.seeTime >= -50) {
         if (--this.spellAttackDelay == 0) {
            this.resetSpellAttackTimer(distanceSquared);
            if (!this.spellCastingMob.isCasting() && !this.spellCastingMob.isDrinkingPotion()) {
               this.doSpellAction();
            }
         } else if (this.spellAttackDelay < 0) {
            this.resetSpellAttackTimer(distanceSquared);
         }

         if (this.spellCastingMob.isCasting()) {
            SpellData spellData = MagicData.getPlayerMagicData(this.mob).getCastingSpell();
            if (this.target.m_21224_() || spellData.getSpell().shouldAIStopCasting(spellData.getLevel(), this.mob, this.target)) {
               this.spellCastingMob.cancelCast();
            }
         }
      }
   }

   public boolean isActing() {
      return this.spellCastingMob.isCasting() || this.spellCastingMob.isDrinkingPotion();
   }

   protected void resetSpellAttackTimer(double distanceSquared) {
      float f = (float)Math.sqrt(distanceSquared) / this.spellcastingRange;
      this.spellAttackDelay = Math.max(1, Mth.m_14143_(f * (this.spellAttackIntervalMax - this.spellAttackIntervalMin) + this.spellAttackIntervalMin));
   }

   protected void doMovement(double distanceSquared) {
      double speed = (this.spellCastingMob.isCasting() ? 0.75F : 1.0F) * this.movementSpeed();
      this.mob.m_21391_(this.target, 30.0F, 30.0F);
      float fleeDist = 0.275F;
      float ss = this.getStrafeMultiplier();
      if (this.allowFleeing
         && !this.spellCastingMob.isCasting()
         && this.spellAttackDelay > 10
         && --this.fleeCooldown <= 0
         && distanceSquared < this.spellcastingRangeSqr * (fleeDist * fleeDist)) {
         Vec3 flee = DefaultRandomPos.m_148407_(this.mob, 16, 7, this.target.m_20182_());
         if (flee != null) {
            this.mob.m_21573_().m_26519_(flee.f_82479_, flee.f_82480_, flee.f_82481_, speed * 1.5);
         } else {
            this.mob.m_21566_().m_24988_(-((float)speed) * ss, (float)speed * ss);
         }
      } else if (distanceSquared < this.spellcastingRangeSqr && this.seeTime >= 5) {
         this.mob.m_21573_().m_26573_();
         if (++this.strafeTime > 25 && this.mob.m_217043_().m_188500_() < 0.1) {
            this.strafingClockwise = !this.strafingClockwise;
            this.strafeTime = 0;
         }

         float strafeForward = (distanceSquared * 6.0 < this.spellcastingRangeSqr ? -1.0F : 0.5F) * 0.2F * (float)this.speedModifier;
         int strafeDir = this.strafingClockwise ? 1 : -1;
         this.mob.m_21566_().m_24988_(strafeForward * ss, (float)speed * strafeDir * ss);
         if (this.mob.f_19862_ && this.mob.m_217043_().m_188501_() < 0.1F) {
            this.tryJump();
         }
      } else if (this.mob.f_19797_ % 5 == 0) {
         if (this.isFlying) {
            this.mob.m_21566_().m_6849_(this.target.m_20185_(), this.target.m_20186_() + 2.0, this.target.m_20189_(), this.speedModifier);
         } else {
            this.mob.m_21573_().m_5624_(this.target, this.speedModifier);
         }
      }
   }

   protected double movementSpeed() {
      return this.speedModifier * this.mob.m_21133_(Attributes.f_22279_) * 2.0;
   }

   protected void tryJump() {
      Vec3 nextBlock = new Vec3(this.mob.f_20900_, 0.0, this.mob.f_20902_).m_82541_();
      BlockPos blockpos = BlockPos.m_274446_(this.mob.m_20182_().m_82549_(nextBlock));
      BlockState blockstate = this.mob.f_19853_.m_8055_(blockpos);
      VoxelShape voxelshape = blockstate.m_60812_(this.mob.f_19853_, blockpos);
      if (!voxelshape.m_83281_() && !blockstate.m_204336_(BlockTags.f_13103_) && !blockstate.m_204336_(BlockTags.f_13039_)) {
         BlockPos blockposAbove = blockpos.m_7494_();
         BlockState blockstateAbove = this.mob.f_19853_.m_8055_(blockposAbove);
         VoxelShape voxelshapeAbove = blockstateAbove.m_60812_(this.mob.f_19853_, blockposAbove);
         if (voxelshapeAbove.m_83281_()) {
            this.mob.m_21569_().m_24901_();
            this.mob.m_21570_(this.mob.f_20900_ * 5.0F);
            this.mob.m_21564_(this.mob.f_20902_ * 5.0F);
         }
      }
   }

   protected void doSpellAction() {
      if (!this.spellCastingMob.getHasUsedSingleAttack() && this.singleUseSpell != SpellRegistry.none() && this.singleUseDelay <= 0) {
         this.spellCastingMob.setHasUsedSingleAttack(true);
         this.spellCastingMob.initiateCastSpell(this.singleUseSpell, this.singleUseLevel);
         this.fleeCooldown = 7 + this.singleUseSpell.getCastTime(this.singleUseLevel);
      } else {
         AbstractSpell spell = this.getNextSpellType();
         int spellLevel = (int)(spell.getMaxLevel() * Mth.m_14179_(this.mob.m_217043_().m_188501_(), this.minSpellQuality, this.maxSpellQuality));
         spellLevel = Math.max(spellLevel, 1);
         if (!spell.shouldAIStopCasting(spellLevel, this.mob, this.target)) {
            this.spellCastingMob.initiateCastSpell(spell, spellLevel);
            this.fleeCooldown = 7 + spell.getCastTime(spellLevel);
         } else {
            this.spellAttackDelay = 5;
         }
      }
   }

   protected AbstractSpell getNextSpellType() {
      NavigableMap<Integer, ArrayList<AbstractSpell>> weightedSpells = new TreeMap<>();
      int attackWeight = this.getAttackWeight();
      int defenseWeight = this.getDefenseWeight() - (this.lastSpellCategory == this.defenseSpells ? 100 : 0);
      int movementWeight = this.getMovementWeight() - (this.lastSpellCategory == this.movementSpells ? 50 : 0);
      int supportWeight = this.getSupportWeight() - (this.lastSpellCategory == this.supportSpells ? 100 : 0);
      int total = 0;
      if (!this.attackSpells.isEmpty() && attackWeight > 0) {
         total += attackWeight;
         weightedSpells.put(total, this.attackSpells);
      }

      if (!this.defenseSpells.isEmpty() && defenseWeight > 0) {
         total += defenseWeight;
         weightedSpells.put(total, this.defenseSpells);
      }

      if (!this.movementSpells.isEmpty() && movementWeight > 0) {
         total += movementWeight;
         weightedSpells.put(total, this.movementSpells);
      }

      if ((!this.supportSpells.isEmpty() || this.drinksPotions) && supportWeight > 0) {
         total += supportWeight;
         weightedSpells.put(total, this.supportSpells);
      }

      if (total <= 0) {
         return SpellRegistry.none();
      }

      int seed = this.mob.m_217043_().m_188503_(total);
      ArrayList<AbstractSpell> spellList = weightedSpells.higherEntry(seed).getValue();
      this.lastSpellCategory = spellList;
      if (!this.drinksPotions || spellList != this.supportSpells || !this.supportSpells.isEmpty() && !(this.mob.m_217043_().m_188501_() < 0.5F)) {
         return spellList.get(this.mob.m_217043_().m_188503_(spellList.size()));
      }

      this.spellCastingMob.startDrinkingPotion();
      return SpellRegistry.none();
   }

   public void m_8056_() {
      super.m_8056_();
      this.mob.m_21561_(true);
   }

   protected int getAttackWeight() {
      int baseWeight = 80;
      if (this.hasLineOfSight && this.target != null) {
         float targetHealth = this.target.m_21223_() / this.target.m_21233_();
         int targetHealthWeight = (int)((1.0F - targetHealth) * baseWeight * 0.75F);
         double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
         int distanceWeight = (int)(1.0 - distanceSquared / this.spellcastingRangeSqr * -60.0);
         return baseWeight + targetHealthWeight + distanceWeight;
      } else {
         return 0;
      }
   }

   protected int getDefenseWeight() {
      int baseWeight = -20;
      if (this.target == null) {
         return baseWeight;
      }

      float x = this.mob.m_21223_();
      float m = this.mob.m_21233_();
      int healthWeight = (int)(50.0F * (-(x * x * x) / (m * m * m) + 1.0F));
      float targetHealth = this.target.m_21223_() / this.target.m_21233_();
      int targetHealthWeight = (int)(1.0F - targetHealth) * -35;
      int threatWeight = this.projectileCount * 95;
      return baseWeight + healthWeight + targetHealthWeight + threatWeight;
   }

   protected int getMovementWeight() {
      if (this.target == null) {
         return 0;
      }

      double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
      double distancePercent = Mth.m_14008_(distanceSquared / this.spellcastingRangeSqr, 0.0, 1.0);
      int distanceWeight = (int)(distancePercent * 50.0);
      int losWeight = this.hasLineOfSight ? 0 : 80;
      float healthInverted = 1.0F - this.mob.m_21223_() / this.mob.m_21233_();
      float distanceInverted = (float)(1.0 - distancePercent);
      int runWeight = (int)(400.0F * healthInverted * healthInverted * distanceInverted * distanceInverted);
      return distanceWeight + losWeight + runWeight;
   }

   protected int getSupportWeight() {
      int baseWeight = -15;
      if (this.target == null) {
         return baseWeight;
      }

      float health = 1.0F - this.mob.m_21223_() / this.mob.m_21233_();
      int healthWeight = (int)(200.0F * health);
      double distanceSquared = this.mob.m_20275_(this.target.m_20185_(), this.target.m_20186_(), this.target.m_20189_());
      double distancePercent = Mth.m_14008_(distanceSquared / this.spellcastingRangeSqr, 0.0, 1.0);
      int distanceWeight = (int)((1.0 - distancePercent) * -75.0);
      return baseWeight + healthWeight + distanceWeight;
   }

   public boolean m_6767_() {
      return !this.isActing();
   }

   public float getStrafeMultiplier() {
      return 1.0F;
   }
}
