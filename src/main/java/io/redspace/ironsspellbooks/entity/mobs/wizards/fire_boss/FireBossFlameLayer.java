package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

@OnlyIn(Dist.CLIENT)
public class FireBossFlameLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/fire_boss/tyros_flame.png");
   static int frameCount = 8;
   static int ticksPerFrame = 1;

   public FireBossFlameLayer(GeoEntityRenderer entityRendererIn) {
      super(entityRendererIn);
   }

   public void renderForBone(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (bone.getName().equals("head") && animatable instanceof FireBossEntity fireBossEntity && fireBossEntity.isSoulMode()) {
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252436_.m_252977_(45.0F));
         RenderUtils.translateToPivotPoint(poseStack, bone);
         poseStack.m_85841_(0.5F, 0.5F, 0.5F);
         VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110452_(TEXTURE));
         Matrix4f poseMatrix = poseStack.m_85850_().m_252922_();
         int anim = animatable.f_19797_ / ticksPerFrame % frameCount;
         float uvMin = (float)anim / frameCount;
         float uvMax = (float)(anim + 1) / frameCount;
         float halfsqrt2 = 0.7071F;

         for (int i = 0; i < 4; i++) {
            poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
            consumer.m_252986_(poseMatrix, 0.0F, 0.0F, -halfsqrt2)
               .m_6122_(255, 255, 255, 255)
               .m_7421_(0.0F, uvMax)
               .m_86008_(OverlayTexture.f_118083_)
               .m_85969_(15728880)
               .m_5601_(0.0F, 1.0F, 0.0F)
               .m_5752_();
            consumer.m_252986_(poseMatrix, 0.0F, 1.0F, -halfsqrt2)
               .m_6122_(255, 255, 255, 255)
               .m_7421_(0.0F, uvMin)
               .m_86008_(OverlayTexture.f_118083_)
               .m_85969_(15728880)
               .m_5601_(0.0F, 1.0F, 0.0F)
               .m_5752_();
            consumer.m_252986_(poseMatrix, 0.0F, 1.0F, halfsqrt2)
               .m_6122_(255, 255, 255, 255)
               .m_7421_(1.0F, uvMin)
               .m_86008_(OverlayTexture.f_118083_)
               .m_85969_(15728880)
               .m_5601_(0.0F, 1.0F, 0.0F)
               .m_5752_();
            consumer.m_252986_(poseMatrix, 0.0F, 0.0F, halfsqrt2)
               .m_6122_(255, 255, 255, 255)
               .m_7421_(1.0F, uvMax)
               .m_86008_(OverlayTexture.f_118083_)
               .m_85969_(15728880)
               .m_5601_(0.0F, 1.0F, 0.0F)
               .m_5752_();
         }

         poseStack.m_85849_();
         bufferSource.m_6299_(renderType);
      }
   }
}
