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

public class FierySmokeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final boolean mirrored;

   public FierySmokeParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.m_6569_(this.f_107223_.m_188501_() * 2.5F + 1.5F);
      this.f_107225_ = 10 + (int)(Math.random() * 30.0);
      this.sprites = spriteSet;
      this.m_108339_(spriteSet);
      this.f_107226_ = -0.0025F;
      this.mirrored = this.f_107223_.m_188499_();
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      this.f_107230_ = Mth.m_144920_(1.0F, 0.0F, (this.f_107224_ - this.f_107225_ + 8) / 8.0F);
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
         this.f_107215_ = this.f_107215_ + this.f_107223_.m_188501_() / 500.0F * (this.f_107223_.m_188499_() ? 1 : -1);
         this.f_107216_ = this.f_107216_ + (this.f_107223_.m_188501_() / 100.0F - this.f_107226_);
         this.f_107217_ = this.f_107217_ + this.f_107223_.m_188501_() / 500.0F * (this.f_107223_.m_188499_() ? 1 : -1);
         this.m_108339_(this.sprites);
         this.m_6569_(1.023F);
      }
   }

   protected float m_5970_() {
      return this.mirrored ? super.m_5952_() : super.m_5970_();
   }

   protected float m_5952_() {
      return this.mirrored ? super.m_5970_() : super.m_5952_();
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public int m_6355_(float p_107564_) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new FierySmokeParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
