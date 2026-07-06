package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class TraceParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final Vec3 destination;
   private final Vec3 forward;
   private final double speed;

   TraceParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double xd, double yd, double zd, TraceParticleOptions options) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107225_ = 4 + this.f_107208_.f_46441_.m_188503_(5);
      this.f_107226_ = 0.0F;
      this.sprites = spriteSet;
      this.f_107663_ = 0.75F + this.f_107208_.f_46441_.m_188501_() * 0.25F;
      this.destination = new Vec3(options.destination.x, options.destination.y, options.destination.z);
      this.forward = this.destination.m_82546_(new Vec3(pX, pY, pZ)).m_82541_();
      this.speed = new Vec3(xd, yd, zd).m_82553_();
      this.f_107227_ = options.color.x;
      this.f_107228_ = options.color.y;
      this.f_107229_ = options.color.z;
      this.f_172258_ = 1.0F;
   }

   private Vec3 vec3Copy(Vector3f vector3f) {
      return new Vec3(vector3f.x, vector3f.y, vector3f.z);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      this.m_6257_(this.forward.f_82479_ * this.speed, this.forward.f_82480_ * this.speed, this.forward.f_82481_ * this.speed);
      if (this.f_107224_++ > this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_108339_(this.sprites);
      }
   }

   public float m_5902_(float scaleFactor) {
      float f = (this.f_107224_ + scaleFactor) / this.f_107225_;
      f *= f;
      return Mth.m_14179_(f, this.f_107663_, this.f_107663_ * 0.5F);
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      Vec3 vec3 = camera.m_90583_();
      float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      Vec3 ray = this.getPos().m_82546_(vec3).m_82541_();
      Vec3 forward = this.forward;
      Vec3 up = forward.m_82537_(ray);
      Vector3f[] vertices = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };

      for (int i = 0; i < 4; i++) {
         float x = (float)(forward.f_82479_ * vertices[i].x + up.f_82479_ * vertices[i].y);
         float y = (float)(forward.f_82480_ * vertices[i].x + up.f_82480_ * vertices[i].y);
         float z = (float)(forward.f_82481_ * vertices[i].x + up.f_82481_ * vertices[i].y);
         vertices[i] = new Vector3f(x, y, z);
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
   public static class Provider implements ParticleProvider<TraceParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         @NotNull TraceParticleOptions options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         TraceParticle shriekparticle = new TraceParticle(pLevel, pX, pY, pZ, this.sprite, pXSpeed, pYSpeed, pZSpeed, options);
         shriekparticle.m_108339_(this.sprite);
         shriekparticle.m_107271_(1.0F);
         return shriekparticle;
      }
   }
}
