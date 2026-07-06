package io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand;

import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

public class ArmorStandAttackGoal extends GenericAnimatedWarlockAttackGoal<CursedArmorStandEntity> {
   public static final float PROTECTION_RANGE = 18.0F;
   public static final float PROTECTION_RANGE_SQR = 324.0F;

   public ArmorStandAttackGoal(CursedArmorStandEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
   }

   @Override
   protected AttackAnimationData getNextAttack(float distanceSquared) {
      if (this.moveList.isEmpty()) {
         return null;
      } else {
         return this.mob.m_21205_().m_41619_() ? this.moveList.get(1) : super.getNextAttack(distanceSquared);
      }
   }

   @Override
   public void playSwingSound() {
      if (this.mob.m_21205_().m_41619_()) {
         this.mob.m_5496_(SoundEvents.f_12314_, 1.0F, Mth.m_216287_(this.mob.m_217043_(), 9, 11) * 0.1F);
      } else {
         super.playSwingSound();
      }
   }

   @Override
   protected void doMovement(double distanceSquared) {
      if (this.mob.spawn != null) {
         double boundaryDistanceSqr = this.mob.spawn.m_82557_(this.target.m_20182_());
         if (boundaryDistanceSqr > 324.0) {
            this.wantsToMelee = false;
            if (boundaryDistanceSqr > 576.0) {
               this.mob.m_21662_();
               this.mob.m_6703_(null);
               this.mob.m_6598_(null);
               this.mob.m_6925_(null);
               this.mob.m_6710_(null);
               this.mob.f_21346_.m_148105_().forEach(WrappedGoal::m_8041_);
               this.m_8041_();
               return;
            }
         } else {
            this.wantsToMelee = true;
         }
      }

      super.doMovement(distanceSquared);
   }
}
