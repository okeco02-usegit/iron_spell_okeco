package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.EnumSet;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class GenericOwnerHurtTargetGoal extends TargetGoal {
   private final Supplier<Entity> owner;
   private LivingEntity ownerLastHurt;
   private int timestamp;

   public GenericOwnerHurtTargetGoal(Mob entity, Supplier<Entity> ownerGetter) {
      super(entity, false);
      this.owner = ownerGetter;
      this.m_7021_(EnumSet.of(Flag.TARGET));
   }

   public boolean m_8036_() {
      if (!(this.owner.get() instanceof LivingEntity owner)) {
         return false;
      } else {
         this.ownerLastHurt = owner.m_21214_();
         int i = owner.m_21215_();
         return i != this.timestamp
            && this.m_26150_(this.ownerLastHurt, TargetingConditions.f_26872_)
            && !(this.ownerLastHurt instanceof IMagicSummon summon && summon.getSummoner() == owner);
      }
   }

   public void m_8056_() {
      this.f_26135_.m_6710_(this.ownerLastHurt);
      Entity owner = this.owner.get();
      if (owner instanceof LivingEntity livingOwner) {
         this.timestamp = livingOwner.m_21215_();
      }

      super.m_8056_();
   }
}
