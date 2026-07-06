package io.redspace.ironsspellbooks.entity.spells.dragon_breath;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DragonBreathProjectile extends AbstractConeProjectile {
   public DragonBreathProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public DragonBreathProjectile(Level level, LivingEntity entity) {
      super((EntityType<? extends AbstractConeProjectile>)EntityRegistry.DRAGON_BREATH_PROJECTILE.get(), level, entity);
   }

   @Override
   public void spawnParticles() {
      Entity owner = this.m_19749_();
      if (this.m_9236_().f_46443_ && owner != null) {
         Vec3 rotation = owner.m_20154_().m_82541_();
         Vec3 pos = owner.m_20182_().m_82549_(rotation.m_82490_(1.6));
         double x = pos.f_82479_;
         double y = pos.f_82480_ + owner.m_20192_() * 0.9F;
         double z = pos.f_82481_;
         double speed = this.f_19796_.m_188500_() * 0.35 + 0.25;

         for (int i = 0; i < 12; i++) {
            double offset = 0.15;
            double ox = Math.random() * 2.0 * offset - offset;
            double oy = Math.random() * 2.0 * offset - offset;
            double oz = Math.random() * 2.0 * offset - offset;
            double angularness = 0.3;
            Vec3 randomVec = new Vec3(
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness
               )
               .m_82541_();
            Vec3 result = rotation.m_82490_(3.0).m_82549_(randomVec).m_82541_().m_82490_(speed);
            this.m_9236_().m_7106_(ParticleTypes.f_123799_, x + ox, y + oy, z + oz, result.f_82479_, result.f_82480_, result.f_82481_);
         }
      }
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.m_82443_();
      if (DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.DRAGON_BREATH_SPELL.get()).getDamageSource(this, this.m_19749_()))
         && this.f_19796_.m_188501_() < 0.3F) {
         this.createDragonBreathPuddle(entity.m_20182_());
      }
   }

   private void createDragonBreathPuddle(Vec3 pos) {
      DragonBreathPool pool = new DragonBreathPool(this.m_9236_());
      pool.m_5602_(this.m_19749_());
      pool.setDamage(this.damage);
      pool.m_20219_(pos);
      this.m_9236_().m_7967_(pool);
   }
}
