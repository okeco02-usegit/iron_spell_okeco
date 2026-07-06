package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import net.minecraft.world.phys.Vec3;

public class FireBossAttackKeyframe extends AttackKeyframe {
   final FireBossAttackKeyframe.SwingData swingData;

   public FireBossAttackKeyframe(int timeStamp, Vec3 lungeVector, FireBossAttackKeyframe.SwingData swingData) {
      this(timeStamp, lungeVector, Vec3.f_82478_, swingData);
   }

   public FireBossAttackKeyframe(int timeStamp, Vec3 lungeVector, Vec3 extraKnockback, FireBossAttackKeyframe.SwingData swingData) {
      super(timeStamp, lungeVector, extraKnockback);
      this.swingData = swingData;
   }

   public record SwingData(boolean vertical, boolean mirrored) {
   }
}
