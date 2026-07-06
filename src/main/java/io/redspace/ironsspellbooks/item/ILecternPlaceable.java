package io.redspace.ironsspellbooks.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ILecternPlaceable {
   List<Component> getPages(ItemStack var1);

   default Optional<ResourceLocation> simpleTextureOverride(ItemStack stack) {
      return Optional.empty();
   }

   default void handleCustomLecternPosing(PoseStack poseStack) {
   }
}
