package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmberousAshParticle extends TextureSheetParticle {
   final float seed;
   final float speed;

   public EmberousAshParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.m_6569_(this.f_107223_.m_188501_() * 0.65F + 0.4F);
      this.f_107225_ = 40 + (int)(Math.random() * 45.0);
      this.f_107226_ = 0.0F;
      this.f_172258_ = 1.0F;
      this.f_107663_ = 0.0625F;
      this.f_107227_ = 1.0F * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.f_107228_ = 0.6F * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.f_107229_ = 0.3F * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.seed = (this.f_107223_.m_188501_() - 0.5F) * 2.0F * 5.0F;
      this.speed = (float)new Vec3(xd, yd, zd).m_82553_();
      if (this.speed > 4.0F) {
         this.f_107215_ = 0.15;
         this.f_107216_ = 0.0;
         this.f_107217_ = 0.0;
      }
   }

   private float f(float x) {
      return Mth.m_14031_(this.seed * Mth.m_14031_(x) + x);
   }

   private float function(float x) {
      return 0.05F * (this.f(2.0F * x) + 1.0F * this.f(0.25F * x) + 2.0F * this.f(0.125F * x));
   }

   public float m_5902_(float pScaleFactor) {
      return Mth.m_14179_((this.f_107224_ + pScaleFactor) / this.f_107225_, super.m_5902_(pScaleFactor), 0.0F)
         * Mth.m_14036_((this.f_107224_ + pScaleFactor) / 5.0F, 0.0F, 1.0F);
   }

   public void m_5989_() {
      super.m_5989_();
      float f = Math.abs(this.seed) < 0.2 ? 1.0F : this.seed;
      this.f_107215_ = 0.3
         * this.seed
         * (0.05F * Mth.m_14031_((this.f_107224_ + 700.0F * this.seed) * 0.2F / f) + this.function(this.f_107224_ * 0.2F + 700.0F * this.seed));
      this.f_107216_ = 0.3
         * this.seed
         * (0.05F * Mth.m_14031_((this.f_107224_ + 500.0F * this.seed) * 0.2F / f) + this.function(this.f_107224_ * 0.2F + 500.0F * this.seed));
      this.f_107217_ = 0.3
         * this.seed
         * (0.05F * Mth.m_14089_((this.f_107224_ + 100.0F * this.seed) * 0.2F / f) + this.function(this.f_107224_ * 0.2F + 100.0F * this.seed));
      if (this.speed > 4.0F) {
         this.f_107215_ = Math.abs(this.f_107215_) * 3.0;
      }

      if (this.f_107223_.m_188501_() < 0.5) {
         this.f_107208_.m_7106_(ParticleTypes.f_123762_, this.f_107212_, this.f_107213_, this.f_107214_, this.f_107215_, this.f_107216_, this.f_107217_);
      }

      if (new Vec3(this.f_107212_ - this.f_107209_, this.f_107213_ - this.f_107210_, this.f_107214_ - this.f_107211_).m_82556_() < 0.001) {
         this.m_107274_();
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
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
         EmberousAshParticle p = new EmberousAshParticle(level, x, y, z, dx, dy, dz);
         p.m_108335_(this.sprites);
         return p;
      }
   }
}
