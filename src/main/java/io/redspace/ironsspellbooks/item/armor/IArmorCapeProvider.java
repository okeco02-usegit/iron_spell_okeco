package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface IArmorCapeProvider {
   ResourceLocation getCapeResourceLocation();

   class CapeData {
      public double xCloakO;
      public double yCloakO;
      public double zCloakO;
      public double xCloak;
      public double yCloak;
      public double zCloak;
      public float bob;
      public float oBob;
      public int lastTick;

      public void moveCloak(LivingEntity livingEntity) {
         this.oBob = this.bob;
         float f;
         if (livingEntity.m_20096_() && !livingEntity.m_21224_()) {
            f = (float)Math.min(0.1, livingEntity.m_20184_().m_165924_());
         } else {
            f = 0.0F;
         }

         this.bob = this.bob + (f - this.bob) * 0.4F;
         this.xCloakO = this.xCloak;
         this.yCloakO = this.yCloak;
         this.zCloakO = this.zCloak;
         double d0 = livingEntity.m_20185_() - this.xCloak;
         double d1 = livingEntity.m_20186_() - this.yCloak;
         double d2 = livingEntity.m_20189_() - this.zCloak;
         double d3 = 10.0;
         if (d0 > 10.0) {
            this.xCloak = livingEntity.m_20185_();
            this.xCloakO = this.xCloak;
         }

         if (d2 > 10.0) {
            this.zCloak = livingEntity.m_20189_();
            this.zCloakO = this.zCloak;
         }

         if (d1 > 10.0) {
            this.yCloak = livingEntity.m_20186_();
            this.yCloakO = this.yCloak;
         }

         if (d0 < -10.0) {
            this.xCloak = livingEntity.m_20185_();
            this.xCloakO = this.xCloak;
         }

         if (d2 < -10.0) {
            this.zCloak = livingEntity.m_20189_();
            this.zCloakO = this.zCloak;
         }

         if (d1 < -10.0) {
            this.yCloak = livingEntity.m_20186_();
            this.yCloakO = this.yCloak;
         }

         this.xCloak += d0 * 0.25;
         this.zCloak += d2 * 0.25;
         this.yCloak += d1 * 0.25;
      }
   }
}
