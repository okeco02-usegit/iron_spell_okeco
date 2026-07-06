package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class FireEruptionAoe extends AoeEntity {
   int waveAnim = -1;

   public FireEruptionAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.reapplicationDelay = 25;
      this.setCircular();
   }

   public FireEruptionAoe(Level level, float radius) {
      this((EntityType<? extends Projectile>)EntityRegistry.FIRE_ERUPTION_AOE.get(), level);
      this.setRadius(radius);
   }

   @Override
   public void applyEffect(LivingEntity target) {
      SpellDamageSource damageSource = ((AbstractSpell)SpellRegistry.RAISE_HELL_SPELL.get())
         .getDamageSource((Entity)(this.m_19749_() == null ? this : this.m_19749_()));
      DamageSources.ignoreNextKnockback(target);
      if (target.m_6469_(damageSource, this.getDamage())) {
         target.m_7311_(100);
         target.m_20256_(target.m_20184_().m_82520_(0.0, 0.65, 0.0));
         target.f_19864_ = true;
      }
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public void m_8119_() {
      float radius = this.getRadius();
      Level level = this.f_19853_;
      if (this.waveAnim++ < radius) {
         if (!level.f_46443_) {
            if (this.waveAnim % 2 == 0) {
               float volume = (this.waveAnim + 8) / 16.0F;
               this.m_5496_((SoundEvent)SoundRegistry.EARTHQUAKE_IMPACT.get(), volume, Utils.random.m_216332_(90, 110) * 0.01F);
            }

            float circumferenceMin = (this.waveAnim - 1) * 2 * 3.14F;
            float circumferenceMax = (this.waveAnim + 1) * 2 * 3.14F;
            int minBlocks = Mth.m_14045_((int)circumferenceMin, 0, 250);
            int maxBlocks = Mth.m_14045_((int)circumferenceMax, 0, 250);
            float anglePerBlockMin = 360.0F / minBlocks;
            float anglePerBlockMax = 360.0F / maxBlocks;

            for (int i = 0; i < minBlocks; i++) {
               Vec3 vec3 = new Vec3(this.waveAnim * Mth.m_14089_(anglePerBlockMin * i), 0.0, this.waveAnim * Mth.m_14031_(anglePerBlockMin * i));
               BlockPos blockPos = BlockPos.m_274446_(Utils.moveToRelativeGroundLevel(level, this.m_20182_().m_82549_(vec3), 4)).m_7495_();
               Utils.createTremorBlock(level, blockPos, 0.1F + this.f_19796_.m_188501_() * 0.2F);
            }

            for (int i = 0; i < maxBlocks; i++) {
               if (!(this.f_19796_.m_188501_() < 0.15F)) {
                  Vec3 vec3 = new Vec3((this.waveAnim + 1) * Mth.m_14089_(anglePerBlockMax * i), 0.0, (this.waveAnim + 1) * Mth.m_14031_(anglePerBlockMax * i));
                  BlockPos blockPos = BlockPos.m_274446_(Utils.moveToRelativeGroundLevel(level, this.m_20182_().m_82549_(vec3), 4));
                  if (level.m_8055_(blockPos.m_7495_()).m_60783_(level, blockPos.m_7495_(), Direction.UP)) {
                     Utils.createTremorBlockWithState(level, Blocks.f_50083_.m_49966_(), blockPos, 0.1F + this.f_19796_.m_188501_() * 0.2F);
                  }
               }
            }

            List<LivingEntity> targets = this.f_19853_
               .m_45976_(LivingEntity.class, this.m_20191_().m_82377_(this.getInflation().f_82479_, this.getInflation().f_82480_, this.getInflation().f_82481_));
            int r1Sqr = this.waveAnim * this.waveAnim;
            int r2Sqr = (this.waveAnim + 1) * (this.waveAnim + 1);

            for (LivingEntity target : targets) {
               double distanceSqr = target.m_20280_(this);
               if (this.m_5603_(target) && distanceSqr >= r1Sqr && distanceSqr <= r2Sqr && this.canHitTargetForGroundContext(target)) {
                  this.applyEffect(target);
               }
            }
         } else {
            int particles = (int)((this.waveAnim + 1) * 2 * 3.14F * 2.5F);
            float anglePerParticle = (float) (Math.PI * 2) / particles;

            for (int i = 0; i < particles; i++) {
               Vec3 trig = new Vec3(Mth.m_14089_(anglePerParticle * i), 0.0, Mth.m_14031_(anglePerParticle * i));
               float r = Mth.m_14179_(Utils.random.m_188501_(), this.waveAnim, this.waveAnim + 1);
               Vec3 pos = trig.m_82490_(r).m_82549_(Utils.getRandomVec3(0.4)).m_82549_(this.m_20182_()).m_82520_(0.0, 0.5, 0.0);
               Vec3 motion = trig.m_82549_(Utils.getRandomVec3(0.5)).m_82490_(0.1);
               level.m_7106_(ParticleHelper.FIRE, pos.f_82479_, pos.f_82480_, pos.f_82481_, motion.f_82479_, motion.f_82480_, motion.f_82481_);
            }
         }
      } else {
         this.m_146870_();
      }
   }

   public boolean m_142391_() {
      return false;
   }

   @Override
   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return !this.f_19853_.m_45772_(target.m_20191_().m_82383_(new Vec3(0.0, -0.9999, 0.0)));
   }

   @Override
   protected Vec3 getInflation() {
      return new Vec3(0.0, 5.0, 0.0);
   }

   @Override
   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, 3.0F);
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }
}
