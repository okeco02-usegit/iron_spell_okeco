package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public abstract class AbstractSpellCastingMobModel extends DefaultedEntityGeoModel<AbstractSpellCastingMob> {
   protected TransformStack transformStack = new TransformStack();
   private long lastRenderedInstance = -1L;

   public AbstractSpellCastingMobModel() {
      super(IronsSpellbooks.id("spellcastingmob"));
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return AbstractSpellCastingMob.modelResource;
   }

   public abstract ResourceLocation getTextureResource(AbstractSpellCastingMob var1);

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
      return AbstractSpellCastingMob.animationInstantCast;
   }

   public void handleAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState) {
      AnimatableManager<GeoAnimatable> manager = entity.getAnimatableInstanceCache().getManagerForId(instanceId);
      Double currentTick = (Double)animationState.getData(DataTickets.TICK);
      float partialTick = animationState.getPartialTick();
      double currentFrameTime = !(entity instanceof Entity) && !(entity instanceof GeoReplacedEntity)
         ? currentTick - manager.getFirstTickTime()
         : currentTick + partialTick;
      boolean isReRender = !manager.isFirstTick() && currentFrameTime == manager.getLastUpdateTime();
      if (!isReRender || instanceId != this.lastRenderedInstance) {
         this.lastRenderedInstance = instanceId;
         this.transformStack.resetDirty();
         super.handleAnimations(entity, instanceId, animationState);
         this.transformStack.popStack();
      }
   }

   public void setCustomAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (entity.shouldBeExtraAnimated()) {
         float partialTick = animationState.getPartialTick();
         CoreGeoBone head = this.getAnimationProcessor().getBone("head");
         CoreGeoBone body = this.getAnimationProcessor().getBone("body");
         CoreGeoBone torso = this.getAnimationProcessor().getBone("torso");
         CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
         CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
         CoreGeoBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
         CoreGeoBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
         if (!entity.isAnimating() || entity.shouldAlwaysAnimateHead()) {
            this.transformStack
               .pushRotation(
                  head,
                  Mth.m_14179_(partialTick, -entity.f_19860_, -entity.m_146909_()) * (float) (Math.PI / 180.0),
                  Mth.m_14179_(
                     partialTick,
                     Mth.m_14177_(-entity.f_20886_ + entity.f_20884_) * (float) (Math.PI / 180.0),
                     Mth.m_14177_(-entity.f_20885_ + entity.f_20883_) * (float) (Math.PI / 180.0)
                  ),
                  0.0F
               );
         }

         Vector2f limbSwing = this.getLimbSwing(entity, entity.f_267362_, partialTick);
         float limbSwingAmount = limbSwing.x;
         float limbSwingSpeed = limbSwing.y;
         if (entity.m_20159_() && entity.m_20202_().shouldRiderSit()) {
            this.transformStack.pushRotation(rightLeg, 1.4137167F, (float) (-Math.PI / 10), -0.07853982F);
            this.transformStack.pushRotation(leftLeg, 1.4137167F, (float) (Math.PI / 10), 0.07853982F);
         } else if (!entity.isAnimating() || entity.shouldAlwaysAnimateLegs()) {
            float strength = 0.75F;
            Vec3 facing = entity.m_20156_().m_82542_(1.0, 0.0, 1.0).m_82541_();
            Vec3 momentum = entity.m_20184_().m_82542_(1.0, 0.0, 1.0).m_82541_();
            Vec3 facingOrth = new Vec3(-facing.f_82481_, 0.0, facing.f_82479_);
            float directionForward = (float)facing.m_82526_(momentum);
            float directionSide = (float)facingOrth.m_82526_(momentum) * 0.35F;
            float rightLateral = -Mth.m_14031_(limbSwingSpeed * 0.6662F) * 4.0F * limbSwingAmount;
            float leftLateral = -Mth.m_14031_(limbSwingSpeed * 0.6662F - (float) Math.PI) * 4.0F * limbSwingAmount;
            this.transformStack
               .pushPosition(
                  rightLeg,
                  rightLateral * directionSide,
                  Mth.m_14089_(limbSwingSpeed * 0.6662F) * 4.0F * strength * limbSwingAmount,
                  rightLateral * directionForward
               );
            this.transformStack.pushRotation(rightLeg, Mth.m_14089_(limbSwingSpeed * 0.6662F) * 1.4F * limbSwingAmount * strength, 0.0F, 0.0F);
            this.transformStack
               .pushPosition(
                  leftLeg,
                  leftLateral * directionSide,
                  Mth.m_14089_(limbSwingSpeed * 0.6662F - (float) Math.PI) * 4.0F * strength * limbSwingAmount,
                  leftLateral * directionForward
               );
            this.transformStack.pushRotation(leftLeg, Mth.m_14089_(limbSwingSpeed * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * strength, 0.0F, 0.0F);
            if (entity.bobBodyWhileWalking()) {
               this.transformStack
                  .pushPosition(
                     body,
                     0.0F,
                     Mth.m_14154_(Mth.m_14089_((limbSwingSpeed * 1.2662F - (float) (Math.PI / 2)) * 0.5F)) * 2.0F * strength * limbSwingAmount,
                     0.0F
                  );
            }
         }

         if (!entity.isAnimating()) {
            this.transformStack.pushRotation(rightArm, Mth.m_14089_(limbSwingSpeed * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F, 0.0F, 0.0F);
            this.transformStack.pushRotation(leftArm, Mth.m_14089_(limbSwingSpeed * 0.6662F) * 2.0F * limbSwingAmount * 0.5F, 0.0F, 0.0F);
            this.bobBone(rightArm, entity.f_19797_ + partialTick, 1.0F);
            this.bobBone(leftArm, entity.f_19797_ + partialTick, -1.0F);
            if (entity.isDrinkingPotion()) {
               this.transformStack
                  .pushRotation(
                     entity.m_21526_() ? leftArm : rightArm,
                     0.61086524F,
                     (entity.m_21526_() ? -25 : 25) * (float) (Math.PI / 180.0),
                     (entity.m_21526_() ? 15 : -15) * (float) (Math.PI / 180.0)
                  );
            }
         }
      }
   }

   protected void bobBone(CoreGeoBone bone, float offset, float multiplier) {
      float z = multiplier * (Mth.m_14089_(offset * 0.09F) * 0.05F + 0.05F);
      float x = multiplier * Mth.m_14031_(offset * 0.067F) * 0.05F;
      this.transformStack.pushRotation(bone, x, 0.0F, z);
   }

   protected Vector2f getLimbSwing(AbstractSpellCastingMob entity, WalkAnimationState walkAnimationState, float partialTick) {
      float limbSwingAmount = 0.0F;
      float limbSwingSpeed = 0.0F;
      if (entity.m_6084_()) {
         limbSwingAmount = walkAnimationState.m_267711_(partialTick);
         limbSwingSpeed = walkAnimationState.m_267590_(partialTick);
         if (entity.m_6162_()) {
            limbSwingSpeed *= 3.0F;
         }

         if (limbSwingAmount > 1.0F) {
            limbSwingAmount = 1.0F;
         }
      }

      return new Vector2f(limbSwingAmount, limbSwingSpeed);
   }
}
