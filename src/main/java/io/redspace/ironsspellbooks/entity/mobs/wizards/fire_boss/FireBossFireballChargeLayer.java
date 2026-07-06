package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class FireBossFireballChargeLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/fire_boss/tyros_flame.png");
   protected final ModelPart fireball;

   public FireBossFireballChargeLayer(GeoEntityRenderer entityRendererIn, Context context) {
      super(entityRendererIn);
      ModelPart modelpart = context.m_174023_(FireballRenderer.MODEL_LAYER_LOCATION);
      this.fireball = modelpart.m_171324_("body");
   }

   public void render(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel bakedModel,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (animatable instanceof FireBossEntity fireBoss && fireBoss.isHalfHealthAttacking()) {
         int tick = 235 - fireBoss.halfHealthTimer;
         if (tick > 11 && tick < 230) {
            poseStack.m_85836_();
            poseStack.m_252781_(Axis.f_252436_.m_252977_(fireBoss.f_20885_));
            poseStack.m_85837_(0.0, fireBoss.m_20191_().m_82376_() * 1.25, 0.0);
            float scale = Mth.m_14179_(tick / 235.0F, 1.0F, 1.5F);
            poseStack.m_85841_(scale, scale, scale);
            VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110458_(FireballRenderer.BASE_TEXTURE));
            float f = animatable.f_19797_ + partialTick;
            float swirlX = Mth.m_14089_(0.08F * f) * 180.0F;
            float swirlY = Mth.m_14031_(0.08F * f) * 180.0F;
            float swirlZ = Mth.m_14089_(0.08F * f + 5464.0F) * 180.0F;
            poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlX));
            poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlY));
            poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlZ));
            this.fireball.m_104306_(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.m_85849_();
         }
      }
   }
}
