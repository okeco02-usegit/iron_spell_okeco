package io.redspace.ironsspellbooks.item.weapons.pyrium_staff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PyriumStaffHeadModel extends Model {
   public static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/item/pyrium_staff_head.png");
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IronsSpellbooks.id("pyrium_staff_head"), "main");
   private final ModelPart root;

   public PyriumStaffHeadModel(ModelPart pRoot) {
      super(RenderType::m_110452_);
      this.root = pRoot;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition bb_main = partdefinition.m_171599_(
         "bb_main",
         CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-1.0F, -1.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      PartDefinition forespike_r1 = bb_main.m_171599_(
         "forespike_r1",
         CubeListBuilder.m_171558_().m_171514_(13, 10).m_171488_(0.0F, -9.0F, 0.0F, 0.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F)
      );
      PartDefinition forespike_r2 = bb_main.m_171599_(
         "forespike_r2",
         CubeListBuilder.m_171558_().m_171514_(13, 10).m_171488_(0.0F, -9.0F, 0.0F, 0.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F)
      );
      PartDefinition forespike_r3 = bb_main.m_171599_(
         "forespike_r3",
         CubeListBuilder.m_171558_().m_171514_(13, 10).m_171488_(0.0F, -9.0F, 0.0F, 0.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 0.0F, -0.5F, 0.0F, 3.1416F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 32, 32);
   }

   public RenderType renderType() {
      return this.m_103119_(TEXTURE);
   }

   public void m_7695_(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float r, float g, float b, float a) {
      this.root.m_104306_(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, r, g, b, a);
   }
}
