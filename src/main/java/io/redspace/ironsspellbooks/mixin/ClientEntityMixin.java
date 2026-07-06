package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ClientEntityMixin {
   @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
   public void changeGlowOutline(CallbackInfoReturnable<Integer> cir) {
      if (ClientMagicData.getActiveSummons().contains(((Entity)this).m_20148_())) {
         cir.setReturnValue(ClientConfigs.summonGlowColor);
      } else if (Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get())) {
         cir.setReturnValue(7095029);
      } else {
         Entity var3 = (Entity)this;
         if (var3 instanceof ItemEntity item) {
            if (item.m_32055_().m_150930_((Item)ItemRegistry.DRAGONSKIN.get())) {
               cir.setReturnValue(13769983);
            }

            if (item.m_32055_().m_150930_((Item)ItemRegistry.LIGHTNING_ROD_STAFF.get())) {
               cir.setReturnValue(5636095);
            }
         }
      }
   }
}
