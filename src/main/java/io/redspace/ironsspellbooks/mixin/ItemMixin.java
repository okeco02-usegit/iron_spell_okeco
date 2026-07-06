package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.item.UpgradeData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
   @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
   public void getHoverName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
      if (UpgradeData.hasUpgradeData(stack)) {
         cir.setReturnValue(
            Component.m_237110_(
               "tooltip.irons_spellbooks.upgrade_plus_format", new Object[]{cir.getReturnValue(), UpgradeData.getUpgradeData(stack).getTotalUpgrades()}
            )
         );
      }
   }
}
