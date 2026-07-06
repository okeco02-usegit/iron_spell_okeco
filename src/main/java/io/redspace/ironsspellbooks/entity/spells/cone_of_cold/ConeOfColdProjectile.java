package io.redspace.ironsspellbooks.entity.spells.cone_of_cold;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ConeOfColdProjectile extends AbstractConeProjectile {
   public ConeOfColdProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public ConeOfColdProjectile(Level level, LivingEntity entity) {
      super((EntityType<? extends AbstractConeProjectile>)EntityRegistry.CONE_OF_COLD_PROJECTILE.get(), level, entity);
   }

   @Override
   public void spawnParticles() {
      Entity owner = this.m_19749_();
      if (this.f_19853_.f_46443_ && owner != null) {
         Vec3 rotation = owner.m_20154_().m_82541_();
         Vec3 pos = owner.m_20182_().m_82549_(rotation.m_82490_(1.5));
         double x = pos.f_82479_;
         double y = pos.f_82480_ + owner.m_20192_() * 0.9F;
         double z = pos.f_82481_;

         for (int i = 0; i < 10; i++) {
            double speed = this.f_19796_.m_188500_() * 0.7 + 0.15;
            double offset = 0.125;
            double ox = Math.random() * 2.0 * offset - offset;
            double oy = Math.random() * 2.0 * offset - offset;
            double oz = Math.random() * 2.0 * offset - offset;
            double angularness = 0.8;
            Vec3 randomVec = new Vec3(
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness
               )
               .m_82541_();
            Vec3 result = rotation.m_82490_(3.0).m_82549_(randomVec).m_82541_().m_82490_(speed);
            this.f_19853_
               .m_7106_(
                  Math.random() > 0.15 ? ParticleHelper.SNOW_DUST : ParticleHelper.SNOWFLAKE,
                  x + ox,
                  y + oy,
                  z + oz,
                  result.f_82479_,
                  result.f_82480_,
                  result.f_82481_
               );
         }
      }
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.m_82443_();
      DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.CONE_OF_COLD_SPELL.get()).getDamageSource(this, this.m_19749_()));
   }
}
