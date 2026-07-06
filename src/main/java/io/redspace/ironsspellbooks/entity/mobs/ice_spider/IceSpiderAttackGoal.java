package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

public class IceSpiderAttackGoal extends GenericAnimatedWarlockAttackGoal<IceSpiderEntity> {
   public IceSpiderAttackGoal(IceSpiderEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
   }

   @Override
   public void m_8037_() {
      this.wantsToMelee = !this.mob.wantsToCastSpells;
      super.m_8037_();
   }

   @Override
   public void handleAttackLogic(double distanceSquared) {
      if (this.mob.getGrappleTargetUUID() == null) {
         super.handleAttackLogic(distanceSquared);
      }
   }

   @Override
   public void playSwingSound() {
      if (this.currentAttack != null && this.currentAttack.animationId.contains("bite")) {
         this.mob.m_216990_((SoundEvent)SoundRegistry.ICE_SPIDER_BITE.get());
      }

      this.mob.m_5496_((SoundEvent)SoundRegistry.ICE_SPIDER_SWING.get(), 1.0F, Utils.random.m_216332_(9, 11) * 0.1F);
   }

   @Override
   public void playImpactSound() {
   }

   public void setTarget(LivingEntity target) {
      this.target = target;
   }

   @Override
   public void doMovement(double distanceSquared) {
      super.doMovement(distanceSquared);
   }
}
