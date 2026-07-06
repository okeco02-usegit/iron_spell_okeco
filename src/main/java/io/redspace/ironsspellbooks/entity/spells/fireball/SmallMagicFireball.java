package io.redspace.ironsspellbooks.entity.spells.fireball;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmallMagicFireball extends AbstractMagicProjectile {
   public SmallMagicFireball(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
   }

   public SmallMagicFireball(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.SMALL_FIREBALL_PROJECTILE.get(), pLevel);
      this.m_5602_(pShooter);
   }

   public void shoot(Vec3 rotation, float inaccuracy) {
      double speed = rotation.m_82553_();
      Vec3 offset = Utils.getRandomVec3(1.0).m_82541_().m_82490_(inaccuracy);
      Vec3 motion = rotation.m_82541_().m_82549_(offset).m_82541_().m_82490_(speed);
      super.shoot(motion);
   }

   @Override
   public void trailParticles() {
      if (this.f_19797_ > 3) {
         Vec3 vec3 = this.m_20184_();
         double d0 = this.m_20185_() - vec3.f_82479_;
         double d1 = this.m_20186_() - vec3.f_82480_;
         double d2 = this.m_20189_() - vec3.f_82481_;
         int count = Mth.m_14045_((int)(vec3.m_82556_() * 4.0), 1, 5);

         for (int i = 0; i < count; i++) {
            Vec3 random = Utils.getRandomVec3(0.1);
            float f = (float)i / count;
            double x = Mth.m_14139_(f, d0, this.m_20185_());
            double y = Mth.m_14139_(f, d1, this.m_20186_());
            double z = Mth.m_14139_(f, d2, this.m_20189_());
            this.f_19853_
               .m_7106_(
                  ParticleHelper.EMBERS,
                  x - random.f_82479_,
                  y + 0.5 - random.f_82480_,
                  z - random.f_82481_,
                  random.f_82479_ * 0.5,
                  random.f_82480_ * 0.5,
                  random.f_82481_ * 0.5
               );
         }
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.FIERY_SPARKS, x, y, z, 5, 0.0, 0.0, 0.0, 0.25, true);
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.FIERY_SPARKS, x, y, z, 5, 0.0, 0.0, 0.0, 0.25, false);
   }

   @Override
   public float getSpeed() {
      return 1.85F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.FIRE_IMPACT);
   }

   @Override
   protected void m_5790_(EntityHitResult pResult) {
      if (!this.f_19853_.f_46443_) {
         Entity target = pResult.m_82443_();
         Entity owner = this.m_19749_();
         DamageSources.applyDamage(target, this.damage, ((AbstractSpell)SpellRegistry.BLAZE_STORM_SPELL.get()).getDamageSource(this, owner));
      }
   }

   protected void m_8060_(BlockHitResult pResult) {
      super.m_8060_(pResult);
      if (!this.f_19853_.f_46443_ && (Boolean)ServerConfigs.SPELL_GREIFING.get()) {
         BlockPos blockpos = pResult.m_82425_().m_121945_(pResult.m_82434_());
         if (this.f_19853_.m_46859_(blockpos)) {
            this.f_19853_.m_46597_(blockpos, BaseFireBlock.m_49245_(this.f_19853_, blockpos));
         }
      }
   }

   @Override
   protected void m_6532_(HitResult pResult) {
      super.m_6532_(pResult);
      this.discardHelper(pResult);
   }
}
