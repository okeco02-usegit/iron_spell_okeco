package io.redspace.ironsspellbooks.registries;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.EnderSlashParticleOptions;
import io.redspace.ironsspellbooks.particle.FallingBlockParticleOption;
import io.redspace.ironsspellbooks.particle.FlameStrikeParticleOptions;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import io.redspace.ironsspellbooks.particle.TraceParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.f_256890_, "irons_spellbooks");
   public static final Supplier<SimpleParticleType> BLOOD_PARTICLE = PARTICLE_TYPES.register("blood", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> WISP_PARTICLE = PARTICLE_TYPES.register("wisp", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> BLOOD_GROUND_PARTICLE = PARTICLE_TYPES.register("blood_ground", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> SNOWFLAKE_PARTICLE = PARTICLE_TYPES.register("snowflake", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> ELECTRICITY_PARTICLE = PARTICLE_TYPES.register("electricity", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> UNSTABLE_ENDER_PARTICLE = PARTICLE_TYPES.register("unstable_ender", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> DRAGON_FIRE_PARTICLE = PARTICLE_TYPES.register("dragon_fire", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> FIRE_PARTICLE = PARTICLE_TYPES.register("fire", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> EMBER_PARTICLE = PARTICLE_TYPES.register("embers", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> SIPHON_PARTICLE = PARTICLE_TYPES.register("spell", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> ACID_PARTICLE = PARTICLE_TYPES.register("acid", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> ACID_BUBBLE_PARTICLE = PARTICLE_TYPES.register("acid_bubble", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> SNOW_DUST = PARTICLE_TYPES.register("snow_dust", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> RING_SMOKE_PARTICLE = PARTICLE_TYPES.register("ring_smoke", () -> new SimpleParticleType(false));
   public static final RegistryObject<ParticleType<FogParticleOptions>> FOG_PARTICLE = PARTICLE_TYPES.register(
      "fog", () -> new ParticleType<FogParticleOptions>(true, FogParticleOptions.DESERIALIZER) {
         public Codec<FogParticleOptions> m_7652_() {
            return FogParticleOptions.CODEC;
         }
      }
   );
   public static final RegistryObject<ParticleType<ShockwaveParticleOptions>> SHOCKWAVE_PARTICLE = PARTICLE_TYPES.register(
      "shockwave", () -> new ParticleType<ShockwaveParticleOptions>(false, ShockwaveParticleOptions.DESERIALIZER) {
         public Codec<ShockwaveParticleOptions> m_7652_() {
            return ShockwaveParticleOptions.CODEC;
         }
      }
   );
   public static final RegistryObject<ParticleType<ZapParticleOption>> ZAP_PARTICLE = PARTICLE_TYPES.register(
      "zap", () -> new ParticleType<ZapParticleOption>(false, ZapParticleOption.DESERIALIZER) {
         public Codec<ZapParticleOption> m_7652_() {
            return ZapParticleOption.CODEC;
         }
      }
   );
   public static final Supplier<SimpleParticleType> FIREFLY_PARTICLE = PARTICLE_TYPES.register("firefly", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> PORTAL_FRAME_PARTICLE = PARTICLE_TYPES.register("portal_frame", () -> new SimpleParticleType(false));
   public static final RegistryObject<ParticleType<BlastwaveParticleOptions>> BLASTWAVE_PARTICLE = PARTICLE_TYPES.register(
      "blastwave", () -> new ParticleType<BlastwaveParticleOptions>(true, BlastwaveParticleOptions.DESERIALIZER) {
         public Codec<BlastwaveParticleOptions> m_7652_() {
            return BlastwaveParticleOptions.CODEC;
         }
      }
   );
   public static final RegistryObject<ParticleType<SparkParticleOptions>> SPARK_PARTICLE = PARTICLE_TYPES.register(
      "spark", () -> new ParticleType<SparkParticleOptions>(true, SparkParticleOptions.DESERIALIZER) {
         public Codec<SparkParticleOptions> m_7652_() {
            return SparkParticleOptions.CODEC;
         }
      }
   );
   public static final Supplier<SimpleParticleType> CLEANSE_PARTICLE = PARTICLE_TYPES.register("cleanse", () -> new SimpleParticleType(false));
   public static final Supplier<ParticleType<FlameStrikeParticleOptions>> FLAME_STRIKE_PARTICLE = PARTICLE_TYPES.register(
      "flame_strike", () -> new ParticleType<FlameStrikeParticleOptions>(true, FlameStrikeParticleOptions.DESERIALIZER) {
         public Codec<FlameStrikeParticleOptions> m_7652_() {
            return FlameStrikeParticleOptions.CODEC;
         }
      }
   );
   public static final Supplier<SimpleParticleType> EMBEROUS_ASH_PARTICLE = PARTICLE_TYPES.register("emberous_ash", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> FIERY_SMOKE_PARTICLE = PARTICLE_TYPES.register("fiery_smoke", () -> new SimpleParticleType(true));
   public static final Supplier<ParticleType<EnderSlashParticleOptions>> ENDER_SLASH_PARTICLE = PARTICLE_TYPES.register(
      "ender_slash", () -> new ParticleType<EnderSlashParticleOptions>(true, EnderSlashParticleOptions.DESERIALIZER) {
         public Codec<EnderSlashParticleOptions> m_7652_() {
            return EnderSlashParticleOptions.CODEC;
         }
      }
   );
   public static final Supplier<ParticleType<TraceParticleOptions>> TRACE_PARTICLE = PARTICLE_TYPES.register(
      "trace", () -> new ParticleType<TraceParticleOptions>(true, TraceParticleOptions.DESERIALIZER) {
         public Codec<TraceParticleOptions> m_7652_() {
            return TraceParticleOptions.CODEC;
         }
      }
   );
   public static final Supplier<ParticleType<FallingBlockParticleOption>> FALLING_BLOCK_PARTICLE = PARTICLE_TYPES.register(
      "falling_block", () -> new ParticleType<FallingBlockParticleOption>(true, FallingBlockParticleOption.DESERIALIZER) {
         public Codec<FallingBlockParticleOption> m_7652_() {
            return FallingBlockParticleOption.codec(this);
         }
      }
   );

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }
}
