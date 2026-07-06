package io.redspace.ironsspellbooks.entity.mobs.goals.melee;

import net.minecraft.world.phys.Vec3;

public class AttackKeyframe {
   private final int timeStamp;
   private final Vec3 lungeVector;
   private final Vec3 extraKnockback;

   public AttackKeyframe(int timeStamp, Vec3 lungeVector, Vec3 extraKnockback) {
      this.timeStamp = timeStamp;
      this.lungeVector = lungeVector;
      this.extraKnockback = extraKnockback;
   }

   public AttackKeyframe(int timeStamp, Vec3 lungeVector) {
      this(timeStamp, lungeVector, Vec3.f_82478_);
   }

   public int timeStamp() {
      return this.timeStamp;
   }

   public Vec3 lungeVector() {
      return this.lungeVector;
   }

   public Vec3 extraKnockback() {
      return this.extraKnockback;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o != null && this.getClass() == o.getClass()) {
         AttackKeyframe that = (AttackKeyframe)o;
         if (this.timeStamp != that.timeStamp) {
            return false;
         } else {
            return !this.lungeVector.equals(that.lungeVector) ? false : this.extraKnockback.equals(that.extraKnockback);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = Integer.hashCode(this.timeStamp);
      result = 31 * result + this.lungeVector.hashCode();
      return 31 * result + this.extraKnockback.hashCode();
   }

   @Override
   public String toString() {
      return "AttackKeyframe{timeStamp=" + this.timeStamp + ", lungeVector=" + this.lungeVector + ", extraKnockback=" + this.extraKnockback + "}";
   }
}
