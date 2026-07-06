package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SparkParticle extends TextureSheetParticle {
   float targetR;
   float targetG;
   float targetB;
   int colorTime;
   boolean touchedGround;
   float bounciness;

   public SparkParticle(SparkParticleOptions options, ClientLevel level, double xCoord, double yCoord, double zCoord, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.m_6569_(this.f_107223_.m_188501_() * 0.65F + 0.4F);
      this.f_107225_ = 20 + (int)(Math.random() * 45.0);
      this.f_107226_ = 1.3F;
      this.f_172258_ = 0.985F;
      this.f_107663_ = 0.0625F;
      this.targetR = options.color.x() * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.targetG = options.color.y() * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.targetB = options.color.z() * (0.9F + this.f_107223_.m_188501_() * 0.1F);
      this.bounciness = 0.6F + this.f_107223_.m_188501_() * 0.2F;
      this.colorTime = 5 + (int)(Math.random() * 20.0);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
   }

   public void m_5989_() {
      if (!this.touchedGround && this.f_107225_ < 30) {
         this.f_107225_++;
      }

      float f = Mth.m_14036_((float)this.f_107224_ / this.colorTime, 0.0F, 1.0F);
      this.f_107227_ = Mth.m_14179_(f, 1.0F, this.targetR);
      this.f_107228_ = Mth.m_14179_(f, 1.0F, this.targetG);
      this.f_107229_ = Mth.m_14179_(f, 1.0F, this.targetB);
      if (this.f_107218_) {
         this.touchedGround = true;
         this.f_107216_ = this.f_107216_ * -this.bounciness;
         this.bounciness *= 0.8F;
         this.f_107663_ *= 0.9F;
      }

      super.m_5989_();
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
   public static class Provider implements ParticleProvider<SparkParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(@NotNull SparkParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         SparkParticle particle = new SparkParticle(options, level, x, y, z, dx, dy, dz);
         particle.m_108335_(this.sprites);
         return particle;
      }
   }
}
