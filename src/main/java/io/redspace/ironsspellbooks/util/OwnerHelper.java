package io.redspace.ironsspellbooks.util;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class OwnerHelper {
   public static LivingEntity getAndCacheOwner(Level level, LivingEntity cachedOwner, UUID summonerUUID) {
      if (cachedOwner != null && cachedOwner.m_6084_()) {
         return cachedOwner;
      }

      if (summonerUUID != null && level instanceof ServerLevel serverLevel) {
         if (serverLevel.m_8791_(summonerUUID) instanceof LivingEntity livingEntity) {
            cachedOwner = livingEntity;
         }

         return cachedOwner;
      } else {
         return null;
      }
   }

   public static void serializeOwner(CompoundTag compoundTag, UUID ownerUUID) {
      if (ownerUUID != null) {
         compoundTag.m_128362_("Summoner", ownerUUID);
      }
   }

   public static UUID deserializeOwner(CompoundTag compoundTag) {
      return compoundTag.m_128403_("Summoner") ? compoundTag.m_128342_("Summoner") : null;
   }
}
