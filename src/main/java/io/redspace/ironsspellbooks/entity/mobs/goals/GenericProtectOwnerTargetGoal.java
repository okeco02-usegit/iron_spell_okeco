package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class GenericProtectOwnerTargetGoal extends TargetGoal {
   private final Supplier<Entity> owner;
   private int intervalToCheck;
   private final int maxIntensity = 100;
   private int currentIntensity;

   public GenericProtectOwnerTargetGoal(Mob entity, Supplier<Entity> getOwner) {
      super(entity, false);
      this.owner = getOwner;
      this.m_7021_(EnumSet.of(Flag.TARGET));
   }

   public boolean m_8036_() {
      if (this.owner.get() instanceof LivingEntity owner) {
         if (--this.intervalToCheck <= 0) {
            List<Mob> entities = owner.f_19853_
               .m_6443_(
                  Mob.class,
                  owner.m_20191_().m_82377_(16.0, 8.0, 16.0),
                  potentionalAggressor -> potentionalAggressor.m_5448_() != null
                     && (
                        potentionalAggressor.m_5448_().m_20148_().equals(owner.m_20148_())
                           || potentionalAggressor.m_5448_() instanceof IMagicSummon summon
                              && summon.getSummoner() != null
                              && summon.getSummoner().m_20148_().equals(owner.m_20148_())
                     )
                     && Utils.hasLineOfSight(this.f_26135_.f_19853_, this.f_26135_.m_146892_(), potentionalAggressor.m_146892_(), false)
               );
            if (entities.isEmpty()) {
               this.currentIntensity = Math.max(0, this.currentIntensity - 10);
               return false;
            } else {
               this.f_26135_.m_6710_((LivingEntity)entities.stream().min(Comparator.comparingDouble(o -> o.m_20280_(owner))).orElse(entities.get(0)));
               return true;
            }
         } else {
            int i = owner.m_21213_();
            int tick = owner.f_19797_;
            int combatIntervalModifier = Mth.m_14045_((tick - i) / 5, 0, 200);
            int intensityModifier = 100 - this.currentIntensity;
            this.intervalToCheck = 20 + combatIntervalModifier + intensityModifier;
            return false;
         }
      } else {
         return false;
      }
   }

   public void m_8056_() {
      this.currentIntensity = 100;
      super.m_8056_();
   }
}
