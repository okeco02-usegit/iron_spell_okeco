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

public class EnderSlashParticleOptions implements ParticleOptions {
   public final float scale;
   public final float xf;
   public final float yf;
   public final float zf;
   public final float xu;
   public final float yu;
   public final float zu;
   public static MapCodec<EnderSlashParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("xf").forGetter(p -> p.xf),
            Codec.FLOAT.fieldOf("yf").forGetter(p -> p.yf),
            Codec.FLOAT.fieldOf("zf").forGetter(p -> p.zf),
            Codec.FLOAT.fieldOf("xu").forGetter(p -> p.xu),
            Codec.FLOAT.fieldOf("yu").forGetter(p -> p.yu),
            Codec.FLOAT.fieldOf("zu").forGetter(p -> p.zu),
            Codec.FLOAT.fieldOf("scale").forGetter(p -> p.scale)
         )
         .apply(object, EnderSlashParticleOptions::new)
   );
   public static Codec<EnderSlashParticleOptions> CODEC = RecordCodecBuilder.create(
      object -> object.group(
            Codec.FLOAT.fieldOf("xf").forGetter(p -> p.xf),
            Codec.FLOAT.fieldOf("yf").forGetter(p -> p.yf),
            Codec.FLOAT.fieldOf("zf").forGetter(p -> p.zf),
            Codec.FLOAT.fieldOf("xu").forGetter(p -> p.xu),
            Codec.FLOAT.fieldOf("yu").forGetter(p -> p.yu),
            Codec.FLOAT.fieldOf("zu").forGetter(p -> p.zu),
            Codec.FLOAT.fieldOf("scale").forGetter(p -> p.scale)
         )
         .apply(object, EnderSlashParticleOptions::new)
   );
   public static final Deserializer<EnderSlashParticleOptions> DESERIALIZER = new Deserializer<EnderSlashParticleOptions>() {
      @NotNull
      public EnderSlashParticleOptions fromCommand(@NotNull ParticleType<EnderSlashParticleOptions> p_123689_, @NotNull StringReader reader) throws CommandSyntaxException {
         return new EnderSlashParticleOptions(
            reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat(), reader.readFloat()
         );
      }

      @NotNull
      public EnderSlashParticleOptions fromNetwork(@NotNull ParticleType<EnderSlashParticleOptions> p_123692_, @NotNull FriendlyByteBuf buf) {
         return new EnderSlashParticleOptions(
            buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()
         );
      }
   };

   public EnderSlashParticleOptions(float xf, float yf, float zf, float xu, float yu, float zu, float scale) {
      this.scale = scale;
      this.xf = xf;
      this.yf = yf;
      this.zf = zf;
      this.xu = xu;
      this.yu = yu;
      this.zu = zu;
   }

   @NotNull
   public ParticleType<EnderSlashParticleOptions> m_6012_() {
      return ParticleRegistry.ENDER_SLASH_PARTICLE.get();
   }

   public void m_7711_(FriendlyByteBuf buf) {
      buf.writeFloat(this.xf);
      buf.writeFloat(this.yf);
      buf.writeFloat(this.zf);
      buf.writeFloat(this.xu);
      buf.writeFloat(this.yu);
      buf.writeFloat(this.zu);
      buf.writeFloat(this.scale);
   }

   public String m_5942_() {
      return "";
   }
}
