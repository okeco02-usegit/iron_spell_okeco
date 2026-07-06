package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlastwaveParticle extends TextureSheetParticle {
   private static final Vector3f ROTATION_VECTOR = (Vector3f)Util.m_137469_(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
   private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
   private static final float DEGREES_90 = (float) (Math.PI / 2);
   private final float targetSize;
   private final SpriteSet sprites;

   BlastwaveParticle(
      ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double xd, double yd, double zd, BlastwaveParticleOptions options
   ) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.targetSize = options.m_175813_();
      this.f_107663_ = 1.0F;
      this.f_107225_ = 8;
      this.f_107226_ = 0.1F;
      this.sprites = spriteSet;
      float f = this.f_107223_.m_188501_() * 0.14F + 0.85F;
      this.f_107227_ = options.m_252837_().x() * f;
      this.f_107228_ = options.m_252837_().y() * f;
      this.f_107229_ = options.m_252837_().z() * f;
      this.f_172258_ = 1.0F;
   }

   public float m_5902_(float partialTick) {
      float f = (partialTick + this.f_107224_) / this.f_107225_;
      return this.f_107663_ * Mth.m_14179_(1.0F - (1.0F - f) * (1.0F - f), this.targetSize * 0.75F, this.targetSize);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_108339_(this.sprites);
         this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
         this.f_107216_ *= 0.85F;
         this.f_107215_ *= 0.94F;
         this.f_107217_ *= 0.94F;
      }
   }

   public boolean shouldCull() {
      return false;
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialticks) {
      this.f_107230_ = 1.0F - Mth.m_14036_((this.f_107224_ + partialticks) / this.f_107225_, 0.0F, 1.0F);
      this.renderRotatedParticle(buffer, camera, partialticks, p_234005_ -> {
         p_234005_.mul(Axis.f_252436_.m_252961_(0.0F));
         p_234005_.mul(Axis.f_252529_.m_252961_((float) (-Math.PI / 2)));
      });
      this.renderRotatedParticle(buffer, camera, partialticks, p_234000_ -> {
         p_234000_.mul(Axis.f_252436_.m_252961_((float) -Math.PI));
         p_234000_.mul(Axis.f_252529_.m_252961_((float) (Math.PI / 2)));
      });
   }

   private void renderRotatedParticle(VertexConsumer pConsumer, Camera camera, float partialTick, Consumer<Quaternionf> pQuaternion) {
      Vec3 vec3 = camera.m_90583_();
      float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      Quaternionf quaternion = new Quaternionf().setAngleAxis(0.0F, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
      pQuaternion.accept(quaternion);
      quaternion.transform(TRANSFORM_VECTOR);
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float f3 = this.m_5902_(partialTick);

      for (int i = 0; i < 4; i++) {
         Vector3f vector3f = avector3f[i];
         vector3f.rotate(quaternion);
         vector3f.mul(f3);
         vector3f.add(f, f1, f2);
      }

      int j = this.m_6355_(partialTick);
      this.makeCornerVertex(pConsumer, avector3f[0], this.m_5952_(), this.m_5950_(), j);
      this.makeCornerVertex(pConsumer, avector3f[1], this.m_5952_(), this.m_5951_(), j);
      this.makeCornerVertex(pConsumer, avector3f[2], this.m_5970_(), this.m_5951_(), j);
      this.makeCornerVertex(pConsumer, avector3f[3], this.m_5970_(), this.m_5950_(), j);
   }

   private void makeCornerVertex(VertexConsumer pConsumer, Vector3f pVec3f, float p_233996_, float p_233997_, int p_233998_) {
      pConsumer.m_5483_(pVec3f.x(), pVec3f.y() + 0.08F, pVec3f.z())
         .m_7421_(p_233996_, p_233997_)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(p_233998_)
         .m_5752_();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<BlastwaveParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         @NotNull BlastwaveParticleOptions options,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         BlastwaveParticle shriekparticle = new BlastwaveParticle(pLevel, pX, pY, pZ, this.sprite, pXSpeed, pYSpeed, pZSpeed, options);
         shriekparticle.m_108339_(this.sprite);
         shriekparticle.m_107271_(1.0F);
         return shriekparticle;
      }
   }
}
