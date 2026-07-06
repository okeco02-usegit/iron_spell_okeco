package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternRenderer.class)
public class LecternRendererMixin {
   @Shadow
   BookModel f_112424_;

   @Inject(
      method = "render(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void render(
      LecternBlockEntity pBlockEntity,
      float pPartialTick,
      PoseStack pPoseStack,
      MultiBufferSource pBufferSource,
      int pPackedLight,
      int pPackedOverlay,
      CallbackInfo ci
   ) {
      BlockState blockstate = pBlockEntity.m_58900_();
      if ((Boolean)blockstate.m_61143_(LecternBlock.f_54467_)) {
         ItemStack stack = pBlockEntity.m_59566_();
         if (stack.m_41720_() instanceof ILecternPlaceable lecternPlaceable) {
            pPoseStack.m_85836_();
            pPoseStack.m_252880_(0.5F, 1.0625F, 0.5F);
            float f = ((Direction)blockstate.m_61143_(LecternBlock.f_54465_)).m_122427_().m_122435_();
            pPoseStack.m_252781_(Axis.f_252436_.m_252977_(-f));
            pPoseStack.m_252781_(Axis.f_252403_.m_252977_(67.5F));
            Optional<ResourceLocation> textureOverride = lecternPlaceable.simpleTextureOverride(stack);
            if (textureOverride.isPresent()) {
               pPoseStack.m_252880_(0.0F, -0.125F, 0.0F);
               this.f_112424_.m_102292_(0.0F, 0.1F, 0.9F, 1.2F);
               VertexConsumer vertexconsumer = pBufferSource.m_6299_(RenderType.m_110446_(textureOverride.get()));
               this.f_112424_.m_102316_(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            } else if (stack.m_41720_() instanceof SpellBook spellBook || stack.m_150930_((Item)ItemRegistry.THE_CHRONICLE.get())) {
               pPoseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
               pPoseStack.m_252781_(Axis.f_252403_.m_252977_(90.0F));
               pPoseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
               pPoseStack.m_252880_(0.125F, -0.625F, 0.125F);
               lecternPlaceable.handleCustomLecternPosing(pPoseStack);
               ItemRenderer itemRenderer = Minecraft.m_91087_().m_91291_();
               itemRenderer.m_269128_(stack, ItemDisplayContext.HEAD, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, null, 0);
            }

            pPoseStack.m_85849_();
            ci.cancel();
         }
      }
   }
}
