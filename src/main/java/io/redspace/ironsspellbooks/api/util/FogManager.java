package io.redspace.ironsspellbooks.api.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.joml.Vector3f;

@EventBusSubscriber
public class FogManager {
   private static final int INTERP_MAX = 80;
   private static final Map<ResourceKey<Level>, FogManager> FOG_MANAGERS = new HashMap<>();
   private double interpolation;
   private FogManager.FogEvent lastEvent = null;
   private final LinkedHashMap<UUID, FogManager.FogEvent> fogEvents = new LinkedHashMap<>();

   private static <K, V> Entry<K, V> lastEntry(LinkedHashMap<K, V> map) {
      Iterator<Entry<K, V>> it = map.entrySet().iterator();
      Entry<K, V> last = null;

      while (it.hasNext()) {
         last = it.next();
      }

      return last;
   }

   public static void createEvent(Entity entity, FogManager.FogEvent event) {
      createEvent(entity.f_19853_.m_46472_(), entity.m_20148_(), event);
   }

   public static void createEvent(ResourceKey<Level> dimension, UUID id, FogManager.FogEvent event) {
      FogManager manager = getManagerFor(dimension);
      if (manager.fogEvents.isEmpty()) {
         manager.interpolation = 80.0;
      }

      manager.fogEvents.put(id, event);
   }

   public static void stopEvent(UUID id) {
      for (FogManager manager : FOG_MANAGERS.values()) {
         if (manager.fogEvents.containsKey(id)) {
            manager.lastEvent = manager.fogEvents.remove(id);
            if (manager.fogEvents.isEmpty()) {
               manager.interpolation = 80.0;
            }
         }
      }
   }

   private static FogManager getManagerFor(ResourceKey<Level> dimension) {
      return FOG_MANAGERS.computeIfAbsent(dimension, dim -> new FogManager());
   }

   public static void clear() {
      FOG_MANAGERS.clear();
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void fog(ComputeFogColor event) {
      if (Minecraft.m_91087_().f_91074_ != null) {
         FogManager manager = getManagerFor(Minecraft.m_91087_().f_91074_.f_19853_.m_46472_());
         if (!manager.fogEvents.isEmpty() || manager.lastEvent != null) {
            FogManager.FogEvent fogEvent = manager.fogEvents.isEmpty() ? manager.lastEvent : lastEntry(manager.fogEvents).getValue();
            float fogRed;
            float fogGreen;
            float fogBlue;
            if (fogEvent.color.isPresent()) {
               fogRed = fogEvent.color.get().x;
               fogGreen = fogEvent.color.get().y;
               fogBlue = fogEvent.color.get().z;
            } else {
               fogRed = event.getRed();
               fogGreen = event.getGreen();
               fogBlue = event.getBlue();
            }

            if (fogEvent.fullbright) {
               float f9 = Math.max(fogRed, Math.max(fogGreen, fogBlue));
               fogRed /= f9;
               fogGreen /= f9;
               fogBlue /= f9;
            }

            if (manager.interpolation > 0.0) {
               manager.interpolation = manager.interpolation - event.getPartialTick();
               float f = Mth.m_14036_((float)(manager.interpolation / 80.0), 0.0F, 1.0F);
               if (manager.fogEvents.isEmpty()) {
                  f = 1.0F - f;
               }

               event.setRed(Mth.m_14179_(f, fogRed, event.getRed()));
               event.setGreen(Mth.m_14179_(f, fogGreen, event.getGreen()));
               event.setBlue(Mth.m_14179_(f, fogBlue, event.getBlue()));
               if (manager.interpolation <= 0.0) {
                  manager.lastEvent = null;
               }
            } else {
               event.setRed(fogRed);
               event.setGreen(fogGreen);
               event.setBlue(fogBlue);
               manager.lastEvent = null;
            }
         }
      }
   }

   public record FogEvent(Optional<Vector3f> color, boolean fullbright) {
   }
}
