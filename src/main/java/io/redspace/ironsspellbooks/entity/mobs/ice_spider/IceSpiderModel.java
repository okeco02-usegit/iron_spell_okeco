package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceSpiderModel extends DefaultedEntityGeoModel<IceSpiderEntity> {
   static final String[] SIDES = new String[]{"right", "left"};
   static final String[] LEGS = new String[]{"Fore", "ForeMiddle", "BackMiddle", "Back"};
   static final String SHOULDER = "Shoulder";
   static final String LEG = "Leg";
   static final float OFFSET_PER_LEG = 0.61086524F;
   protected TransformStack transformStack = new TransformStack();
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/ice_spider/ice_spider.png");
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/ice_spider.geo.json");
   public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/ice_spider.animation.json");
   private long lastRenderedInstance = -1L;

   public IceSpiderModel() {
      super(IronsSpellbooks.id("spellcastingmob"));
   }

   public ResourceLocation getModelResource(IceSpiderEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(IceSpiderEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(IceSpiderEntity animatable) {
      return ANIMATION;
   }

   public void handleAnimations(IceSpiderEntity entity, long instanceId, AnimationState<IceSpiderEntity> animationState) {
      float partialTick = animationState.getPartialTick();
      AnimatableManager<GeoAnimatable> manager = entity.getAnimatableInstanceCache().getManagerForId(instanceId);
      Double currentTick = (Double)animationState.getData(DataTickets.TICK);
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

   public void setCustomAnimations(IceSpiderEntity entity, long instanceId, AnimationState<IceSpiderEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      float partialTick = animationState.getPartialTick();
      this.transformStack
         .pushPosition(
            this.getAnimationProcessor().getBone("torso"),
            (float)IceSpiderEntity.TORSO_OFFSET.f_82479_,
            (float)IceSpiderEntity.TORSO_OFFSET.f_82480_ * entity.getCrouchHeightMultiplier(partialTick),
            (float)IceSpiderEntity.TORSO_OFFSET.f_82481_
         );
      Vec3 normal = Utils.lerp(partialTick, entity.lastNormal, entity.normal);
      Quaternionf normalRotation = Utils.rotationBetweenVectors(normal.m_252839_(), new Vector3f(0.0F, 1.0F, 0.0F));
      Vector3f headRotation = new Vector3f(
         Mth.m_14179_(partialTick, entity.f_19860_, entity.m_146909_()) * (float) (Math.PI / 180.0),
         Mth.m_14179_(
            partialTick,
            Mth.m_14177_(entity.f_20886_ - entity.f_20884_) * (float) (Math.PI / 180.0),
            Mth.m_14177_(entity.f_20885_ - entity.f_20883_) * (float) (Math.PI / 180.0)
         ),
         0.0F
      );
      normalRotation.invert().transform(headRotation);
      CoreGeoBone head = this.getAnimationProcessor().getBone("head");
      this.transformStack.pushRotation(head, -headRotation.x, -headRotation.y, -headRotation.z);
      Vector2f limbSwingVec = this.getLimbSwing(entity, entity.f_267362_, partialTick);
      float limbSwing = limbSwingVec.y;
      float limbSwingAmount = limbSwingVec.x;
      float f = 0.5F;
      float yRange = (float) (Math.PI / 9) * f;
      float zRange = (float) (Math.PI / 15) * f;
      float speed = 0.1F / f;
      float primaryY = this.legY(limbSwing, speed, 0.0F) * yRange * limbSwingAmount;
      float secondaryY = this.legY(limbSwing, speed, (float) Math.PI) * yRange * limbSwingAmount;
      float primaryZ = this.legZ(limbSwing, speed, (float) Math.PI) * zRange * limbSwingAmount;
      float secondaryZ = this.legZ(limbSwing, speed, 0.0F) * zRange * limbSwingAmount;

      for (int i = 0; i < SIDES.length; i++) {
         for (int j = 0; j < LEGS.length; j++) {
            int sideSign = Mth.m_14205_(i - 0.5);
            float baseY = 0.0F;
            float baseZ = Mth.m_14179_(entity.crouchTweenPercent(partialTick), 10.0F, 0.0F) * (float) (Math.PI / 180.0);
            String shoulderBone = String.format("%s%s%s", SIDES[i], LEGS[j], "Shoulder");
            String legBone = String.format("%s%s%s", SIDES[i], LEGS[j], "Leg");
            boolean primary = j % 2 == 0;

            try {
               this.transformStack
                  .pushRotation(
                     Objects.requireNonNull(this.getAnimationProcessor().getBone(shoulderBone)),
                     0.0F,
                     ((primary ? primaryY : secondaryY) + baseY) * sideSign,
                     0.0F
                  );
               this.transformStack
                  .pushRotation(
                     Objects.requireNonNull(this.getAnimationProcessor().getBone(legBone)), 0.0F, 0.0F, ((primary ? primaryZ : secondaryZ) + baseZ) * -sideSign
                  );
            } catch (Exception e) {
               IronsSpellbooks.LOGGER.error("beep");
            }
         }
      }
   }

   private float legY(float limbSwing, float speedFactor, float offset) {
      float f = offset - (float) (Math.PI / 2);
      return Mth.m_14031_(limbSwing * (float) (Math.PI * 2) * speedFactor + f + Mth.m_14031_(limbSwing * (float) (Math.PI * 2) * speedFactor + f) * 0.5F);
   }

   private float legZ(float limbSwing, float speedFactor, float offset) {
      float f = Mth.m_14031_(limbSwing * (float) (Math.PI * 2) * speedFactor + offset);
      f = f * f * f;
      return Math.max(f, 0.0F);
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
