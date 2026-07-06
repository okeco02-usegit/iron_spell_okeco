package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.gui.overlays.ScreenTooltipOverlay;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class AlchemistCauldronRenderer implements BlockEntityRenderer<AlchemistCauldronTile> {
   ItemRenderer itemRenderer;
   private static final Vec3 ITEM_POS = new Vec3(0.5, 1.5, 0.5);

   public AlchemistCauldronRenderer(Context context) {
      this.itemRenderer = context.m_234447_();
   }

   public void render(
      AlchemistCauldronTile cauldron, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      int waterLevel = cauldron.getFluidAmount();
      float waterOffset = Mth.m_14179_(waterLevel / 1000.0F, 0.25F, 0.9F);
      if (waterLevel > 0) {
         this.renderWater(cauldron, poseStack, bufferSource, packedLight, waterOffset);
      }

      NonNullList<ItemStack> floatingItems = cauldron.inputItems;

      for (int i = 0; i < floatingItems.size(); i++) {
         ItemStack itemStack = (ItemStack)floatingItems.get(i);
         if (!itemStack.m_41619_()) {
            float f = waterLevel > 0 ? (float)cauldron.m_58904_().m_46467_() + partialTick : 15.0F;
            Vec2 floatOffset = this.getFloatingItemOffset(f, i * 587);
            float yRot = (f + i * 213) / (i + 1) * 1.5F;
            this.renderItem(
               itemStack,
               new Vec3(floatOffset.f_82470_, waterOffset + i * 0.01F, floatOffset.f_82471_),
               yRot,
               cauldron,
               partialTick,
               poseStack,
               bufferSource,
               packedLight,
               packedOverlay
            );
         }
      }

      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null
         && Math.abs(player.m_20185_() - cauldron.m_58899_().m_123341_()) < 5.0
         && Math.abs(player.m_20186_() - cauldron.m_58899_().m_123342_()) < 5.0
         && Math.abs(player.m_20189_() - cauldron.m_58899_().m_123343_()) < 5.0
         && player.m_6047_()
         && Minecraft.m_91087_().f_91077_ instanceof BlockHitResult blockHitResult
         && blockHitResult.m_82425_().equals(cauldron.m_58899_())) {
         List<Component> text = new ArrayList<>();
         text.add(
            Component.m_237115_("block.irons_spellbooks.alchemist_cauldron").m_130944_(new ChatFormatting[]{ChatFormatting.UNDERLINE, ChatFormatting.WHITE})
         );
         List<FluidStack> fluids = cauldron.fluidInventory.fluids();
         if (fluids.isEmpty()) {
            text.add(Component.m_237115_("ui.irons_spellbooks.empty").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}));
         } else {
            List<ObjectIntImmutablePair<MutableComponent>> fluidInfo = new ArrayList<>();

            for (int i = fluids.size() - 1; i >= 0; i--) {
               FluidStack fluid = fluids.get(i);
               fluidInfo.add(
                  new ObjectIntImmutablePair(
                     fluid.getFluid().getFluidType().getDescription(fluid).m_6881_().m_130940_(ChatFormatting.DARK_AQUA), fluid.getAmount()
                  )
               );
            }

            for (ObjectIntImmutablePair<MutableComponent> info : fluidInfo) {
               text.add(
                  Component.m_237113_("  ")
                     .m_7220_((Component)info.left())
                     .m_130946_(": ")
                     .m_7220_(Component.m_237113_(info.rightInt() + "mb").m_130940_(ChatFormatting.GOLD))
               );
            }
         }

         ScreenTooltipOverlay.renderTooltip(text, (sw, sh, mx, my, tw, th) -> new Vector2i(sw / 2 + 30, sh / 2 - th / 2));
      }
   }

   public Vec2 getFloatingItemOffset(float time, int offset) {
      float xspeed = offset % 2 == 0 ? 0.0075F : 0.025F * (1.0F + offset % 88 * 0.001F);
      float yspeed = offset % 2 == 0 ? 0.025F : 0.0075F * (1.0F + offset % 88 * 0.001F);
      float x = (time + offset) * xspeed;
      x = (Math.abs(x % 2.0F - 1.0F) + 1.0F) / 2.0F;
      float y = (time + offset + 4356.0F) * yspeed;
      y = (Math.abs(y % 2.0F - 1.0F) + 1.0F) / 2.0F;
      x = Mth.m_14179_(x, -0.2F, 0.75F);
      y = Mth.m_14179_(y, -0.2F, 0.75F);
      return new Vec2(x, y);
   }

   private void renderWater(AlchemistCauldronTile cauldron, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float waterOffset) {
      Matrix4f pose = poseStack.m_85850_().m_252922_();
      float totalFluid = cauldron.getFluidAmount();
      float runningFluid = totalFluid;
      float f = 0.0F;
      float padding = 0.0625F;

      for (FluidStack fluid : cauldron.fluidInventory.fluids()) {
         int skylight = packedLight >> 4 & 15;
         int luminosity = Math.max(skylight, fluid.getFluid().getFluidType().getLightLevel(fluid));
         int fluidlight = packedLight & 15728640 | luminosity << 4;
         IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.getFluid());
         Function<ResourceLocation, TextureAtlasSprite> spriteAtlas = Minecraft.m_91087_().m_91258_(InventoryMenu.f_39692_);
         TextureAtlasSprite texture = spriteAtlas.apply(clientFluid.getStillTexture(fluid.getFluid().m_76145_(), cauldron.m_58904_(), cauldron.m_58899_()));
         VertexConsumer consumer = texture.m_118381_(bufferSource.m_6299_(RenderType.m_110466_()));
         Vector3f rgb = this.colorFromLong(
            clientFluid.getTintColor(fluid) & clientFluid.getTintColor(fluid.getFluid().m_76145_(), cauldron.m_58904_(), cauldron.m_58899_())
         );
         float opacity = runningFluid / totalFluid;
         runningFluid -= fluid.getAmount();
         consumer.m_252986_(pose, 1.0F - padding, waterOffset + f, 0.0F + padding)
            .m_85950_(rgb.x(), rgb.y(), rgb.z(), opacity)
            .m_7421_(1.0F - padding, 0.0F + padding)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(fluidlight)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(pose, 0.0F + padding, waterOffset + f, 0.0F + padding)
            .m_85950_(rgb.x(), rgb.y(), rgb.z(), opacity)
            .m_7421_(0.0F + padding, 0.0F + padding)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(fluidlight)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(pose, 0.0F + padding, waterOffset + f, 1.0F - padding)
            .m_85950_(rgb.x(), rgb.y(), rgb.z(), opacity)
            .m_7421_(0.0F + padding, 1.0F - padding)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(fluidlight)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         consumer.m_252986_(pose, 1.0F - padding, waterOffset + f, 1.0F - padding)
            .m_85950_(rgb.x(), rgb.y(), rgb.z(), opacity)
            .m_7421_(1.0F - padding, 1.0F - padding)
            .m_86008_(OverlayTexture.f_118083_)
            .m_85969_(fluidlight)
            .m_5601_(0.0F, 1.0F, 0.0F)
            .m_5752_();
         f += 0.001F;
      }
   }

   private Vector3f colorFromLong(long color) {
      return new Vector3f((float)(color >> 16 & 255L) / 255.0F, (float)(color >> 8 & 255L) / 255.0F, (float)(color & 255L) / 255.0F);
   }

   private void renderItem(
      ItemStack itemStack,
      Vec3 offset,
      float yRot,
      AlchemistCauldronTile tile,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      poseStack.m_85836_();
      int renderId = (int)tile.m_58899_().m_121878_();
      poseStack.m_85837_(offset.f_82479_, offset.f_82480_, offset.f_82481_);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
      poseStack.m_85841_(0.4F, 0.4F, 0.4F);
      this.itemRenderer
         .m_269128_(
            itemStack,
            ItemDisplayContext.FIXED,
            LevelRenderer.m_109541_(tile.m_58904_(), tile.m_58899_()),
            packedOverlay,
            poseStack,
            bufferSource,
            tile.m_58904_(),
            renderId
         );
      poseStack.m_85849_();
   }
}
