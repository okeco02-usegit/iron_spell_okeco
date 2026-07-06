package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.item.armor.IArmorCapeProvider;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorCapeLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {
   private ModelPart cape;
   private Consumer<PoseStack> bodyTransformer;
   public static ModelLayerLocation ARMOR_CAPE_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "armor_cape"), "main");

   public ArmorCapeLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> pRenderer) {
      super(pRenderer);
      this.cape = Minecraft.m_91087_().m_167973_().m_171103_(ARMOR_CAPE_LAYER).m_171324_("cape");
      this.bodyTransformer = poseStack -> ((HumanoidModel)this.m_117386_()).f_102810_.m_104299_(poseStack);
   }

   public ArmorCapeLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> pRenderer, Consumer<PoseStack> bodyTransformer) {
      this(pRenderer);
      this.bodyTransformer = bodyTransformer;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_(
         "cape",
         CubeListBuilder.m_171558_().m_171514_(0, 0).m_171496_(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, CubeDeformation.f_171458_, 1.0F, 0.5F),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void render(
      PoseStack pPoseStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      LivingEntity livingEntity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (this.shouldRender(livingEntity)) {
         ResourceLocation texture = ((IArmorCapeProvider)livingEntity.m_6844_(EquipmentSlot.CHEST).m_41720_()).getCapeResourceLocation();
         IArmorCapeProvider.CapeData capeData = ((MagicData.IExtendedEntity)livingEntity).irons_spellbooks$getCapData();
         int lastTick = capeData.lastTick;
         if (lastTick != livingEntity.f_19797_) {
            capeData.moveCloak(livingEntity);
            capeData.lastTick = livingEntity.f_19797_;
         }

         pPoseStack.m_85836_();
         pPoseStack.m_252880_(0.0F, 0.0F, 0.125F);
         double d0 = Mth.m_14139_(pPartialTicks, capeData.xCloakO, capeData.xCloak)
            - Mth.m_14139_(pPartialTicks, livingEntity.f_19854_, livingEntity.m_20185_());
         double d1 = Mth.m_14139_(pPartialTicks, capeData.yCloakO, capeData.yCloak)
            - Mth.m_14139_(pPartialTicks, livingEntity.f_19855_, livingEntity.m_20186_());
         double d2 = Mth.m_14139_(pPartialTicks, capeData.zCloakO, capeData.zCloak)
            - Mth.m_14139_(pPartialTicks, livingEntity.f_19856_, livingEntity.m_20189_());
         float f = Mth.m_14189_(pPartialTicks, livingEntity.f_20884_, livingEntity.f_20883_);
         double d3 = Mth.m_14031_(f * (float) (Math.PI / 180.0));
         double d4 = -Mth.m_14089_(f * (float) (Math.PI / 180.0));
         float f1 = (float)d1 * 10.0F;
         f1 = Mth.m_14036_(f1, -6.0F, 32.0F);
         float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
         f2 = Mth.m_14036_(f2, 0.0F, 150.0F);
         float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
         f3 = Mth.m_14036_(f3, -20.0F, 20.0F);
         if (f2 < 0.0F) {
            f2 = 0.0F;
         }

         float f4 = Mth.m_14179_(pPartialTicks, capeData.oBob, capeData.bob);
         f1 += Mth.m_14031_(Mth.m_14179_(pPartialTicks, livingEntity.f_19867_, livingEntity.f_19787_) * 6.0F) * 32.0F * f4;
         if (livingEntity.m_6047_()) {
            f1 += 25.0F;
            this.cape.f_104202_ = 1.4F;
            this.cape.f_104201_ = 1.85F;
         } else {
            this.cape.f_104202_ = 0.0F;
            this.cape.f_104201_ = 0.0F;
         }

         this.bodyTransformer.accept(pPoseStack);
         pPoseStack.m_252781_(Axis.f_252529_.m_252977_(6.0F + f2 / 2.0F + f1));
         pPoseStack.m_252781_(Axis.f_252403_.m_252977_(f3 / 2.0F));
         pPoseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F - f3 / 2.0F));
         VertexConsumer vertexconsumer = pBuffer.m_6299_(RenderType.m_110458_(texture));
         this.cape.m_104301_(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.f_118083_);
         pPoseStack.m_85849_();
      }
   }

   private boolean shouldRender(LivingEntity livingEntity) {
      ItemStack itemstack = livingEntity.m_6844_(EquipmentSlot.CHEST);
      return !itemstack.m_150930_(Items.f_42741_)
         && itemstack.m_41720_() instanceof IArmorCapeProvider
         && !this.hasPlayerCape(livingEntity)
         && !livingEntity.m_21023_((MobEffect)MobEffectRegistry.ANGEL_WINGS.get());
   }

   private boolean hasPlayerCape(LivingEntity livingEntity) {
      return livingEntity instanceof AbstractClientPlayer player && player.m_108561_() != null;
   }
}
