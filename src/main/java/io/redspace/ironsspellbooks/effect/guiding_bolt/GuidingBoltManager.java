package io.redspace.ironsspellbooks.effect.guiding_bolt;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.data.IronsDataStorage;
import io.redspace.ironsspellbooks.network.spells.GuidingBoltManagerStartTrackingPacket;
import io.redspace.ironsspellbooks.network.spells.GuidingBoltManagerStopTrackingPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GuidingBoltManager implements INBTSerializable<CompoundTag> {
   public static final GuidingBoltManager INSTANCE = new GuidingBoltManager();
   public static final GuidingBoltManager CLIENT_INSTANCE = new GuidingBoltManager();
   private final HashMap<UUID, HashSet<Projectile>> trackedEntities = new HashMap<>();
   private final HashMap<ResourceKey<Level>, List<Projectile>> dirtyProjectiles = new HashMap<>();
   private final int tickDelay = 1;

   public void startTracking(LivingEntity entity) {
      if (!entity.f_19853_.f_46443_ && !this.trackedEntities.containsKey(entity.m_20148_())) {
         this.trackedEntities.put(entity.m_20148_(), new HashSet<>());
         IronsDataStorage.INSTANCE.m_77762_();
      }
   }

   public void stopTracking(LivingEntity entity) {
      if (!entity.f_19853_.f_46443_) {
         this.trackedEntities.remove(entity.m_20148_());
         IronsDataStorage.INSTANCE.m_77762_();
         PacketDistributor.sendToPlayersTrackingEntity(entity, new GuidingBoltManagerStopTrackingPacket(entity));
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      ListTag uuids = new ListTag();

      for (UUID key : this.trackedEntities.keySet()) {
         uuids.add(NbtUtils.m_129226_(key));
      }

      tag.m_128365_("TrackedEntities", uuids);
      return tag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      for (Tag uuidTag : compoundTag.m_128437_("TrackedEntities", 11)) {
         try {
            UUID uuid = NbtUtils.m_129233_(uuidTag);
            this.trackedEntities.put(uuid, new HashSet<>());
         } catch (Exception ignored) {
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileShot(EntityJoinLevelEvent event) {
      if (event.getLevel() instanceof ServerLevel serverLevel
         && !INSTANCE.trackedEntities.isEmpty()
         && event.getEntity() instanceof Projectile projectile
         && !projectile.m_6095_().m_204039_(ModTags.GUIDING_BOLT_IMMUNE)) {
         INSTANCE.dirtyProjectiles.computeIfAbsent(serverLevel.m_46472_(), key -> new ArrayList<>()).add(projectile);
      }
   }

   @SubscribeEvent
   public static void serverTick(LevelTickEvent event) {
      if (!INSTANCE.dirtyProjectiles.isEmpty() && event.phase != Phase.START) {
         if (event.level instanceof ServerLevel serverLevel) {
            HashMap<Entity, List<Projectile>> toSync = new HashMap<>();
            List<Projectile> dirtyProjectiles = INSTANCE.dirtyProjectiles.getOrDefault(serverLevel.m_46472_(), List.of());

            for (int i = dirtyProjectiles.size() - 1; i >= 0; i--) {
               Projectile projectile = dirtyProjectiles.get(i);
               if (projectile.m_213877_()) {
                  dirtyProjectiles.remove(i);
               } else if (projectile.isAddedToWorld()) {
                  Vec3 start = projectile.m_20182_();
                  int searchRange = 48;
                  Vec3 end = Utils.raycastForBlock(serverLevel, start, projectile.m_20184_().m_82541_().m_82490_(searchRange).m_82549_(start), Fluid.NONE)
                     .m_82450_();

                  for (Entry<UUID, HashSet<Projectile>> entityToTrackedProjectiles : INSTANCE.trackedEntities.entrySet()) {
                     Entity entity = serverLevel.m_8791_(entityToTrackedProjectiles.getKey());
                     if (entity != null
                        && !(Math.abs(entity.m_20185_() - projectile.m_20185_()) > searchRange)
                        && !(Math.abs(entity.m_20186_() - projectile.m_20186_()) > searchRange)
                        && !(Math.abs(entity.m_20189_() - projectile.m_20189_()) > searchRange)) {
                        float homeRadius = 3.5F + Math.min(entity.m_20205_() * 0.5F, 2.0F);
                        if (entity.m_20191_().m_82400_(homeRadius).m_82390_(start)
                           || Utils.checkEntityIntersecting(entity, start, end, homeRadius).m_6662_() == Type.ENTITY) {
                           updateTrackedProjectiles(entityToTrackedProjectiles.getValue(), projectile);
                           toSync.computeIfAbsent(entity, key -> new ArrayList<>()).add(projectile);
                           break;
                        }
                     }
                  }

                  dirtyProjectiles.remove(i);
               }
            }

            for (Entry<Entity, List<Projectile>> entry : toSync.entrySet()) {
               Entity entity = entry.getKey();
               PacketDistributor.sendToPlayersTrackingEntity(entity, new GuidingBoltManagerStartTrackingPacket(entity, entry.getValue()));
            }
         }
      }
   }

   private static void updateTrackedProjectiles(Set<Projectile> tracked, Projectile toTrack) {
      updateTrackedProjectiles(tracked, Set.of(toTrack));
   }

   private static void updateTrackedProjectiles(Set<Projectile> tracked, Set<Projectile> toTrack) {
      tracked.removeIf(Entity::m_213877_);
      tracked.addAll(toTrack);
   }

   @SubscribeEvent
   public static void livingTick(LivingTickEvent event) {
      GuidingBoltManager manager = event.getEntity().f_19853_ instanceof ServerLevel ? INSTANCE : CLIENT_INSTANCE;
      if (!manager.trackedEntities.isEmpty()) {
         LivingEntity livingEntity = event.getEntity();
         if (livingEntity.f_19797_ % 1 == 0) {
            HashSet<Projectile> projectiles = manager.trackedEntities.get(event.getEntity().m_20148_());
            if (projectiles != null) {
               if (livingEntity.m_213877_() || livingEntity.m_21224_()) {
                  manager.stopTracking(livingEntity);
                  return;
               }

               List<Projectile> projectilesToRemove = new ArrayList<>();

               for (Projectile projectile : projectiles) {
                  Vec3 motion = projectile.m_20184_();
                  float speed = (float)motion.m_82553_();
                  Vec3 home = livingEntity.m_20191_().m_82399_().m_82546_(projectile.m_20182_()).m_82541_().m_82490_(speed * 0.55F * 1.0F / 3.0F);
                  if (!projectile.m_213877_() && !(home.m_82526_(motion) < -0.75)) {
                     Vec3 newMotion = motion.m_82549_(home).m_82541_().m_82490_(speed);
                     projectile.m_20256_(newMotion);
                  } else {
                     projectilesToRemove.add(projectile);
                  }
               }

               projectiles.removeAll(projectilesToRemove);
            }
         }
      }
   }

   public static void handleClientboundStartTracking(UUID uuid, List<Integer> projectileIds) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      Set<Projectile> projectiles = new HashSet<>();

      for (Integer i : projectileIds) {
         if (level.m_6815_(i) instanceof Projectile projectile) {
            updateTrackedProjectiles(projectiles, projectile);
         }
      }

      CLIENT_INSTANCE.trackedEntities.computeIfAbsent(uuid, key -> new HashSet<>()).addAll(projectiles);
   }

   public static void handleClientboundStopTracking(UUID uuid) {
      CLIENT_INSTANCE.trackedEntities.remove(uuid);
   }

   public static void handleClientLogout() {
      CLIENT_INSTANCE.trackedEntities.clear();
   }
}
