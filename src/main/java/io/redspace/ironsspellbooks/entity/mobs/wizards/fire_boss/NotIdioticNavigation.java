package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class NotIdioticNavigation extends GroundPathNavigation {
   protected static final boolean debugparticles = false;

   public NotIdioticNavigation(Mob pMob, Level pLevel) {
      super(pMob, pLevel);
   }

   protected void m_6804_() {
      super.m_6804_();
      ArrayList<Node> dumbNodes = new ArrayList<>();
      if (this.f_26496_ != null && this.f_26496_.m_77399_() < this.f_26496_.f_77362_.size()) {
         try {
            Vec3 lastImportantNode = this.f_26496_.m_77401_().m_164701_();
            Vec3 finalNode = this.f_26496_.m_77395_().m_164701_();
            if (Math.abs(lastImportantNode.f_82480_ - finalNode.f_82480_) <= 2.0
               && this.f_26495_
                     .m_45547_(
                        new ClipContext(lastImportantNode.m_82520_(0.0, 0.75, 0.0), finalNode.m_82520_(0.0, 0.75, 0.0), Block.COLLIDER, Fluid.NONE, null)
                     )
                     .m_6662_()
                  == Type.MISS
               && this.isTraversable(lastImportantNode, finalNode)) {
               for (int i = this.f_26496_.m_77399_() + 1; i < this.f_26496_.f_77362_.size() - 1; i++) {
                  dumbNodes.add(this.f_26496_.m_77375_(i));
               }
            } else {
               for (int i = this.f_26496_.m_77399_() + 2; i < this.f_26496_.f_77362_.size(); i++) {
                  Vec3 node1 = this.f_26496_.m_77375_(i - 1).m_164701_();
                  Vec3 node2 = this.f_26496_.m_77375_(i).m_164701_();
                  Vec3 delta1 = node1.m_82546_(lastImportantNode).m_82542_(1.0, 3.0, 1.0).m_82541_();
                  Vec3 delta2 = node2.m_82546_(node1).m_82542_(1.0, 3.0, 1.0).m_82541_();
                  if (delta1.m_82526_(delta2) > 0.88 && this.isTraversable(lastImportantNode, node2)) {
                     dumbNodes.add(this.f_26496_.m_77375_(i - 1));
                  } else {
                     lastImportantNode = node1;
                  }
               }
            }

            this.f_26496_.f_77362_.removeAll(dumbNodes);
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.error(e.getMessage());
            this.f_26496_ = null;
         }
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

   public void m_7638_() {
      super.m_7638_();
   }
}
