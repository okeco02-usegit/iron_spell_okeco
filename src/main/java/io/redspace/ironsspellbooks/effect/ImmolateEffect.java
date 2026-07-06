package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ImmolateEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final int STACKS_REQUIRED = 3;
   public static final int STACKS_REQUIRED_AMPLIFIER = 2;
   private static final Map<LivingEntity, Entity> EFFECT_CREDIT = new WeakHashMap<>();
   private static final Map<MobEffectInstance, Integer> DELAYED_INSTANCES = new WeakHashMap<>();
   static int duration;

   public ImmolateEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public static MobEffectInstance addImmolateStack(LivingEntity entity, @Nullable Entity afflicter) {
      MobEffectInstance previous = entity.m_21124_((MobEffect)MobEffectRegistry.IMMOLATE.get());
      MobEffectInstance inst;
      if (previous != null) {
         inst = new MobEffectInstance(
            (MobEffect)MobEffectRegistry.IMMOLATE.get(), 300, previous.m_19564_() + 1, previous.m_19571_(), previous.m_19572_(), previous.m_19575_()
         );
      } else {
         inst = new MobEffectInstance((MobEffect)MobEffectRegistry.IMMOLATE.get(), 300, 0, false, false, true);
      }

      if (afflicter != null) {
         EFFECT_CREDIT.put(entity, afflicter);
      }

      entity.m_7292_(inst);
      return inst;
   }

   @Override
   public void clientTick(LivingEntity livingEntity, MobEffectInstance instance) {
      int amplifier = instance.m_19564_();
      ParticleOptions particle = ParticleTypes.f_123762_;
      if (amplifier >= 1) {
         particle = ParticleHelper.FIRE;
      }

      RandomSource random = livingEntity.m_217043_();

      for (int i = 0; i < 2; i++) {
         Vec3 motion = new Vec3(random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F);
         motion = motion.m_82490_(0.04F);
         livingEntity.f_19853_
            .m_7106_(
               particle, livingEntity.m_20208_(0.4F), livingEntity.m_20187_(), livingEntity.m_20262_(0.4F), motion.f_82479_, motion.f_82480_, motion.f_82481_
            );
      }
   }

   public void m_6742_(LivingEntity livingEntity, int amplifier) {
      MobEffectInstance self = livingEntity.m_21124_((MobEffect)MobEffectRegistry.IMMOLATE.get());
      if (!DELAYED_INSTANCES.containsKey(self) || DELAYED_INSTANCES.get(self) - duration > 4) {
         float explosionRadius = 6.0F;
         Level level = livingEntity.f_19853_;
         if (!level.f_46443_) {
            Entity attacker = EFFECT_CREDIT.remove(livingEntity);
            double baseDamage = damageFor(attacker);
            DamageSource source = new DamageSource(level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ISSDamageTypes.FIRE_MAGIC), attacker);
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = level.m_45933_(null, livingEntity.m_20191_().m_82400_(explosionRadius));
            Vec3 losPoint = Utils.raycastForBlock(level, livingEntity.m_20182_(), livingEntity.m_20182_().m_82520_(0.0, 1.0, 0.0), Fluid.NONE).m_82450_();

            for (Entity entity : entities) {
               double distanceSqr = entity.m_20238_(livingEntity.m_20182_());
               if (distanceSqr < explosionRadiusSqr
                  && entity.m_271807_()
                  && !DamageSources.isFriendlyFireBetween(attacker, entity)
                  && Utils.hasLineOfSight(level, losPoint, entity.m_20191_().m_82399_(), true)) {
                  double p = 1.0 - distanceSqr / explosionRadiusSqr;
                  float damage = (float)(baseDamage * p);
                  if (entity.m_6469_(source, damage) && entity instanceof LivingEntity livingVictim) {
                     MobEffectInstance inst = addImmolateStack(livingVictim, attacker);
                     DELAYED_INSTANCES.put(inst, inst.m_19557_());
                  }
               }
            }

            PacketDistributor.sendToPlayersTrackingEntity(livingEntity, new FieryExplosionParticlesPacket(livingEntity.m_20191_().m_82399_(), 1.5F));
            level.m_6263_(
               null,
               livingEntity.m_20185_(),
               livingEntity.m_20186_(),
               livingEntity.m_20189_(),
               SoundEvents.f_11913_,
               livingEntity.m_5720_(),
               4.0F,
               (1.0F + (level.f_46441_.m_188501_() - level.f_46441_.m_188501_()) * 0.2F) * 0.7F
            );
            livingEntity.m_21195_(this);
         }
      }
   }

   public static double damageFor(@Nullable Entity entity) {
      double baseDamage = 10.0;
      if (entity instanceof LivingEntity livingAttacker) {
         baseDamage = baseDamage
            * livingAttacker.m_21133_((Attribute)AttributeRegistry.SPELL_POWER.get())
            * livingAttacker.m_21133_((Attribute)AttributeRegistry.FIRE_SPELL_POWER.get());
      }

      return baseDamage;
   }

   public boolean m_6584_(int duration, int amplifier) {
      ImmolateEffect.duration = duration;
      return amplifier >= 2;
   }
}
