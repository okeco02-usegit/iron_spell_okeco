package io.redspace.ironsspellbooks.gui.overlays;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ScreenEffectsOverlay implements IGuiOverlay {
   public static final ScreenEffectsOverlay instance = new ScreenEffectsOverlay();
   public static final ResourceLocation MAGIC_AURA_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/gui/overlays/enchanted_ward_vignette.png"
   );
   public static final ResourceLocation HEARTSTOP_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/gui/overlays/heartstop.png");
   public static final ResourceLocation ICE_BLOCK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/ice.png");

   public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
      if (!Minecraft.m_91087_().f_91066_.f_92062_ && !Minecraft.m_91087_().f_91074_.m_5833_()) {
         Player player = Minecraft.m_91087_().f_91074_;
         if (player != null) {
            if (player.m_21023_((MobEffect)MobEffectRegistry.HEARTSTOP.get())) {
               renderOverlayAdditive(guiHelper, HEARTSTOP_TEXTURE, 0.25F, 0.0F, 0.0F, 0.25F, screenWidth, screenHeight);
            }

            if (Minecraft.m_91087_().f_91066_.m_92176_().m_90612_() && player.m_20201_().m_6095_().equals(EntityRegistry.ICE_TOMB.get())) {
               renderOverlay(guiHelper, ICE_BLOCK_TEXTURE, 1.0F, 1.0F, 1.0F, 0.5F, screenWidth, screenHeight);
            }
         }
      }
   }

   private static void renderOverlayAdditive(GuiGraphics gui, ResourceLocation texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.ONE, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE);
      gui.m_280246_(r, g, b, a);
      gui.m_280398_(texture, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
      gui.m_280246_(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }

   private static void renderOverlay(GuiGraphics gui, ResourceLocation texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      gui.m_280246_(r, g, b, a);
      gui.m_280398_(texture, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      gui.m_280246_(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
