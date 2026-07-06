package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
   @Inject(method = "shouldEntityAppearGlowing", at = @At("RETURN"), cancellable = true)
   public void irons_spellbooks$changeGlowOutline(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
      if (Minecraft.m_91087_().f_91074_ != null && pEntity != null && !(Boolean)cir.getReturnValue()) {
         if ((Boolean)ClientConfigs.SUMMONS_GLOW.get() && ClientMagicData.getActiveSummons().contains(pEntity.m_20148_())) {
            cir.setReturnValue(true);
         } else if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get())
            && pEntity instanceof LivingEntity
            && Mth.m_14154_((float)(pEntity.m_20186_() - Minecraft.m_91087_().f_91074_.m_20186_())) < 18.0F) {
            cir.setReturnValue(true);
         }
      }
   }
}
