package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.ParticleHelper;
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
import org.joml.Vector3f;

public class EnderSlashParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final Vec3 forward;
   private final Vec3 up;
   private final Vector3f[] localVertices;

   EnderSlashParticle(
      ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double xd, double yd, double zd, EnderSlashParticleOptions options
   ) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107225_ = 5;
      this.f_107226_ = 0.0F;
      this.sprites = spriteSet;
      this.f_107663_ = options.scale * 3.25F;
      this.forward = new Vec3(options.xf, options.yf, options.zf).m_82541_();
      this.up = new Vec3(options.xu, options.yu, options.zu).m_82541_();
      this.localVertices = this.calculateVertices();
      if (new Vec3(xd, yd, zd).m_82556_() > 0.0) {
         this.f_107215_ = xd;
         this.f_107216_ = yd;
         this.f_107217_ = zd;
      } else {
         this.f_107215_ = this.forward.f_82479_ * 0.1;
         this.f_107216_ = this.forward.f_82480_ * 0.1;
         this.f_107217_ = this.forward.f_82481_ * 0.1;
      }

      this.f_172258_ = 1.0F;
   }

   private Vec3 vec3Copy(Vector3f vector3f) {
      return new Vec3(vector3f.x, vector3f.y, vector3f.z);
   }

   public void m_5989_() {
      if (this.f_107224_ == 0) {
         this.createEmberTrail();
      }

      this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
      if (this.f_107224_++ > this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_108339_(this.sprites);
      }
   }

   private void createEmberTrail() {
      int particleCount = (int)(15.0F * this.f_107663_);

      for (int i = 1; i < particleCount - 1; i++) {
         float t = (float)i / particleCount;
         float u = 1.0F - t;
         Vec3 localPos = this.vec3Copy(this.localVertices[1])
            .m_82490_(0.4)
            .m_82490_(u * u * u)
            .m_82549_(
               this.vec3Copy(this.localVertices[2])
                  .m_82490_(3.0F * u * u * t)
                  .m_82549_(
                     this.vec3Copy(this.localVertices[3])
                        .m_82490_(3.0F * u * t * t)
                        .m_82549_(this.vec3Copy(this.localVertices[0]).m_82490_(0.85).m_82490_(t * t * t))
                  )
            )
            .m_82490_(this.f_107663_ * 0.85);
         Vec3 pos = localPos.m_82549_(Utils.getRandomVec3(0.2 + i * 0.01F));
         Vec3 motion = new Vec3(this.f_107215_, this.f_107216_, this.f_107217_).m_82490_(this.f_107223_.m_188500_() * 6.0);
         if (this.f_107223_.m_188501_() < 0.5F) {
            this.f_107208_
               .m_7106_(
                  ParticleHelper.UNSTABLE_ENDER,
                  this.f_107212_ + pos.f_82479_,
                  this.f_107213_ + pos.f_82480_,
                  this.f_107214_ + pos.f_82481_,
                  motion.f_82479_ * 1.5,
                  motion.f_82480_ * 1.5,
                  motion.f_82481_ * 1.5
               );
         }
      }
   }

   private Vector3f[] calculateVertices() {
      Vec3 forward = this.forward;
      Vec3 up = this.up;
      Vec3 right = forward.m_82537_(up);
      Vector3f[] vertices = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };

      for (int i = 0; i < 4; i++) {
         float x = (float)(forward.f_82479_ * vertices[i].x + right.f_82479_ * vertices[i].y);
         float y = (float)(forward.f_82480_ * vertices[i].x + right.f_82480_ * vertices[i].y);
         float z = (float)(forward.f_82481_ * vertices[i].x + right.f_82481_ * vertices[i].y);
         vertices[i] = new Vector3f(x, y, z);
      }

      return vertices;
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      Vec3 vec3 = camera.m_90583_();
      float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      Vector3f[] vertices = new Vector3f[4];

      for (int i = 0; i < 4; i++) {
         Vector3f localVertex = this.localVertices[i];
         vertices[i] = new Vector3f(localVertex.x, localVertex.y, localVertex.z);
         vertices[i].mul(this.m_5902_(partialTick));
         vertices[i].add(f, f1, f2);
      }

      int j = this.m_6355_(partialTick);
      this.makeCornerVertex(buffer, vertices[0], this.m_5952_(), this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[1], this.m_5952_(), this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[2], this.m_5970_(), this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[3], this.m_5970_(), this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[3], this.m_5970_(), this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[2], this.m_5970_(), this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[1], this.m_5952_(), this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[0], this.m_5952_(), this.m_5950_(), j);
   }

   private void makeCornerVertex(VertexConsumer pConsumer, Vector3f pVec3f, float p_233996_, float p_233997_, int p_233998_) {
      pConsumer.m_5483_(pVec3f.x(), pVec3f.y(), pVec3f.z())
         .m_7421_(p_233996_, p_233997_)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(p_233998_);
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<EnderSlashParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         @NotNull EnderSlashParticleOptions options,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         EnderSlashParticle shriekparticle = new EnderSlashParticle(pLevel, pX, pY, pZ, this.sprite, pXSpeed, pYSpeed, pZSpeed, options);
         shriekparticle.m_108339_(this.sprite);
         shriekparticle.m_107271_(1.0F);
         return shriekparticle;
      }
   }
}
