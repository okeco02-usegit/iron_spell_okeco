package io.redspace.ironsspellbooks.api.util;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MusicManager {
   private static final Map<ResourceKey<Level>, MusicManager> MUSIC_MANAGERS = new HashMap<>();
   private final Stack<Pair<UUID, IMusicHandler>> musicHandlers = new Stack<>();
   private boolean resumeNext;

   public static void createEvent(Entity entity, IMusicHandler event) {
      createEvent(entity.f_19853_.m_46472_(), entity.m_20148_(), event);
   }

   public static void createEvent(ResourceKey<Level> dimension, UUID id, IMusicHandler event) {
      if ((Boolean)ClientConfigs.ENABLE_BOSS_MUSIC.get()) {
         MusicManager manager = getManagerFor(dimension);
         if (!manager.musicHandlers.isEmpty()) {
            ((IMusicHandler)manager.musicHandlers.peek().right()).stop();
         }

         event.init();
         manager.musicHandlers.push(new ObjectObjectImmutablePair(id, event));
      }
   }

   public static void stopEvent(UUID uuid) {
      for (MusicManager manager : MUSIC_MANAGERS.values()) {
         Iterator<Pair<UUID, IMusicHandler>> itr = manager.musicHandlers.iterator();

         while (itr.hasNext()) {
            Pair<UUID, IMusicHandler> entry = itr.next();
            if (((UUID)entry.left()).equals(uuid)) {
               ((IMusicHandler)entry.right()).stop();
               if (!manager.musicHandlers.isEmpty()) {
                  manager.resumeNext = true;
               }

               itr.remove();
               break;
            }
         }
      }
   }

   private static MusicManager getManagerFor(ResourceKey<Level> dimension) {
      return MUSIC_MANAGERS.computeIfAbsent(dimension, dim -> new MusicManager());
   }

   public static void clear() {
      for (MusicManager manager : MUSIC_MANAGERS.values()) {
         Iterator<Pair<UUID, IMusicHandler>> itr = manager.musicHandlers.iterator();

         while (itr.hasNext()) {
            Pair<UUID, IMusicHandler> entry = itr.next();
            ((IMusicHandler)entry.right()).hardStop();
            itr.remove();
         }
      }

      MUSIC_MANAGERS.clear();
   }

   @SubscribeEvent
   public static void tick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         if (Minecraft.m_91087_().f_91074_ != null && !Minecraft.m_91087_().m_91104_()) {
            MusicManager manager = getManagerFor(Minecraft.m_91087_().f_91074_.f_19853_.m_46472_());
            if (manager.musicHandlers.isEmpty()) {
               return;
            }

            Pair<UUID, IMusicHandler> entry = manager.musicHandlers.peek();
            UUID uuid = (UUID)entry.left();
            IMusicHandler musicHandler = (IMusicHandler)entry.right();
            if (manager.resumeNext) {
               musicHandler.triggerResume();
               manager.resumeNext = false;
            }

            if (musicHandler.isDone()) {
               manager.musicHandlers.remove(entry);
            } else {
               musicHandler.tick();
            }
         }
      }
   }
}
