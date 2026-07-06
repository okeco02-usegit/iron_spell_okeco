package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.state.BoneSnapshot;

public class TransformStack {
   private final Map<CoreGeoBone, Stack<Vector3f>> positionStack = new HashMap<>();
   private final Map<CoreGeoBone, Stack<Vector3f>> rotationStack = new HashMap<>();
   private final Set<CoreGeoBone> toReset = new HashSet<>();

   public void pushPosition(CoreGeoBone bone, Vector3f appendVec) {
      Stack<Vector3f> stack = this.positionStack.getOrDefault(bone, new Stack<>());
      stack.push(appendVec);
      this.positionStack.put(bone, stack);
   }

   public void resetDirty() {
      this.toReset.forEach(bone -> {
         BoneSnapshot snapshot = bone.getInitialSnapshot();
         bone.updatePosition(snapshot.getOffsetX(), snapshot.getOffsetY(), snapshot.getOffsetZ());
         bone.updateRotation(snapshot.getRotX(), snapshot.getRotY(), snapshot.getRotZ());
         bone.resetStateChanges();
      });
      this.toReset.clear();
   }

   public void pushPosition(CoreGeoBone bone, float x, float y, float z) {
      this.pushPosition(bone, new Vector3f(x, y, z));
   }

   public void pushRotation(CoreGeoBone bone, Vector3f appendVec) {
      Stack<Vector3f> stack = this.rotationStack.getOrDefault(bone, new Stack<>());
      stack.push(appendVec);
      this.rotationStack.put(bone, stack);
   }

   public void pushRotation(CoreGeoBone bone, float x, float y, float z) {
      this.pushRotation(bone, new Vector3f(x, y, z));
   }

   public void pushRotationDegrees(CoreGeoBone bone, float x, float y, float z) {
      this.pushRotation(bone, new Vector3f(x * (float) (Math.PI / 180.0), y * (float) (Math.PI / 180.0), z * (float) (Math.PI / 180.0)));
   }

   public void popStack() {
      this.positionStack.forEach((bone, stack) -> {
         this.toReset.add(bone);
         Vector3f position = new Vector3f(bone.getPosX(), bone.getPosY(), bone.getPosZ());
         stack.forEach(position::add);
         this.setPosImpl(bone, position);
      });
      this.rotationStack.forEach((bone, stack) -> {
         this.toReset.add(bone);
         Vector3f rotation = new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ());
         stack.forEach(rotation::add);
         this.setRotImpl(bone, rotation);
      });
      this.positionStack.clear();
      this.rotationStack.clear();
   }

   public void setRotImpl(CoreGeoBone bone, Vector3f vector3f) {
      bone.updateRotation(wrapRadians(vector3f.x()), wrapRadians(vector3f.y()), wrapRadians(vector3f.z()));
   }

   public void setPosImpl(CoreGeoBone bone, Vector3f vector3f) {
      bone.updatePosition(vector3f.x, vector3f.y, vector3f.z);
   }

   public static float wrapRadians(float pValue) {
      float twoPi = 6.2831F;
      float pi = 3.14155F;
      float f = pValue % twoPi;
      if (f >= pi) {
         f -= twoPi;
      }

      if (f < -pi) {
         f += twoPi;
      }

      return f;
   }
}
