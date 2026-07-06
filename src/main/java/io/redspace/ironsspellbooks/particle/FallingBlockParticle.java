package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(Dist.CLIENT)
public class FallingBlockParticle extends TextureSheetParticle {
   private final BlockState blockState;
   private final boolean particlesOnImpact;
   private final BlockPos originalPos;
   private static final List<FallingBlockParticle.Renderable> toRender = new ArrayList<>();

   @SubscribeEvent
   public static void globalrender(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_ENTITIES) {
         BlockRenderDispatcher dispatcher = Minecraft.m_91087_().m_91289_();
         ClientLevel level = Minecraft.m_91087_().f_91073_;
         if (level == null) {
            toRender.clear();
         } else {
            RenderBuffers bufs = Minecraft.m_91087_().m_91269_();
            BufferSource buf = bufs.m_110104_();

            for (FallingBlockParticle.Renderable particle : toRender) {
               PoseStack poseStack = event.getPoseStack();
               poseStack.m_85836_();
               poseStack.m_252880_((float)particle.relativePos.f_82479_, (float)particle.relativePos.f_82480_, (float)particle.relativePos.f_82481_);
               BlockPos blockpos = particle.worldPos.m_7494_();
               poseStack.m_85837_(-0.5, 0.0, -0.5);
               BakedModel model = dispatcher.m_110910_(particle.state);

               for (RenderType renderType : model.getRenderTypes(particle.state, RandomSource.m_216335_(0L), ModelData.EMPTY)) {
                  dispatcher.m_110937_()
                     .tesselateBlock(
                        level,
                        model,
                        particle.state,
                        blockpos,
                        poseStack,
                        buf.m_6299_(renderType),
                        false,
                        RandomSource.m_216327_(),
                        particle.state.m_60726_(particle.originalPos),
                        OverlayTexture.f_118083_,
                        ModelData.EMPTY,
                        renderType
                     );
               }

               poseStack.m_85849_();
            }

            toRender.clear();
         }
      }
   }

   FallingBlockParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xd, double yd, double zd, FallingBlockParticleOption options) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.f_107215_ = options.getMotion().f_82479_;
      this.f_107216_ = options.getMotion().f_82480_;
      this.f_107217_ = options.getMotion().f_82481_;
      this.f_107225_ = 200;
      this.f_107663_ = 1.0F;
      this.blockState = options.getState();
      this.f_107226_ = 0.08F;
      this.originalPos = BlockPos.m_274561_(this.f_107212_, this.f_107213_, this.f_107214_);
      this.particlesOnImpact = false;
   }

   public void m_5989_() {
      boolean onGround = this.f_107218_;
      this.f_107224_++;
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
      this.f_107216_ = this.f_107216_ - this.f_107226_;
      if (this.blockState.m_60795_() || onGround || this.f_107224_ > this.f_107225_) {
         if (onGround && this.particlesOnImpact) {
            double speed = Math.sqrt(this.f_107215_ * this.f_107215_ + this.f_107216_ * this.f_107216_ + this.f_107217_ * this.f_107217_);

            for (int i = 0; i < 25; i++) {
               Vec3 random = Utils.getRandomVec3(1.0).m_82542_(1.0, 0.25, 1.0).m_82541_().m_82490_(speed * 10.0 + 0.1);
               this.f_107208_
                  .m_7106_(
                     new BlockParticleOption(ParticleTypes.f_123794_, this.blockState),
                     this.f_107212_,
                     this.f_107213_,
                     this.f_107214_,
                     random.f_82479_,
                     random.f_82480_,
                     random.f_82481_
                  );
            }
         }

         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      if (this.blockState.m_60799_() == RenderShape.MODEL) {
         Vec3 vec3 = camera.m_90583_();
         float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
         float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
         float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
         toRender.add(
            new FallingBlockParticle.Renderable(
               BlockPos.m_274561_(this.f_107212_, this.f_107213_, this.f_107214_), this.originalPos, new Vec3(f, f1, f2), this.blockState
            )
         );
      }
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107429_;
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<FallingBlockParticleOption> {
      public Particle createParticle(
         @NotNull FallingBlockParticleOption options,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         return new FallingBlockParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, options);
      }
   }

   record Renderable(BlockPos worldPos, BlockPos originalPos, Vec3 relativePos, BlockState state) {
   }
}
