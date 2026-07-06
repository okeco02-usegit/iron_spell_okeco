package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElectricityParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public ElectricityParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.f_172258_ = 0.77F;
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.f_107663_ *= 1.0F;
      this.m_6569_(1.5F);
      this.f_107225_ = 5 + (int)(Math.random() * 15.0);
      this.sprites = spriteSet;
      this.f_107226_ = 0.0F;
      this.m_108339_(spriteSet);
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
   }

   public void m_5989_() {
      super.m_5989_();
      float xj = this.f_107223_.m_188501_() / 12.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      float yj = this.f_107223_.m_188501_() / 12.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      float zj = this.f_107223_.m_188501_() / 12.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      this.m_107264_(this.f_107212_ + xj, this.f_107213_ + yj, this.f_107214_ + zj);
      this.randomlyAnimate();
   }

   private void randomlyAnimate() {
      this.m_108337_(this.sprites.m_213979_(Utils.random));
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   protected int m_6355_(float p_107249_) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new ElectricityParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
