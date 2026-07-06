package io.redspace.ironsspellbooks.entity.mobs.goals;

import java.util.function.Supplier;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PatrolNearLocationGoal extends WaterAvoidingRandomStrollGoal {
   Vec3 origin;
   Supplier<Vec3> originHolder;
   float radiusSqr;

   public PatrolNearLocationGoal(PathfinderMob pMob, float radius, double pSpeedModifier) {
      super(pMob, pSpeedModifier);
      this.originHolder = pMob::m_20182_;
      this.radiusSqr = radius * radius;
   }

   @Nullable
   protected Vec3 m_7037_() {
      Vec3 f = super.m_7037_();
      if (this.origin == null) {
         this.origin = this.originHolder.get();
      }

      if (this.f_25725_.m_20182_().m_165925_() > this.radiusSqr) {
         f = LandRandomPos.m_148492_(this.f_25725_, 8, 4, this.origin);
      }

      return f;
   }
}
