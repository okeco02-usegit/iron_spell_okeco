package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.TrialSpawner;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.TrialSpawnerData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public TrialSpawnerRenderer(Context context) {
      this.entityRenderer = context.m_234446_();
   }

   public void render(
      TrialSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      Level level = blockEntity.m_58904_();
      if (level != null) {
         TrialSpawner trialspawner = blockEntity.getTrialSpawner();
         TrialSpawnerData trialspawnerdata = trialspawner.getData();
         Entity entity = trialspawnerdata.getOrCreateDisplayEntity(trialspawner, level, trialspawner.getState());
         if (entity != null) {
            renderEntityInSpawner(
               partialTick, poseStack, bufferSource, packedLight, entity, this.entityRenderer, trialspawnerdata.getOSpin(), trialspawnerdata.getSpin()
            );
         }
      }
   }

   public static void renderEntityInSpawner(
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Entity entity,
      EntityRenderDispatcher entityRenderer,
      double oSpin,
      double spin
   ) {
      poseStack.m_85836_();
      poseStack.m_252880_(0.5F, 0.0F, 0.5F);
      float f = 0.53125F;
      float f1 = Math.max(entity.m_20205_(), entity.m_20206_());
      if (f1 > 1.0) {
         f /= f1;
      }

      poseStack.m_252880_(0.0F, 0.4F, 0.0F);
      poseStack.m_252781_(Axis.f_252436_.m_252977_((float)Mth.m_14139_(partialTick, oSpin, spin) * 10.0F));
      poseStack.m_252880_(0.0F, -0.2F, 0.0F);
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-30.0F));
      poseStack.m_85841_(f, f, f);
      entityRenderer.m_114384_(entity, 0.0, 0.0, 0.0, 0.0F, partialTick, poseStack, buffer, packedLight);
      poseStack.m_85849_();
   }
}
