package io.redspace.ironsspellbooks.entity.mobs.goals;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RoamVillageGoal extends PatrolNearLocationGoal {
   GlobalPos villagePoi;
   int searchCooldown;

   public RoamVillageGoal(PathfinderMob pMob, float radius, double pSpeedModifier) {
      super(pMob, radius, pSpeedModifier);
   }

   @Nullable
   @Override
   protected Vec3 m_7037_() {
      return this.villagePoi != null ? Vec3.m_82539_(this.villagePoi.m_122646_()) : super.m_7037_();
   }

   public boolean m_8036_() {
      if (this.villagePoi == null && this.searchCooldown-- <= 0) {
         this.findVillagePoi();
         this.searchCooldown = 200;
      }

      return (this.f_25725_.f_19853_.m_46461_() || this.isDuringRaid()) && this.villagePoi != null && super.m_8036_();
   }

   private boolean isDuringRaid() {
      return false;
   }

   protected void findVillagePoi() {
      if (this.f_25725_.f_19853_ instanceof ServerLevel serverLevel) {
         Optional<BlockPos> optional1 = serverLevel.m_8904_()
            .m_27186_(poiTypeHolder -> poiTypeHolder.m_203565_(PoiTypes.f_218061_), x -> true, this.f_25725_.m_20183_(), 100, Occupancy.ANY);
         optional1.ifPresent(blockPos -> this.villagePoi = GlobalPos.m_122643_(serverLevel.m_46472_(), blockPos));
      }
   }
}
