package io.redspace.ironsspellbooks.entity.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DyeableArmorRenderer<T extends Item & GeoItem> extends GenericCustomArmorRenderer<T> {
   public DyeableArmorRenderer(GeoModel<T> model) {
      super(model);
   }

   public void renderCubesOfBone(
      PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      int color = -1;
      if (bone.getName().startsWith("dye") && this.currentStack != null) {
         color = Minecraft.m_91087_().getItemColors().m_92676_(this.currentStack, 0) | 0xFF000000;
      }

      Vec3i c = new Vec3i(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
      super.renderCubesOfBone(
         poseStack, bone, buffer, packedLight, packedOverlay, c.m_123341_() / 255.0F, c.m_123342_() / 255.0F, c.m_123343_() / 255.0F, alpha
      );
   }
}
