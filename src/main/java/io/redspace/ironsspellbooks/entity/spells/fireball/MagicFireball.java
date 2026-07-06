package io.redspace.ironsspellbooks.entity.spells.fireball;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ExplosionEvent.Start;

public class MagicFireball extends AbstractMagicProjectile {
   public MagicFireball(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
   }

   public MagicFireball(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.MAGIC_FIREBALL.get(), pLevel);
      this.m_5602_(pShooter);
   }

   @Override
   public void trailParticles() {
      Vec3 vec3 = this.m_20184_();
      double d0 = this.m_20185_() - vec3.f_82479_;
      double d1 = this.m_20186_() - vec3.f_82480_;
      double d2 = this.m_20189_() - vec3.f_82481_;
      int count = Mth.m_14045_((int)(vec3.m_82556_() * 2.0), 1, 4);

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(this.m_20206_() * 0.2F);
         float f = (float)i / count;
         double x = Mth.m_14139_(f, d0, this.m_20185_() + vec3.f_82479_);
         double y = Mth.m_14139_(f, d1, this.m_20186_() + vec3.f_82480_);
         double z = Mth.m_14139_(f, d2, this.m_20189_() + vec3.f_82481_);
         this.f_19853_
            .m_6493_(ParticleHelper.FIERY_SMOKE, true, x - random.f_82479_, y + this.m_20206_() * 0.5F - random.f_82480_, z - random.f_82481_, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return 1.15F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(() -> SoundEvents.f_11913_);
   }

   @Override
   protected void m_6532_(HitResult hitResult) {
      if (!this.f_19853_.f_46443_) {
         this.impactParticles(this.f_19790_, this.f_19791_, this.f_19792_);
         float explosionRadius = this.getExplosionRadius();
         float explosionRadiusSqr = explosionRadius * explosionRadius;
         List<Entity> entities = this.f_19853_.m_45933_(this, this.m_20191_().m_82400_(explosionRadius));
         Vec3 losPoint = Utils.raycastForBlock(this.f_19853_, this.m_20182_(), this.m_20182_().m_82520_(0.0, 2.0, 0.0), Fluid.NONE).m_82450_();

         for (Entity entity : entities) {
            double distanceSqr = entity.m_20238_(hitResult.m_82450_());
            if (distanceSqr < explosionRadiusSqr && this.m_5603_(entity) && Utils.hasLineOfSight(this.f_19853_, losPoint, entity.m_20191_().m_82399_(), true)) {
               double p = 1.0 - distanceSqr / explosionRadiusSqr;
               float damage = (float)(this.damage * p);
               DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get()).getDamageSource(this, this.m_19749_()));
            }
         }

         if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
            Explosion explosion = new Explosion(
               this.f_19853_,
               null,
               ((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get()).getDamageSource(this, this.m_19749_()),
               null,
               this.m_20185_(),
               this.m_20186_(),
               this.m_20189_(),
               this.getExplosionRadius() / 2.0F,
               true,
               BlockInteraction.DESTROY
            );
            if (!MinecraftForge.EVENT_BUS.post(new Start(this.f_19853_, explosion))) {
               explosion.m_46061_();
               explosion.m_46075_(false);
            }
         }

         PacketDistributor.sendToPlayersTrackingEntity(
            this, new FieryExplosionParticlesPacket(hitResult.m_82450_().m_82546_(this.m_20184_().m_82490_(0.5)), this.getExplosionRadius())
         );
         this.m_5496_(SoundEvents.f_11913_, 4.0F, (1.0F + (this.f_19853_.f_46441_.m_188501_() - this.f_19853_.f_46441_.m_188501_()) * 0.2F) * 0.7F);
         this.discardHelper(hitResult);
      }
   }
}
