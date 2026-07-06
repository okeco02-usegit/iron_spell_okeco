package io.redspace.ironsspellbooks.entity.mobs.goals;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class GenericDefendVillageTargetGoal extends TargetGoal {
   private final Mob protector;
   @Nullable
   private LivingEntity potentialTarget;
   private final TargetingConditions attackTargeting = TargetingConditions.m_148352_().m_26883_(64.0);

   public GenericDefendVillageTargetGoal(Mob mob) {
      super(mob, false, true);
      this.protector = mob;
      this.m_7021_(EnumSet.of(Flag.TARGET));
   }

   public boolean m_8036_() {
      AABB aabb = this.protector.m_20191_().m_82377_(10.0, 8.0, 10.0);
      List<Villager> list = this.protector.f_19853_.m_45971_(Villager.class, this.attackTargeting, this.protector, aabb);
      List<Player> list1 = this.protector.f_19853_.m_45955_(this.attackTargeting, this.protector, aabb);

      for (Villager villager : list) {
         for (Player player : list1) {
            int i = villager.m_35532_(player);
            if (i <= -100) {
               this.potentialTarget = player;
            }
         }
      }

      return this.potentialTarget == null
         ? false
         : !(this.potentialTarget instanceof Player) || !this.potentialTarget.m_5833_() && !((Player)this.potentialTarget).m_7500_();
   }

   public void m_8056_() {
      this.protector.m_6710_(this.potentialTarget);
      super.m_8056_();
   }
}
