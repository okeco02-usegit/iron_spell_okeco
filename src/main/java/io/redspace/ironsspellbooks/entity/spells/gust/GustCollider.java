package io.redspace.ironsspellbooks.entity.spells.gust;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GustCollider extends AbstractConeProjectile {
   public float strength;
   public float range;
   public int amplifier;

   public GustCollider(Level level, LivingEntity owner) {
      this((EntityType<GustCollider>)EntityRegistry.GUST_COLLIDER.get(), level);
      this.m_5602_(owner);
      IronsSpellbooks.LOGGER.debug("GustCollider<init>: {} {}", owner.m_146908_(), owner.m_146909_());
      this.m_19915_(owner.m_146908_(), owner.m_146909_());
   }

   public GustCollider(EntityType<GustCollider> gustColliderEntityType, Level level) {
      super(gustColliderEntityType, level);
   }

   @Override
   public void spawnParticles() {
      if (this.f_19853_.f_46443_ && this.f_19797_ <= 2) {
         Vec3 rotation = this.m_20154_().m_82541_();
         Vec3 pos = this.m_20182_().m_82549_(rotation.m_82490_(1.6));
         double x = pos.f_82479_;
         double y = pos.f_82480_;
         double z = pos.f_82481_;
         double speed = this.f_19796_.m_188500_() * 0.4 + 0.45;

         for (int i = 0; i < 5; i++) {
            double offset = 0.25;
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
            this.f_19853_.m_7106_(ParticleTypes.f_123759_, x + ox, y + oy, z + oz, result.f_82479_, result.f_82480_, result.f_82481_);
         }
      }
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      Entity entity = this.m_19749_();
      if (entity != null
         && entityHitResult.m_82443_() instanceof LivingEntity target
         && target.m_20280_(entity) < this.range * this.range
         && !DamageSources.isFriendlyFireBetween(entity, target)) {
         Vec3 knockback = new Vec3(entity.m_20185_() - target.m_20185_(), entity.m_20186_() - target.m_20186_(), entity.m_20189_() - target.m_20189_())
            .m_82541_()
            .m_82490_(-this.strength);
         target.m_20256_(target.m_20184_().m_82549_(knockback));
         target.f_19864_ = true;
         target.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.AIRBORNE.get(), 60, this.amplifier));
      }
   }

   @Override
   public void m_8119_() {
      double x = this.m_20185_();
      double y = this.m_20186_();
      double z = this.m_20189_();
      if (this.f_19797_ > 8) {
         this.m_146870_();
      } else {
         super.m_8119_();
      }

      this.m_20343_(x, y, z);
   }

   @Nullable
   public Entity m_19749_() {
      return this.f_19797_ >= 1 ? null : super.m_19749_();
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
   }
}
