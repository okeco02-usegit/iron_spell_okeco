package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class VoidTentacleModel extends GeoModel<VoidTentacle> {
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/void_tentacle.geo.json");
   public static final ResourceLocation textureResource = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle.png");
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "animations/void_tentacle_animations.json"
   );

   public ResourceLocation getModelResource(VoidTentacle object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(VoidTentacle mob) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(VoidTentacle animatable) {
      return animationResource;
   }

   public void setCustomAnimations(VoidTentacle animatable, long instanceId, AnimationState<VoidTentacle> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
      float seed = (float)(animatable.m_20185_() * animatable.m_20189_()) % 173.0F;
      float speed = 0.55F;
      float f = (seed + animatable.f_19797_ + animationState.getPartialTick()) * speed;
      List<CoreGeoBone> bones = List.of(
         this.getAnimationProcessor().getBone("root"),
         this.getAnimationProcessor().getBone("segment_1"),
         this.getAnimationProcessor().getBone("segment_2"),
         this.getAnimationProcessor().getBone("segment_3"),
         this.getAnimationProcessor().getBone("segment_4")
      );
      int age = animatable.f_19797_;
      float tween = Mth.m_14036_(age < 15 ? (age - 5) / 10.0F : (age > 240 ? 1.0F - (age - 240) / 50.0F : 1.0F), 0.0F, 1.0F);

      for (int i = 0; i < bones.size(); i++) {
         CoreGeoBone bone = bones.get(i);
         float intensity = 3.0F - i * 0.2F;
         bone.updateRotation(
            Mth.m_14179_(tween, bone.getRotX(), intensity * (float) (Math.PI / 180.0) * shittyNoise(f + 100.0F + i)),
            0.0F,
            Mth.m_14179_(tween, bone.getRotZ(), intensity * (float) (Math.PI / 180.0) * shittyNoise(f + i))
         );
      }

      this.getAnimationProcessor()
         .getBone("root")
         .updateRotation(0.0F, (float) (Math.PI / 180.0) * (shittyNoise(f + 150.0F) * 0.25F + animatable.f_19797_ + animationState.getPartialTick()), 0.0F);
   }

   private static float shittyNoise(float f) {
      return (Mth.m_14031_(f * 0.1F) + Mth.m_14031_(f * 0.25F) + 2.0F * Mth.m_14031_(f * 0.333F) + 3.0F * Mth.m_14031_(f * 0.5F) + 4.0F * Mth.m_14031_(f))
         * 1.5F;
   }
}
