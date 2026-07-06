package io.redspace.ironsspellbooks.api.backwards_compat;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class UpgradeTypeCache {
   public static final HashMap<ResourceKey<UpgradeOrbType>, Holder<UpgradeOrbType>> CACHE = new HashMap<>();

   public static void onClientLoad(RegistryAccess registryAccess, List<ResourceKey<UpgradeOrbType>> types) {
      CACHE.clear();

      for (ResourceKey<UpgradeOrbType> type : types) {
         CACHE.put(type, UpgradeOrbTypeRegistry.upgradeTypeRegistry(registryAccess).m_246971_(type));
      }
   }

   public static void doCache(RegistryAccess registryAccess) {
      CACHE.clear();
      Registry<UpgradeOrbType> registry = UpgradeOrbTypeRegistry.upgradeTypeRegistry(registryAccess);

      for (Entry<ResourceKey<UpgradeOrbType>, UpgradeOrbType> entry : registry.m_6579_()) {
         CACHE.put(entry.getKey(), registry.m_263177_(entry.getValue()));
      }
   }

   @SubscribeEvent
   public static void onDatapackLoad(OnDatapackSyncEvent event) {
      ServerPlayer player = event.getPlayer();
      if (player == null || CACHE.isEmpty()) {
         RegistryAccess access = event.getPlayerList().m_7873_().m_206579_();
         doCache(access);
      }
   }
}
