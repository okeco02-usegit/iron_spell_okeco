package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class FireBossMoveControl extends MoveControl {
   int customMovementTimer;
   int customMovementDuration;
   Function<Float, Vec3> currentCustomMovementControl;

   public FireBossMoveControl(Mob pMob) {
      super(pMob);
   }

   public void m_8126_() {
      if (this.customMovementTimer > 0) {
         this.customMovementTimer--;
         LivingEntity target = this.f_24974_.m_5448_();
         if (target != null) {
            float f = Mth.m_14036_((float)this.customMovementTimer / this.customMovementDuration, 0.0F, 1.0F);
            Vec3 movement = this.currentCustomMovementControl.apply(f).m_82490_(this.f_24974_.m_21133_(Attributes.f_22279_));
            float angle = -Utils.getAngle(this.f_24974_.m_20185_(), this.f_24974_.m_20189_(), target.m_20185_(), target.m_20189_()) - (float) (Math.PI / 2);
            this.f_24974_.m_20256_(this.f_24974_.m_20184_().m_82549_(movement.m_82524_(angle).m_82490_(f * f)));
            float slowdownRange = (float)this.f_24974_.m_21133_((Attribute)ForgeMod.ENTITY_REACH.get()) * this.f_24974_.m_6134_() * 0.9F;
            if (this.f_24974_.m_20280_(target) < slowdownRange * slowdownRange) {
               this.customMovementTimer -= 2;
            }
         } else {
            this.customMovementTimer = 0;
         }
      } else {
         super.m_8126_();
      }
   }

   protected float m_24991_(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
      double d0 = this.f_24975_ - this.f_24974_.m_20185_();
      double d1 = this.f_24977_ - this.f_24974_.m_20189_();
      return d0 * d0 + d1 * d1 < 0.5 ? pSourceAngle : super.m_24991_(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
   }

   public void triggerCustomMovement(int duration, Function<Float, Vec3> progressToMovement) {
      this.currentCustomMovementControl = progressToMovement;
      this.customMovementTimer = duration;
      this.customMovementDuration = duration;
   }
}
