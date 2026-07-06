package io.redspace.ironsspellbooks.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ShockwaveParticleOptions extends DustParticleOptionsBase {
   protected final float unclampedScale;
   protected final boolean fullbright;
   public static final Codec<ShockwaveParticleOptions> CODEC = RecordCodecBuilder.create(
      p_175793_ -> p_175793_.group(
            ExtraCodecs.f_252432_.fieldOf("color").forGetter(option -> option.f_175800_),
            Codec.FLOAT.fieldOf("scale").forGetter(option -> option.unclampedScale),
            Codec.BOOL.fieldOf("fullbright").forGetter(option -> option.fullbright)
         )
         .apply(p_175793_, ShockwaveParticleOptions::new)
   );
   public static final Deserializer<ShockwaveParticleOptions> DESERIALIZER = new Deserializer<ShockwaveParticleOptions>() {
      @NotNull
      public ShockwaveParticleOptions fromCommand(@NotNull ParticleType<ShockwaveParticleOptions> p_123689_, @NotNull StringReader reader) throws CommandSyntaxException {
         Vector3f vector3f = DustParticleOptionsBase.m_252853_(reader);
         reader.expect(' ');
         float f = reader.readFloat();
         reader.expect(' ');
         boolean glowing = reader.readBoolean();
         return new ShockwaveParticleOptions(vector3f, f, glowing);
      }

      @NotNull
      public ShockwaveParticleOptions fromNetwork(@NotNull ParticleType<ShockwaveParticleOptions> p_123692_, @NotNull FriendlyByteBuf buf) {
         return new ShockwaveParticleOptions(DustParticleOptionsBase.m_253064_(buf), buf.readFloat(), buf.readBoolean());
      }
   };

   public ShockwaveParticleOptions(Vector3f color, float scale, boolean glowing, String trailParticle) {
      super(color, scale);
      this.unclampedScale = scale;
      this.fullbright = glowing;
   }

   public ShockwaveParticleOptions(Vector3f color, float scale, boolean glowing) {
      this(color, scale, glowing, "");
   }

   public float m_175813_() {
      return this.unclampedScale;
   }

   public boolean isFullbright() {
      return this.fullbright;
   }

   public Vector3f color() {
      return this.f_175800_;
   }

   public void m_7711_(FriendlyByteBuf pBuffer) {
      pBuffer.writeFloat(this.f_175800_.x());
      pBuffer.writeFloat(this.f_175800_.y());
      pBuffer.writeFloat(this.f_175800_.z());
      pBuffer.writeFloat(this.unclampedScale);
      pBuffer.writeBoolean(this.fullbright);
   }

   @NotNull
   public ParticleType<ShockwaveParticleOptions> m_6012_() {
      return (ParticleType<ShockwaveParticleOptions>)ParticleRegistry.SHOCKWAVE_PARTICLE.get();
   }
}
