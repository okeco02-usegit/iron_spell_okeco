package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientShieldHelper {
   private static final ArrayList<AbstractShieldEntity> trackedEntities = new ArrayList<>();

   @SubscribeEvent
   public static synchronized void trackShieldCreated(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof AbstractShieldEntity ase) {
         trackedEntities.add(ase);
      }
   }

   @SubscribeEvent
   public static synchronized void trackShieldRemoved(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof AbstractShieldEntity ase) {
         trackedEntities.remove(ase);
      }
   }

   @SubscribeEvent
   public static synchronized void onPlayerLogOut(LoggingOut event) {
      trackedEntities.clear();
   }

   public static synchronized List<VoxelShape> getShieldsFor(AABB boundingBox) {
      if (!trackedEntities.isEmpty() && (Boolean)ClientConfigs.SHIELD_PARTICLE_COLLISIONS.get()) {
         List<VoxelShape> shieldCollisions = new ArrayList<>();

         for (AbstractShieldEntity s : trackedEntities) {
            if (boundingBox.m_82381_(s.m_20191_().m_82400_(1.0))) {
               shieldCollisions.addAll(s.getVoxels());
            }
         }

         return shieldCollisions;
      } else {
         return List.of();
      }
   }
}
