package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PocketDimensionEffects extends DimensionSpecialEffects {
   public static final ResourceLocation SKY_LOCATION = IronsSpellbooks.id("textures/environment/pocket_dimension_sky.png");
   public static final ResourceLocation CLOUDS_LOCATION = IronsSpellbooks.id("textures/environment/pocket_clouds.png");
   public static final ResourceLocation WISP_LOCATION = IronsSpellbooks.id("textures/environment/single_cloud.png");
   public static final ResourceLocation NOISE = IronsSpellbooks.id("textures/environment/noise_tile.png");

   public PocketDimensionEffects() {
      super(Float.NaN, false, SkyType.NONE, false, false);
   }

   public Vec3 m_5927_(Vec3 fogColor, float brightness) {
      return fogColor;
   }

   public boolean m_5781_(int x, int y) {
      return false;
   }

   public boolean renderSky(
      ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog
   ) {
      poseStack.m_85836_();
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      Tesselator tesselator = Tesselator.m_85913_();
      float skyDistance = 100.0F;
      renderBox(poseStack, tesselator, skyDistance, 0.0F, 1.0F, GameRenderer::m_172820_, SKY_LOCATION, -12237499);
      float f = ticks + partialTick;
      float scale = 0.8F;
      int layers = 6;
      Random random = new Random(431L);
      RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);

      for (int i = 0; i < layers; i++) {
         poseStack.m_85836_();
         int j = layers - i - 1;
         float speed = (0.01F + i * i * 0.09F) * 0.015F;
         float x = (i * 68731 + f * speed * (random.nextFloat() - 0.5F)) % 360.0F;
         float y = (i * 74869 + f * speed * (random.nextFloat() - 0.5F)) % 360.0F;
         float z = (i * 98744 + f * speed * (random.nextFloat() - 0.5F)) % 360.0F;
         poseStack.m_252781_(Axis.f_252529_.m_252977_(x));
         poseStack.m_252781_(Axis.f_252436_.m_252977_(y));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(z));
         Vector3f rgb = new Vector3f(random.nextFloat() * 0.5F + 0.5F, random.nextFloat() * 0.5F + 0.5F, random.nextFloat() * 0.5F + 0.5F);
         float intensity = Mth.m_14179_((float)j / layers, 0.25F, 0.8F);
         rgb.mul(intensity);
         rgb = new Vector3f(Math.min(rgb.x, 1.0F), Math.min(rgb.y, 1.0F), Math.min(rgb.z, 1.0F));
         RenderSystem.setShaderColor(rgb.x, rgb.y, rgb.z, 1.0F);
         renderBox(poseStack, tesselator, skyDistance * scale, 0.0F, 4.0F + 2.0F * scale, GameRenderer::m_172820_, CLOUDS_LOCATION, -8355712);
         poseStack.m_85849_();
         scale -= 0.04F;
      }

      Vector3f color = new Vector3f(0.1F, 0.4F, 0.6F);
      color.mul(0.075F);
      float zoff = renderNebula(poseStack, color, random, f, skyDistance, tesselator, scale, 0.0F);
      color = new Vector3f(0.6F, 0.1F, 0.5F);
      color.mul(0.125F);
      zoff = renderNebula(poseStack, color, random, f, skyDistance, tesselator, scale, zoff);
      color = new Vector3f(0.3F, 0.3F, 0.3F);
      color.mul(0.125F);
      zoff = renderNebula(poseStack, color, random, f, skyDistance, tesselator, scale, zoff);
      this.renderBorderAura(level, ticks, partialTick, poseStack, camera, projectionMatrix);
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.m_85849_();
      return true;
   }

   public void renderBorderAura(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix) {
      poseStack.m_85836_();
      Quaternionf quaternionf = camera.m_253121_().conjugate(new Quaternionf());
      Vec3 cameraPos = camera.m_90583_();
      poseStack.m_252880_((float)(-cameraPos.f_82479_), (float)(-cameraPos.f_82480_), (float)(-cameraPos.f_82481_));
      int traversal = (int)(cameraPos.f_82481_ / 256.0) * 256;
      float HARDCODE_WIDTH = 7.0F;
      float halfWidth = HARDCODE_WIDTH / 2.0F;
      float HARDCODE_X = 4.0F + halfWidth;
      float HARDCODE_Y = 1.0F;
      float HARDCODE_Z = 4.0F + halfWidth + traversal;
      RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      poseStack.m_252880_(HARDCODE_X, HARDCODE_Y, HARDCODE_Z);
      Tesselator tesselator = Tesselator.m_85913_();
      RenderSystem.setShader(GameRenderer::m_172820_);
      RenderSystem.setShaderTexture(0, NOISE);
      float uvScrollMin = (ticks + partialTick) / 20.0F / 12.0F % 1.0F;
      float uvScrollMax = uvScrollMin + 0.020833334F;
      float uvTile = Mth.m_14143_(HARDCODE_WIDTH / 3.0F);

      for (int i = 0; i < 4; i++) {
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252436_.m_252977_(i * 90));
         Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
         BufferBuilder bufferbuilder = tesselator.m_85915_();
         bufferbuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85819_);
         int baseColor = -6745686;
         bufferbuilder.m_252986_(matrix4f, -halfWidth, HARDCODE_Y - 1.0F, halfWidth).m_7421_(0.0F, uvScrollMax).m_193479_(baseColor).m_5752_();
         bufferbuilder.m_252986_(matrix4f, -halfWidth, HARDCODE_Y + 2.0F, halfWidth).m_7421_(0.0F, uvScrollMin).m_193479_(-16777216).m_5752_();
         bufferbuilder.m_252986_(matrix4f, halfWidth, HARDCODE_Y + 2.0F, halfWidth).m_7421_(uvTile, uvScrollMin).m_193479_(-16777216).m_5752_();
         bufferbuilder.m_252986_(matrix4f, halfWidth, HARDCODE_Y - 1.0F, halfWidth).m_7421_(uvTile, uvScrollMax).m_193479_(baseColor).m_5752_();
         BufferUploader.m_231202_(bufferbuilder.m_231175_());
         poseStack.m_85849_();
      }

      poseStack.m_85849_();
   }

   private static float renderNebula(
      PoseStack poseStack, Vector3f color, Random random, float f, float skyDistance, Tesselator tesselator, float scale, float zoff
   ) {
      RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);
      int clouds = 15;

      for (int i = 0; i < clouds; i++) {
         float clusterScale = 0.15F + i * 0.003F;
         poseStack.m_85836_();
         int count = i + 1;
         float speed = 0.005F;
         float x = (random.nextInt(360) + f * speed) % 360.0F;
         float y = (random.nextInt(360) + f * speed) % 360.0F;
         float z = (random.nextInt(360) + f * speed) % 360.0F;
         poseStack.m_252781_(Axis.f_252529_.m_252977_(x));
         poseStack.m_252781_(Axis.f_252436_.m_252977_(y));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(z));

         for (int j = 0; j < count; j++) {
            Vector3f offset = new Vector3f(random.nextFloat() - 0.5F, 0.0F, random.nextFloat() - 0.5F);
            offset.mul(skyDistance * 0.25F * (1.0F + j * 0.025F));
            poseStack.m_85836_();
            poseStack.m_252880_(offset.x, zoff, offset.z);
            renderPlane(poseStack, tesselator, skyDistance * scale, 0.0F, 1.0F, GameRenderer::m_172820_, WISP_LOCATION, clusterScale, -10461088);
            poseStack.m_85849_();
            zoff += 0.03F;
         }

         poseStack.m_85849_();
      }

      return zoff;
   }

   private static void renderBox(
      PoseStack poseStack,
      Tesselator tesselator,
      float skyDistance,
      float uvMin,
      float uvMax,
      Supplier<ShaderInstance> shaderSupplier,
      ResourceLocation texture,
      int color
   ) {
      RenderSystem.setShader(shaderSupplier);
      RenderSystem.setShaderTexture(0, texture);

      for (int i = 0; i < 6; i++) {
         poseStack.m_85836_();
         if (i == 1) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         }

         if (i == 2) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         }

         if (i == 3) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(180.0F));
         }

         if (i == 4) {
            poseStack.m_252781_(Axis.f_252403_.m_252977_(90.0F));
         }

         if (i == 5) {
            poseStack.m_252781_(Axis.f_252403_.m_252977_(-90.0F));
         }

         Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
         BufferBuilder bufferbuilder = tesselator.m_85915_();
         bufferbuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85819_);
         bufferbuilder.m_252986_(matrix4f, -skyDistance, -skyDistance, -skyDistance).m_7421_(uvMin, uvMin).m_193479_(color).m_5752_();
         bufferbuilder.m_252986_(matrix4f, -skyDistance, -skyDistance, skyDistance).m_7421_(uvMin, uvMax).m_193479_(color).m_5752_();
         bufferbuilder.m_252986_(matrix4f, skyDistance, -skyDistance, skyDistance).m_7421_(uvMax, uvMax).m_193479_(color).m_5752_();
         bufferbuilder.m_252986_(matrix4f, skyDistance, -skyDistance, -skyDistance).m_7421_(uvMax, uvMin).m_193479_(color).m_5752_();
         BufferUploader.m_231202_(bufferbuilder.m_231175_());
         poseStack.m_85849_();
      }
   }

   private static void renderPlane(
      PoseStack poseStack,
      Tesselator tesselator,
      float skyDistance,
      float uvMin,
      float uvMax,
      Supplier<ShaderInstance> shaderSupplier,
      ResourceLocation texture,
      float scale,
      int color
   ) {
      RenderSystem.setShader(shaderSupplier);
      RenderSystem.setShaderTexture(0, texture);
      poseStack.m_85836_();
      Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
      BufferBuilder bufferbuilder = tesselator.m_85915_();
      bufferbuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85819_);
      bufferbuilder.m_252986_(matrix4f, -skyDistance * scale, -skyDistance, -skyDistance * scale).m_7421_(uvMin, uvMin).m_193479_(color).m_5752_();
      bufferbuilder.m_252986_(matrix4f, -skyDistance * scale, -skyDistance, skyDistance * scale).m_7421_(uvMin, uvMax).m_193479_(color).m_5752_();
      bufferbuilder.m_252986_(matrix4f, skyDistance * scale, -skyDistance, skyDistance * scale).m_7421_(uvMax, uvMax).m_193479_(color).m_5752_();
      bufferbuilder.m_252986_(matrix4f, skyDistance * scale, -skyDistance, -skyDistance * scale).m_7421_(uvMax, uvMin).m_193479_(color).m_5752_();
      BufferUploader.m_231202_(bufferbuilder.m_231175_());
      poseStack.m_85849_();
   }
}
