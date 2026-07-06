package io.redspace.ironsspellbooks.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class BossbarManager {
   private static final Map<UUID, BossbarManager.BossbarSprite> CUSTOM_BARS = new HashMap<>();

   public static void startTracking(UUID uuid, BossbarManager.BossbarSprite sprite) {
      CUSTOM_BARS.put(uuid, sprite);
   }

   public static void stopTracking(UUID uuid) {
      CUSTOM_BARS.remove(uuid);
   }

   @SubscribeEvent
   public static void renderCustomBossbar(BossEventProgress event) {
      BossbarManager.BossbarSprite customSprite = CUSTOM_BARS.get(event.getBossEvent().m_18860_());
      if (customSprite != null) {
         GuiGraphics guiGraphics = event.getGuiGraphics();
         int y = event.getY() + customSprite.yBarOffset;
         int x = (guiGraphics.m_280182_() - customSprite.width) / 2;
         RenderSystem.enableBlend();
         ResourceLocation sprite = customSprite.spriteLocation.m_246208_("textures/gui/sprites/").m_266382_(".png");
         guiGraphics.m_280163_(sprite, x, y, 0.0F, 0.0F, customSprite.width, customSprite.height, customSprite.width, customSprite.height * 2);
         int progress = Mth.m_269140_(event.getBossEvent().m_142717_(), 0, customSprite.width - customSprite.buffer * 2) + customSprite.buffer;
         if (progress > 0) {
            guiGraphics.m_280163_(sprite, x, y, 0.0F, customSprite.height, progress, customSprite.height, customSprite.width, customSprite.height * 2);
         }

         RenderSystem.disableBlend();
         Component component = event.getBossEvent().m_18861_();
         int l = Minecraft.m_91087_().f_91062_.m_92852_(component);
         int i1 = guiGraphics.m_280182_() / 2 - l / 2;
         int j1 = y - 9 - customSprite.yBarOffset;
         event.setIncrement(event.getIncrement() - 5 + customSprite.height + customSprite.yBarOffset);
         guiGraphics.m_280430_(Minecraft.m_91087_().f_91062_, component, i1, j1, 16777215);
         event.setCanceled(true);
      }
   }

   public record BossbarSprite(ResourceLocation spriteLocation, int width, int height, int buffer, int yBarOffset) {
   }
}
