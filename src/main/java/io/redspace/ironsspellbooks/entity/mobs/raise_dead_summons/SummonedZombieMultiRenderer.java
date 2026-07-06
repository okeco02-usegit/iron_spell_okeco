package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class SummonedZombieMultiRenderer extends HumanoidRenderer<SummonedZombie> {
   ZombieRenderer vanillaRenderer;

   public SummonedZombieMultiRenderer(Context pContext) {
      super(pContext, new SummonedZombieModel());
      this.vanillaRenderer = new ZombieRenderer(pContext) {
         public ResourceLocation m_5478_(Zombie pEntity) {
            return SummonedZombieModel.TEXTURE;
         }
      };
      this.vanillaRenderer.m_115326_(new SpellTargetingLayer.Vanilla(this.vanillaRenderer));
   }

   public void render(SummonedZombie entity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
      if (entity.isAnimatingRise()) {
         super.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
      } else {
         this.vanillaRenderer.m_7392_(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
      }
   }
}
