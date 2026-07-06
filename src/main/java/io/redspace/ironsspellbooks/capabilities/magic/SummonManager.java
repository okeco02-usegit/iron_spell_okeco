package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.data.IronsDataStorage;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class SummonManager implements INBTSerializable<CompoundTag> {
   public static final SummonManager INSTANCE = new SummonManager();
   private final HashMap<UUID, List<CompoundTag>> offlineSummonersToSavedEntities = new HashMap<>();
   private final HashMap<UUID, UUID> summonToOwner = new HashMap<>();
   private final HashMap<UUID, Set<UUID>> ownerToSummons = new HashMap<>();
   private final PriorityQueue<SummonManager.ExpirationInstance> summonExpirations = new PriorityQueue<>(
      Comparator.comparingInt(SummonManager.ExpirationInstance::expirationServerTick)
   );

   @Nullable
   public static Entity getOwner(@NotNull Entity summon) {
      if (summon.f_19853_ instanceof ServerLevel serverLevel) {
         if (INSTANCE.summonToOwner.containsKey(summon.m_20148_())) {
            return serverLevel.m_8791_(INSTANCE.summonToOwner.get(summon.m_20148_()));
         }
      } else {
         IronsSpellbooks.LOGGER.warn("Summon {} attempting to lookup owner from client!", summon);
      }

      return null;
   }

   public static Set<UUID> getSummons(Entity owner) {
      return Set.copyOf(INSTANCE.ownerToSummons.getOrDefault(owner.m_20148_(), Set.of()));
   }

   public static void setOwner(@NotNull Entity summon, @NotNull Entity owner) {
      removeSummon(summon);
      INSTANCE.summonToOwner.put(summon.m_20148_(), owner.m_20148_());
      startTrackingSummon(owner, summon);
      IronsDataStorage.INSTANCE.m_77762_();
   }

   public static void initSummon(Entity owner, Entity summon, int duration, SummonedEntitiesCastData summonedEntitiesCastData) {
      setOwner(summon, owner);
      setDuration(summon, duration);
      summonedEntitiesCastData.add(summon);
   }

   public static void setDuration(Entity summon, int duration) {
      if (summon.f_19853_ instanceof ServerLevel serverLevel) {
         INSTANCE.summonExpirations
            .add(new SummonManager.ExpirationInstance(summon.m_20148_(), serverLevel.m_7654_().m_129921_(), serverLevel.m_7654_().m_129921_() + duration));
      }
   }

   public static void removeSummon(Entity summon) {
      UUID owner = INSTANCE.summonToOwner.remove(summon.m_20148_());
      if (owner != null) {
         IronsDataStorage.INSTANCE.m_77762_();
         Set<UUID> summons = INSTANCE.ownerToSummons.get(owner);
         if (summons != null) {
            UUID summonUuid = summon.m_20148_();
            summons.remove(summonUuid);
            IronsDataStorage.INSTANCE.m_77762_();
            if (summons.isEmpty()) {
               INSTANCE.ownerToSummons.remove(owner);
            }

            if (summon.f_19853_ instanceof ServerLevel serverLevel) {
               removeFromRecastData(serverLevel, owner, summonUuid);
            }
         }
      }
   }

   public void handlePlayerDisconnect(ServerPlayer serverPlayer) {
      Set<UUID> summons = this.ownerToSummons.get(serverPlayer.m_20148_());
      if (summons != null) {
         ServerLevel serverLevel = serverPlayer.m_284548_();
         ArrayList<CompoundTag> savedSummons = new ArrayList<>();

         for (UUID uuid : summons) {
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity != null) {
               CompoundTag saveData = new CompoundTag();
               entity.m_20223_(saveData);
               int durationRemaining = INSTANCE.getExpirationTick(entity.m_20148_()) - serverLevel.m_7654_().m_129921_();
               saveData.m_128405_("summon_duration_remaining", durationRemaining);
               entity.m_142467_(RemovalReason.UNLOADED_WITH_PLAYER);
               savedSummons.add(saveData);
            }
         }

         IronsDataStorage.INSTANCE.m_77762_();
         INSTANCE.offlineSummonersToSavedEntities.put(serverPlayer.m_20148_(), savedSummons);
         INSTANCE.stopTrackingSummonerAndSummons(serverPlayer);
      }
   }

   public static boolean recastFinishedHelper(
      ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable
   ) {
      if (recastResult == RecastResult.COUNTERSPELL) {
         MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().forceAddRecast(recastInstance);
      } else if (recastResult != RecastResult.TIMEOUT) {
         if (castDataSerializable instanceof SummonedEntitiesCastData summonedEntitiesCastData) {
            ServerLevel serverLevel = serverPlayer.m_284548_();
            summonedEntitiesCastData.getSummons().forEach(uuid -> {
               Entity toRemove = serverLevel.m_8791_(uuid);
               if (toRemove instanceof IMagicSummon summon) {
                  summon.onUnSummon();
               } else if (toRemove != null) {
                  toRemove.m_146870_();
               }
            });
         }
      } else if (((CurioBaseItem)ItemRegistry.GREATER_CONJURERS_TALISMAN.get()).isEquippedBy(serverPlayer)) {
         return false;
      }

      return true;
   }

   private static void removeFromRecastData(ServerLevel level, UUID ownerUuid, UUID summonUuid) {
      if (level.m_8791_(ownerUuid) instanceof Player player) {
         MagicData playerMagicData = MagicData.getPlayerMagicData(player);
         PlayerRecasts recasts = playerMagicData.getPlayerRecasts();

         for (RecastInstance recastInstance : recasts.getActiveRecasts()) {
            if (recastInstance.getCastData() instanceof SummonedEntitiesCastData summonData && summonData.getSummons().contains(summonUuid)) {
               summonData.handleRemove(summonUuid, playerMagicData, recastInstance);
               break;
            }
         }
      }
   }

   private Optional<SummonManager.ExpirationInstance> getExpirationInstance(UUID uuid) {
      for (SummonManager.ExpirationInstance inst : this.summonExpirations) {
         if (inst.uuid.equals(uuid)) {
            return Optional.of(inst);
         }
      }

      return Optional.empty();
   }

   private int getExpirationTick(UUID uuid) {
      return this.getExpirationInstance(uuid).map(SummonManager.ExpirationInstance::expirationServerTick).orElse(0);
   }

   private static void startTrackingSummon(Entity owner, Entity summon) {
      Set<UUID> summons = INSTANCE.ownerToSummons.computeIfAbsent(owner.m_20148_(), uuid -> new HashSet<>());
      summons.add(summon.m_20148_());
      IronsDataStorage.INSTANCE.m_77762_();
   }

   private void stopTrackingSummonerAndSummons(Entity summoner) {
      Set<UUID> summons = this.ownerToSummons.remove(summoner.m_20148_());
      if (summons != null) {
         IronsDataStorage.INSTANCE.m_77762_();
         summons.forEach(summonUUID -> {
            if (this.summonToOwner.remove(summonUUID) != null) {
               this.getExpirationInstance(summonUUID).ifPresent(this.summonExpirations::remove);
            }
         });
      }
   }

   public static void stopTrackingExpiration(Entity summon) {
      INSTANCE.getExpirationInstance(summon.m_20148_()).ifPresent(INSTANCE.summonExpirations::remove);
   }

   public CompoundTag serializeNBT() {
      CompoundTag manager = new CompoundTag();
      ListTag offlineSummonsInstances = new ListTag();

      for (Entry<UUID, List<CompoundTag>> entry : this.offlineSummonersToSavedEntities.entrySet()) {
         CompoundTag tag = new CompoundTag();
         tag.m_128362_("summoner", entry.getKey());
         ListTag summons = new ListTag();
         summons.addAll(entry.getValue());
         tag.m_128365_("summons", summons);
         offlineSummonsInstances.add(tag);
      }

      manager.m_128365_("OfflineSummons", offlineSummonsInstances);
      return manager;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      for (Tag tag : compoundTag.m_128437_("OfflineSummons", 10)) {
         CompoundTag entry = (CompoundTag)tag;
         UUID uuid = entry.m_128342_("summoner");
         ListTag summons = entry.m_128437_("summons", 10);
         ArrayList<CompoundTag> summonsList = new ArrayList<>();
         summons.forEach(t -> summonsList.add((CompoundTag)t));
         this.offlineSummonersToSavedEntities.put(uuid, summonsList);
      }
   }

   @SubscribeEvent
   public static void levelTick(ServerTickEvent event) {
      if (event.phase != Phase.START) {
         MinecraftServer server = event.getServer();
         int tick = server.m_129921_();
         if (!INSTANCE.summonExpirations.isEmpty() && tick % 20 == 0) {
            for (SummonManager.ExpirationInstance nextDespawn = INSTANCE.summonExpirations.peek();
               nextDespawn.expirationServerTick < tick;
               nextDespawn = INSTANCE.summonExpirations.peek()
            ) {
               INSTANCE.summonExpirations.remove();
               UUID uuid = nextDespawn.uuid;
               Entity toRemove = null;

               for (ServerLevel serverLevel : server.m_129785_()) {
                  toRemove = serverLevel.m_8791_(uuid);
                  if (toRemove != null) {
                     break;
                  }
               }

               if (toRemove instanceof IMagicSummon summon) {
                  summon.onUnSummon();
               } else if (toRemove != null) {
                  toRemove.m_146870_();
               }

               if (INSTANCE.summonExpirations.isEmpty()) {
                  break;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         INSTANCE.handlePlayerDisconnect(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onServerStopping(ServerStoppingEvent event) {
      event.getServer().m_6846_().m_11314_().forEach(INSTANCE::handlePlayerDisconnect);
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      if (player.f_19853_ instanceof ServerLevel serverLevel) {
         IronsDataStorage.INSTANCE.m_77762_();
         List<CompoundTag> savedSummons = INSTANCE.offlineSummonersToSavedEntities.remove(player.m_20148_());
         MinecraftServer server = serverLevel.m_7654_();
         if (savedSummons != null) {
            Set<UUID> summonsSet = new HashSet<>();
            UUID ownerUUID = player.m_20148_();

            for (CompoundTag summon : savedSummons) {
               Entity summonedEntity = (Entity)EntityType.m_20642_(summon, serverLevel).orElse(null);
               if (summonedEntity != null) {
                  serverLevel.m_47205_(summonedEntity);
                  UUID summonUUID = summonedEntity.m_20148_();
                  summonsSet.add(summonUUID);
                  INSTANCE.summonToOwner.put(summonUUID, ownerUUID);
                  INSTANCE.summonExpirations
                     .add(
                        new SummonManager.ExpirationInstance(summonUUID, server.m_129921_(), server.m_129921_() + summon.m_128451_("summon_duration_remaining"))
                     );
               }

               INSTANCE.ownerToSummons.put(ownerUUID, summonsSet);
            }
         }
      }
   }

   record ExpirationInstance(UUID uuid, int summonedServerTick, int expirationServerTick) {
   }
}
