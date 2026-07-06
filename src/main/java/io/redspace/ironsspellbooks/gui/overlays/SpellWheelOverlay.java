package io.redspace.ironsspellbooks.gui.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Vector4f;

public class SpellWheelOverlay implements IGuiOverlay {
   public static SpellWheelOverlay instance = new SpellWheelOverlay();
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/gui/icons.png");
   private final Vector4f lineColor = new Vector4f(1.0F, 0.85F, 0.7F, 1.0F);
   private final Vector4f radialButtonColor = new Vector4f(0.04F, 0.03F, 0.01F, 0.6F);
   private final Vector4f highlightColor = new Vector4f(0.8F, 0.7F, 0.55F, 0.7F);
   private final double ringInnerEdge = 20.0;
   private double ringOuterEdge = 80.0;
   private final double ringOuterEdgeMax = 80.0;
   private final double ringOuterEdgeMin = 65.0;
   public boolean active;
   private int wheelSelection;
   private SpellSelectionManager swsm;

   public void open() {
      this.active = true;
      this.wheelSelection = -1;
      Minecraft.m_91087_().f_91067_.m_91602_();
   }

   public void close() {
      this.active = false;
      if (this.wheelSelection >= 0) {
         this.swsm.makeSelection(this.wheelSelection);
      }

      Minecraft.m_91087_().f_91067_.m_91601_();
   }

   public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
      if (this.active) {
         Minecraft minecraft = Minecraft.m_91087_();
         Player player = minecraft.f_91074_;
         if (player != null && minecraft.f_91080_ == null && !minecraft.f_91067_.m_91600_()) {
            this.swsm = ClientMagicData.getSpellSelectionManager();
            int totalSpellsAvailable = this.swsm.getSpellCount();
            if (totalSpellsAvailable <= 0) {
               this.close();
            } else {
               PoseStack poseStack = guiHelper.m_280168_();
               poseStack.m_85836_();
               int centerX = screenWidth / 2;
               int centerY = screenHeight / 2;
               Vec2 screenCenter = new Vec2(minecraft.m_91268_().m_85443_() * 0.5F, minecraft.m_91268_().m_85444_() * 0.5F);
               Vec2 mousePos = new Vec2((float)minecraft.f_91067_.m_91589_(), (float)minecraft.f_91067_.m_91594_());
               double radiansPerSpell = Math.toRadians(360.0F / totalSpellsAvailable);
               float mouseRotation = (Utils.getAngle(mousePos, screenCenter) + 1.57F + (float)radiansPerSpell * 0.5F) % 6.283F;
               this.wheelSelection = (int)Mth.m_14008_(mouseRotation / radiansPerSpell, 0.0, totalSpellsAvailable - 1);
               if (mousePos.m_165914_(screenCenter) < 4225.0) {
                  this.wheelSelection = Math.max(0, this.swsm.getSelectionIndex());
               }

               guiHelper.m_280509_(0, 0, screenWidth, screenHeight, 0);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               Tesselator tesselator = Tesselator.m_85913_();
               BufferBuilder buffer = tesselator.m_85915_();
               buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
               this.drawRadialBackgrounds(buffer, centerX, centerY, this.wheelSelection);
               this.drawDividingLines(buffer, centerX, centerY);
               tesselator.m_85914_();
               RenderSystem.disableBlend();
               SpellData selectedSpell = this.swsm.getSpellData(this.wheelSelection);
               int spellLevel = selectedSpell.getSpell().getLevelFor(selectedSpell.getLevel(), player);
               Font font = gui.m_93082_();
               List<MutableComponent> info = selectedSpell.getSpell().getUniqueInfo(spellLevel, minecraft.f_91074_);
               int textHeight = Math.max(3, info.size()) * 9 + 5;
               int textCenterMargin = 5;
               int textTitleMargin = 5;
               AbstractSpell spell = selectedSpell.getSpell();
               MutableComponent title = spell.getDisplayName(minecraft.f_91074_).m_130948_(Style.f_131099_.m_131162_(true));
               MutableComponent level = Component.m_237110_(
                  "ui.irons_spellbooks.level",
                  new Object[]{TooltipsUtils.getLevelComponenet(selectedSpell, player).m_130948_(spell.getRarity(spellLevel).getDisplayName().m_7383_())}
               );
               MutableComponent mana = Component.m_237110_("ui.irons_spellbooks.mana_cost", new Object[]{selectedSpell.getSpell().getManaCost(spellLevel)})
                  .m_130940_(ChatFormatting.AQUA);
               int cooldownTicks = MagicManager.getEffectiveSpellCooldown(spell, player, this.swsm.getSpellSlot(this.wheelSelection).getCastSource());
               MutableComponent cooldownTime = Component.m_237110_(
                     "tooltip.irons_spellbooks.cooldown_length_seconds", new Object[]{Utils.timeFromTicks(cooldownTicks, 2)}
                  )
                  .m_130940_(ChatFormatting.YELLOW);
               this.drawTextBackground(
                  guiHelper, centerX, centerY, this.ringOuterEdge + textHeight - textTitleMargin - 9.0, textCenterMargin, Math.max(2, info.size()) * 9
               );
               guiHelper.m_280614_(font, title, centerX - font.m_92852_(title) / 2, (int)(centerY - (this.ringOuterEdge + textHeight)), 16777215, true);
               int infoHeight = (int)(centerY - (this.ringOuterEdge + textHeight) + 9.0 + textTitleMargin);
               guiHelper.m_280614_(font, level, centerX - font.m_92852_(level) - textCenterMargin, infoHeight, 16777215, true);
               if (spell.getManaCost(spellLevel) > 0) {
                  infoHeight += 9;
                  guiHelper.m_280614_(font, mana, centerX - font.m_92852_(mana) - textCenterMargin, infoHeight, 16777215, true);
               }

               if (cooldownTicks > 0) {
                  infoHeight += 9;
                  guiHelper.m_280614_(font, cooldownTime, centerX - font.m_92852_(cooldownTime) - textCenterMargin, infoHeight, 16777215, true);
               }

               for (int i = 0; i < info.size(); i++) {
                  MutableComponent line = info.get(i);
                  guiHelper.m_280614_(
                     font, line, centerX + textCenterMargin, (int)(centerY - (80.0 + textHeight) + 9 * (i + 1) + textTitleMargin), 3924795, true
                  );
               }

               float scale = Mth.m_14179_(totalSpellsAvailable / 15.0F, 2.0F, 1.25F) * 0.65F;
               double radius = 3.0F / scale * 40.0 * 0.5 * (0.85F + 0.25F * (totalSpellsAvailable / 15.0F));
               Vec2[] locations = new Vec2[totalSpellsAvailable];

               for (int i = 0; i < locations.length; i++) {
                  locations[i] = new Vec2((float)(Math.sin(radiansPerSpell * i) * radius), (float)(-Math.cos(radiansPerSpell * i) * radius));
               }

               for (int i = 0; i < locations.length; i++) {
                  SpellData currentSpell = this.swsm.getSpellData(i);
                  if (currentSpell != null) {
                     ResourceLocation texture = currentSpell.getSpell().getSpellIconResource();
                     poseStack.m_85836_();
                     poseStack.m_252880_(centerX, centerY, 0.0F);
                     poseStack.m_85841_(scale, scale, scale);
                     int iconWidth = 8;
                     int borderWidth = 16;
                     int cdWidth = 8;
                     guiHelper.m_280163_(texture, (int)locations[i].f_82470_ - iconWidth, (int)locations[i].f_82471_ - iconWidth, 0.0F, 0.0F, 16, 16, 16, 16);
                     guiHelper.m_280218_(
                        TEXTURE,
                        (int)locations[i].f_82470_ - borderWidth,
                        (int)locations[i].f_82471_ - borderWidth,
                        this.swsm.getSelectionIndex() == i ? 32 : 0,
                        106,
                        32,
                        32
                     );
                     float f = ClientMagicData.getCooldownPercent(currentSpell.getSpell());
                     if (f > 0.0F) {
                        RenderSystem.enableBlend();
                        int pixels = (int)(16.0F * f + 1.0F);
                        guiHelper.m_280218_(TEXTURE, (int)locations[i].f_82470_ - cdWidth, (int)locations[i].f_82471_ + cdWidth - pixels, 47, 87, 16, pixels);
                     }

                     poseStack.m_85849_();
                  }
               }

               poseStack.m_85849_();
            }
         } else {
            this.close();
         }
      }
   }

   private void drawTextBackground(GuiGraphics guiHelper, double centerX, double centerY, double textYOffset, int textCenterMargin, int textHeight) {
      guiHelper.m_280509_(0, 0, (int)(centerX * 2.0), (int)(centerY * 2.0), 0);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
      centerY = centerY - textYOffset - 2.0;
      int heightMax = textHeight / 2 + 4;
      int heightMin = 0;
      int widthMax = 70;
      int widthMin = 0;
      int var18 = -1;
      int var17 = 1;
      buffer.m_5483_(centerX + var18, centerY + heightMin, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), 0.0F)
         .m_5752_();
      buffer.m_5483_(centerX + var18, centerY + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), this.radialButtonColor.w())
         .m_5752_();
      buffer.m_5483_(centerX + var17, centerY + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), this.radialButtonColor.w())
         .m_5752_();
      buffer.m_5483_(centerX + var17, centerY + heightMin, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), 0.0F)
         .m_5752_();
      buffer.m_5483_(centerX + var18, centerY + heightMin + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), this.radialButtonColor.w())
         .m_5752_();
      buffer.m_5483_(centerX + var18, centerY + heightMax + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), 0.0F)
         .m_5752_();
      buffer.m_5483_(centerX + var17, centerY + heightMax + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), 0.0F)
         .m_5752_();
      buffer.m_5483_(centerX + var17, centerY + heightMin + heightMax, 0.0)
         .m_85950_(this.radialButtonColor.x(), this.radialButtonColor.y(), this.radialButtonColor.z(), this.radialButtonColor.w())
         .m_5752_();
      tesselator.m_85914_();
      RenderSystem.disableBlend();
   }

   private void drawRadialBackgrounds(BufferBuilder buffer, double centerX, double centerY, int selectedSpellIndex) {
      double quarterCircle = Math.PI / 2;
      int totalSpellsAvailable = this.swsm.getSpellCount();
      int segments;
      if (totalSpellsAvailable < 6) {
         segments = totalSpellsAvailable % 2 == 1 ? 15 : 12;
      } else {
         segments = totalSpellsAvailable * 2;
      }

      double radiansPerObject = (Math.PI * 2) / segments;
      double radiansPerSpell = (Math.PI * 2) / totalSpellsAvailable;
      this.ringOuterEdge = Math.max(65.0, 80.0);

      for (int i = 0; i < segments; i++) {
         double beginRadians = i * radiansPerObject - (quarterCircle + radiansPerSpell / 2.0);
         double endRadians = (i + 1) * radiansPerObject - (quarterCircle + radiansPerSpell / 2.0);
         double x1m1 = Math.cos(beginRadians) * 20.0;
         double x2m1 = Math.cos(endRadians) * 20.0;
         double y1m1 = Math.sin(beginRadians) * 20.0;
         double y2m1 = Math.sin(endRadians) * 20.0;
         double x1m2 = Math.cos(beginRadians) * this.ringOuterEdge;
         double x2m2 = Math.cos(endRadians) * this.ringOuterEdge;
         double y1m2 = Math.sin(beginRadians) * this.ringOuterEdge;
         double y2m2 = Math.sin(endRadians) * this.ringOuterEdge;
         boolean isHighlighted = i * totalSpellsAvailable / segments == selectedSpellIndex;
         Vector4f color = this.radialButtonColor;
         if (isHighlighted) {
            color = this.highlightColor;
         }

         buffer.m_5483_(centerX + x1m1, centerY + y1m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
         buffer.m_5483_(centerX + x2m1, centerY + y2m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
         buffer.m_5483_(centerX + x2m2, centerY + y2m2, 0.0).m_85950_(color.x(), color.y(), color.z(), 0.0F).m_5752_();
         buffer.m_5483_(centerX + x1m2, centerY + y1m2, 0.0).m_85950_(color.x(), color.y(), color.z(), 0.0F).m_5752_();
         color = this.lineColor;
         double categoryLineWidth = 2.0;
         double categoryLineOuterEdge = 20.0 + categoryLineWidth;
         double x1m3 = Math.cos(beginRadians) * categoryLineOuterEdge;
         double x2m3 = Math.cos(endRadians) * categoryLineOuterEdge;
         double y1m3 = Math.sin(beginRadians) * categoryLineOuterEdge;
         double y2m3 = Math.sin(endRadians) * categoryLineOuterEdge;
         buffer.m_5483_(centerX + x1m1, centerY + y1m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
         buffer.m_5483_(centerX + x2m1, centerY + y2m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
         buffer.m_5483_(centerX + x2m3, centerY + y2m3, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
         buffer.m_5483_(centerX + x1m3, centerY + y1m3, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
      }
   }

   private void drawDividingLines(BufferBuilder buffer, double centerX, double centerY) {
      int totalSpellsAvailable = this.swsm.getSpellCount();
      if (totalSpellsAvailable > 1) {
         double quarterCircle = Math.PI / 2;
         double radiansPerSpell = (Math.PI * 2) / totalSpellsAvailable;
         this.ringOuterEdge = Math.max(65.0, 80.0);

         for (int i = 0; i < totalSpellsAvailable; i++) {
            double closeWidth = 0.13962634F;
            double farWidth = 0.034906585F;
            double beginCloseRadians = i * radiansPerSpell - (quarterCircle + radiansPerSpell / 2.0) - 0.034906585F;
            double endCloseRadians = beginCloseRadians + 0.13962634F;
            double beginFarRadians = i * radiansPerSpell - (quarterCircle + radiansPerSpell / 2.0) - 0.008726646F;
            double endFarRadians = beginCloseRadians + 0.034906585F;
            double x1m1 = Math.cos(beginCloseRadians) * 20.0;
            double x2m1 = Math.cos(endCloseRadians) * 20.0;
            double y1m1 = Math.sin(beginCloseRadians) * 20.0;
            double y2m1 = Math.sin(endCloseRadians) * 20.0;
            double x1m2 = Math.cos(beginFarRadians) * this.ringOuterEdge * 1.4;
            double x2m2 = Math.cos(endFarRadians) * this.ringOuterEdge * 1.4;
            double y1m2 = Math.sin(beginFarRadians) * this.ringOuterEdge * 1.4;
            double y2m2 = Math.sin(endFarRadians) * this.ringOuterEdge * 1.4;
            Vector4f color = this.lineColor;
            buffer.m_5483_(centerX + x1m1, centerY + y1m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
            buffer.m_5483_(centerX + x2m1, centerY + y2m1, 0.0).m_85950_(color.x(), color.y(), color.z(), color.w()).m_5752_();
            buffer.m_5483_(centerX + x2m2, centerY + y2m2, 0.0).m_85950_(color.x(), color.y(), color.z(), 0.0F).m_5752_();
            buffer.m_5483_(centerX + x1m2, centerY + y1m2, 0.0).m_85950_(color.x(), color.y(), color.z(), 0.0F).m_5752_();
         }
      }
   }

   private void setOpaqueTexture(ResourceLocation texture) {
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, texture);
   }

   private void setTranslucentTexture(ResourceLocation texture) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::m_172649_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, texture);
   }

   private boolean inTriangle(double x1, double y1, double x2, double y2, double x3, double y3, double x, double y) {
      double ab = (x1 - x) * (y2 - y) - (x2 - x) * (y1 - y);
      double bc = (x2 - x) * (y3 - y) - (x3 - x) * (y2 - y);
      double ca = (x3 - x) * (y1 - y) - (x1 - x) * (y3 - y);
      return this.sign(ab) == this.sign(bc) && this.sign(bc) == this.sign(ca);
   }

   private int sign(double n) {
      return n > 0.0 ? 1 : -1;
   }
}
