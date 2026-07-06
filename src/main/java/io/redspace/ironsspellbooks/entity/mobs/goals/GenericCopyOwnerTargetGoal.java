package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class GenericCopyOwnerTargetGoal extends TargetGoal {
   private final Supplier<Entity> ownerGetter;

   public GenericCopyOwnerTargetGoal(PathfinderMob pMob, Supplier<Entity> ownerGetter) {
      super(pMob, false);
      this.ownerGetter = ownerGetter;
   }

   public boolean m_8036_() {
      if (this.ownerGetter.get() instanceof Mob owner) {
         LivingEntity target = owner.m_5448_();
         return target == null
            ? false
            : this.m_26150_(target, TargetingConditions.f_26872_) && !(target instanceof IMagicSummon summon && summon.getSummoner() == owner);
      } else {
         return false;
      }
   }

   public void m_8056_() {
      LivingEntity target = ((Mob)this.ownerGetter.get()).m_5448_();
      this.f_26135_.m_6710_(target);
      this.f_26135_.m_6274_().m_21882_(MemoryModuleType.f_26372_, target, 200L);
      super.m_8056_();
   }
}
