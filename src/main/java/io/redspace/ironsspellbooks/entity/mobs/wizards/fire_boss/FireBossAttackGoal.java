package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.particle.FlameStrikeParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.phys.Vec3;

public class FireBossAttackGoal extends GenericAnimatedWarlockAttackGoal<FireBossEntity> {
   private static final AttributeModifier MODIFIER_FIRE_BALLER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("fireballer")), "fireballer", 0.5, Operation.ADDITION
   );
   int fireballcooldown;

   public FireBossAttackGoal(FireBossEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
   }

   @Override
   protected void doMovement(double distanceSquared) {
      double speed = (this.spellCastingMob.isCasting() ? 0.75F : 1.0F) * this.movementSpeed();
      this.mob.m_21391_(this.target, 30.0F, 30.0F);
      float meleeRange = this.meleeRange();
      float strafeMultiplier = this.getStrafeMultiplier();
      if (distanceSquared < this.spellcastingRangeSqr && this.seeTime >= 5) {
         this.mob.m_21573_().m_26573_();
         if (++this.strafeTime > 40 && this.mob.m_217043_().m_188500_() < 0.08) {
            this.strafingClockwise = !this.strafingClockwise;
            this.strafeTime = 0;
         }

         float strafeForward = this.meleeMoveSpeedModifier;
         if (distanceSquared > meleeRange * meleeRange * 3.0F * 3.0F) {
            strafeForward *= 2.0F;
         } else if (distanceSquared > meleeRange * meleeRange * 0.75 * 0.75) {
            strafeForward *= 1.3F;
         } else {
            strafeForward *= -1.15F;
         }

         int strafeDir = this.strafingClockwise ? 1 : -1;
         this.mob.getMoveControl().m_24988_(strafeForward * strafeMultiplier, (float)speed * strafeDir * strafeMultiplier);
      } else if (this.mob.f_19797_ % 5 == 0) {
         this.mob.m_21570_(0.0F);
         this.mob.m_21573_().m_5624_(this.target, this.speedModifier);
      }
   }

   public void setTarget(LivingEntity target) {
      this.target = target;
   }

   @Override
   protected void onHitFrame(AttackKeyframe attackKeyframe, float meleeRange) {
      if (attackKeyframe instanceof InvokeDaggerKeyframe) {
         this.mob.procSpectralDagger();
         this.mob.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_ACCENT.get(), 3.0F, 1.0F);
      } else {
         super.onHitFrame(attackKeyframe, meleeRange);
         if (attackKeyframe instanceof FireBossAttackKeyframe fireKeyframe) {
            boolean mirrored = fireKeyframe.swingData.mirrored();
            boolean vertical = fireKeyframe.swingData.vertical();
            Vec3 forward = this.mob.m_20156_();
            float reach = 2.0F * this.mob.m_6134_();
            Vec3 hitLocation = this.mob.m_20191_().m_82399_().m_82549_(this.mob.m_20156_().m_82542_(reach, 0.5, reach));
            MagicManager.spawnParticles(
               this.mob.f_19853_,
               new FlameStrikeParticleOptions((float)forward.f_82479_, (float)forward.f_82480_, (float)forward.f_82481_, mirrored, vertical, this.mob.m_6134_()),
               hitLocation.f_82479_,
               hitLocation.f_82480_,
               hitLocation.f_82481_,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
         }
      }
   }

   @Override
   public void m_8041_() {
      super.m_8041_();
      this.mob.m_21051_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get()).m_22130_(MODIFIER_FIRE_BALLER);
   }

   @Override
   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      if (this.fireballcooldown > 0) {
         if (this.fireballcooldown == 180) {
            this.mob.m_21051_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get()).m_22130_(MODIFIER_FIRE_BALLER);
         }

         this.fireballcooldown--;
      } else if (!this.mob.m_20096_() && distanceSquared > meleeRange * meleeRange * 2.0F * 2.0F && !this.isActing()) {
         this.mob.m_21051_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get()).m_22130_(MODIFIER_FIRE_BALLER);
         this.mob.m_21051_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get()).m_22118_(MODIFIER_FIRE_BALLER);
         this.mob.initiateCastSpell((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get(), 5);
         this.fireballcooldown = 200;
         return;
      }

      if (this.meleeAnimTimer > 0 && this.currentAttack != null) {
         int shortcut = 5;
         if (this.meleeAnimTimer < shortcut && this.currentAttack.attacks.keySet().intStream().noneMatch(i -> i > this.currentAttack.lengthInTicks - shortcut)) {
            this.meleeAnimTimer = 0;
         }
      }

      boolean delayNextAttack = this.mob.spectralDaggerActive() || !this.mob.m_20096_() && this.mob.m_217043_().m_188499_();
      if (delayNextAttack) {
         this.meleeAttackDelay++;
      }

      super.handleAttackLogic(distanceSquared);
   }

   @Override
   protected void doMeleeAction() {
      super.doMeleeAction();
      if (this.currentAttack != null) {
         float r = this.meleeRange();
         if (this.mob.m_20280_(this.target) > 0.5625 * r * r) {
            int i = this.currentAttack.attacks.keySet().intStream().sorted().findFirst().orElse(0);
            this.mob.getMoveControl().triggerCustomMovement(i + 5, f -> new Vec3(0.0, 0.0, 0.5 * (1.0F + this.currentAttack.rangeMultiplier)));
         }
      }
   }

   @Override
   protected double movementSpeed() {
      return this.meleeMoveSpeedModifier;
   }

   @Override
   public void playSwingSound() {
      this.mob.m_5496_((SoundEvent)SoundRegistry.HELLRAZOR_SWING.get(), 1.0F, Mth.m_216287_(this.mob.m_217043_(), 9, 11) * 0.1F);
   }
}
