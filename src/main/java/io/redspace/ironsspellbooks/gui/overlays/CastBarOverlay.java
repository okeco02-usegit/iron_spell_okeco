package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CastBarOverlay implements IGuiOverlay {
   public static CastBarOverlay instance = new CastBarOverlay();
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/gui/icons.png");
   static final int IMAGE_WIDTH = 54;
   static final int COMPLETION_BAR_WIDTH = 44;
   static final int IMAGE_HEIGHT = 21;

   public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
      if (!Minecraft.m_91087_().f_91066_.f_92062_ && !Minecraft.m_91087_().f_91074_.m_5833_()) {
         if (ClientMagicData.isCasting() && (!ClientMagicData.isCasting() || ClientMagicData.getCastType() != CastType.INSTANT)) {
            float castCompletionPercent = ClientMagicData.getCastCompletionPercent();
            String castTimeString = Utils.timeFromTicks((1.0F - castCompletionPercent) * ClientMagicData.getCastDuration(), 1);
            if (ClientMagicData.getCastType() == CastType.CONTINUOUS) {
               castCompletionPercent = 1.0F - castCompletionPercent;
            }

            int barX = screenWidth / 2 - 27;
            int barY = screenHeight / 2 + screenHeight / 8;
            guiHelper.m_280163_(TEXTURE, barX, barY, 0.0F, 42.0F, 54, 21, 256, 256);
            guiHelper.m_280218_(TEXTURE, barX, barY, 0, 63, (int)(44.0F * castCompletionPercent + 5.0F), 21);
            Font font = Minecraft.m_91087_().f_91062_;
            int textX = barX + (54 - font.m_92895_(castTimeString)) / 2;
            int textY = barY + 10 - 9 / 2 + 1;
            guiHelper.m_280488_(font, castTimeString, textX, textY, 16777215);
         }
      }
   }
}
