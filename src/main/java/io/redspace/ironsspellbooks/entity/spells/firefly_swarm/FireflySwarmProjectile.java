package io.redspace.ironsspellbooks.entity.spells.firefly_swarm;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireflySwarmProjectile extends PathfinderMob implements AntiMagicSusceptible {
   static final int maxLife = 200;
   public static final float radius = 2.0F;
   UUID targetUUID;
   Entity cachedTarget;
   UUID ownerUUID;
   Entity cachedOwner;
   Entity nextTarget;
   float damage;

   public FireflySwarmProjectile(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21342_ = new FlyingMoveControl(this, 15, true);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public FireflySwarmProjectile(Level level, @Nullable Entity owner, @Nullable Entity target, float damage) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.FIREFLY_SWARM.get(), level);
      this.setOwner(owner);
      this.setTarget(target);
      this.damage = damage;
   }

   public boolean m_6087_() {
      return false;
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new FlyingPathNavigation(this, pLevel);
   }

   public void m_8119_() {
      if (this.f_19853_.f_46443_) {
         for (int i = 0; i < 2; i++) {
            Vec3 motion = Utils.getRandomVec3(0.05F).m_82549_(this.m_20184_());
            Vec3 spawn = Utils.getRandomVec3(0.25);
            this.f_19853_
               .m_7106_(
                  ParticleHelper.FIREFLY,
                  this.m_20185_() + spawn.f_82479_,
                  this.m_20186_() + this.m_20206_() * 0.5F + spawn.f_82481_,
                  this.m_20189_() + spawn.f_82481_,
                  motion.f_82479_,
                  motion.f_82480_,
                  motion.f_82481_
               );
         }
      }

      super.m_8119_();
      if (this.f_19797_ > 200) {
         this.m_146870_();
      }
   }

   protected void m_8024_() {
      super.m_8024_();
      LivingEntity target = this.m_5448_();
      if (target != null) {
         this.f_21344_.m_5624_(target, 7.0);
      }

      if (this.f_19797_ % 8 == 0) {
         if (this.f_19853_.m_186437_(this, this.m_20191_().m_82386_(0.0, -1.0, 0.0))) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, 0.02, 0.0));
         } else {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.008, 0.0));
         }
      }

      if (!this.f_21342_.m_24995_()) {
         this.m_20256_(this.m_20184_().m_82542_(0.95F, 1.0, 0.95F));
      }

      if (this.f_19797_ % 7 == 0) {
         float fade = 1.0F - Mth.m_14036_((this.f_19797_ - 200 + 40) / 200.0F, 0.0F, 1.0F);
         this.m_5496_((SoundEvent)SoundRegistry.FIREFLY_SWARM_IDLE.get(), 0.25F * fade, 0.95F + Utils.random.m_188501_() * 0.1F);
      }

      if (this.f_19797_ % 15 == 0) {
         float inflate = 2.0F - this.m_20205_() * 0.5F;
         this.f_19853_
            .m_6249_(this, this.m_20191_().m_82400_(inflate), this::canHitEntity)
            .forEach(
               entity -> {
                  if (this.canHitEntity(entity)) {
                     boolean hit = DamageSources.applyDamage(
                        entity, this.damage, ((AbstractSpell)SpellRegistry.FIREFLY_SWARM_SPELL.get()).getDamageSource(this, this.getOwner())
                     );
                     if (hit) {
                        this.m_5496_((SoundEvent)SoundRegistry.FIREFLY_SWARM_ATTACK.get(), 0.75F, 0.9F + Utils.random.m_188501_() * 0.2F);
                        if (target == null) {
                           this.setTarget(entity);
                        } else if (target != entity) {
                           this.nextTarget = entity;
                        }
                     }
                  }
               }
            );
         if (this.m_5448_() == null || this.m_5448_().m_21224_()) {
            this.setTarget(this.nextTarget);
            if (this.nextTarget != null && this.nextTarget.m_213877_()) {
               this.nextTarget = null;
            }
         }
      }
   }

   protected boolean canHitEntity(Entity target) {
      if (!target.m_5833_() && target.m_6084_() && target.m_6087_()) {
         Entity owner = this.getOwner();
         return owner != target && !DamageSources.isFriendlyFireBetween(owner, target);
      } else {
         return false;
      }
   }

   public void setOwner(@Nullable Entity owner) {
      if (owner != null) {
         this.ownerUUID = owner.m_20148_();
         this.cachedOwner = owner;
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.m_213877_()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.f_19853_ instanceof ServerLevel) {
         this.cachedOwner = ((ServerLevel)this.f_19853_).m_8791_(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   @org.jetbrains.annotations.Nullable
   public LivingEntity m_5448_() {
      return this.getFireflyTarget() instanceof LivingEntity livingEntity ? livingEntity : null;
   }

   public void setTarget(@Nullable Entity target) {
      if (target != null) {
         this.targetUUID = target.m_20148_();
         this.cachedTarget = target;
      }
   }

   @Nullable
   public Entity getFireflyTarget() {
      if (this.cachedTarget != null && !this.cachedTarget.m_213877_()) {
         return this.cachedTarget;
      } else if (this.targetUUID != null && this.f_19853_ instanceof ServerLevel) {
         this.cachedTarget = ((ServerLevel)this.f_19853_).m_8791_(this.targetUUID);
         return this.cachedTarget;
      } else {
         return null;
      }
   }

   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      if (this.targetUUID != null) {
         pCompound.m_128362_("Target", this.targetUUID);
      }

      if (this.ownerUUID != null) {
         pCompound.m_128362_("Owner", this.ownerUUID);
      }

      pCompound.m_128405_("Age", this.f_19797_);
      pCompound.m_128350_("Damage", this.damage);
   }

   public void m_7378_(CompoundTag pCompound) {
      if (pCompound.m_128403_("Target")) {
         this.targetUUID = pCompound.m_128342_("Target");
      }

      if (pCompound.m_128403_("Owner")) {
         this.ownerUUID = pCompound.m_128342_("Owner");
      }

      this.f_19797_ = pCompound.m_128451_("Age");
      this.damage = pCompound.m_128457_("Damage");
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.m_146870_();
   }
}
