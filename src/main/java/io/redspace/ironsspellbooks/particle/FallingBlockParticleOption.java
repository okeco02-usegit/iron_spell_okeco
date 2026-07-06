package io.redspace.ironsspellbooks.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class FallingBlockParticleOption implements ParticleOptions {
   private static final Codec<BlockState> BLOCK_STATE_CODEC = CodecHelper.withAlternative(
      BlockState.f_61039_, BuiltInRegistries.f_256975_.m_194605_().xmap(Block::m_49966_, BlockStateBase::m_60734_)
   );
   private final ParticleType<FallingBlockParticleOption> type;
   private final BlockState state;
   private final Vec3 motion;
   public static final Deserializer<FallingBlockParticleOption> DESERIALIZER = new Deserializer<FallingBlockParticleOption>() {
      public FallingBlockParticleOption fromCommand(ParticleType<FallingBlockParticleOption> p_123645_, StringReader p_123646_) throws CommandSyntaxException {
         p_123646_.expect(' ');
         BlockState state = BlockStateParser.m_234691_(BuiltInRegistries.f_256975_.m_255303_(), p_123646_, false).f_234748_();
         Vector3f vector = DustParticleOptionsBase.m_252853_(p_123646_);
         return new FallingBlockParticleOption(p_123645_, state, new Vec3(vector.x(), vector.y(), vector.z()));
      }

      @NotNull
      public FallingBlockParticleOption fromNetwork(@NotNull ParticleType<FallingBlockParticleOption> p_123692_, @NotNull FriendlyByteBuf buf) {
         return new FallingBlockParticleOption(
            (BlockState)buf.m_236816_(Block.f_49791_), buf.readBoolean() ? new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()) : Vec3.f_82478_
         );
      }
   };

   public Vec3 getMotion() {
      return this.motion;
   }

   public static Codec<FallingBlockParticleOption> codec(ParticleType<FallingBlockParticleOption> particleType) {
      return RecordCodecBuilder.create(
         builder -> builder.group(
               BLOCK_STATE_CODEC.fieldOf("block_state").forGetter(FallingBlockParticleOption::getState),
               Vec3.f_231074_.optionalFieldOf("motion", Vec3.f_82478_).forGetter(FallingBlockParticleOption::getMotion)
            )
            .apply(builder, (state, motion) -> new FallingBlockParticleOption(particleType, state, motion))
      );
   }

   public FallingBlockParticleOption(ParticleType<FallingBlockParticleOption> type, BlockState state, Vec3 motion) {
      this.type = type;
      this.state = state;
      this.motion = motion;
   }

   public FallingBlockParticleOption(ParticleType<FallingBlockParticleOption> type, BlockState state) {
      this(type, state, Vec3.f_82478_);
   }

   public FallingBlockParticleOption(BlockState state, Vec3 motion) {
      this(ParticleRegistry.FALLING_BLOCK_PARTICLE.get(), state, motion);
   }

   public FallingBlockParticleOption(BlockState state) {
      this(state, Vec3.f_82478_);
   }

   public ParticleType<FallingBlockParticleOption> m_6012_() {
      return this.type;
   }

   public void m_7711_(FriendlyByteBuf buf) {
      buf.m_236818_(Block.f_49791_, this.state);
      Vec3 motion = this.getMotion();
      if (motion == Vec3.f_82478_) {
         buf.writeBoolean(false);
      } else {
         buf.writeBoolean(true);
         buf.writeDouble(motion.f_82479_);
         buf.writeDouble(motion.f_82480_);
         buf.writeDouble(motion.f_82481_);
      }
   }

   public String m_5942_() {
      return "";
   }

   public BlockState getState() {
      return this.state;
   }
}
