package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

public class LeapBackGoal extends AnimatedActionGoal<IceSpiderEntity> {
   public LeapBackGoal(IceSpiderEntity mob) {
      super(mob);
   }

   @Override
   protected boolean canStartAction() {
      return this.mob.wantsToLeapBack;
   }

   @Override
   protected int getActionTimestamp() {
      return 0;
   }

   @Override
   protected int getActionDuration() {
      return 10;
   }

   @Override
   protected int getCooldown() {
      return 0;
   }

   @Override
   protected String getAnimationId() {
      return "leap_back";
   }

   @Override
   protected void doAction() {
      this.mob.m_5496_((SoundEvent)SoundRegistry.ICE_SPIDER_SWING.get(), 3.0F, Utils.random.m_216332_(13, 16) * 0.1F);
      Vec3 leapVector = new Vec3(0.0, 0.5, -2.2);
      this.mob.m_20256_(this.mob.m_20184_().m_82549_(this.mob.rotateWithBody(leapVector)));
      this.mob.wantsToLeapBack = false;
   }
}
