package io.redspace.ironsspellbooks.render;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpectralItemModel implements BakedModel {
   private final BakedModel original;

   public SpectralItemModel(BakedModel original) {
      this.original = original;
   }

   @NotNull
   public ItemOverrides m_7343_() {
      return this.original.m_7343_();
   }

   @NotNull
   public List<BakedQuad> m_213637_(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
      return this.original.m_213637_(state, side, rand);
   }

   public boolean m_7541_() {
      return this.original.m_7541_();
   }

   public boolean m_7539_() {
      return this.original.m_7539_();
   }

   public boolean m_7547_() {
      return this.original.m_7547_();
   }

   public boolean m_7521_() {
      return this.original.m_7521_();
   }

   @NotNull
   public TextureAtlasSprite m_6160_() {
      return this.original.m_6160_();
   }

   public ItemTransforms m_7442_() {
      return this.original.m_7442_();
   }

   @NotNull
   public List<RenderType> getRenderTypes(@NotNull ItemStack itemStack, boolean fabulous) {
      return List.of(RenderHelper.CustomerRenderType.magic(InventoryMenu.f_39692_));
   }
}
