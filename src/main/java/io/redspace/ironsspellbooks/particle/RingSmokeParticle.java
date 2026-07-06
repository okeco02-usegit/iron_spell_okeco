package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RingSmokeParticle extends TextureSheetParticle {
   private static final Vector3f ROTATION_VECTOR = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
   private final float targetSize;
   private final boolean isFullbright;
   private float rx;
   private float ry;

   RingSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xd, double yd, double zd) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107215_ = xd * 0.7F;
      this.f_107216_ = yd * 0.7F;
      this.f_107217_ = zd * 0.7F;
      Vec3 deltaMovement = new Vec3(xd, yd, zd);
      this.rx = (float)(-Math.asin(yd / deltaMovement.m_82553_()));
      this.ry = (float) (Math.PI / 2) - (float)Mth.m_14136_(zd, xd);
      this.targetSize = 2.5F;
      this.f_107663_ = 0.5F;
      this.f_107225_ = (int)(20.0 + Mth.m_14005_(0.0, this.targetSize - deltaMovement.m_82553_() * 5.0) * 20.0);
      this.f_107226_ = 0.0F;
      float f = this.f_107223_.m_188501_() * 0.14F + 0.85F;
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_172258_ = 1.0F;
      this.isFullbright = false;
   }

   public float m_5902_(float partialTick) {
      float f = (partialTick + this.f_107224_) / this.f_107225_;
      return Mth.m_14179_(f, 0.15F, this.targetSize);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
         this.f_107216_ *= 0.99F;
         this.f_107215_ *= 0.99F;
         this.f_107217_ *= 0.99F;
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialticks) {
      this.f_107230_ = 1.0F - Mth.m_14036_((this.f_107224_ + partialticks) / this.f_107225_, 0.0F, 1.0F);
      this.renderRotatedParticle(buffer, camera, partialticks, quaternion -> {
         quaternion.mul(Axis.f_252436_.m_252961_(this.ry));
         quaternion.mul(Axis.f_252529_.m_252961_(this.rx));
      });
      this.renderRotatedParticle(buffer, camera, partialticks, quaternion -> {
         quaternion.mul(Axis.f_252436_.m_252961_(this.ry));
         quaternion.mul(Axis.f_252529_.m_252961_((float) Math.PI));
         quaternion.mul(Axis.f_252529_.m_252961_(this.rx));
      });
   }

   private void renderRotatedParticle(VertexConsumer pConsumer, Camera camera, float partialTick, Consumer<Quaternionf> pQuaternion) {
      Vec3 vec3 = camera.m_90583_();
      float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      Quaternionf quaternion = new Quaternionf().setAngleAxis(0.0F, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
      pQuaternion.accept(quaternion);
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
      pConsumer.m_5483_(pVec3f.x(), pVec3f.y(), pVec3f.z())
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
      if (this.isFullbright) {
         return 15728880;
      }

      BlockPos blockpos = BlockPos.m_274561_(this.f_107212_, this.f_107213_, this.f_107214_).m_7494_();
      return this.f_107208_.m_46805_(blockpos) ? LevelRenderer.m_109541_(this.f_107208_, blockpos) : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         RingSmokeParticle shriekparticle = new RingSmokeParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
         shriekparticle.m_108335_(this.sprite);
         shriekparticle.m_107271_(1.0F);
         return shriekparticle;
      }
   }
}
