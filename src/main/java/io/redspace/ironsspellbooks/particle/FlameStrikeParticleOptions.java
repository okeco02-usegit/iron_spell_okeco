package io.redspace.ironsspellbooks.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class FlameStrikeParticleOptions implements ParticleOptions {
   public final float scale;
   public final float xf;
   public final float yf;
   public final float zf;
   public final boolean mirror;
   public final boolean vertical;
   public static Codec<FlameStrikeParticleOptions> CODEC = RecordCodecBuilder.create(
      object -> object.group(
            Codec.FLOAT.fieldOf("xf").forGetter(p -> p.xf),
            Codec.FLOAT.fieldOf("yf").forGetter(p -> p.yf),
            Codec.FLOAT.fieldOf("zf").forGetter(p -> p.zf),
            Codec.BOOL.fieldOf("mirror").forGetter(p -> p.mirror),
            Codec.BOOL.fieldOf("vertical").forGetter(p -> p.vertical),
            Codec.FLOAT.fieldOf("scale").forGetter(p -> p.scale)
         )
         .apply(object, FlameStrikeParticleOptions::new)
   );
   public static final Deserializer<FlameStrikeParticleOptions> DESERIALIZER = new Deserializer<FlameStrikeParticleOptions>() {
      @NotNull
      public FlameStrikeParticleOptions fromCommand(@NotNull ParticleType<FlameStrikeParticleOptions> p_123689_, @NotNull StringReader p_123690_) throws CommandSyntaxException {
         return new FlameStrikeParticleOptions(
            p_123690_.readFloat(), p_123690_.readFloat(), p_123690_.readFloat(), p_123690_.readBoolean(), p_123690_.readBoolean(), p_123690_.readFloat()
         );
      }

      @NotNull
      public FlameStrikeParticleOptions fromNetwork(@NotNull ParticleType<FlameStrikeParticleOptions> p_123692_, @NotNull FriendlyByteBuf buf) {
         return new FlameStrikeParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readFloat());
      }
   };

   public FlameStrikeParticleOptions(float xf, float yf, float zf, boolean mirror, boolean vertical, float scale) {
      this.scale = scale;
      this.xf = xf;
      this.yf = yf;
      this.zf = zf;
      this.mirror = mirror;
      this.vertical = vertical;
   }

   @NotNull
   public ParticleType<FlameStrikeParticleOptions> m_6012_() {
      return ParticleRegistry.FLAME_STRIKE_PARTICLE.get();
   }

   public void m_7711_(FriendlyByteBuf buf) {
      FlameStrikeParticleOptions option = this;
      buf.writeFloat(option.xf);
      buf.writeFloat(option.yf);
      buf.writeFloat(option.zf);
      buf.writeBoolean(option.mirror);
      buf.writeBoolean(option.vertical);
      buf.writeFloat(option.scale);
   }

   public String m_5942_() {
      return "";
   }
}
