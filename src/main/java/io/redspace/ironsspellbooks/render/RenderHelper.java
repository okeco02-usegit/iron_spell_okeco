package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.OffsetTexturingStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderHelper {
   public static int colorLerp(float f, int colorA, int colorB) {
      int redA = colorA >> 16 & 0xFF;
      int greenA = colorA >> 8 & 0xFF;
      int blueA = colorA & 0xFF;
      int redB = colorB >> 16 & 0xFF;
      int greenB = colorB >> 8 & 0xFF;
      int blueB = colorB & 0xFF;
      return color255((int)Mth.m_14179_(f, redA, redB), (int)Mth.m_14179_(f, greenA, greenB), (int)Mth.m_14179_(f, blueA, blueB));
   }

   public static int color255(int pRed, int pGreen, int pBlue, int pAlpha) {
      return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
   }

   public static int color255(int pRed, int pGreen, int pBlue) {
      return color255(pRed, pGreen, pBlue, 255);
   }

   public static int colorf(float pRed, float pGreen, float pBlue, float pAlpha) {
      return color255((int)(255.0F * pRed), (int)(255.0F * pGreen), (int)(255.0F * pBlue), (int)(255.0F * pAlpha));
   }

   public static int colorf(float pRed, float pGreen, float pBlue) {
      return colorf(pRed, pGreen, pBlue, 1.0F);
   }

   public static RenderHelper.QuadBuilder quadBuilder() {
      return new RenderHelper.QuadBuilder();
   }

   public static class CustomerRenderType extends RenderType {
      protected static final TransparencyStateShard ONE_MINUS = new TransparencyStateShard("one_minus", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_SRC_ALPHA, DestFactor.SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      });
      private static final Function<ResourceLocation, RenderType> DARK_PORTAL_GLOW = Util.m_143827_(
         pLocation -> m_173215_(
            "dark_portal",
            DefaultVertexFormat.f_85812_,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.m_110628_()
               .m_173292_(f_173074_)
               .m_173290_(new TextureStateShard(pLocation, false, false))
               .m_110685_(ONE_MINUS)
               .m_110661_(f_110110_)
               .m_110671_(f_110152_)
               .m_110677_(f_110154_)
               .m_110691_(false)
         )
      );
      private static final Function<ResourceLocation, RenderType> MAGIC = Util.m_143827_(
         pLocation -> m_173215_(
            "magic_glow",
            DefaultVertexFormat.f_85812_,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.m_110628_()
               .m_173292_(f_173074_)
               .m_173290_(new TextureStateShard(pLocation, false, false))
               .m_110685_(f_110135_)
               .m_110661_(f_110158_)
               .m_110671_(f_110152_)
               .m_110677_(f_110154_)
               .m_110691_(false)
         )
      );
      private static final Function<ResourceLocation, RenderType> MAGIC_NO_CULL = Util.m_143827_(
         pLocation -> m_173215_(
            "magic_glow_no_cull",
            DefaultVertexFormat.f_85812_,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.m_110628_()
               .m_173292_(f_173074_)
               .m_173290_(new TextureStateShard(pLocation, false, false))
               .m_110685_(f_110135_)
               .m_110661_(f_110110_)
               .m_110671_(f_110152_)
               .m_110677_(f_110154_)
               .m_110691_(false)
         )
      );
      public static final Function<ResourceLocation, RenderType> PYRIUM_STAFF_ORB = Util.m_143827_(
         pLocation -> m_173215_(
            "dark_portal_cull",
            DefaultVertexFormat.f_85812_,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.m_110628_()
               .m_173292_(f_173074_)
               .m_173290_(new TextureStateShard(pLocation, false, false))
               .m_110685_(f_110134_)
               .m_110661_(f_110158_)
               .m_110671_(f_110152_)
               .m_110677_(f_110154_)
               .m_110691_(false)
         )
      );

      public CustomerRenderType(
         String pName,
         VertexFormat pFormat,
         Mode pMode,
         int pBufferSize,
         boolean pAffectsCrumbling,
         boolean pSortOnUpload,
         Runnable pSetupState,
         Runnable pClearState
      ) {
         super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
      }

      @NotNull
      public static RenderType darkGlow(@NotNull ResourceLocation pLocation) {
         return DARK_PORTAL_GLOW.apply(pLocation);
      }

      public static RenderType magic(ResourceLocation pLocation) {
         return MAGIC.apply(pLocation);
      }

      public static RenderType magicNoCull(ResourceLocation pLocation) {
         return MAGIC_NO_CULL.apply(pLocation);
      }

      public static RenderType magicSwirl(ResourceLocation pLocation, float pU, float pV) {
         return m_173215_(
            "magic_glow_swirl",
            DefaultVertexFormat.f_85812_,
            Mode.QUADS,
            256,
            false,
            true,
            CompositeState.m_110628_()
               .m_173292_(f_173074_)
               .m_173290_(new TextureStateShard(pLocation, false, false))
               .m_110683_(new OffsetTexturingStateShard(pU, pV))
               .m_110685_(f_110135_)
               .m_110661_(f_110158_)
               .m_110671_(f_110152_)
               .m_110677_(f_110154_)
               .m_110691_(false)
         );
      }
   }

   public static class QuadBuilder {
      List<Vector3f> verticies;
      List<Vector3f> normals;
      List<Vec2> uvs;
      List<Integer> colors;
      Integer light = null;
      Integer overlay = null;
      @Nullable
      Matrix4f matrix;
      @Nullable
      Matrix3f normalmatrix;

      private QuadBuilder() {
         this.verticies = new ArrayList<>();
         this.normals = new ArrayList<>();
         this.uvs = new ArrayList<>();
         this.colors = new IntArrayList();
      }

      public RenderHelper.QuadBuilder vertex(float x, float y) {
         this.verticies.add(new Vector3f(x, y, 0.0F));
         return this;
      }

      public RenderHelper.QuadBuilder vertex(float x, float y, float z) {
         this.verticies.add(new Vector3f(x, y, z));
         return this;
      }

      public RenderHelper.QuadBuilder uv(float u, float v) {
         this.uvs.add(new Vec2(u, v));
         return this;
      }

      public RenderHelper.QuadBuilder normal(float x, float y, float z) {
         this.normals.add(new Vector3f(x, y, z));
         return this;
      }

      public RenderHelper.QuadBuilder normal(Matrix3f normalmatrix, float x, float y, float z) {
         this.normalmatrix = normalmatrix;
         this.normals.add(new Vector3f(x, y, z));
         return this;
      }

      public RenderHelper.QuadBuilder matrix(Matrix4f matrix) {
         this.matrix = matrix;
         return this;
      }

      public RenderHelper.QuadBuilder color(Vector4f color) {
         this.colors.add(RenderHelper.colorf(color.x, color.y, color.z, color.w));
         return this;
      }

      public RenderHelper.QuadBuilder color(int color) {
         this.colors.add(color);
         return this;
      }

      public RenderHelper.QuadBuilder color(Vector3f color) {
         return this.color(new Vector4f(color.x, color.y, color.z, 1.0F));
      }

      public RenderHelper.QuadBuilder color(float r, float g, float b) {
         return this.color(r, g, b, 1.0F);
      }

      public RenderHelper.QuadBuilder color(float r, float g, float b, float a) {
         return this.color(new Vector4f(r, g, b, a));
      }

      public RenderHelper.QuadBuilder light(int light) {
         this.light = light;
         return this;
      }

      public RenderHelper.QuadBuilder overlay(int overlay) {
         this.overlay = overlay;
         return this;
      }

      public void build(VertexConsumer consumer) {
         for (int i = 0; i < this.verticies.size(); i++) {
            Vector3f vertex = this.verticies.get(i);
            int color;
            if (this.colors.isEmpty()) {
               color = 16777215;
            } else if (this.colors.size() != 1 && this.colors.size() == this.verticies.size()) {
               color = this.colors.get(i);
            } else {
               color = this.colors.get(0);
            }

            if (this.matrix != null) {
               consumer.m_252986_(this.matrix, vertex.x, vertex.y, vertex.z);
            } else {
               consumer.m_5483_(vertex.x, vertex.y, vertex.z);
            }

            consumer.m_193479_(color);
            if (!this.uvs.isEmpty()) {
               consumer.m_7421_(this.uvs.get(i).f_82470_, this.uvs.get(i).f_82471_);
            }

            if (!this.normals.isEmpty()) {
               if (this.normalmatrix != null) {
                  consumer.m_252939_(this.normalmatrix, this.normals.get(i).x, this.normals.get(i).y, this.normals.get(i).z);
               } else {
                  consumer.m_5601_(this.normals.get(i).x, this.normals.get(i).y, this.normals.get(i).z);
               }
            }

            if (this.light != null) {
               consumer.m_85969_(this.light);
            }

            if (this.overlay != null) {
               consumer.m_86008_(this.overlay);
            }

            consumer.m_5752_();
         }
      }

      public void build(GuiGraphics graphics, RenderType renderType) {
         this.build(graphics.m_280091_().m_6299_(renderType));
         graphics.m_280262_();
      }

      public void build(GuiGraphics graphics) {
         this.build(graphics, RenderType.m_285907_());
      }
   }
}
