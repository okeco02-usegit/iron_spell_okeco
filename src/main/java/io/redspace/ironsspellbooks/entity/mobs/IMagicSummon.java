package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public interface IMagicSummon extends AntiMagicSusceptible {
   default Entity getSummoner() {
      return SummonManager.getOwner((Entity)this);
   }

   void onUnSummon();

   @Override
   default void onAntiMagic(MagicData playerMagicData) {
      this.onUnSummon();
   }

   default boolean shouldIgnoreDamage(DamageSource damageSource) {
      return !damageSource.m_269533_(DamageTypeTags.f_268738_) && !ServerConfigs.CAN_ATTACK_OWN_SUMMONS.get() && damageSource.m_7639_() != null
         ? DamageSources.isFriendlyFireBetween(damageSource.m_7639_(), (Entity)this)
         : false;
   }

   default boolean isAlliedHelper(Entity entity) {
      Entity owner = this.getSummoner();
      if (owner == null) {
         return false;
      } else if (entity instanceof IMagicSummon magicSummon) {
         Entity otherOwner = magicSummon.getSummoner();
         return otherOwner != null && (owner == otherOwner || otherOwner.m_7307_(otherOwner));
      } else if (!(entity instanceof OwnableEntity tamableAnimal)) {
         return false;
      } else {
         LivingEntity otherOwner = tamableAnimal.m_269323_();
         return otherOwner != null && (owner == otherOwner || otherOwner.m_7307_(otherOwner));
      }
   }

   default void onDeathHelper() {
      if (this instanceof LivingEntity entity) {
         Level level = entity.f_19853_;
         Component deathMessage = entity.m_21231_().m_19293_();
         if (!level.f_46443_ && level.m_46469_().m_46207_(GameRules.f_46142_) && this.getSummoner() instanceof ServerPlayer player) {
            player.m_213846_(deathMessage);
         }
      }
   }

   default void onRemovedHelper(Entity entity) {
      if (!entity.f_19853_.f_46443_) {
         RemovalReason reason = entity.m_146911_();
         if (reason != null && reason == RemovalReason.UNLOADED_TO_CHUNK) {
         }

         if (reason == RemovalReason.DISCARDED && this.getSummoner() instanceof ServerPlayer player) {
            player.m_213846_(Component.m_237110_("ui.irons_spellbooks.summon_despawn_message", new Object[]{((Entity)this).m_5446_()}));
         }

         if (reason != null && reason.m_146965_()) {
            SummonManager.removeSummon(entity);
            SummonManager.stopTrackingExpiration(entity);
         }
      }
   }

   @Deprecated(forRemoval = true)
   default void onRemovedHelper(Entity entity, RegistryObject<MobEffect> holder) {
      RemovalReason reason = entity.m_146911_();
      if (reason != null && this.getSummoner() instanceof ServerPlayer player && reason.m_146965_()) {
         MobEffectInstance effect = player.m_21124_((MobEffect)holder.get());
         if (effect != null) {
            MobEffectInstance decrement = new MobEffectInstance((MobEffect)holder.get(), effect.m_19557_(), effect.m_19564_() - 1, false, false, true);
            if (decrement.m_19564_() >= 0) {
               player.m_21221_().put((MobEffect)holder.get(), decrement);
               player.f_8906_.m_9829_(new ClientboundUpdateMobEffectPacket(player.m_19879_(), decrement));
            } else {
               player.m_21195_((MobEffect)holder.get());
            }
         }
      }

      this.onRemovedHelper(entity);
   }
}
