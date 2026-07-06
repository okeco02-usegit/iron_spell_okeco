package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.item.Item;
import org.joml.Vector2f;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class FireBossModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/fire_boss/tyros.png");
   public static final ResourceLocation TEXTURE_SOUL_MODE = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/entity/fire_boss/tyros_soul_mode.png"
   );
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/tyros.geo.json");
   private static final float tilt = (float) (Math.PI / 12);
   private static final Vector3f forward = new Vector3f(0.0F, 0.0F, Mth.m_14031_((float) (Math.PI / 12)) * -12.0F);
   int lastTick;

   @Override
   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return object instanceof FireBossEntity fireBossEntity && fireBossEntity.isSoulMode() ? TEXTURE_SOUL_MODE : TEXTURE;
   }

   @Override
   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return MODEL;
   }

   @Override
   public void setCustomAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState) {
      if (!Minecraft.m_91087_().m_91104_()) {
         if (entity instanceof FireBossEntity fireBossEntity) {
            this.handleParticles(fireBossEntity);
            float partialTick = animationState.getPartialTick();
            Vector2f limbSwing = this.getLimbSwing(entity, entity.f_267362_, partialTick);
            if (entity.isAnimating()) {
               fireBossEntity.isAnimatingDampener = Mth.m_14179_(0.15F * partialTick, fireBossEntity.isAnimatingDampener, 0.0F);
            } else {
               fireBossEntity.isAnimatingDampener = Mth.m_14179_(0.05F * partialTick, fireBossEntity.isAnimatingDampener, 1.0F);
            }

            if (entity.m_21205_().m_150930_((Item)ItemRegistry.HELLRAZOR.get()) || entity.m_21205_().m_150930_((Item)ItemRegistry.DECREPIT_SCYTHE.get())) {
               CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
               CoreGeoBone rightHand = this.getAnimationProcessor().getBone("bipedHandRight");
               Vector3f armPose = new Vector3f(-30.0F, -30.0F, 10.0F);
               armPose.mul((float) (Math.PI / 180.0) * fireBossEntity.isAnimatingDampener);
               this.transformStack.pushRotation(rightArm, armPose);
               Vector3f scythePos = new Vector3f(-5.0F, 0.0F, -48.0F);
               scythePos.mul((float) (Math.PI / 180.0) * fireBossEntity.isAnimatingDampener);
               this.transformStack.pushRotation(rightHand, scythePos);
               if (!entity.isAnimating()) {
                  float walkDampener = Mth.m_14089_(limbSwing.y() * 0.6662F + (float) Math.PI) * 2.0F * limbSwing.x() * 0.5F * -0.75F;
                  this.transformStack.pushRotation(rightArm, walkDampener, 0.0F, 0.0F);
               }
            }

            if (fireBossEntity.isHalfHealthAttacking()) {
               CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
               CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
               CoreGeoBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
               CoreGeoBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
               float f = fireBossEntity.f_19797_ + partialTick;
               this.bobBone(rightArm, f * 3.0F, -4.0F);
               this.bobBone(leftArm, f * 3.0F, 4.0F);
               this.bobBone(rightLeg, f, -1.5F);
               this.bobBone(leftLeg, f + 90.0F, 1.5F);
            }
         }

         super.setCustomAnimations(entity, instanceId, animationState);
      }
   }

   public void handleParticles(FireBossEntity entity) {
      CoreGeoBone particleEmitter = this.getAnimationProcessor().getBone("particle_emitter");
      CoreGeoBone body = this.getAnimationProcessor().getBone("body");
      CoreGeoBone offhand = this.getAnimationProcessor().getBone("bipedHandLeft");
   }

   @Override
   protected Vector2f getLimbSwing(AbstractSpellCastingMob entity, WalkAnimationState walkAnimationState, float partialTick) {
      Vector2f swing = super.getLimbSwing(entity, walkAnimationState, partialTick);
      swing.mul(0.6F, 1.0F);
      return swing;
   }
}
