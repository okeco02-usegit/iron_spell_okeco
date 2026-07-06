package io.redspace.ironsspellbooks.entity.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.ArmorCapeLayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

public class HumanoidRenderer<T extends Mob & GeoAnimatable> extends GeoEntityRenderer<T> {
   private static final String LEFT_HAND = "bipedHandLeft";
   private static final String RIGHT_HAND = "bipedHandRight";
   private static final String LEFT_BOOT = "armorBipedLeftFoot";
   private static final String RIGHT_BOOT = "armorBipedRightFoot";
   private static final String LEFT_BOOT_2 = "armorBipedLeftFoot2";
   private static final String RIGHT_BOOT_2 = "armorBipedRightFoot2";
   private static final String LEFT_ARMOR_LEG = "armorBipedLeftLeg";
   private static final String RIGHT_ARMOR_LEG = "armorBipedRightLeg";
   private static final String LEFT_ARMOR_LEG_2 = "armorBipedLeftLeg2";
   private static final String RIGHT_ARMOR_LEG_2 = "armorBipedRightLeg2";
   private static final String CHESTPLATE = "armorBipedBody";
   private static final String RIGHT_SLEEVE = "armorBipedRightArm";
   private static final String LEFT_SLEEVE = "armorBipedLeftArm";
   private static final String HELMET = "armorBipedHead";
   private final RenderLayer hardCodedCapeLayer = new ArmorCapeLayer(null, poseStack -> {});

   public HumanoidRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
      this.addRenderLayer(new ItemArmorGeoLayer<T>(this) {
         @Nullable
         protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
            return switch (bone.getName()) {
               case "armorBipedLeftFoot", "armorBipedRightFoot", "armorBipedLeftFoot2", "armorBipedRightFoot2" -> this.bootsStack;
               case "armorBipedLeftLeg", "armorBipedRightLeg", "armorBipedLeftLeg2", "armorBipedRightLeg2" -> this.leggingsStack;
               case "armorBipedBody", "armorBipedRightArm", "armorBipedLeftArm" -> this.chestplateStack;
               case "armorBipedHead" -> this.helmetStack;
               default -> null;
            };
         }

         @Nonnull
         protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
            return switch (bone.getName()) {
               case "armorBipedLeftFoot", "armorBipedRightFoot", "armorBipedLeftFoot2", "armorBipedRightFoot2" -> EquipmentSlot.FEET;
               case "armorBipedLeftLeg", "armorBipedRightLeg", "armorBipedLeftLeg2", "armorBipedRightLeg2" -> EquipmentSlot.LEGS;
               case "armorBipedRightArm" -> !animatable.m_21526_() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
               case "armorBipedLeftArm" -> animatable.m_21526_() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
               case "armorBipedBody" -> EquipmentSlot.CHEST;
               case "armorBipedHead" -> EquipmentSlot.HEAD;
               default -> super.getEquipmentSlotForBone(bone, stack, animatable);
            };
         }

         @Nonnull
         protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
            return switch (bone.getName()) {
               case "armorBipedLeftFoot", "armorBipedLeftFoot2", "armorBipedLeftLeg", "armorBipedLeftLeg2" -> baseModel.f_102814_;
               case "armorBipedRightFoot", "armorBipedRightFoot2", "armorBipedRightLeg", "armorBipedRightLeg2" -> baseModel.f_102813_;
               case "armorBipedRightArm" -> baseModel.f_102811_;
               case "armorBipedLeftArm" -> baseModel.f_102812_;
               case "armorBipedBody" -> baseModel.f_102810_;
               case "armorBipedHead" -> baseModel.f_102808_;
               default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            };
         }
      });
      this.addRenderLayer(
         new BlockAndItemGeoLayer<T>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, T animatable) {
               if (animatable instanceof AbstractSpellCastingMob castingMob) {
                  String boneName = bone.getName();
                  if (HumanoidRenderer.this.isBoneMainHand(castingMob, boneName)) {
                     if (castingMob.isDrinkingPotion()) {
                        return AbstractSpellCastingMobRenderer.makePotion(castingMob);
                     }

                     if (HumanoidRenderer.this.shouldWeaponBeSheathed(castingMob) && castingMob.m_6844_(EquipmentSlot.MAINHAND).m_41720_() instanceof SwordItem
                        )
                      {
                        return ItemStack.f_41583_;
                     }
                  }

                  if (boneName.equals("torso")
                     && HumanoidRenderer.this.shouldWeaponBeSheathed(castingMob)
                     && castingMob.m_6844_(EquipmentSlot.MAINHAND).m_41720_() instanceof SwordItem) {
                     return castingMob.m_6844_(EquipmentSlot.MAINHAND);
                  }
               }
               return switch (bone.getName()) {
                  case "bipedHandLeft" -> animatable.m_21526_() ? animatable.m_21205_() : animatable.m_21206_();
                  case "bipedHandRight" -> animatable.m_21526_() ? animatable.m_21206_() : animatable.m_21205_();
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
               return switch (bone.getName()) {
                  case "bipedHandRight", "torso" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  case "bipedHandLeft" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               T animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               poseStack.m_85837_(0.0, 0.0, -0.0625);
               poseStack.m_85837_(0.0, -0.0625, 0.0);
               boolean offhand = stack == animatable.m_21206_();
               if (!offhand) {
                  poseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
                  if (stack.m_41720_() instanceof ShieldItem) {
                     poseStack.m_85837_(0.0, 0.125, -0.25);
                  }
               } else {
                  poseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
                  if (stack.m_41720_() instanceof ShieldItem) {
                     poseStack.m_85837_(0.0, 0.125, 0.25);
                     poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
                  }
               }

               if (animatable instanceof AbstractSpellCastingMob mob && bone.getName().equals("torso") && HumanoidRenderer.this.shouldWeaponBeSheathed(mob)) {
                  float hipOffset = animatable.m_6844_(EquipmentSlot.CHEST).m_41619_() ? 0.25F : 0.325F;
                  poseStack.m_85837_(animatable.m_21526_() ? hipOffset : -hipOffset, 0.0, -0.4);
                  poseStack.m_252781_(Axis.f_252529_.m_252977_(215.0F));
                  poseStack.m_85841_(0.85F, 0.85F, 0.85F);
               }

               HumanoidRenderer.this.adjustHandItemRendering(poseStack, stack, animatable, partialTick, offhand);
               super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
         }
      );
   }

   protected boolean isBoneMainHand(AbstractSpellCastingMob entity, String boneName) {
      return entity.m_21526_() && boneName.equals("bipedHandLeft") || !entity.m_21526_() && boneName.equals("bipedHandRight");
   }

   protected boolean shouldWeaponBeSheathed(AbstractSpellCastingMob entity) {
      return entity.shouldSheathSword() && !entity.m_5912_();
   }

   protected void adjustHandItemRendering(PoseStack poseStack, ItemStack stack, T animatable, float partialTick, boolean offhand) {
   }

   public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (!entity.m_20145_() || !entity.m_20177_(ClientUtils.getClientPlayer())) {
         super.m_7392_(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
         poseStack.m_85836_();
         float f = Mth.m_14189_(partialTick, entity.f_20884_, entity.f_20883_);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F - f));
         if (entity.f_20919_ > 0) {
            float deathRotation = (entity.f_20919_ + partialTick - 1.0F) / 20.0F * 1.6F;
            poseStack.m_252781_(Axis.f_252403_.m_252977_(Math.min(Mth.m_14116_(deathRotation), 1.0F) * this.getDeathMaxRotation(entity)));
         }

         this.model.getBone("torso").ifPresent(bone -> RenderUtils.prepMatrixForBone(poseStack, bone));
         poseStack.m_85841_(-1.0F, -1.0F, 1.0F);
         poseStack.m_252880_(0.0F, -1.501F, 0.0F);
         this.hardCodedCapeLayer.m_6494_(poseStack, bufferSource, packedLight, entity, 0.0F, 0.0F, partialTick, 0.0F, 0.0F, 0.0F);
         poseStack.m_85849_();
      }
   }

   @org.jetbrains.annotations.Nullable
   public RenderType getRenderType(
      T animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick
   ) {
      return animatable.m_20145_() ? RenderType.m_110467_(texture) : RenderType.m_110443_(texture, false);
   }
}
