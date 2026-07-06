package io.redspace.ironsspellbooks.entity.armor;

import java.util.ArrayList;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class GenericCustomArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {
   protected final ArrayList<GenericCustomArmorRenderer<T>.AsyncBone> asyncBones = new ArrayList<>();

   public ResourceLocation getTextureLocation(T animatable) {
      return super.getTextureLocation(animatable);
   }

   public GenericCustomArmorRenderer(GeoModel<T> model) {
      super(model);
      this.asyncBones.add(new GenericCustomArmorRenderer.AsyncBone("armorLeggingTorsoLayer", EquipmentSlot.LEGS, m -> m.f_102810_, Vec3.f_82478_));
      this.asyncBones
         .add(new GenericCustomArmorRenderer.AsyncBone("armorTorsoExtensionRightLeg", EquipmentSlot.CHEST, m -> m.f_102813_, new Vec3(2.0, 12.0, 0.0)));
      this.asyncBones
         .add(new GenericCustomArmorRenderer.AsyncBone("armorTorsoExtensionLeftLeg", EquipmentSlot.CHEST, m -> m.f_102814_, new Vec3(-2.0, 12.0, 0.0)));
   }

   protected void grabRelevantBones(BakedGeoModel bakedModel) {
      if (this.lastModel != bakedModel) {
         this.asyncBones.forEach(bone -> bone.grabBone(this.model));
      }

      super.grabRelevantBones(bakedModel);
   }

   protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
      super.applyBoneVisibilityBySlot(currentSlot);
      this.asyncBones.forEach(bone -> bone.applyVisibilityBySlot(currentSlot));
   }

   public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
      super.applyBoneVisibilityByPart(currentSlot, currentPart, model);
      this.asyncBones.forEach(bone -> bone.applyVisibilityByPart(model, currentPart));
   }

   protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
      super.applyBaseTransformations(baseModel);
      this.asyncBones
         .forEach(
            bone -> {
               if (bone.actualBone != null) {
                  ModelPart bodyPart = bone.partToFollow.apply(baseModel);
                  RenderUtils.matchModelPartRot(bodyPart, bone.actualBone);
                  bone.actualBone
                     .updatePosition(
                        (float)bone.partOffset.f_82479_ + bodyPart.f_104200_,
                        (float)bone.partOffset.f_82480_ + -bodyPart.f_104201_,
                        (float)bone.partOffset.f_82481_ + bodyPart.f_104202_
                     );
               }
            }
         );
   }

   public void m_8009_(boolean pVisible) {
      super.m_8009_(pVisible);
      this.asyncBones.forEach(bone -> this.setBoneVisible(bone.actualBone, pVisible));
   }

   public class AsyncBone {
      @Nullable
      private GeoBone actualBone = null;
      private final String boneName;
      private final EquipmentSlot itemSlot;
      private final Function<HumanoidModel<?>, ModelPart> partToFollow;
      private final Vec3 partOffset;

      public AsyncBone(String boneName, EquipmentSlot itemSlot, Function<HumanoidModel<?>, ModelPart> partToFollow, Vec3 partOffset) {
         this.boneName = boneName;
         this.itemSlot = itemSlot;
         this.partToFollow = partToFollow;
         this.partOffset = partOffset;
      }

      public void grabBone(GeoModel<?> model) {
         this.actualBone = (GeoBone)model.getBone(this.boneName).orElse(null);
      }

      public void applyVisibilityBySlot(EquipmentSlot currentSlot) {
         if (currentSlot == this.itemSlot) {
            GenericCustomArmorRenderer.this.setBoneVisible(this.actualBone, true);
         } else {
            GenericCustomArmorRenderer.this.setBoneVisible(this.actualBone, false);
         }
      }

      public void applyVisibilityByPart(HumanoidModel<?> model, ModelPart part) {
         if (part == this.partToFollow.apply(model)) {
            GenericCustomArmorRenderer.this.setBoneVisible(this.actualBone, true);
         } else {
            GenericCustomArmorRenderer.this.setBoneVisible(this.actualBone, false);
         }
      }
   }
}
