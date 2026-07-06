package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.capabilities.magic.ClientSpellTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerCooldowns;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ClientMagicData {
   private static final MagicData playerMagicData = new MagicData();
   private static final Set<UUID> activeSummons = new HashSet<>();
   private static final HashMap<Integer, SyncedSpellData> playerSyncedDataLookup = new HashMap<>();
   private static final SyncedSpellData emptySyncedData = new SyncedSpellData(-999);
   static SpellSelectionManager spellSelectionManager;
   private static ClientSpellTargetingData spellTargetingData;

   public static SpellSelectionManager getSpellSelectionManager() {
      if (spellSelectionManager == null) {
         Player player = MinecraftInstanceHelper.getPlayer();
         if (player != null) {
            spellSelectionManager = new SpellSelectionManager(player);
         }
      }

      return spellSelectionManager;
   }

   public static void updateSpellSelectionManager(@NotNull ServerPlayer player) {
      spellSelectionManager = new SpellSelectionManager(player);
   }

   public static void updateSpellSelectionManager() {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         spellSelectionManager = new SpellSelectionManager(Minecraft.m_91087_().f_91074_);
      }
   }

   public static void setTargetingData(ClientSpellTargetingData spellTargetingData) {
      ClientMagicData.spellTargetingData = spellTargetingData;
   }

   public static ClientSpellTargetingData getTargetingData() {
      if (spellTargetingData == null) {
         setTargetingData(new ClientSpellTargetingData());
      }

      return spellTargetingData;
   }

   public static void resetTargetingData() {
      spellTargetingData = null;
   }

   public static PlayerCooldowns getCooldowns() {
      return playerMagicData.getPlayerCooldowns();
   }

   public static PlayerRecasts getRecasts() {
      return playerMagicData.getPlayerRecasts();
   }

   public static void cacheClientSummons() {
      PlayerRecasts recasts = getRecasts();
      activeSummons.clear();
      recasts.getActiveRecasts().forEach(instance -> {
         if (instance.getCastData() instanceof SummonedEntitiesCastData summonedEntitiesCastData) {
            activeSummons.addAll(summonedEntitiesCastData.getSummons());
         }
      });
   }

   public static void setRecasts(PlayerRecasts playerRecasts) {
      playerMagicData.setPlayerRecasts(playerRecasts);
      cacheClientSummons();
   }

   public static Set<UUID> getActiveSummons() {
      return activeSummons;
   }

   public static float getCooldownPercent(AbstractSpell spell) {
      return playerMagicData.getPlayerCooldowns().getCooldownPercent(spell);
   }

   public static int getPlayerMana() {
      return (int)playerMagicData.getMana();
   }

   public static void setMana(int playerMana) {
      playerMagicData.setMana(playerMana);
   }

   public static CastType getCastType() {
      return playerMagicData.getCastType();
   }

   public static String getCastingSpellId() {
      return playerMagicData.getCastingSpellId();
   }

   public static int getCastingSpellLevel() {
      return playerMagicData.getCastingSpellLevel();
   }

   public static int getCastDurationRemaining() {
      return playerMagicData.getCastDurationRemaining();
   }

   public static int getCastDuration() {
      return playerMagicData.getCastDuration();
   }

   public static boolean isCasting() {
      return playerMagicData.isCasting();
   }

   public static void handleCastDuration() {
      playerMagicData.handleCastDuration();
   }

   public static float getCastCompletionPercent() {
      return playerMagicData.getCastCompletionPercent();
   }

   public static void setClientCastState(String spellId, int spellLevel, int castDuration, CastSource castSource, String castingEquipmentSlot) {
      playerMagicData.initiateCast(SpellRegistry.getSpell(spellId), spellLevel, castDuration, castSource, castingEquipmentSlot);
   }

   public static void resetClientCastState(UUID playerUUID) {
      if (Minecraft.m_91087_().f_91074_.m_20148_().equals(playerUUID)) {
         playerMagicData.resetCastingState();
         resetTargetingData();
      }

      if (Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_6117_() && Minecraft.m_91087_().f_91074_.m_20148_().equals(playerUUID)) {
         Minecraft.m_91087_().f_91074_.m_5810_();
      }
   }

   public static SyncedSpellData getSyncedSpellData(LivingEntity livingEntity) {
      if (livingEntity instanceof Player) {
         return playerSyncedDataLookup.getOrDefault(livingEntity.m_19879_(), emptySyncedData);
      } else {
         return livingEntity instanceof IMagicEntity abstractSpellCastingMob
            ? abstractSpellCastingMob.getMagicData().getSyncedData()
            : new SyncedSpellData(null);
      }
   }

   public static void handlePlayerSyncedData(SyncedSpellData playerSyncedData) {
      playerSyncedDataLookup.put(playerSyncedData.getServerPlayerId(), playerSyncedData);
   }

   public static void handleAbstractCastingMobSyncedData(int entityId, SyncedSpellData syncedSpellData) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         if (level.m_6815_(entityId) instanceof IMagicEntity abstractSpellCastingMob) {
            abstractSpellCastingMob.setSyncedSpellData(syncedSpellData);
         }
      }
   }
}
