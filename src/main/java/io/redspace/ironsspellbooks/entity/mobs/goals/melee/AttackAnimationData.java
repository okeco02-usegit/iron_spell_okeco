package io.redspace.ironsspellbooks.entity.mobs.goals.melee;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.world.phys.Vec3;

public class AttackAnimationData {
   public final int lengthInTicks;
   public final String animationId;
   public final Int2ObjectOpenHashMap<AttackKeyframe> attacks;
   public final boolean canCancel;
   public final Optional<Float> areaAttackThreshold;
   public final float rangeMultiplier;

   public AttackAnimationData(int lengthInTicks, String animationId, int... attackTimestamps) {
      this.animationId = animationId;
      this.lengthInTicks = lengthInTicks;
      this.attacks = new Int2ObjectOpenHashMap();
      this.canCancel = false;

      for (int i : attackTimestamps) {
         this.attacks.put(i, new AttackKeyframe(i, new Vec3(0.0, 0.0, 0.45F)));
      }

      this.areaAttackThreshold = Optional.empty();
      this.rangeMultiplier = 1.0F;
   }

   public AttackAnimationData(
      String animationId, int lengthInTicks, boolean canCancel, Optional<Float> areaAttackThreshold, Int2ObjectOpenHashMap<AttackKeyframe> attacks
   ) {
      this(animationId, lengthInTicks, canCancel, areaAttackThreshold, attacks, 1.0F);
   }

   public AttackAnimationData(
      String animationId,
      int lengthInTicks,
      boolean canCancel,
      Optional<Float> areaAttackThreshold,
      Int2ObjectOpenHashMap<AttackKeyframe> attacks,
      float rangeMultiplier
   ) {
      this.animationId = animationId;
      this.lengthInTicks = lengthInTicks;
      this.attacks = attacks;
      this.canCancel = canCancel;
      this.areaAttackThreshold = areaAttackThreshold;
      this.rangeMultiplier = rangeMultiplier;
   }

   public boolean isHitFrame(int tickCount) {
      return this.attacks.containsKey(this.lengthInTicks - tickCount);
   }

   public AttackKeyframe getHitFrame(int tickCount) {
      return (AttackKeyframe)this.attacks.get(this.lengthInTicks - tickCount);
   }

   public boolean isSingleHit() {
      return this.attacks.size() == 1;
   }

   public static AttackAnimationData.Builder builder(String animationId) {
      return new AttackAnimationData.Builder(animationId);
   }

   public static class Builder {
      public int lengthInTicks;
      public String animationId;
      public Int2ObjectOpenHashMap<AttackKeyframe> attacks;
      public boolean canCancel = false;
      public Optional<Float> areaAttackThreshold = Optional.empty();
      public float rangeMultiplier = 1.0F;

      public Builder(String animationId) {
         this.animationId = animationId;
      }

      public AttackAnimationData.Builder length(int lengthInTicks) {
         this.lengthInTicks = lengthInTicks;
         return this;
      }

      public AttackAnimationData.Builder cancellable() {
         this.canCancel = true;
         return this;
      }

      public AttackAnimationData.Builder rangeMultiplier(float rangeMultiplier) {
         this.rangeMultiplier = rangeMultiplier;
         return this;
      }

      public AttackAnimationData.Builder area(float threshold) {
         this.areaAttackThreshold = Optional.of(threshold);
         return this;
      }

      public AttackAnimationData.Builder attacks(AttackKeyframe... attacks) {
         this.attacks = new Int2ObjectOpenHashMap();

         for (AttackKeyframe a : attacks) {
            this.attacks.put(a.timeStamp(), a);
         }

         return this;
      }

      public AttackAnimationData.Builder attacks(int... attackTimestamps) {
         this.attacks = new Int2ObjectOpenHashMap();

         for (int i : attackTimestamps) {
            this.attacks.put(i, new AttackKeyframe(i, new Vec3(0.0, 0.0, 0.45F)));
         }

         return this;
      }

      public AttackAnimationData build() {
         return new AttackAnimationData(this.animationId, this.lengthInTicks, this.canCancel, this.areaAttackThreshold, this.attacks, this.rangeMultiplier);
      }
   }
}
