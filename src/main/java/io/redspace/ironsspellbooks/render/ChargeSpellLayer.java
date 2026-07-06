package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.fire_arrow.FireArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrowRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class ChargeSpellLayer {
   private static <T extends LivingEntity> void handleRender(
      PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, T entity, String spellId, boolean offhand
   ) {
      if (spellId.equals(((AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get()).getSpellId())) {
         poseStack.m_85837_((offhand ? -1 : 1) / 32.0F - 0.125, 0.5, 0.0);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         LightningLanceRenderer.renderModel(poseStack, bufferSource, entity.f_19797_);
      } else if (spellId.equals(((AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get()).getSpellId())) {
         poseStack.m_85837_((offhand ? -1 : 1) / 32.0F, 0.5, 0.0);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         MagicArrowRenderer.renderModel(poseStack, bufferSource);
      } else if (spellId.equals(((AbstractSpell)SpellRegistry.POISON_ARROW_SPELL.get()).getSpellId())) {
         poseStack.m_252880_((offhand ? -1 : 1) / 32.0F, 1.0F, 0.0F);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         PoisonArrowRenderer.renderModel(poseStack, bufferSource, pPackedLight);
      } else if (spellId.equals(((AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get()).getSpellId())) {
         poseStack.m_85837_((offhand ? -1 : 1) / 32.0F, 0.5, 0.0);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         FireArrowRenderer.renderModel(poseStack, bufferSource);
      }
   }

   public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob> {
      public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRenderer) {
         super(entityRenderer);
      }

      public void render(
         PoseStack poseStack,
         AbstractSpellCastingMob entity,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         SyncedSpellData syncedSpellData = ClientMagicData.getSyncedSpellData(entity);
         String spellId = syncedSpellData.getCastingSpellId();
         GeoBone hand = (GeoBone)bakedModel.getBone("bipedHandRight").get();
         GeoBone arm = (GeoBone)bakedModel.getBone("right_arm").get();
         poseStack.m_85836_();
         RenderUtils.translateToPivotPoint(poseStack, arm);
         RenderUtils.rotateMatrixAroundBone(poseStack, arm);
         RenderUtils.translateAwayFromPivotPoint(poseStack, arm);
         poseStack.m_252880_(
            -(arm.getPivotX() - hand.getPivotX()) / 16.0F, (arm.getPivotY() - hand.getPivotY()) / 16.0F, (arm.getPivotZ() - hand.getPivotZ()) / 16.0F
         );
         ChargeSpellLayer.handleRender(poseStack, bufferSource, packedLight, entity, spellId, false);
         poseStack.m_85849_();
      }
   }

   public static class Vanilla<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
      public Vanilla(RenderLayerParent<T, M> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource bufferSource,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float pPartialTick,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         SyncedSpellData syncedSpellData = ClientMagicData.getSyncedSpellData(entity);
         if (syncedSpellData.isCasting()) {
            String spellId = syncedSpellData.getCastingSpellId();
            poseStack.m_85836_();
            ((HumanoidModel)this.m_117386_()).m_6002_(HumanoidArm.RIGHT, poseStack);
            ChargeSpellLayer.handleRender(poseStack, bufferSource, pPackedLight, entity, spellId, false);
            poseStack.m_85849_();
         }
      }
   }
}
