package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.gui.IronBookAccess;
import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternScreen.class)
public abstract class ClientBookAccessMixin extends BookViewScreen {
   @Final
   @Shadow
   private LecternMenu f_99017_;

   @Inject(method = "bookChanged", at = @At("HEAD"), cancellable = true)
   void irons_spellbooks$injectCustomBookContents(CallbackInfo ci) {
      ItemStack itemstack = this.f_99017_.m_39835_();
      if (itemstack.m_41720_() instanceof ILecternPlaceable placeable) {
         this.m_98288_(new IronBookAccess(placeable.getPages(itemstack)));
         ci.cancel();
      }
   }
}
