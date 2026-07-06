package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SpellRenderingHelper {
   public static final ResourceLocation SOLID = IronsSpellbooks.id("textures/entity/ray/solid.png");
   public static final ResourceLocation BEACON = IronsSpellbooks.id("textures/entity/ray/beacon_beam.png");
   public static final ResourceLocation STRAIGHT_GLOW = IronsSpellbooks.id("textures/entity/ray/ribbon_glow.png");
   public static final ResourceLocation TWISTING_GLOW = IronsSpellbooks.id("textures/entity/ray/twisting_glow.png");

   public static void renderSpellHelper(
      SyncedSpellData spellData, LivingEntity castingMob, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks
   ) {
      if (((AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get()).getSpellId().equals(spellData.getCastingSpellId())) {
         renderRayOfSiphoning(castingMob, poseStack, bufferSource, partialTicks);
      }
   }

   public static void renderRayOfSiphoning(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.0F, entity.m_20192_() * 0.8F, 0.0F);
      Pose pose = poseStack.m_85850_();
      Vec3 start = Vec3.f_82478_;
      Vec3 impact = Utils.raycastForEntity(entity.m_9236_(), entity, RayOfSiphoningSpell.getRange(0), true).m_82450_();
      float distance = (float)entity.m_146892_().m_82554_(impact);
      float radius = 0.12F;
      int r = 178;
      int g = 0;
      int b = 0;
      int a = 255;
      float deltaTicks = entity.f_19797_ + partialTicks;
      float deltaUV = -deltaTicks % 10.0F;
      float max = Mth.m_14187_(deltaUV * 0.2F - Mth.m_14143_(deltaUV * 0.1F));
      float min = -1.0F + max;
      Vec3 dir = entity.m_20154_().m_82541_();
      float dx = (float)dir.f_82479_;
      float dz = (float)dir.f_82481_;
      float yRot = (float)Mth.m_14136_(dz, dx) - 1.5707F;
      float dxz = Mth.m_14116_(dx * dx + dz * dz);
      float dy = (float)dir.f_82480_;
      float xRot = (float)Mth.m_14136_(dy, dxz);
      poseStack.m_252781_(Axis.f_252436_.m_252961_(-yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252961_(-xRot));

      for (float j = 1.0F; j <= distance; j += 0.5F) {
         Vec3 wiggle = new Vec3(
            Mth.m_14031_(deltaTicks * 0.8F) * 0.02F, Mth.m_14031_(deltaTicks * 0.8F + 100.0F) * 0.02F, Mth.m_14089_(deltaTicks * 0.8F) * 0.02F
         );
         Vec3 end = new Vec3(0.0, 0.0, Math.min(j, distance)).m_82549_(wiggle);
         VertexConsumer inner = bufferSource.m_6299_(RenderType.m_110454_(BEACON, true));
         drawHull(start, end, radius, radius, pose, inner, r, g, b, a, min, max);
         VertexConsumer outer = bufferSource.m_6299_(RenderType.m_110473_(TWISTING_GLOW));
         drawQuad(start, end, radius * 4.0F, 0.0F, pose, outer, r, g, b, a, min, max);
         drawQuad(start, end, 0.0F, radius * 4.0F, pose, outer, r, g, b, a, min, max);
         start = end;
      }

      poseStack.m_85849_();
   }

   public static void drawHull(
      Vec3 from, Vec3 to, float width, float height, Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax
   ) {
      drawQuad(from.m_82492_(0.0, height * 0.5F, 0.0), to.m_82492_(0.0, height * 0.5F, 0.0), width, 0.0F, pose, consumer, r, g, b, a, uvMin, uvMax);
      drawQuad(from.m_82520_(0.0, height * 0.5F, 0.0), to.m_82520_(0.0, height * 0.5F, 0.0), width, 0.0F, pose, consumer, r, g, b, a, uvMin, uvMax);
      drawQuad(from.m_82492_(width * 0.5F, 0.0, 0.0), to.m_82492_(width * 0.5F, 0.0, 0.0), 0.0F, height, pose, consumer, r, g, b, a, uvMin, uvMax);
      drawQuad(from.m_82520_(width * 0.5F, 0.0, 0.0), to.m_82520_(width * 0.5F, 0.0, 0.0), 0.0F, height, pose, consumer, r, g, b, a, uvMin, uvMax);
   }

   public static void drawQuad(
      Vec3 from, Vec3 to, float width, float height, Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax
   ) {
      Matrix4f poseMatrix = pose.m_252922_();
      Matrix3f normalMatrix = pose.m_252943_();
      float halfWidth = width * 0.5F;
      float halfHeight = height * 0.5F;
      consumer.m_252986_(poseMatrix, (float)from.f_82479_ - halfWidth, (float)from.f_82480_ - halfHeight, (float)from.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(0.0F, uvMin)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)from.f_82479_ + halfWidth, (float)from.f_82480_ + halfHeight, (float)from.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(1.0F, uvMin)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)to.f_82479_ + halfWidth, (float)to.f_82480_ + halfHeight, (float)to.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(1.0F, uvMax)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
      consumer.m_252986_(poseMatrix, (float)to.f_82479_ - halfWidth, (float)to.f_82480_ - halfHeight, (float)to.f_82481_)
         .m_6122_(r, g, b, a)
         .m_7421_(0.0F, uvMax)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(240)
         .m_252939_(normalMatrix, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }
}
