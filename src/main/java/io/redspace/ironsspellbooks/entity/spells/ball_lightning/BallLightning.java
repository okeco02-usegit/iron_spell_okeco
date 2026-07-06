package io.redspace.ironsspellbooks.entity.spells.ball_lightning;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class BallLightning extends AbstractMagicProjectile {
   public static final int lifetime = 100;
   int bounces;
   HashMap<UUID, Integer> victims = new HashMap<>();

   public BallLightning(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
   }

   public BallLightning(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.BALL_LIGHTNING.get(), level);
      this.m_5602_(shooter);
   }

   @Override
   public void trailParticles() {
      Vec3 pos = this.m_20191_().m_82399_().m_82549_(this.m_20184_());
      Vec3 random = Utils.getRandomVec3(0.28);
      pos = pos.m_82549_(this.m_20184_());
      this.f_19853_.m_7106_(ParticleHelper.ELECTRICITY, pos.f_82479_, pos.f_82480_, pos.f_82481_, random.f_82479_, random.f_82480_, random.f_82481_);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ELECTRIC_SPARKS, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
   }

   @Override
   public float getSpeed() {
      return 0.6F;
   }

   @Override
   protected boolean m_5603_(Entity pTarget) {
      return super.m_5603_(pTarget) && this.canHitVictim(pTarget);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ > 100) {
         this.m_146870_();
         if (!this.f_19853_.f_46443_) {
            this.impactParticles(this.m_20185_(), this.m_20191_().m_82399_().f_82480_, this.m_20189_());
         }
      }
   }

   @Override
   public void handleHitDetection() {
      Vec3 vec3 = this.m_20184_();
      Vec3 pos = this.m_20182_();
      Vec3 vec32 = pos.m_82549_(vec3);
      HitResult hitresult = this.f_19853_.m_45547_(new ClipContext(pos, vec32, Block.COLLIDER, Fluid.NONE, this));
      if (hitresult.m_6662_() != Type.MISS) {
         this.m_6532_(hitresult);
      } else {
         for (Entity entity : this.f_19853_.m_6249_(this, this.m_20191_().m_82400_(0.25), this::m_5603_)) {
            this.m_6532_(new EntityHitResult(entity, this.m_20191_().m_82399_().m_82549_(entity.m_20191_().m_82399_()).m_82490_(0.5)));
         }
      }
   }

   public boolean canHitVictim(Entity entity) {
      Integer timestamp = this.victims.get(entity.m_20148_());
      return timestamp == null || entity.f_19797_ - timestamp >= 10;
   }

   @Override
   protected void m_5790_(EntityHitResult pResult) {
      super.m_5790_(pResult);
      Entity target = pResult.m_82443_();
      if (target instanceof LivingEntity livingEntity) {
         DamageSources.ignoreNextKnockback(livingEntity);
      }

      DamageSources.applyDamage(target, this.getDamage(), ((AbstractSpell)SpellRegistry.BALL_LIGHTNING_SPELL.get()).getDamageSource(this, this.m_19749_()));
      this.victims.put(target.m_20148_(), target.f_19797_);
   }

   protected void m_8060_(BlockHitResult pResult) {
      super.m_8060_(pResult);
      switch (pResult.m_82434_()) {
         case UP:
         case DOWN:
            this.m_20256_(this.m_20184_().m_82542_(1.0, this.m_20068_() ? -1.0 : -0.8F, 1.0));
            break;
         case EAST:
         case WEST:
            this.m_20256_(this.m_20184_().m_82542_(-1.0, 1.0, 1.0));
            break;
         case NORTH:
         case SOUTH:
            this.m_20256_(this.m_20184_().m_82542_(1.0, 1.0, -1.0));
      }

      if (++this.bounces >= 6) {
         this.m_146870_();
      }
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.CHAIN_LIGHTNING_CHAIN);
   }
}
