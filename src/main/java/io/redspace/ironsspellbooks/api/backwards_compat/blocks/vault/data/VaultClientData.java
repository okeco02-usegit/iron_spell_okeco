package io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data;

import net.minecraft.util.Mth;

public class VaultClientData {
   public static final float ROTATION_SPEED = 10.0F;
   private float currentSpin;
   private float previousSpin;

   VaultClientData() {
   }

   public float currentSpin() {
      return this.currentSpin;
   }

   public float previousSpin() {
      return this.previousSpin;
   }

   void updateDisplayItemSpin() {
      this.previousSpin = this.currentSpin;
      this.currentSpin = Mth.m_14177_(this.currentSpin + 10.0F);
   }
}
