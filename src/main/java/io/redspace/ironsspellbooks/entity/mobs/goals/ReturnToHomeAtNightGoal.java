package io.redspace.ironsspellbooks.entity.mobs.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ReturnToHomeAtNightGoal<T extends PathfinderMob & HomeOwner> extends WaterAvoidingRandomStrollGoal {
   T homeOwnerMob;

   public ReturnToHomeAtNightGoal(T pMob, double pSpeedModifier) {
      super(pMob, pSpeedModifier);
      this.homeOwnerMob = pMob;
   }

   public boolean m_8036_() {
      return this.homeOwnerMob.getHome() != null && !this.f_25725_.f_19853_.m_46461_() && super.m_8036_();
   }

   @Nullable
   protected Vec3 m_7037_() {
      return this.homeOwnerMob.getHome() == null ? super.m_7037_() : Vec3.m_82539_(this.homeOwnerMob.getHome());
   }
}
