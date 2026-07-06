package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class DeadKingRenderer extends AbstractSpellCastingMobRenderer {
   public DeadKingRenderer(Context renderManager) {
      super(renderManager, new DeadKingModel());
      this.addRenderLayer(new DeadKingEmissiveLayer(this));
   }

   @Override
   public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (entity instanceof DeadKingBoss king) {
         this.getGeoModel().getBone("left_leg").ifPresent(bone -> bone.setHidden(king.isPhase(DeadKingBoss.Phases.FinalPhase)));
         this.getGeoModel().getBone("right_leg").ifPresent(bone -> bone.setHidden(king.isPhase(DeadKingBoss.Phases.FinalPhase)));
      }

      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   public void preRender(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float r,
      float g,
      float b,
      float a
   ) {
      if (animatable instanceof DeadKingBoss) {
         poseStack.m_85841_(1.3F, 1.3F, 1.3F);
      }

      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, r, g, b, a);
   }

   @Override
   public RenderType getRenderType(AbstractSpellCastingMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return animatable.m_20145_() ? RenderType.m_110473_(texture) : RenderType.m_110458_(texture);
   }

   protected void adjustHandItemRendering(PoseStack poseStack, ItemStack itemStack, AbstractSpellCastingMob animatable, float partialTick, boolean offhand) {
      if (itemStack.m_150930_((Item)ItemRegistry.BLOOD_STAFF.get())) {
         poseStack.m_85837_(0.0, 0.25, 0.0);
      }
   }
}
