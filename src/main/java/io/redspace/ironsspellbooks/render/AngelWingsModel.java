package io.redspace.ironsspellbooks.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AngelWingsModel<T extends LivingEntity> extends AgeableListModel<T> {
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   public static final String MAIN = "main";
   public static final String ANGEL_WINGS = "angel_wings";
   public static ModelLayerLocation ANGEL_WINGS_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "angel_wings"), "main");

   public AngelWingsModel(ModelPart pRoot) {
      this.leftWing = pRoot.m_171324_("left_wing");
      this.rightWing = pRoot.m_171324_("right_wing");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      CubeDeformation cubedeformation = new CubeDeformation(1.0F);
      partdefinition.m_171599_(
         "left_wing",
         CubeListBuilder.m_171558_().m_171514_(22, 0).m_171488_(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation),
         PartPose.m_171423_(5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
      );
      partdefinition.m_171599_(
         "right_wing",
         CubeListBuilder.m_171558_().m_171514_(22, 0).m_171480_().m_171488_(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation),
         PartPose.m_171423_(-5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }

   protected Iterable<ModelPart> m_5607_() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> m_5608_() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      float f = (float) (Math.PI / 12);
      float f1 = (float) (-Math.PI / 12);
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (pEntity.m_21255_()) {
         float f4 = 1.0F;
         Vec3 vec3 = pEntity.m_20184_();
         if (vec3.f_82480_ < 0.0) {
            Vec3 vec31 = vec3.m_82541_();
            f4 = 1.0F - (float)Math.pow(-vec31.f_82480_, 1.5);
         }

         f = f4 * (float) (Math.PI / 9) + (1.0F - f4) * f;
         f1 = f4 * (float) (-Math.PI / 2) + (1.0F - f4) * f1;
      } else if (pEntity.m_6047_()) {
         f = (float) (Math.PI * 2.0 / 9.0);
         f1 = (float) (-Math.PI / 4);
         f2 = 3.0F;
         f3 = 0.08726646F;
      }

      this.leftWing.f_104201_ = f2;
      if (pEntity instanceof AbstractClientPlayer abstractclientplayer) {
         abstractclientplayer.f_108542_ = abstractclientplayer.f_108542_ + (f - abstractclientplayer.f_108542_) * 0.1F;
         abstractclientplayer.f_108543_ = abstractclientplayer.f_108543_ + (f3 - abstractclientplayer.f_108543_) * 0.1F;
         abstractclientplayer.f_108544_ = abstractclientplayer.f_108544_ + (f1 - abstractclientplayer.f_108544_) * 0.1F;
         this.leftWing.f_104203_ = abstractclientplayer.f_108542_;
         this.leftWing.f_104204_ = abstractclientplayer.f_108543_;
         this.leftWing.f_104205_ = abstractclientplayer.f_108544_;
      } else {
         this.leftWing.f_104203_ = f;
         this.leftWing.f_104205_ = f1;
         this.leftWing.f_104204_ = f3;
      }

      this.rightWing.f_104204_ = -this.leftWing.f_104204_;
      this.rightWing.f_104201_ = this.leftWing.f_104201_;
      this.rightWing.f_104203_ = this.leftWing.f_104203_;
      this.rightWing.f_104205_ = -this.leftWing.f_104205_;
   }
}
