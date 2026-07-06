package io.redspace.ironsspellbooks.entity.spells.fire_breath;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FireBreathProjectile extends AbstractConeProjectile {
   public FireBreathProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public FireBreathProjectile(Level level, LivingEntity entity) {
      super((EntityType<? extends AbstractConeProjectile>)EntityRegistry.FIRE_BREATH_PROJECTILE.get(), level, entity);
   }

   @Override
   public void m_8119_() {
      if (!this.f_19853_.f_46443_ && this.dealDamageActive && (Boolean)ServerConfigs.SPELL_GREIFING.get()) {
         float range = (float) (Math.PI / 12);

         for (int i = 0; i < 3; i++) {
            Vec3 cast = this.m_19749_()
               .m_20154_()
               .m_82541_()
               .m_82496_(Utils.random.m_188501_() * range * 2.0F - range)
               .m_82524_(Utils.random.m_188501_() * range * 2.0F - range);
            HitResult hitResult = this.f_19853_
               .m_45547_(
                  new ClipContext(this.m_19749_().m_146892_(), this.m_19749_().m_146892_().m_82549_(cast.m_82490_(10.0)), Block.COLLIDER, Fluid.NONE, this)
               );
            if (hitResult.m_6662_() == Type.BLOCK) {
               HitResult shieldResult = Utils.raycastForEntityOfClass(
                  this.f_19853_, this, this.m_19749_().m_146892_(), hitResult.m_82450_(), false, AbstractShieldEntity.class
               );
               if (shieldResult.m_6662_() == Type.MISS) {
                  Vec3 pos = hitResult.m_82450_().m_82546_(cast.m_82490_(0.5));
                  BlockPos blockPos = BlockPos.m_274561_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
                  if (this.f_19853_.m_8055_(blockPos).m_60795_()) {
                     this.f_19853_.m_46597_(blockPos, BaseFireBlock.m_49245_(this.f_19853_, blockPos));
                  }
               }
            }
         }
      }

      super.m_8119_();
   }

   @Override
   public void spawnParticles() {
      Entity owner = this.m_19749_();
      if (this.f_19853_.f_46443_ && owner != null) {
         Vec3 rotation = owner.m_20154_().m_82541_();
         Vec3 pos = owner.m_20182_().m_82549_(rotation.m_82490_(1.6));
         double x = pos.f_82479_;
         double y = pos.f_82480_ + owner.m_20192_() * 0.9F;
         double z = pos.f_82481_;
         double speed = this.f_19796_.m_188500_() * 0.35 + 0.35;

         for (int i = 0; i < 10; i++) {
            double offset = 0.15;
            double ox = Math.random() * 2.0 * offset - offset;
            double oy = Math.random() * 2.0 * offset - offset;
            double oz = Math.random() * 2.0 * offset - offset;
            double angularness = 0.5;
            Vec3 randomVec = new Vec3(
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness,
                  Math.random() * 2.0 * angularness - angularness
               )
               .m_82541_();
            Vec3 result = rotation.m_82490_(3.0).m_82549_(randomVec).m_82541_().m_82490_(speed);
            this.f_19853_.m_7106_(ParticleHelper.FIRE_EMITTER, x + ox, y + oy, z + oz, result.f_82479_, result.f_82480_, result.f_82481_);
         }
      }
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.m_82443_();
      DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.FIRE_BREATH_SPELL.get()).getDamageSource(this, this.m_19749_()));
   }
}
