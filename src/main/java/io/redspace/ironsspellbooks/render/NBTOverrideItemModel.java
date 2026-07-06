package io.redspace.ironsspellbooks.render;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NBTOverrideItemModel implements BakedModel {
   private final BakedModel original;
   private final ItemOverrides itemOverrides;

   public NBTOverrideItemModel(BakedModel original, ModelBakery loader) {
      this.original = original;
      BlockModel missing = (BlockModel)loader.m_119341_(ModelBakery.f_119230_);
      this.itemOverrides = new ItemOverrides(new ModelBaker() {
         public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
            return null;
         }

         public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
            return null;
         }

         public UnbakedModel m_245361_(ResourceLocation resourceLocation) {
            return null;
         }

         @Nullable
         public BakedModel m_245240_(ResourceLocation resourceLocation, ModelState modelState) {
            return null;
         }
      }, missing, Collections.emptyList()) {
         public BakedModel m_173464_(
            @NotNull BakedModel original, @NotNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int seed
         ) {
            if (itemStack.m_41782_()) {
               Optional<ResourceLocation> override = NBTOverrideItemModel.this.getModelFromTag(itemStack, itemStack.m_41783_());
               if (override.isPresent()) {
                  ModelManager manager = Minecraft.m_91087_().m_91304_();
                  BakedModel missingx = manager.m_119422_(ModelBakery.f_119230_);
                  BakedModel model = manager.getModel(override.get());
                  return model == missingx ? original : model;
               }
            }

            return original;
         }
      };
   }

   abstract Optional<ResourceLocation> getModelFromTag(ItemStack var1, CompoundTag var2);

   @NotNull
   public ItemOverrides m_7343_() {
      return this.itemOverrides;
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
}
