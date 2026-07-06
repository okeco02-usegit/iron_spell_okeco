package io.redspace.ironsspellbooks.api.util;

import io.redspace.ironsspellbooks.network.SyncAllCameraShakesPacket;
import io.redspace.ironsspellbooks.network.SyncCameraShakePacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CameraShakeManager {
   public static final ArrayList<CameraShakeData> cameraShakeData = new ArrayList<>();
   public static ArrayList<CameraShakeData> clientCameraShakeData = new ArrayList<>();
   private static int nextId = 0;
   private static final int fadeoutDuration = 20;
   private static final float fadeoutMultiplier = 0.05F;

   public static int getNextId() {
      return nextId++;
   }

   @SubscribeEvent
   public static void serverTick(ServerTickEvent event) {
      if (!cameraShakeData.isEmpty() && event.phase != Phase.START) {
         ArrayList<CameraShakeData> completed = new ArrayList<>();

         for (CameraShakeData data : cameraShakeData) {
            data.tickCount++;
            if (data.tickCount >= data.duration) {
               completed.add(data);
            }
         }

         if (!completed.isEmpty()) {
            completed.forEach(CameraShakeManager::removeCameraShake);
         }
      }
   }

   public static void addCameraShake(CameraShakeData data) {
      cameraShakeData.add(data);
      PacketDistributor.sendToAllPlayers(new SyncCameraShakePacket(data, false));
   }

   public static void removeCameraShake(CameraShakeData data) {
      if (cameraShakeData.removeIf(instance -> instance.id == data.id)) {
         PacketDistributor.sendToAllPlayers(new SyncCameraShakePacket(data, true));
      }
   }

   public static void addClientCameraShake(CameraShakeData data) {
      clientCameraShakeData.add(data);
   }

   public static void removeClientCameraShake(CameraShakeData data) {
      clientCameraShakeData.removeIf(instance -> instance.id == data.id);
   }

   public static void doSync(ServerPlayer serverPlayer) {
      PacketDistributor.sendToPlayer(serverPlayer, new SyncAllCameraShakesPacket(cameraShakeData));
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleCameraShake(ComputeCameraAngles event) {
      if (!clientCameraShakeData.isEmpty()) {
         Entity player = event.getCamera().m_90592_();
         List<CameraShakeData> sortedActiveCameraShakes = clientCameraShakeData.stream()
            .filter(data -> data.dimension.equals(player.f_19853_.m_46472_()))
            .sorted(Comparator.comparingDouble(o -> o.origin.m_82557_(player.m_20182_())))
            .toList();
         if (!sortedActiveCameraShakes.isEmpty()) {
            CameraShakeData cameraShake = sortedActiveCameraShakes.get(0);
            Vec3 closestPos = cameraShake.origin;
            float distanceMultiplier = 1.0F / (cameraShake.radius * cameraShake.radius);
            float fadeout = cameraShake.duration - cameraShake.tickCount >= 20 ? 1.0F : (cameraShake.duration - cameraShake.tickCount) * 0.05F;
            fadeout = Mth.m_14036_(fadeout, 0.0F, 1.0F);
            float intensity = (float)Mth.m_14085_(1.0, 0.0, closestPos.m_82557_(player.m_20182_()) * distanceMultiplier) * fadeout;
            float f = (float)(player.f_19797_ + event.getPartialTick());
            float yaw = Mth.m_14089_(f * 1.5F) * intensity * 0.5F;
            float pitch = Mth.m_14089_(f * 2.0F) * intensity * 0.5F;
            float roll = Mth.m_14031_(f * 2.2F) * intensity * 0.5F;
            event.setYaw(event.getYaw() + yaw);
            event.setRoll(event.getRoll() + roll);
            event.setPitch(event.getPitch() + pitch);
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleCameraShake(ClientTickEvent event) {
      if (event.phase != Phase.START && (!Minecraft.m_91087_().m_257720_() || !Minecraft.m_91087_().m_91104_())) {
         ArrayList<CameraShakeData> toRemove = new ArrayList<>();

         for (CameraShakeData data : clientCameraShakeData) {
            data.tickCount++;
            if (data.tickCount > data.duration + 5) {
               toRemove.add(data);
            }
         }

         clientCameraShakeData.removeAll(toRemove);
      }
   }
}
