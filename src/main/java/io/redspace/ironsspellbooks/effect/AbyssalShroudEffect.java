package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.datagen.DamageTypeTagGenerator;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbyssalShroudEffect extends MagicMobEffect implements ISyncedMobEffect {
   public AbyssalShroudEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public static boolean doEffect(LivingEntity livingEntity, DamageSource damageSource) {
      if (!livingEntity.f_19853_.f_46443_
         && !damageSource.m_269533_(DamageTypeTagGenerator.BYPASS_EVASION)
         && !damageSource.m_269533_(DamageTypeTags.f_268549_)
         && !damageSource.m_269533_(DamageTypeTags.f_268738_)) {
         RandomSource random = livingEntity.m_217043_();
         Level level = livingEntity.f_19853_;
         Vec3 sideStep = new Vec3(random.m_188499_() ? 1.0 : -1.0, 0.0, -0.25);
         sideStep.m_82524_(livingEntity.m_146908_());
         particleCloud(livingEntity);
         Vec3 ground = livingEntity.m_20182_().m_82549_(sideStep);
         ground = Utils.moveToRelativeGroundLevel(level, ground, 2, 1);
         EntityDimensions dimensions = livingEntity.m_6972_(livingEntity.m_20089_());
         Vec3 vec3 = ground.m_82520_(0.0, dimensions.f_20378_ / 2.0, 0.0);
         VoxelShape voxelshape = Shapes.m_83064_(AABB.m_165882_(vec3, dimensions.f_20377_ + 0.2F, dimensions.f_20378_ + 0.2F, dimensions.f_20377_ + 0.2F));
         Optional<Vec3> optional = level.m_151418_(null, voxelshape, vec3, dimensions.f_20377_, dimensions.f_20378_, dimensions.f_20377_);
         if (optional.isPresent()) {
            ground = optional.get().m_82520_(0.0, -dimensions.f_20378_ / 2.0F + 1.0E-6, 0.0);
         }

         if (level.m_186437_(
            null, AABB.m_165882_(ground.m_82520_(0.0, dimensions.f_20378_ / 2.0F, 0.0), dimensions.f_20377_, dimensions.f_20378_, dimensions.f_20377_)
         )) {
            ground = livingEntity.m_20182_();
         }

         if (livingEntity.m_20159_()) {
            livingEntity.m_8127_();
         }

         if (!level.m_8055_(BlockPos.m_274446_(ground).m_7495_()).m_60795_()) {
            livingEntity.m_6021_(ground.f_82479_, ground.f_82480_, ground.f_82481_);
            particleCloud(livingEntity);
         }

         if (damageSource.m_7639_() != null) {
            livingEntity.m_7618_(Anchor.EYES, damageSource.m_7639_().m_146892_().m_82492_(0.0, 0.15, 0.0));
         }

         level.m_6263_(
            null,
            livingEntity.m_20185_(),
            livingEntity.m_20186_(),
            livingEntity.m_20189_(),
            (SoundEvent)SoundRegistry.ABYSSAL_TELEPORT.get(),
            SoundSource.AMBIENT,
            1.0F,
            0.9F + random.m_188501_() * 0.2F
         );
         return true;
      } else {
         return false;
      }
   }

   private static void particleCloud(LivingEntity entity) {
      Vec3 pos = entity.m_20182_().m_82520_(0.0, entity.m_20206_() / 2.0F, 0.0);
      MagicManager.spawnParticles(
         entity.m_9236_(),
         ParticleTypes.f_123762_,
         pos.f_82479_,
         pos.f_82480_,
         pos.f_82481_,
         70,
         entity.m_20205_() / 4.0F,
         entity.m_20206_() / 5.0F,
         entity.m_20205_() / 4.0F,
         0.035,
         false
      );
   }

   @Override
   public void clientTick(LivingEntity entity, MobEffectInstance instance) {
      Vec3 backwards = entity.m_20156_().m_82490_(0.003).m_82548_().m_82520_(0.0, 0.02, 0.0);
      RandomSource random = entity.m_217043_();

      for (int i = 0; i < 2; i++) {
         Vec3 motion = new Vec3(random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F, random.m_188501_() * 2.0F - 1.0F);
         motion = motion.m_82490_(0.04F).m_82549_(backwards);
         entity.f_19853_
            .m_7106_(
               ParticleTypes.f_123762_, entity.m_20208_(0.4F), entity.m_20187_(), entity.m_20262_(0.4F), motion.f_82479_, motion.f_82480_, motion.f_82481_
            );
      }
   }
}
