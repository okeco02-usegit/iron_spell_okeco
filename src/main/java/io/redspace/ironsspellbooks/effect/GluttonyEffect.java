package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GluttonyEffect extends MagicMobEffect {
   public GluttonyEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @SubscribeEvent
   public static void finishEating(Finish event) {
      LivingEntity entity = event.getEntity();
      if (!entity.f_19853_.f_46443_) {
         FoodProperties food = event.getItem().getFoodProperties(entity);
         if (food != null) {
            MobEffectInstance gluttony = entity.m_21124_((MobEffect)MobEffectRegistry.GLUTTONY.get());
            if (gluttony != null) {
               MagicData pmg = MagicData.getPlayerMagicData(entity);
               pmg.addMana(food.m_38744_() * ratioForAmplifier(gluttony.m_19564_()));
               if (entity instanceof ServerPlayer serverPlayer) {
                  PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(pmg));
               }
            }
         }
      }
   }

   public static float ratioForAmplifier(int amplifier) {
      return (4 + amplifier) * 0.5F;
   }
}
