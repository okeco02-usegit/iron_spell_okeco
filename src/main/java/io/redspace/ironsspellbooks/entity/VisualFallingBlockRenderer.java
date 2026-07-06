package io.redspace.ironsspellbooks.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class VisualFallingBlockRenderer extends EntityRenderer<VisualFallingBlockEntity> {
   private final BlockRenderDispatcher dispatcher;

   public VisualFallingBlockRenderer(Context pContext) {
      super(pContext);
      this.f_114477_ = 0.5F;
      this.dispatcher = pContext.m_234597_();
   }

   public void render(
      VisualFallingBlockEntity entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight
   ) {
      BlockState blockstate = entity.m_31980_();
      if (blockstate.m_60799_() == RenderShape.MODEL) {
         Level level = entity.f_19853_;
         pMatrixStack.m_85836_();
         BlockPos blockpos = BlockPos.m_274561_(entity.m_20185_(), entity.m_20191_().f_82292_, entity.m_20189_());
         pMatrixStack.m_85837_(-0.5, 0.0, -0.5);
         BakedModel model = this.dispatcher.m_110910_(blockstate);

         for (RenderType renderType : model.getRenderTypes(blockstate, RandomSource.m_216335_(blockstate.m_60726_(entity.m_31978_())), ModelData.EMPTY)) {
            this.dispatcher
               .m_110937_()
               .tesselateBlock(
                  level,
                  model,
                  blockstate,
                  blockpos,
                  pMatrixStack,
                  pBuffer.m_6299_(renderType),
                  false,
                  RandomSource.m_216327_(),
                  blockstate.m_60726_(entity.m_31978_()),
                  OverlayTexture.f_118083_,
                  ModelData.EMPTY,
                  renderType
               );
         }

         pMatrixStack.m_85849_();
         super.m_7392_(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      }
   }

   public ResourceLocation getTextureLocation(VisualFallingBlockEntity pEntity) {
      return TextureAtlas.f_118259_;
   }
}
