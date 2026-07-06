package io.redspace.ironsspellbooks.entity.mobs.keeper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;

public class KeeperRenderer extends AbstractSpellCastingMobRenderer {
   public KeeperRenderer(Context context) {
      super(context, new KeeperModel());
      this.addRenderLayer(new GeoKeeperGhostLayer(this));
      this.f_114477_ = 0.65F;
   }

   @Override
   public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      int light = entity instanceof KeeperEntity keeper && keeper.isSummoned() ? Mth.m_14045_(packedLight + 100, 0, 240) : packedLight;
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
   }

   public void preRender(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      poseStack.m_85841_(1.3F, 1.3F, 1.3F);
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
   }

   @Override
   public RenderType getRenderType(AbstractSpellCastingMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.m_110473_(texture);
   }

   public int getPackedOverlay(AbstractSpellCastingMob animatable, float u, float partialTick) {
      return OverlayTexture.m_118093_(OverlayTexture.m_118088_(u), OverlayTexture.m_118096_(animatable.f_20919_ > 0));
   }

   @Override
   public Color getRenderColor(AbstractSpellCastingMob animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (animatable instanceof KeeperEntity keeper && keeper.isRising()) {
         color = new Color(RenderHelper.colorf(1.0F, 1.0F, 1.0F, Mth.m_14036_((25 - keeper.riseAnimTick + 1) / 25.0F, 0.0F, 1.0F)));
      }

      return color;
   }
}
