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

public class FlameStrikeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final Vec3 forward;
   private final boolean mirror;
   private final boolean vertical;
   private final Vector3f[] localVertices;

   FlameStrikeParticle(
      ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double xd, double yd, double zd, FlameStrikeParticleOptions options
   ) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
      this.f_107225_ = 4;
      this.f_107226_ = 0.0F;
      this.sprites = spriteSet;
      this.f_107663_ = options.scale * 3.25F;
      this.forward = new Vec3(options.xf, options.yf, options.zf).m_82541_();
      this.mirror = options.mirror;
      this.vertical = options.vertical;
      this.localVertices = this.calculateVertices();
      this.f_172258_ = 1.0F;
   }

   private Vec3 vec3Copy(Vector3f vector3f) {
      return new Vec3(vector3f.x, vector3f.y, vector3f.z);
   }

   public void m_5989_() {
      if (this.f_107224_ == 0) {
         this.createEmberTrail();
      }

      if (this.f_107224_++ > this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_108339_(this.sprites);
      }
   }

   private void createEmberTrail() {
      int particleCount = (int)(9.0F * this.f_107663_);

      for (int i = 1; i < particleCount - 1; i++) {
         float t = (float)i / particleCount;
         float u = 1.0F - t;
         Vec3 localPos = this.vec3Copy(this.localVertices[1])
            .m_82490_(u * u * u)
            .m_82549_(
               this.vec3Copy(this.localVertices[2])
                  .m_82490_(3.0F * u * u * t)
                  .m_82549_(this.vec3Copy(this.localVertices[3]).m_82490_(3.0F * u * t * t).m_82549_(this.vec3Copy(this.localVertices[0]).m_82490_(t * t * t)))
            )
            .m_82490_(this.f_107663_ * 0.75F)
            .m_82549_(Utils.getRandomVec3(0.3));
         this.f_107208_
            .m_7106_(
               ParticleHelper.EMBERS, this.f_107212_ + localPos.f_82479_, this.f_107213_ + localPos.f_82480_, this.f_107214_ + localPos.f_82481_, 0.0, 0.0, 0.0
            );
      }
   }

   private Vector3f[] calculateVertices() {
      boolean vertical = this.vertical;
      Vec3 forward = this.forward;
      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      if (forward.m_82526_(up) > 0.999) {
         up = new Vec3(1.0, 0.0, 0.0);
      }

      Vec3 right = forward.m_82537_(up);
      up = up.m_82546_(this.proj(forward, up)).m_82541_();
      right = right.m_82546_(this.proj(forward, right)).m_82546_(this.proj(up, right)).m_82541_();
      Vec3 primary;
      Vec3 secondary;
      if (!vertical) {
         primary = forward;
         secondary = right;
      } else {
         primary = forward;
         secondary = up;
      }

      Vector3f[] vertices = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };

      for (int i = 0; i < 4; i++) {
         float x = (float)(primary.f_82479_ * vertices[i].x + secondary.f_82479_ * vertices[i].y);
         float y = (float)(primary.f_82480_ * vertices[i].x + secondary.f_82480_ * vertices[i].y);
         float z = (float)(primary.f_82481_ * vertices[i].x + secondary.f_82481_ * vertices[i].y);
         vertices[i] = new Vector3f(x, y, z);
      }

      return vertices;
   }

   public Vec3 proj(Vec3 u, Vec3 v) {
      return u.m_82490_(v.m_82526_(u) / u.m_82556_());
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      boolean mirrored = !this.mirror;
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
      this.makeCornerVertex(buffer, vertices[0], this.m_5952_(), mirrored ? this.m_5951_() : this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[1], this.m_5952_(), mirrored ? this.m_5950_() : this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[2], this.m_5970_(), mirrored ? this.m_5950_() : this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[3], this.m_5970_(), mirrored ? this.m_5951_() : this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[3], this.m_5970_(), mirrored ? this.m_5951_() : this.m_5950_(), j);
      this.makeCornerVertex(buffer, vertices[2], this.m_5970_(), mirrored ? this.m_5950_() : this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[1], this.m_5952_(), mirrored ? this.m_5950_() : this.m_5951_(), j);
      this.makeCornerVertex(buffer, vertices[0], this.m_5952_(), mirrored ? this.m_5951_() : this.m_5950_(), j);
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
      return ParticleRenderType.f_107430_;
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<FlameStrikeParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprite) {
         this.sprite = pSprite;
      }

      public Particle createParticle(
         @NotNull FlameStrikeParticleOptions options,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         FlameStrikeParticle shriekparticle = new FlameStrikeParticle(pLevel, pX, pY, pZ, this.sprite, pXSpeed, pYSpeed, pZSpeed, options);
         shriekparticle.m_108339_(this.sprite);
         shriekparticle.m_107271_(1.0F);
         return shriekparticle;
      }
   }
}
