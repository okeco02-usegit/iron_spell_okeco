package io.redspace.ironsspellbooks.entity.mobs.wizards;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class GenericAnimatedWarlockAttackGoal<T extends PathfinderMob & IAnimatedAttacker & IMagicEntity> extends WarlockAttackGoal {
   protected List<AttackAnimationData> moveList = new ArrayList<>();
   protected final T mob;
   protected int meleeAnimTimer = -1;
   @Nullable
   public AttackAnimationData currentAttack;
   @Nullable
   public AttackAnimationData nextAttack;
   @Nullable
   public AttackAnimationData queueCombo;
   float comboChance = 0.3F;

   public GenericAnimatedWarlockAttackGoal(T abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.wantsToMelee = true;
      this.mob = abstractSpellCastingMob;
   }

   @Override
   public boolean isActing() {
      return super.isActing() || this.isMeleeing();
   }

   public boolean isMeleeing() {
      return this.meleeAnimTimer > 0;
   }

   @Override
   public void m_8056_() {
      super.m_8056_();
      this.nextAttack = this.getNextAttack(0.0F);
   }

   @Override
   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      float rangeMultiplier = this.nextAttack == null ? 1.0F : this.nextAttack.rangeMultiplier;
      float procRangeSqr = meleeRange * meleeRange * rangeMultiplier * rangeMultiplier * 1.2F * 1.2F;
      if (this.meleeAnimTimer >= 0 || this.wantsToMelee && !(distanceSquared > procRangeSqr) && !this.mob.isCasting()) {
         this.mob.m_21563_().m_148051_(this.target);
         if (this.meleeAnimTimer > 0 && this.currentAttack != null) {
            this.forceFaceTarget();
            this.meleeAnimTimer--;
            if (this.currentAttack.isHitFrame(this.meleeAnimTimer)) {
               AttackKeyframe attackData = this.currentAttack.getHitFrame(this.meleeAnimTimer);
               this.onHitFrame(attackData, meleeRange);
            }

            if (this.currentAttack.canCancel
               && (
                  this.currentAttack.isSingleHit()
                     || this.currentAttack.lengthInTicks - this.meleeAnimTimer > this.currentAttack.attacks.keySet().intStream().sorted().findFirst().orElse(0)
               )) {
               Vec3 delta = this.mob.m_20182_().m_82546_(this.target.m_20182_());
               double modifiedDistanceSquared = delta.f_82479_ * delta.f_82479_ + delta.f_82480_ * delta.f_82480_ * 0.5 * 0.5 + delta.f_82481_ * delta.f_82481_;
               if (modifiedDistanceSquared > meleeRange * meleeRange * 1.5 * 1.5) {
                  this.stopMeleeAction();
               }
            }
         } else if (this.queueCombo != null && this.target != null && !this.target.m_21224_()) {
            this.nextAttack = this.queueCombo;
            this.queueCombo = null;
            this.doMeleeAction();
         } else if (this.meleeAnimTimer == 0) {
            this.nextAttack = this.getNextAttack((float)distanceSquared);
            this.resetMeleeAttackInterval(distanceSquared);
            this.meleeAnimTimer = -1;
         } else if (distanceSquared < procRangeSqr) {
            if (this.hasLineOfSight && --this.meleeAttackDelay == 0) {
               this.doMeleeAction();
            } else if (this.meleeAttackDelay < 0) {
               this.resetMeleeAttackInterval(distanceSquared);
            }
         }
      } else {
         super.handleAttackLogic(distanceSquared);
      }
   }

   protected void onHitFrame(AttackKeyframe attackKeyframe, float meleeRange) {
      this.playSwingSound();
      float f = -Utils.getAngle(this.mob.m_20185_(), this.mob.m_20189_(), this.target.m_20185_(), this.target.m_20189_()) - (float) (Math.PI / 2);
      Vec3 lunge = attackKeyframe.lungeVector().m_82524_(f);
      this.doLunge(lunge, meleeRange);
      Vec3 forward = this.mob.m_20156_();

      for (LivingEntity target : this.currentAttack.areaAttackThreshold.isEmpty()
         ? List.of(this.target)
         : this.mob
            .f_19853_
            .m_6443_(
               this.target.getClass(),
               this.mob.m_20191_().m_82400_(this.spellcastingRange),
               entity -> forward.m_82526_(entity.m_20182_().m_82546_(this.mob.m_20182_()).m_82541_())
                  >= this.currentAttack.areaAttackThreshold.get().floatValue()
            )) {
         if (target.m_20280_(this.mob) <= meleeRange * meleeRange && Utils.hasLineOfSight(this.mob.f_19853_, this.mob, target, true)) {
            this.handleDamaging(target, attackKeyframe);
         }
      }
   }

   protected void doLunge(Vec3 vector, float meleeRange) {
      this.mob.m_5997_(vector.f_82479_, vector.f_82480_, vector.f_82481_);
   }

   protected boolean handleDamaging(LivingEntity target, AttackKeyframe attackData) {
      boolean flag = this.mob.m_7327_(target);
      target.f_19802_ = 0;
      float f = -Utils.getAngle(this.mob.m_20185_(), this.mob.m_20189_(), target.m_20185_(), target.m_20189_()) - (float) (Math.PI / 2);
      if (flag) {
         this.playImpactSound();
         if (attackData.extraKnockback() != Vec3.f_82478_) {
            target.m_20256_(target.m_20184_().m_82549_(attackData.extraKnockback().m_82524_(f)));
         }

         if (this.currentAttack.isSingleHit() && this.mob.m_217043_().m_188501_() < this.comboChance * (target.m_21254_() ? 2 : 1)) {
            this.queueCombo = this.getNextAttack(0.0F);
         }
      }

      return flag;
   }

   protected AttackAnimationData getNextAttack(float distanceSquared) {
      return this.moveList.isEmpty() ? null : this.moveList.get(this.mob.m_217043_().m_188503_(this.moveList.size()));
   }

   private void forceFaceTarget() {
      double d0 = this.target.m_20185_() - this.mob.m_20185_();
      double d1 = this.target.m_20189_() - this.mob.m_20189_();
      float yRot = (float)(Mth.m_14136_(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
      this.mob.m_5618_(yRot);
      this.mob.m_5616_(yRot);
      this.mob.m_146922_(yRot);
   }

   public void stopMeleeAction() {
      if (this.currentAttack != null) {
         this.meleeAnimTimer = 0;
         PacketDistributor.sendToPlayersTrackingEntity(this.mob, new SyncAnimationPacket("", this.mob));
      }
   }

   @Override
   protected void doMeleeAction() {
      this.currentAttack = this.nextAttack;
      if (this.currentAttack != null) {
         this.mob.m_6674_(InteractionHand.MAIN_HAND);
         this.meleeAnimTimer = this.currentAttack.lengthInTicks;
         PacketDistributor.sendToPlayersTrackingEntity(this.mob, new SyncAnimationPacket(this.currentAttack.animationId, this.mob));
      }
   }

   @Override
   public boolean m_8045_() {
      return super.m_8045_() || this.meleeAnimTimer > 0;
   }

   @Override
   public void m_8041_() {
      super.m_8041_();
      this.meleeAnimTimer = -1;
      this.queueCombo = null;
   }

   public void playSwingSound() {
      this.mob.m_5496_((SoundEvent)SoundRegistry.GENERIC_BLADE_SWING.get(), 1.0F, Mth.m_216287_(this.mob.m_217043_(), 12, 18) * 0.1F);
   }

   public void playImpactSound() {
   }

   public GenericAnimatedWarlockAttackGoal<T> setMoveset(List<AttackAnimationData> moveset) {
      this.moveList = moveset;
      this.nextAttack = this.getNextAttack(0.0F);
      return this;
   }

   public GenericAnimatedWarlockAttackGoal<T> setComboChance(float comboChance) {
      this.comboChance = comboChance;
      return this;
   }
}
