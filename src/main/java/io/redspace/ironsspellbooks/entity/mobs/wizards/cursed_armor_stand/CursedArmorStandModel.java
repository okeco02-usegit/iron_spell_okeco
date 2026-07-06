package io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class CursedArmorStandModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/cultist.png");
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/armor_stand.geo.json");
   public static double[] rightArmPos = new double[]{0.0, 0.0, 0.0};

   @Override
   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return ArmorStandRenderer.f_113780_;
   }

   @Override
   public void setCustomAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState) {
      if (entity instanceof CursedArmorStandEntity cursedArmorStandEntity && cursedArmorStandEntity.isArmorStandFrozen()) {
         CursedArmorStandEntity.Pose pose = cursedArmorStandEntity.getArmorstandPose();
         CoreGeoBone head = this.getAnimationProcessor().getBone("head");
         CoreGeoBone body = this.getAnimationProcessor().getBone("body");
         CoreGeoBone torso = this.getAnimationProcessor().getBone("torso");
         CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
         CoreGeoBone rightHand = this.getAnimationProcessor().getBone("bipedHandRight");
         CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
         CoreGeoBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
         CoreGeoBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
         switch (pose) {
            case DEFAULT:
               this.transformStack.pushRotation(leftArm, (float) (Math.PI / 18), 0.0F, (float) (-Math.PI / 18));
               this.transformStack.pushRotation(rightArm, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 18));
               this.transformStack.pushRotation(leftLeg, (float) (-Math.PI / 180.0), 0.0F, (float) (-Math.PI / 180.0));
               this.transformStack.pushRotation(rightLeg, (float) (Math.PI / 180.0), 0.0F, (float) (Math.PI / 180.0));
               break;
            case KNEELING:
               this.transformStack.pushPosition(rightLeg, 0.0F, 2.0F, -3.0F);
               this.transformStack.pushRotation(rightLeg, -0.91629785F, 0.0F, 0.0F);
               this.transformStack.pushPosition(leftLeg, (float) (Math.PI / 180.0), 6.0F, -4.0F);
               this.transformStack.pushRotation(rightArm, (float) (Math.PI / 2), (float) (Math.PI / 4), (float) (Math.PI / 2));
               this.transformStack.pushRotation(leftArm, (float) (Math.PI / 4), 0.0F, 0.0F);
               this.transformStack.pushRotation(head, (float) (-Math.PI / 4), 0.0F, 0.0F);
               this.transformStack.pushRotation(torso, (float) (-Math.PI / 18), 0.0F, 0.0F);
               this.transformStack.pushPosition(body, 0.0F, -6.0F, 0.0F);
               break;
            case HEROIC:
               this.transformStack.pushRotation(rightArm, 2.3125613F, 0.0F, 0.0F);
               this.transformStack.pushRotation(leftArm, (float) (-Math.PI / 9), 0.0F, 0.0F);
               this.transformStack.pushRotation(leftLeg, (float) (-Math.PI / 15), 0.0F, 0.0F);
               this.transformStack.pushPosition(rightLeg, 0.0F, 0.0F, -2.0F);
               this.transformStack.pushRotation(head, 0.43633232F, 0.0F, 0.0F);
               break;
            case STOIC:
               this.transformStack.pushRotationDegrees(rightArm, 80.0F, 35.0F, 0.0F);
               this.transformStack.pushRotationDegrees(leftArm, 80.0F, -35.0F, 0.0F);
               this.transformStack.pushRotationDegrees(rightHand, 0.0F, 90.0F, 0.0F);
               this.transformStack.pushRotationDegrees(rightLeg, 0.0F, -1.0F, 0.0F);
               this.transformStack.pushRotationDegrees(leftLeg, 0.0F, 1.0F, 0.0F);
         }

         float partialTick = animationState.getPartialTick();
         if (cursedArmorStandEntity.helmetJiggle > 0) {
            float f = this.elastic(1.0F - (cursedArmorStandEntity.helmetJiggle - partialTick) / 15.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedHead"), 0.0F, f, 0.0F);
         }

         if (cursedArmorStandEntity.chestJiggle > 0) {
            float f = this.elastic(1.0F - (cursedArmorStandEntity.chestJiggle - partialTick) / 15.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedBody"), 0.0F, f, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedRightArm"), 0.0F, f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedLeftArm"), 0.0F, -f * 0.75F, 0.0F);
         }

         if (cursedArmorStandEntity.legJiggle > 0) {
            float f = this.elastic(1.0F - (cursedArmorStandEntity.legJiggle - partialTick) / 15.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedRightLeg"), 0.0F, -f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedLeftLeg"), 0.0F, f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedRightLeg2"), 0.0F, -f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedLeftLeg2"), 0.0F, f * 0.75F, 0.0F);
         }

         if (cursedArmorStandEntity.bootJiggle > 0) {
            float f = this.elastic(1.0F - (cursedArmorStandEntity.bootJiggle - partialTick) / 15.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedRightFoot"), 0.0F, -f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedLeftFoot"), 0.0F, f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedRightFoot2"), 0.0F, -f * 0.75F, 0.0F);
            this.transformStack.pushRotation(this.getAnimationProcessor().getBone("armorBipedLeftFoot2"), 0.0F, f * 0.75F, 0.0F);
         }

         this.transformStack.popStack();
      } else {
         super.setCustomAnimations(entity, instanceId, animationState);
      }
   }

   private float elastic(float f) {
      float x = (float)(Math.pow(2.0, -10.0F * f) * Mth.m_14089_((10.0F * f - 0.75F) * 2.0F * 0.6F * (float) Math.PI / 3.0F)) * 0.2F;
      return Math.abs(x) < 0.001 ? 0.0F : x;
   }

   @Override
   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return MODEL;
   }
}
