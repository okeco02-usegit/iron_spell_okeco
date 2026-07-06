package io.redspace.ironsspellbooks.entity.spells.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class ShieldModel extends EntityModel<ShieldEntity> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "shield_model"), "main"
   );
   private final ModelPart bb_main;

   public ShieldModel(ModelPart root) {
      this.bb_main = root.m_171324_("bb_main");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition bb_main = partdefinition.m_171599_(
         "bb_main",
         CubeListBuilder.m_171558_()
            .m_171514_(4, 8)
            .m_171488_(-6.0F, -3.0F, 0.0F, 12.0F, 8.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(7, 7)
            .m_171488_(-3.0F, 5.0F, 0.0F, 6.0F, 1.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(9, 6)
            .m_171488_(-1.0F, 6.0F, 0.0F, 2.0F, 1.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(5, 16)
            .m_171488_(-5.0F, -4.0F, 0.0F, 10.0F, 1.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(7, 18)
            .m_171488_(-3.0F, -6.0F, 0.0F, 6.0F, 1.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(6, 17)
            .m_171488_(-4.0F, -5.0F, 0.0F, 8.0F, 1.0F, 0.1F, new CubeDeformation(0.0F))
            .m_171514_(8, 19)
            .m_171488_(-2.0F, -7.0F, 0.0F, 4.0F, 1.0F, 0.1F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }

   public void setupAnim(ShieldEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void m_7695_(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float r, float g, float b, float a) {
      this.bb_main.m_104306_(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, r, g, b, a);
   }
}
