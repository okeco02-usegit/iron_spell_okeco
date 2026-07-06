package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public FireParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.m_6569_(this.f_107223_.m_188501_() * 1.75F + 1.0F);
      this.f_172258_ = (float)(this.f_172258_ - this.f_107223_.m_188501_() * 0.1);
      this.f_107225_ = 10 + (int)(Math.random() * 25.0);
      this.sprites = spriteSet;
      this.m_108339_(spriteSet);
      this.f_107226_ = -0.01F;
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107215_ = this.f_107215_ + this.f_107223_.m_188501_() / 500.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      this.f_107217_ = this.f_107217_ + this.f_107223_.m_188501_() / 500.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      this.animateContinuously();
      if (this.f_107223_.m_188501_() <= 0.25F) {
         this.f_107208_.m_7106_(ParticleHelper.EMBERS, this.f_107212_, this.f_107213_, this.f_107214_, this.f_107215_, this.f_107216_, this.f_107217_);
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   public int m_6355_(float p_107564_) {
      return 15728880;
   }

   private void animateContinuously() {
      if (this.f_107224_ % 8 == 0) {
         this.m_108337_(this.sprites.m_213979_(this.f_107223_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new FireParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
