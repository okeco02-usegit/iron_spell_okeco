package io.redspace.ironsspellbooks.entity.armor.pumpkin;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.PumpkinArmorItem;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;

public class PumpkinArmorRenderer extends GenericCustomArmorRenderer<PumpkinArmorItem> {
   public GeoBone bodyHeadLayerBone = null;

   public PumpkinArmorRenderer(GeoModel<PumpkinArmorItem> model) {
      super(model);
   }

   @Nullable
   public GeoBone getBodyHeadLayerBone() {
      return (GeoBone)this.model.getBone("armorBodyHeadLayer").orElse(null);
   }

   @Override
   protected void grabRelevantBones(BakedGeoModel bakedModel) {
      if (this.lastModel == bakedModel) {
         this.bodyHeadLayerBone = this.getBodyHeadLayerBone();
      }

      super.grabRelevantBones(bakedModel);
   }

   @Override
   protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
      super.applyBoneVisibilityBySlot(currentSlot);
      if (currentSlot == EquipmentSlot.CHEST) {
         this.setBoneVisible(this.bodyHeadLayerBone, true);
      }
   }

   @Override
   public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
      super.applyBoneVisibilityByPart(currentSlot, currentPart, model);
      if (currentPart == model.f_102810_ && currentSlot == EquipmentSlot.CHEST) {
         this.setBoneVisible(this.bodyHeadLayerBone, true);
      }
   }

   @Override
   protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
      super.applyBaseTransformations(baseModel);
      if (this.bodyHeadLayerBone != null) {
         ModelPart bodyPart = baseModel.f_102808_;
         RenderUtils.matchModelPartRot(bodyPart, this.bodyHeadLayerBone);
         this.bodyHeadLayerBone.updatePosition(bodyPart.f_104200_, -bodyPart.f_104201_, bodyPart.f_104202_);
      }
   }

   @Override
   public void m_8009_(boolean pVisible) {
      super.m_8009_(pVisible);
      this.setBoneVisible(this.bodyHeadLayerBone, pVisible);
   }
}
