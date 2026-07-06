package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DeadKingAnimatedWarlockAttackGoal extends WarlockAttackGoal {
   final DeadKingBoss deadKing;
   int meleeAnimTimer = -1;
   public DeadKingBoss.AttackType currentAttack;
   public DeadKingBoss.AttackType nextAttack;
   public DeadKingBoss.AttackType queueCombo;

   public DeadKingAnimatedWarlockAttackGoal(DeadKingBoss abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.deadKing = abstractSpellCastingMob;
      this.nextAttack = this.randomizeNextAttack(0.0F);
      this.wantsToMelee = true;
   }

   @Deprecated(forRemoval = true)
   public DeadKingAnimatedWarlockAttackGoal(
      DeadKingBoss abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval, float range
   ) {
      this(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
   }

   @Override
   public boolean isActing() {
      return super.isActing() || this.meleeAnimTimer > 0;
   }

   @Override
   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      if (this.meleeAnimTimer >= 0 || this.wantsToMelee && !(distanceSquared > meleeRange * meleeRange) && !this.spellCastingMob.isCasting()) {
         this.mob.m_21563_().m_148051_(this.target);
         this.deadKing.isMeleeing = this.meleeAnimTimer > 0;
         if (this.meleeAnimTimer > 0) {
            this.forceFaceTarget();
            this.meleeAnimTimer--;
            if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer - 4)) {
               if (this.currentAttack == DeadKingBoss.AttackType.SLAM) {
                  this.mob.m_216990_((SoundEvent)SoundRegistry.DEAD_KING_SLAM.get());
               } else {
                  this.playSwingSound();
               }
            } else if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
               Vec3 lunge = this.target.m_20182_().m_82546_(this.mob.m_20182_()).m_82541_().m_82490_(0.35F);
               this.mob.m_5997_(lunge.f_82479_, lunge.f_82480_, lunge.f_82481_);
               if (this.currentAttack == DeadKingBoss.AttackType.SLAM) {
                  Vec3 slamPos = this.mob.m_20182_().m_82549_(this.mob.m_20156_().m_82542_(1.0, 0.0, 1.0).m_82541_().m_82490_(2.5));
                  Vec3 bbHalf = new Vec3(meleeRange, meleeRange, meleeRange).m_82490_(0.3);
                  float damage = (float)this.mob.m_21133_(Attributes.f_22281_) * 1.5F;
                  this.mob
                     .f_19853_
                     .m_45976_(LivingEntity.class, new AABB(slamPos.m_82546_(bbHalf), slamPos.m_82549_(bbHalf)))
                     .forEach(
                        entity -> {
                           if (entity.m_6087_() && !DamageSources.isFriendlyFireBetween(this.mob, entity)) {
                              entity.m_6469_(this.mob.m_9236_().m_269111_().m_269333_(this.mob), damage);
                              Vec3 impulse = slamPos.m_82546_(entity.m_20182_())
                                 .m_82520_(0.0, 0.75, 0.0)
                                 .m_82541_()
                                 .m_82490_(Mth.m_14139_(entity.m_20238_(this.mob.m_20182_()) / (meleeRange * meleeRange), 2.0, 0.5));
                              entity.m_20256_(entity.m_20184_().m_82549_(impulse));
                              entity.f_19864_ = true;
                              if (entity instanceof Player player && player.m_21254_()) {
                                 player.m_36384_(true);
                              }
                           }
                        }
                     );
               } else if (distanceSquared <= meleeRange * meleeRange && Utils.hasLineOfSight(this.mob.f_19853_, this.mob, this.target, true)) {
                  boolean flag = this.mob.m_7327_(this.target);
                  this.target.f_19802_ = 0;
                  if (flag && this.currentAttack.data.isSingleHit() && (this.mob.m_217043_().m_188501_() < 0.75F || this.target.m_21254_())) {
                     this.queueCombo = this.randomizeNextAttack(0.0F);
                  }
               }
            }
         } else if (this.queueCombo != null && this.target != null && !this.target.m_21224_()) {
            this.nextAttack = this.queueCombo;
            this.queueCombo = null;
            this.doMeleeAction();
         } else if (this.meleeAnimTimer == 0) {
            this.nextAttack = this.randomizeNextAttack((float)distanceSquared);
            this.resetMeleeAttackInterval(distanceSquared);
            this.meleeAnimTimer = -1;
         } else if (distanceSquared < meleeRange * meleeRange * 1.2 * 1.2) {
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

   private DeadKingBoss.AttackType randomizeNextAttack(float distanceSquared) {
      return this.mob.m_217043_().m_188501_() < 0.3F ? DeadKingBoss.AttackType.SLAM : DeadKingBoss.AttackType.DOUBLE_SWING;
   }

   private void forceFaceTarget() {
      double d0 = this.target.m_20185_() - this.mob.m_20185_();
      double d1 = this.target.m_20189_() - this.mob.m_20189_();
      float yRot = (float)(Mth.m_14136_(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
      this.mob.m_5618_(yRot);
      this.mob.m_5616_(yRot);
      this.mob.m_146922_(yRot);
   }

   @Override
   protected void doMeleeAction() {
      this.currentAttack = this.nextAttack;
      if (this.currentAttack != null) {
         this.mob.m_6674_(InteractionHand.MAIN_HAND);
         this.meleeAnimTimer = this.currentAttack.data.lengthInTicks;
         PacketDistributor.sendToPlayersTrackingEntity(this.deadKing, new SyncAnimationPacket(this.currentAttack.toString(), this.deadKing));
      }
   }

   @Override
   protected void doMovement(double distanceSquared) {
      this.mob.m_21563_().m_148051_(this.target);
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
      this.mob.m_5496_((SoundEvent)SoundRegistry.DEAD_KING_SWING.get(), 1.0F, Mth.m_216287_(this.mob.m_217043_(), 9, 13) * 0.1F);
   }
}
