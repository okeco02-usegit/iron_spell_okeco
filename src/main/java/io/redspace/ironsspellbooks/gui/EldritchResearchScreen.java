package io.redspace.ironsspellbooks.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.network.spells.LearnSpellPacket;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class EldritchResearchScreen extends Screen {
   private static final ResourceLocation WINDOW_LOCATION = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/gui/eldritch_research_screen/window.png"
   );
   private static final ResourceLocation FRAME_LOCATION = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/gui/eldritch_research_screen/spell_frame.png"
   );
   public static final int WINDOW_WIDTH = 252;
   public static final int WINDOW_HEIGHT = 256;
   private static final int WINDOW_INSIDE_X = 9;
   private static final int WINDOW_INSIDE_Y = 18;
   public static final int WINDOW_INSIDE_WIDTH = 234;
   public static final int WINDOW_INSIDE_HEIGHT = 229;
   private static final int WINDOW_TITLE_X = 8;
   private static final int WINDOW_TITLE_Y = 6;
   public static final int BACKGROUND_TILE_WIDTH = 16;
   public static final int BACKGROUND_TILE_HEIGHT = 16;
   public static final int BACKGROUND_TILE_COUNT_X = 14;
   public static final int BACKGROUND_TILE_COUNT_Y = 7;
   int leftPos;
   int topPos;
   InteractionHand activeHand;
   List<AbstractSpell> learnableSpells;
   List<EldritchResearchScreen.SpellNode> nodes;
   SyncedSpellData playerData;
   Vec2 maxViewportOffset;
   Vec2 viewportOffset;
   boolean isMouseHoldingSpell;
   boolean isMouseDragging;
   int heldSpellIndex = -1;
   int heldSpellTime = -1;
   int lastPlayerTick;
   static final int TIME_TO_HOLD = 15;
   private static final Component ALREADY_LEARNED = Component.m_237115_("ui.irons_spellbooks.research_already_learned").m_130940_(ChatFormatting.DARK_AQUA);
   private static final Component UNLEARNED = Component.m_237115_("ui.irons_spellbooks.research_warning").m_130940_(ChatFormatting.RED);

   public EldritchResearchScreen(Component pTitle, InteractionHand activeHand) {
      super(pTitle);
      this.activeHand = activeHand;
   }

   protected void m_7856_() {
      this.learnableSpells = SpellRegistry.getEnabledSpells().stream().filter(spell -> spell.getSchoolType().equals(SchoolRegistry.ELDRITCH.get())).toList();
      if (this.f_96541_ != null) {
         this.playerData = ClientMagicData.getSyncedSpellData(this.f_96541_.f_91074_);
      }

      this.viewportOffset = Vec2.f_82462_;
      this.leftPos = (this.f_96543_ - 252) / 2;
      this.topPos = (this.f_96544_ - 256) / 2;
      this.nodes = new ArrayList<>();
      RandomSource randomSource = RandomSource.m_216335_(431L);
      float f = (float) (Math.PI / 3);
      float r = 35.0F;
      float circumference = 0.0F;
      float offset = 0.5F;
      float a = offset;

      for (int i = 0; i < this.learnableSpells.size(); i++) {
         if (circumference > r * (float) (Math.PI * 2)) {
            r += 40.0F;
            f = 35.0F / r;
            a -= f;
            circumference = 0.0F;
         }

         a += f;
         int x = this.leftPos + 126 - 8 + (int)(r * Mth.m_14089_(a));
         int y = this.topPos + 128 - 8 + (int)(r * Mth.m_14031_(a));
         this.nodes.add(new EldritchResearchScreen.SpellNode(this.learnableSpells.get(i), x, y));
         circumference += r * f * 1.1F;
      }

      float maxDistX = 0.0F;
      float maxDistY = 0.0F;

      for (int i = 0; i < this.nodes.size(); i++) {
         for (int j = 1; j < this.nodes.size(); j++) {
            int x = Math.abs(this.nodes.get(i).x - this.nodes.get(j).x);
            if (x > maxDistX) {
               maxDistX = x;
            }

            int y = Math.abs(this.nodes.get(i).y - this.nodes.get(j).y);
            if (y > maxDistY) {
               maxDistY = y;
            }
         }
      }

      this.maxViewportOffset = new Vec2((int)maxDistX, (int)maxDistY);
   }

   public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.m_280024_(0, 0, this.f_96543_, this.f_96544_, -1072689136, -804253680);
      this.drawBackdrop(guiGraphics, this.leftPos + 9, this.topPos + 18);
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         if (player.f_19797_ != this.lastPlayerTick) {
            this.lastPlayerTick = player.f_19797_;
            if (this.isMouseHoldingSpell
               && this.heldSpellIndex >= 0
               && this.heldSpellIndex < this.nodes.size()
               && !this.nodes.get(this.heldSpellIndex).spell.isLearned(player)) {
               if (this.heldSpellTime > 15) {
                  this.heldSpellTime = -1;
                  PacketDistributor.sendToServer(new LearnSpellPacket(this.activeHand, this.nodes.get(this.heldSpellIndex).spell.getSpellId()));
                  player.m_6330_((SoundEvent)SoundRegistry.LEARN_ELDRITCH_SPELL.get(), SoundSource.MASTER, 1.0F, Utils.random.m_216332_(9, 11) * 0.1F);
               }

               this.heldSpellTime++;
               if (this.lastPlayerTick % 2 == 0) {
                  player.m_6330_(SoundEvents.f_12404_, SoundSource.MASTER, 1.0F, Mth.m_14179_(this.heldSpellTime / 15.0F, 0.5F, 1.5F));
                  player.m_6330_((SoundEvent)SoundRegistry.UI_TICK.get(), SoundSource.MASTER, 1.0F, Mth.m_14179_(this.heldSpellTime / 15.0F, 0.5F, 1.5F));
               }
            } else if (this.heldSpellTime >= 0) {
               this.heldSpellTime = Math.max(this.heldSpellTime - 3, -1);
            }
         }

         this.handleConnections(guiGraphics, partialTick);
         List<FormattedCharSequence> tooltip = null;

         for (int i = 0; i < this.nodes.size(); i++) {
            EldritchResearchScreen.SpellNode node = this.nodes.get(i);
            this.drawNode(guiGraphics, node, player, i == this.heldSpellIndex && this.heldSpellTime > 0);
            if (this.isHoveringNode(node, mouseX, mouseY)) {
               tooltip = buildTooltip(node.spell, this.f_96547_);
            }
         }

         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         guiGraphics.m_280218_(WINDOW_LOCATION, this.leftPos, this.topPos, 0, 0, 252, 256);
         if (tooltip != null) {
            guiGraphics.m_280245_(Minecraft.m_91087_().f_91062_, tooltip, mouseX, mouseY);
         }
      }
   }

   private void renderProgressOverlay(GuiGraphics gui, int x, int y, float progress) {
      x += (int)this.viewportOffset.f_82470_;
      y += (int)this.viewportOffset.f_82471_;
      gui.m_280509_(x, y, x + Mth.m_14167_(16.0F * progress), y + 16, ARGB32.m_13660_(127, 244, 65, 255));
   }

   private void drawNode(GuiGraphics guiGraphics, EldritchResearchScreen.SpellNode node, LocalPlayer player, boolean drawProgress) {
      this.drawWithClipping(node.spell.getSpellIconResource(), guiGraphics, node.x, node.y, 0, 0, 16, 16, 16, 16, this.leftPos + 9, this.topPos + 18, 234, 229);
      if (drawProgress) {
         this.renderProgressOverlay(guiGraphics, node.x, node.y, this.heldSpellTime / 15.0F);
      }

      this.drawWithClipping(
         FRAME_LOCATION,
         guiGraphics,
         node.x - 8,
         node.y - 8,
         node.spell.isLearned(player) ? 32 : 0,
         0,
         32,
         32,
         64,
         32,
         this.leftPos + 9,
         this.topPos + 18,
         234,
         229
      );
   }

   private void drawWithClipping(
      ResourceLocation texture,
      GuiGraphics guiGraphics,
      int x,
      int y,
      int uvx,
      int uvy,
      int width,
      int height,
      int imageWidth,
      int imageHeight,
      int bbx,
      int bby,
      int bbw,
      int bbh
   ) {
      x += (int)this.viewportOffset.f_82470_;
      if (x < bbx) {
         int xDiff = bbx - x;
         width -= xDiff;
         uvx += xDiff;
         x += xDiff;
      } else if (x > bbx + bbw - width) {
         int xDiff = x - (bbx + bbw - width);
         width -= xDiff;
      }

      y += (int)this.viewportOffset.f_82471_;
      if (y < bby) {
         int yDiff = bby - y;
         height -= yDiff;
         uvy += yDiff;
         y += yDiff;
      } else if (y > bby + bbh - height) {
         int yDiff = y - (bby + bbh - height);
         height -= yDiff;
      }

      if (width > 0 && height > 0) {
         guiGraphics.m_280411_(texture, x, y, width, height, uvx, uvy, width, height, imageWidth, imageHeight);
      }
   }

   public static List<FormattedCharSequence> buildTooltip(AbstractSpell spell, Font font) {
      boolean learned = spell.isLearned(Minecraft.m_91087_().f_91074_);
      MutableComponent name = spell.getDisplayName(null).m_130940_(learned ? ChatFormatting.DARK_AQUA : ChatFormatting.RED);
      List<FormattedCharSequence> description = font.m_92923_(
         Component.m_237115_(String.format("%s.guide", spell.getComponentId())).m_130940_(ChatFormatting.GRAY), 180
      );
      ArrayList<FormattedCharSequence> hoverText = new ArrayList<>();
      hoverText.add(FormattedCharSequence.m_13714_(name.getString(), name.m_7383_().m_131162_(true)));
      hoverText.addAll(description);
      hoverText.add(FormattedCharSequence.f_13691_);
      hoverText.add((learned ? ALREADY_LEARNED : UNLEARNED).m_7532_());
      return hoverText;
   }

   private void handleConnections(GuiGraphics guiGraphics, float partialTick) {
      guiGraphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, 0);
      RenderSystem.enableDepthTest();
      float f = Mth.m_14031_((Minecraft.m_91087_().f_91074_.f_19797_ + partialTick) * 0.1F);
      float glowIntensity = f * f * 0.8F + 0.2F;
      Vector4f color = new Vector4f(0.5294118F, 0.6039216F, 0.68235296F, 0.5F);
      Vector4f glowcolor = new Vector4f(0.95686275F, 0.25490198F, 1.0F, 0.5F);
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);

      for (int i = 0; i < this.nodes.size() - 1; i++) {
         Vec2 a = new Vec2(this.nodes.get(i).x, this.nodes.get(i).y);
         Vec2 b = new Vec2(this.nodes.get(i + 1).x, this.nodes.get(i + 1).y);
         Vec2 orth = new Vec2(-(b.f_82471_ - a.f_82471_), b.f_82470_ - a.f_82470_).m_165902_().m_165903_(1.5F);
         float x1m1 = a.f_82470_ + orth.f_82470_ + 8.0F + (int)this.viewportOffset.f_82470_;
         float x2m1 = b.f_82470_ + orth.f_82470_ + 8.0F + (int)this.viewportOffset.f_82470_;
         float y1m1 = a.f_82471_ + orth.f_82471_ + 8.0F + (int)this.viewportOffset.f_82471_;
         float y2m1 = b.f_82471_ + orth.f_82471_ + 8.0F + (int)this.viewportOffset.f_82471_;
         float x1m2 = a.f_82470_ - orth.f_82470_ + 8.0F + (int)this.viewportOffset.f_82470_;
         float x2m2 = b.f_82470_ - orth.f_82470_ + 8.0F + (int)this.viewportOffset.f_82470_;
         float y1m2 = a.f_82471_ - orth.f_82471_ + 8.0F + (int)this.viewportOffset.f_82471_;
         float y2m2 = b.f_82471_ - orth.f_82471_ + 8.0F + (int)this.viewportOffset.f_82471_;
         Vector4f color1 = lerpColor(color, glowcolor, glowIntensity * (this.nodes.get(i).spell.isLearned(Minecraft.m_91087_().f_91074_) ? 1 : 0));
         Vector4f color2 = lerpColor(color, glowcolor, glowIntensity * (this.nodes.get(i + 1).spell.isLearned(Minecraft.m_91087_().f_91074_) ? 1 : 0));
         RenderHelper.quadBuilder()
            .vertex(x1m1, y1m1)
            .color(this.fadeOutTowardEdges(guiGraphics, x1m1, y1m1, color1))
            .vertex(x2m1, y2m1)
            .color(this.fadeOutTowardEdges(guiGraphics, x2m1, y2m1, color2))
            .vertex(x2m2, y2m2)
            .color(this.fadeOutTowardEdges(guiGraphics, x2m2, y2m2, color2))
            .vertex(x1m2, y1m2)
            .color(this.fadeOutTowardEdges(guiGraphics, x1m2, y1m2, color1))
            .build(buffer);
      }

      tesselator.m_85914_();
   }

   private Vector4f fadeOutTowardEdges(GuiGraphics guiGraphics, double x, double y, Vector4f color) {
      float margin = 40.0F;
      int maxWidth = 252;
      int maxHeight = 256;
      int boundXMin = (int)Mth.m_14008_(x + this.viewportOffset.f_82470_ - this.leftPos, 0.0, maxWidth);
      int boundXMax = maxWidth - (int)Mth.m_14008_(x + this.viewportOffset.f_82470_ - this.leftPos, 0.0, maxWidth);
      int boundYMin = (int)Mth.m_14008_(y + this.viewportOffset.f_82471_ - this.topPos, 0.0, maxHeight);
      int boundYMax = maxHeight - (int)Mth.m_14008_(y + this.viewportOffset.f_82471_ - this.topPos, 0.0, maxHeight);
      float px = Mth.m_14036_(Math.min(boundXMin, boundXMax) / margin, 0.0F, 1.0F);
      float py = Mth.m_14036_(Math.min(boundYMin, boundYMax) / margin, 0.0F, 1.0F);
      float alpha = Mth.m_14116_(px * py);
      return new Vector4f(color.x, color.y, color.z, color.w * alpha);
   }

   private int colorFromRGBA(Vector4f rgba) {
      int r = (int)(rgba.x() * 255.0F) & 0xFF;
      int g = (int)(rgba.y() * 255.0F) & 0xFF;
      int b = (int)(rgba.z() * 255.0F) & 0xFF;
      int a = (int)(rgba.w() * 255.0F) & 0xFF;
      return (r << 24) + (g << 16) + (b << 8) + a;
   }

   private void drawBackdrop(GuiGraphics guiGraphics, int left, int top) {
      float f = Minecraft.m_91087_().f_91074_ != null ? Minecraft.m_91087_().f_91074_.f_19797_ * 0.02F : 0.0F;
      float color = (Mth.m_14031_(f) + 1.0F) * 0.25F + 0.15F;
      RenderHelper.QuadBuilder definitelynothowabuilderworks = RenderHelper.quadBuilder()
         .vertex(left, top + 229)
         .vertex(left + 234, top + 229)
         .vertex(left + 234, top)
         .vertex(left, top)
         .color(0.0F, 0.0F, 0.0F, color);
      definitelynothowabuilderworks.build(guiGraphics, RenderType.m_173239_());
      definitelynothowabuilderworks.build(guiGraphics, RenderType.m_286086_());
   }

   private static Vector4f lerpColor(Vector4f a, Vector4f b, float pDelta) {
      float f = 1.0F - pDelta;
      float x = a.x() * f + b.x() * pDelta;
      float y = a.y() * f + b.y() * pDelta;
      float z = a.z() * f + b.z() * pDelta;
      float w = a.w() * f + b.w() * pDelta;
      return new Vector4f(x, y, z, w);
   }

   public boolean m_6375_(double pMouseX, double pMouseY, int pButton) {
      int mouseX = (int)pMouseX;
      int mouseY = (int)pMouseY;
      if (Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_21120_(this.activeHand).m_150930_((Item)ItemRegistry.ELDRITCH_PAGE.get())) {
         for (int i = 0; i < this.nodes.size(); i++) {
            if (this.isHoveringNode(this.nodes.get(i), mouseX, mouseY)) {
               this.heldSpellIndex = i;
               this.isMouseHoldingSpell = true;
               break;
            }
         }
      }

      if (!this.isMouseHoldingSpell && this.isHovering(this.leftPos + 9, this.topPos + 18, 234, 229, mouseX, mouseY)) {
         this.isMouseDragging = true;
      }

      return super.m_6375_(pMouseX, pMouseY, pButton);
   }

   public boolean isHoveringNode(EldritchResearchScreen.SpellNode node, int mouseX, int mouseY) {
      return this.isHovering(node.x - 2 + (int)this.viewportOffset.f_82470_, node.y - 2 + (int)this.viewportOffset.f_82471_, 20, 20, mouseX, mouseY);
   }

   public boolean m_6348_(double pMouseX, double pMouseY, int pButton) {
      this.isMouseHoldingSpell = false;
      this.isMouseDragging = false;
      return super.m_6348_(pMouseX, pMouseY, pButton);
   }

   public boolean m_7979_(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (this.isMouseDragging) {
      }

      return super.m_7979_(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   public boolean m_7933_(int pKeyCode, int pScanCode, int pModifiers) {
      Key mouseKey = InputConstants.m_84827_(pKeyCode, pScanCode);
      if (this.f_96541_.f_91066_.f_92092_.isActiveAndMatches(mouseKey)) {
         this.m_7379_();
         return true;
      } else {
         return super.m_7933_(pKeyCode, pScanCode, pModifiers);
      }
   }

   public boolean m_7043_() {
      return false;
   }

   private boolean isHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
      return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
   }

   record NodeConnection(EldritchResearchScreen.SpellNode node1, EldritchResearchScreen.SpellNode node2) {
   }

   record SpellNode(AbstractSpell spell, int x, int y) {
   }
}
