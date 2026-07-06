package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.api.util.Utils;
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
import org.joml.Vector3f;

public class FireflyParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private boolean lit;
   private float litTween;
   private int litTimer;
   private float wander;
   private final float flickerIntensity;
   private static final Vector3f litColor = new Vector3f(1.0F, 1.0F, 1.0F);
   private static final Vector3f unlitColor = new Vector3f(0.08627451F, 0.078431375F, 0.07058824F);

   public FireflyParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.m_6569_(2.5F);
      this.f_107225_ = 20 + (int)(Math.random() * 90.0);
      this.sprites = spriteSet;
      this.f_107226_ = 0.0F;
      this.lit = Utils.random.m_188499_();
      this.litTween = this.lit ? 1.0F : 0.0F;
      this.wander = Utils.random.m_188501_() * 2.5F;
      this.wander = this.wander * (this.wander * this.wander * this.wander);
      this.m_108337_(this.sprites.m_5819_(0, 1));
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.flickerIntensity = Utils.random.m_216332_(18, 45) * 0.01F;
   }

   public void m_5989_() {
      float xj = this.f_107223_.m_188501_() * 0.001F * this.wander * (this.f_107223_.m_188499_() ? 1 : -1);
      float yj = this.f_107223_.m_188501_() * 0.001F * this.wander * (this.f_107223_.m_188499_() ? 1 : -1) + 2.5E-4F;
      float zj = this.f_107223_.m_188501_() * 0.001F * this.wander * (this.f_107223_.m_188499_() ? 1 : -1);
      this.wander *= 0.98F;
      if (this.f_107218_) {
         this.f_107216_ = Math.abs(this.f_107216_);
      }

      this.f_107215_ += xj;
      this.f_107216_ += yj;
      this.f_107217_ += zj;
      if (--this.litTimer <= 0) {
         this.lit = !this.lit;
         this.litTimer = this.f_107223_.m_216332_(5, 20);
      }

      if (this.lit) {
         this.litTween = Mth.m_14179_(this.flickerIntensity, this.litTween, 1.0F);
      } else {
         this.litTween = Mth.m_14179_(this.flickerIntensity, this.litTween, 0.0F);
      }

      this.f_107227_ = Mth.m_14179_(this.litTween, unlitColor.x(), litColor.x());
      this.f_107228_ = Mth.m_14179_(this.litTween, unlitColor.y(), litColor.y());
      this.f_107229_ = Mth.m_14179_(this.litTween, unlitColor.z(), litColor.z());
      if (this.f_107224_ >= this.f_107225_ - 1 && this.litTween > 0.1F) {
         this.f_107225_++;
      }

      super.m_5989_();
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
         return new FireflyParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
