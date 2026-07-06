package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UnstableEnderParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   public UnstableEnderParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.f_172258_ = 0.77F;
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.f_107663_ = 0.14F * (this.f_107223_.m_188501_() * 0.15F + 0.3F);
      this.m_6569_(2.25F);
      this.f_107225_ = 7 + (int)(Math.random() * 10.0 + Math.min(new Vec3(xd, yd, zd).m_82553_() * 100.0, 20.0));
      this.sprites = spriteSet;
      this.f_107226_ = 0.0F;
      this.randomlyAnimate();
      float f = this.f_107223_.m_188501_() * 0.6F + 0.4F;
      this.f_107227_ = f * 0.9F;
      this.f_107228_ = f * 0.3F;
      this.f_107229_ = f;
   }

   public void m_5989_() {
      super.m_5989_();
      float xj = this.f_107223_.m_188501_() / 50.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      float yj = this.f_107223_.m_188501_() / 50.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      float zj = this.f_107223_.m_188501_() / 50.0F * (this.f_107223_.m_188499_() ? 1 : -1);
      this.m_107264_(this.f_107212_ + xj, this.f_107213_ + yj, this.f_107214_ + zj);
   }

   private void randomlyAnimate() {
      this.m_108337_(this.sprites.m_213979_(Utils.random));
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   public int m_6355_(float p_107564_) {
      return 15728880;
   }

   public float m_5902_(float p_107567_) {
      float f = (this.f_107224_ + p_107567_) / this.f_107225_;
      f = 1.0F - f;
      f *= f;
      f = 1.0F - f;
      return this.f_107663_ * f;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new UnstableEnderParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
