package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CleanseParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private static final int BASE_LIFETIME = 8;

   protected CleanseParticle(
      ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, float pSizeMultiplier, SpriteSet pSprites
   ) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.sprites = pSprites;
      this.f_172258_ = 0.96F;
      this.f_107226_ = -0.1F;
      this.f_172259_ = true;
      this.f_107215_ *= 0.0;
      this.f_107216_ *= 0.9;
      this.f_107217_ *= 0.0;
      this.f_107215_ += pXSpeed;
      this.f_107216_ += pYSpeed;
      this.f_107217_ += pZSpeed;
      this.f_107663_ *= 0.75F * pSizeMultiplier;
      this.f_107225_ = (int)(8.0F / Mth.m_216283_(this.f_107223_, 0.5F, 1.0F) * pSizeMultiplier);
      this.f_107225_ = Math.max(this.f_107225_, 1);
      this.m_108339_(pSprites);
      this.f_107219_ = true;
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   public int m_6355_(float pPartialTick) {
      return 240;
   }

   public void m_5989_() {
      super.m_5989_();
      this.m_108339_(this.sprites);
   }

   public float m_5902_(float pScaleFactor) {
      return this.f_107663_ * Mth.m_14036_((this.f_107224_ + pScaleFactor) / this.f_107225_ * 32.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         return new CleanseParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, 1.5F, this.sprites);
      }
   }
}
