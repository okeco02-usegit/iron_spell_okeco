package io.redspace.ironsspellbooks.api.backwards_compat;

import io.redspace.ironsspellbooks.gui.IronBookAccess;
import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import io.redspace.ironsspellbooks.network.OpenHeldBookPacket;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen.WrittenBookAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ClientHelper {
   public static void handleOpenBookPacket(OpenHeldBookPacket packet) {
      MinecraftInstanceHelper.ifPlayerPresent(player -> {
         ItemStack itemstack = player.m_21120_(InteractionHand.values()[packet.hand]);
         if (itemstack.m_41720_() instanceof ILecternPlaceable iLecternPlaceable) {
            Minecraft.m_91087_().m_91152_(new BookViewScreen(new IronBookAccess(iLecternPlaceable.getPages(itemstack))));
         } else {
            Minecraft.m_91087_().m_91152_(new BookViewScreen(new WrittenBookAccess(itemstack)));
         }
      });
   }
}
