package io.redspace.ironsspellbooks.gui.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Vector2ic;
import org.joml.Vector4i;

public class ScreenTooltipOverlay implements IGuiOverlay {
   public static final ScreenTooltipOverlay instance = new ScreenTooltipOverlay();
   ScreenTooltipOverlay.RenderInfo toRender = null;

   public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
      if (!Minecraft.m_91087_().f_91066_.f_92062_ && this.toRender != null) {
         PoseStack pose = guiHelper.m_280168_();
         Font font = Minecraft.m_91087_().f_91062_;
         List<ClientTextTooltip> components = Language.m_128107_()
            .m_128112_(this.toRender.tooltip.stream().map(ScreenTooltipOverlay::cast).toList())
            .stream()
            .<ClientTextTooltip>map(ClientTextTooltip::new)
            .toList();
         int maxTextWidth = 0;
         int totalHeight = components.size() == 1 ? -2 : 0;

         for (ClientTooltipComponent clienttooltipcomponent : components) {
            int k = clienttooltipcomponent.m_142069_(font);
            if (k > maxTextWidth) {
               maxTextWidth = k;
            }

            totalHeight += clienttooltipcomponent.m_142103_();
         }

         Vector2ic vector2ic = this.toRender.positioner.m_262814_(guiHelper.m_280182_(), guiHelper.m_280206_(), 0, 0, maxTextWidth, totalHeight);
         int x = vector2ic.x();
         int y = vector2ic.y();
         pose.m_85836_();
         Integer bgColor1 = this.toRender.colors.<Integer>map(Vector4i::x).orElse(-1877999600);
         Integer bgColor2 = this.toRender.colors.<Integer>map(Vector4i::y).orElse(-1877999600);
         Integer edgeColor1 = this.toRender.colors.<Integer>map(Vector4i::z).orElse(1884291327);
         Integer edgeColor2 = this.toRender.colors.<Integer>map(Vector4i::w).orElse(1881669759);
         TooltipRenderUtil.renderTooltipBackground(guiHelper, x, y, maxTextWidth, totalHeight, 400, bgColor1, bgColor2, edgeColor1, edgeColor2);
         guiHelper.m_280262_();
         pose.m_252880_(0.0F, 0.0F, 400.0F);
         int lineY = y;

         for (int l1 = 0; l1 < components.size(); l1++) {
            ClientTooltipComponent clienttooltipcomponent1 = (ClientTooltipComponent)components.get(l1);
            clienttooltipcomponent1.m_142440_(font, x, lineY, pose.m_85850_().m_252922_(), guiHelper.m_280091_());
            lineY += clienttooltipcomponent1.m_142103_() + (l1 == 0 ? 2 : 0);
         }

         pose.m_85849_();
         this.toRender = null;
      }
   }

   private static FormattedText cast(Component component) {
      return component;
   }

   public static void renderTooltip(List<Component> tooltip, ClientTooltipPositioner positioner) {
      instance.toRender = new ScreenTooltipOverlay.RenderInfo(tooltip, positioner, Optional.empty());
   }

   public static void renderTooltip(List<Component> tooltip, ClientTooltipPositioner positioner, Vector4i colors) {
      instance.toRender = new ScreenTooltipOverlay.RenderInfo(tooltip, positioner, Optional.of(colors));
   }

   private record RenderInfo(List<Component> tooltip, ClientTooltipPositioner positioner, Optional<Vector4i> colors) {
   }
}
