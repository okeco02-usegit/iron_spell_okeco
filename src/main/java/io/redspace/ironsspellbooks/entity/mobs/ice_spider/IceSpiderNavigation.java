package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class IceSpiderNavigation extends GroundPathNavigation {
   public IceSpiderNavigation(Mob pMob, Level pLevel) {
      super(pMob, pLevel);
   }

   public void setPath(Path path) {
      this.f_26496_ = path;
   }

   protected void m_6804_() {
      if (this.f_26496_ != null && this.f_26496_.m_77399_() < this.f_26496_.f_77362_.size()) {
         try {
            double baseY = this.f_26494_.m_20186_();
            float maxStepUp = this.f_26494_.m_274421_();
            Vec3 finalNode = this.f_26496_.m_77395_().m_164701_();
            if (finalNode.f_82480_ - baseY > maxStepUp) {
               Vec3 directionVector = finalNode.m_82546_(this.f_26494_.m_20182_());

               for (int i = this.f_26496_.m_77399_(); i < this.f_26496_.f_77362_.size(); i++) {
                  Vec3 node = this.f_26496_.m_77375_(i).m_164701_();
                  if (finalNode.m_82546_(node).m_82526_(directionVector) > 0.8 && this.isTraversable(node, finalNode)) {
                     ArrayList<Node> inbetweenNodes = new ArrayList<>();

                     for (int j = i + 1; j < this.f_26496_.f_77362_.size() - 1; j++) {
                        inbetweenNodes.add((Node)this.f_26496_.f_77362_.get(j));
                     }

                     this.f_26496_.f_77362_.removeAll(inbetweenNodes);
                     return;
                  }
               }
            }
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.error(e.getMessage());
            this.f_26496_ = null;
         }

         super.m_6804_();
      }
   }

   protected boolean isTraversable(Vec3 pos1, Vec3 pos2) {
      Vec3 step = pos2.m_82546_(pos1);
      double distance = step.m_82553_();
      step = step.m_82490_(1.0 / distance);

      for (int i = 0; i < distance; i++) {
         BlockPos currentPos = BlockPos.m_274446_(pos1.m_82549_(step.m_82490_(i)));
         if (this.f_26494_.m_6095_().m_20630_(this.f_26495_.m_8055_(currentPos))) {
            return false;
         }

         if (!this.f_26495_.m_8055_(currentPos.m_7495_()).m_60783_(this.f_26495_, currentPos.m_7495_(), Direction.UP)) {
            return false;
         }
      }

      return true;
   }
}
