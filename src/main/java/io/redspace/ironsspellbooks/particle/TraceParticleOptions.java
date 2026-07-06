package io.redspace.ironsspellbooks.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class TraceParticleOptions implements ParticleOptions {
   public final Vector3f color;
   public final Vector3f destination;
   public static MapCodec<TraceParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("x").forGetter(p -> p.destination.x),
            Codec.FLOAT.fieldOf("y").forGetter(p -> p.destination.y),
            Codec.FLOAT.fieldOf("z").forGetter(p -> p.destination.z),
            Codec.FLOAT.fieldOf("r").forGetter(p -> p.color.x),
            Codec.FLOAT.fieldOf("g").forGetter(p -> p.color.y),
            Codec.FLOAT.fieldOf("b").forGetter(p -> p.color.z)
         )
         .apply(object, TraceParticleOptions::new)
   );
   public static Codec<TraceParticleOptions> CODEC = RecordCodecBuilder.create(
      object -> object.group(
            Codec.FLOAT.fieldOf("x").forGetter(p -> p.destination.x),
            Codec.FLOAT.fieldOf("y").forGetter(p -> p.destination.y),
            Codec.FLOAT.fieldOf("z").forGetter(p -> p.destination.z),
            Codec.FLOAT.fieldOf("r").forGetter(p -> p.color.x),
            Codec.FLOAT.fieldOf("g").forGetter(p -> p.color.y),
            Codec.FLOAT.fieldOf("b").forGetter(p -> p.color.z)
         )
         .apply(object, TraceParticleOptions::new)
   );
   public static final Deserializer<TraceParticleOptions> DESERIALIZER = new Deserializer<TraceParticleOptions>() {
      @NotNull
      public TraceParticleOptions fromCommand(@NotNull ParticleType<TraceParticleOptions> p_123689_, @NotNull StringReader reader) throws CommandSyntaxException {
         return new TraceParticleOptions(reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat());
      }

      @NotNull
      public TraceParticleOptions fromNetwork(@NotNull ParticleType<TraceParticleOptions> p_123692_, @NotNull FriendlyByteBuf buf) {
         return new TraceParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
      }
   };

   public TraceParticleOptions(Vector3f destination, Vector3f color) {
      this.color = color;
      this.destination = destination;
   }

   public TraceParticleOptions(float x, float y, float z, float r, float g, float b) {
      this(new Vector3f(x, y, z), new Vector3f(r, g, b));
   }

   public ParticleType<?> m_6012_() {
      return ParticleRegistry.TRACE_PARTICLE.get();
   }

   public void m_7711_(FriendlyByteBuf buf) {
      buf.writeFloat(this.destination.x);
      buf.writeFloat(this.destination.y);
      buf.writeFloat(this.destination.z);
      buf.writeFloat(this.color.x);
      buf.writeFloat(this.color.y);
      buf.writeFloat(this.color.z);
   }

   public String m_5942_() {
      return "";
   }
}
