package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;

public class FireBossRenderer extends AbstractSpellCastingMobRenderer {
   static final int deathFadeTime = 120;

   public FireBossRenderer(Context context) {
      super(context, new FireBossModel());
      this.f_114477_ = 0.65F;
      this.addRenderLayer(new FireBossSoulLayer(this));
      this.addRenderLayer(new FireBossFlameLayer(this));
      this.addRenderLayer(new FireBossFireballChargeLayer(this, context));
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
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
      float scale = animatable instanceof FireBossEntity fireBoss && fireBoss.isSoulMode() ? 2.0125F : 1.75F;
      poseStack.m_85841_(scale, scale, scale);
   }

   @Override
   public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (entity instanceof FireBossEntity fireBossEntity && fireBossEntity.isSpawning()) {
         float f = fireBossEntity.getSpawnWalkPercent(partialTick);
         if (f == 0.0F) {
            return;
         }

         this.f_114477_ = Mth.m_14179_(f, 2.0F, 0.65F);
         this.f_114478_ = Mth.m_14179_(f, 0.0F, 1.0F);
      } else {
         this.f_114478_ = 1.0F;
         this.f_114477_ = 0.65F;
      }

      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, Mth.m_14045_(packedLight + 100, 0, 240));
   }

   public void applyRenderLayersForBone(
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
      super.applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      if (bone.getName().equals("bipedHandLeft") && animatable instanceof FireBossEntity fireBoss && fireBoss.spectralDaggerActive()) {
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         poseStack.m_85837_(-0.375, 0.1, -1.0);
         Minecraft.m_91087_()
            .m_91291_()
            .m_115143_(
               ((Item)ItemRegistry.HELLRAZOR.get()).m_7968_(),
               ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
               true,
               poseStack,
               bufferSource,
               packedLight,
               packedOverlay,
               Minecraft.m_91087_().m_91304_().getModel(IronsSpellbooks.id("item/fiery_dagger"))
            );
         poseStack.m_85849_();
      }
   }

   public int getPackedOverlay(AbstractSpellCastingMob animatable, float u, float partialTick) {
      return OverlayTexture.f_118083_;
   }

   @Override
   public Color getRenderColor(AbstractSpellCastingMob animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      float f = 1.0F;
      if (animatable.f_20919_ > 40) {
         f = Mth.m_14036_((160 - animatable.f_20919_) / 120.0F, 0.0F, 1.0F);
      } else if (animatable instanceof FireBossEntity fireBoss && fireBoss.isSpawning()) {
         f = fireBoss.getSpawnWalkPercent(partialTick);
      }

      if (!animatable.m_20145_() && f != 1.0F) {
         color = new Color(RenderHelper.colorf(1.0F, 1.0F, 1.0F, f));
      }

      return color;
   }

   @Override
   public RenderType getRenderType(AbstractSpellCastingMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return !animatable.m_21224_() && !(animatable instanceof FireBossEntity fireBoss && (fireBoss.isSpawning() || fireBoss.isDespawning()))
         ? super.getRenderType(animatable, texture, bufferSource, partialTick)
         : RenderType.m_110473_(texture);
   }

   protected float getDeathMaxRotation(AbstractSpellCastingMob animatable) {
      return 0.0F;
   }
}
