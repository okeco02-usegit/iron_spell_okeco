package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class KeeperAnimatedWarlockAttackGoal extends WarlockAttackGoal {
   final KeeperEntity keeper;
   int meleeAnimTimer = -1;
   public KeeperEntity.AttackType currentAttack;
   public KeeperEntity.AttackType nextAttack;
   public KeeperEntity.AttackType queueCombo;
   private boolean hasLunged;
   private boolean hasHitLunge;
   private Vec3 oldLungePos;

   public KeeperAnimatedWarlockAttackGoal(KeeperEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.keeper = abstractSpellCastingMob;
      this.nextAttack = this.randomizeNextAttack(0.0F);
      this.wantsToMelee = true;
   }

   @Override
   protected float meleeBias() {
      return 1.0F;
   }

   @Override
   public boolean isActing() {
      return super.isActing() || this.meleeAnimTimer > 0;
   }

   @Override
   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      float distance = Mth.m_14116_((float)distanceSquared);
      this.mob.m_21563_().m_148051_(this.target);
      if (this.meleeAnimTimer > 0) {
         this.forceFaceTarget();
         this.meleeAnimTimer--;
         if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer - 4)) {
            if (this.currentAttack != KeeperEntity.AttackType.Lunge) {
               this.playSwingSound();
            }
         } else if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
            if (this.currentAttack != KeeperEntity.AttackType.Lunge) {
               Vec3 lunge = this.target.m_20182_().m_82546_(this.mob.m_20182_()).m_82541_().m_82490_(0.55F);
               this.mob.m_5997_(lunge.f_82479_, lunge.f_82480_, lunge.f_82481_);
               if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.f_19853_, this.mob, this.target, true)) {
                  boolean flag = this.mob.m_7327_(this.target);
                  this.target.f_19802_ = 0;
                  if (flag) {
                     this.playImpactSound();
                     if (this.currentAttack.data.isSingleHit() && (this.mob.m_217043_().m_188501_() < 0.75F || this.target.m_21254_())) {
                        this.queueCombo = this.randomizeNextAttack(0.0F);
                     }
                  }
               }
            } else {
               if (!this.hasLunged) {
                  Vec3 lunge = this.target.m_20182_().m_82546_(this.mob.m_20182_()).m_82541_().m_82542_(2.4, 0.5, 2.4).m_82520_(0.0, 0.15, 0.0);
                  this.mob.m_5997_(lunge.f_82479_, lunge.f_82480_, lunge.f_82481_);
                  this.oldLungePos = this.mob.m_20182_();
                  this.mob.m_21573_().m_26573_();
                  this.hasLunged = true;
                  this.playSwingSound();
               }

               if (!this.hasHitLunge && distance <= meleeRange * 0.45F) {
                  if (this.mob.m_7327_(this.target)) {
                     this.playImpactSound();
                  }

                  Vec3 knockback = this.oldLungePos.m_82546_(this.target.m_20182_());
                  this.target.m_147240_(1.0, knockback.f_82479_, knockback.f_82481_);
                  this.hasHitLunge = true;
               }
            }
         }
      } else if (this.queueCombo != null && this.target != null && !this.target.m_21224_()) {
         this.nextAttack = this.queueCombo;
         this.queueCombo = null;
         this.doMeleeAction();
      } else if (this.meleeAnimTimer == 0) {
         this.nextAttack = this.randomizeNextAttack(distance);
         this.resetMeleeAttackInterval(distanceSquared);
         this.meleeAnimTimer = -1;
      } else if (distance < meleeRange * (this.nextAttack == KeeperEntity.AttackType.Lunge ? 3 : 1)) {
         if (this.hasLineOfSight && --this.meleeAttackDelay == 0) {
            this.doMeleeAction();
         } else if (this.meleeAttackDelay < 0) {
            this.resetMeleeAttackInterval(distanceSquared);
         }
      } else if (--this.meleeAttackDelay < 0) {
         this.resetMeleeAttackInterval(distanceSquared);
         this.nextAttack = this.randomizeNextAttack(distance);
      }
   }

   private KeeperEntity.AttackType randomizeNextAttack(float distance) {
      float meleeRange = this.meleeRange();
      int i;
      if (distance < meleeRange * 1.5F) {
         i = KeeperEntity.AttackType.values().length - 1;
      } else {
         if (this.mob.m_217043_().m_188501_() < 0.25F && distance > meleeRange * 2.5F) {
            return KeeperEntity.AttackType.Lunge;
         }

         i = KeeperEntity.AttackType.values().length;
      }

      return KeeperEntity.AttackType.values()[this.mob.m_217043_().m_188503_(i)];
   }

   private void forceFaceTarget() {
      if (!this.hasLunged) {
         double d0 = this.target.m_20185_() - this.mob.m_20185_();
         double d1 = this.target.m_20189_() - this.mob.m_20189_();
         float yRot = (float)(Mth.m_14136_(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
         this.mob.m_5618_(yRot);
         this.mob.m_5616_(yRot);
         this.mob.m_146922_(yRot);
      }
   }

   @Override
   protected void doMeleeAction() {
      this.currentAttack = this.nextAttack;
      if (this.currentAttack != null) {
         this.mob.m_6674_(InteractionHand.MAIN_HAND);
         this.meleeAnimTimer = this.currentAttack.data.lengthInTicks;
         this.hasLunged = false;
         this.hasHitLunge = false;
         PacketDistributor.sendToPlayersTrackingEntity(this.keeper, new SyncAnimationPacket(this.currentAttack.toString(), this.keeper));
      }
   }

   @Override
   protected void doMovement(double distanceSquared) {
      float meleeRange = this.meleeRange();
      if (this.target.m_21224_()) {
         this.mob.m_21573_().m_26573_();
      } else if (distanceSquared > meleeRange * meleeRange) {
         this.mob.m_21573_().m_5624_(this.target, this.speedModifier * 1.3F);
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
      this.mob.m_5496_((SoundEvent)SoundRegistry.KEEPER_SWING.get(), 1.0F, Mth.m_216287_(this.mob.m_217043_(), 9, 13) * 0.1F);
   }

   public void playImpactSound() {
      this.mob.m_5496_((SoundEvent)SoundRegistry.KEEPER_SWORD_IMPACT.get(), 1.0F, Mth.m_216287_(this.mob.m_217043_(), 9, 13) * 0.1F);
   }
}
