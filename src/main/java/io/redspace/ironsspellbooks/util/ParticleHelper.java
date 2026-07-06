package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public class ParticleHelper {
   public static final ParticleOptions FIRE = (ParticleOptions)ParticleRegistry.DRAGON_FIRE_PARTICLE.get();
   public static final ParticleOptions FIRE_EMITTER = (ParticleOptions)ParticleRegistry.FIRE_PARTICLE.get();
   public static final ParticleOptions BLOOD = (ParticleOptions)ParticleRegistry.BLOOD_PARTICLE.get();
   public static final ParticleOptions WISP = (ParticleOptions)ParticleRegistry.WISP_PARTICLE.get();
   public static final ParticleOptions BLOOD_GROUND = (ParticleOptions)ParticleRegistry.BLOOD_GROUND_PARTICLE.get();
   public static final ParticleOptions SNOWFLAKE = (ParticleOptions)ParticleRegistry.SNOWFLAKE_PARTICLE.get();
   public static final ParticleOptions ELECTRICITY = (ParticleOptions)ParticleRegistry.ELECTRICITY_PARTICLE.get();
   public static final ParticleOptions UNSTABLE_ENDER = (ParticleOptions)ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get();
   public static final ParticleOptions EMBERS = (ParticleOptions)ParticleRegistry.EMBER_PARTICLE.get();
   public static final ParticleOptions SIPHON = (ParticleOptions)ParticleRegistry.SIPHON_PARTICLE.get();
   public static final ParticleOptions ACID = (ParticleOptions)ParticleRegistry.ACID_PARTICLE.get();
   public static final ParticleOptions ACID_BUBBLE = (ParticleOptions)ParticleRegistry.ACID_BUBBLE_PARTICLE.get();
   public static final ParticleOptions FOG = new FogParticleOptions(new Vector3f(1.0F, 1.0F, 1.0F), 1.0F);
   public static final ParticleOptions VOID_TENTACLE_FOG = new FogParticleOptions(new Vector3f(0.0F, 0.075F, 0.13F), 2.0F);
   public static final ParticleOptions ROOT_FOG = new FogParticleOptions(new Vector3f(0.23921569F, 0.15686275F, 0.07058824F), 0.4F);
   public static final ParticleOptions COMET_FOG = new FogParticleOptions(new Vector3f(0.75F, 0.55F, 1.0F), 1.5F);
   public static final ParticleOptions FOG_THUNDER_LIGHT = new FogParticleOptions(new Vector3f(0.5F, 0.5F, 0.5F), 1.5F);
   public static final ParticleOptions FOG_THUNDER_DARK = new FogParticleOptions(new Vector3f(0.4F, 0.4F, 0.4F), 1.5F);
   public static final ParticleOptions POISON_CLOUD = new FogParticleOptions(new Vector3f(0.08F, 0.64F, 0.16F), 1.0F);
   public static final ParticleOptions FOG_CAMPFIRE_SMOKE = new FogParticleOptions(new Vector3f(0.5411765F, 0.50980395F, 0.49019608F), 1.0F);
   public static final ParticleOptions ICY_FOG = new FogParticleOptions(new Vector3f(0.8156863F, 0.9764706F, 1.0F), 0.6F);
   public static final ParticleOptions SUNBEAM = new FogParticleOptions(new Vector3f(0.95F, 0.97F, 0.36F), 1.0F);
   public static final ParticleOptions FIREFLY = (ParticleOptions)ParticleRegistry.FIREFLY_PARTICLE.get();
   public static final ParticleOptions PORTAL_FRAME = (ParticleOptions)ParticleRegistry.PORTAL_FRAME_PARTICLE.get();
   public static final ParticleOptions FIERY_SPARKS = new SparkParticleOptions(new Vector3f(1.0F, 0.6F, 0.3F));
   public static final ParticleOptions ELECTRIC_SPARKS = new SparkParticleOptions(new Vector3f(0.333F, 1.0F, 1.0F));
   public static final ParticleOptions ENDER_SPARKS = new SparkParticleOptions(new Vector3f(1.0F, 0.333F, 1.0F));
   public static final ParticleOptions SNOW_DUST = (ParticleOptions)ParticleRegistry.SNOW_DUST.get();
   public static final ParticleOptions CLEANSE_PARTICLE = (ParticleOptions)ParticleRegistry.CLEANSE_PARTICLE.get();
   public static final ParticleOptions FIERY_SMOKE = (ParticleOptions)ParticleRegistry.FIERY_SMOKE_PARTICLE.get();
}
