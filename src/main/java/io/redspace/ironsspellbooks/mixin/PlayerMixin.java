package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
   @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
   void canEatForGluttony(boolean pCanAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
      if (((Player)this).m_21023_((MobEffect)MobEffectRegistry.GLUTTONY.get())) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "isModelPartShown", at = @At("RETURN"), cancellable = true)
   void irons_spellbooks$hideJacketLayers(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue()) {
         Player self = (Player)this;
         switch (part) {
            case HAT:
               cir.setReturnValue(!(self.m_6844_(EquipmentSlot.HEAD).m_41720_() instanceof IDisableHat));
               break;
            case JACKET:
            case LEFT_SLEEVE:
            case RIGHT_SLEEVE:
               if (self.m_6844_(EquipmentSlot.CHEST).m_41720_() instanceof IDisableJacket chestplate && chestplate.disableForSlot(EquipmentSlot.CHEST)) {
                  cir.setReturnValue(false);
               }
               break;
            case LEFT_PANTS_LEG:
            case RIGHT_PANTS_LEG:
               if (self.m_6844_(EquipmentSlot.LEGS).m_41720_() instanceof IDisableJacket leggings && leggings.disableForSlot(EquipmentSlot.LEGS)
                  || self.m_6844_(EquipmentSlot.FEET).m_41720_() instanceof IDisableJacket boots && boots.disableForSlot(EquipmentSlot.FEET)) {
                  cir.setReturnValue(false);
               }
         }
      }
   }
}
