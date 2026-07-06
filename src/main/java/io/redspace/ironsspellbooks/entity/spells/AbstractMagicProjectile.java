package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractMagicProjectile extends Projectile implements AntiMagicSusceptible, IEntityAdditionalSpawnData {
   private static final EntityDataAccessor<Boolean> DATA_CURSOR_HOMING = SynchedEntityData.m_135353_(
      AbstractMagicProjectile.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Boolean> DATA_RICOCHET = SynchedEntityData.m_135353_(AbstractMagicProjectile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> DATA_PIERCE_LEVEL = SynchedEntityData.m_135353_(
      AbstractMagicProjectile.class, EntityDataSerializers.f_135028_
   );
   protected static final int EXPIRE_TIME = 300;
   protected float damage;
   protected float explosionRadius;
   @Nullable
   protected Entity cachedHomingTarget;
   @Nullable
   protected UUID homingTargetUUID;
   public Vec3 deltaMovementOld = Vec3.f_82478_;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public abstract void trailParticles();

   public abstract void impactParticles(double var1, double var3, double var5);

   public abstract float getSpeed();

   public abstract Optional<Supplier<SoundEvent>> getImpactSound();

   public AbstractMagicProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public void shoot(Vec3 rotation) {
      this.m_20256_(rotation.m_82490_(this.getSpeed()));
   }

   protected boolean m_5603_(Entity pTarget) {
      Entity owner = this.m_19749_();
      return super.m_5603_(pTarget) && pTarget != owner && (owner == null || !owner.m_7307_(pTarget));
   }

   public void m_6043_() {
      if (this.f_19853_ instanceof ServerLevel serverLevel && !serverLevel.m_7726_().f_8325_.m_143145_().m_183913_(this.m_146902_().m_45588_())) {
         this.m_146870_();
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ == 1) {
         this.deltaMovementOld = this.m_20184_();
      }

      if (this.f_19797_ > 300) {
         this.m_146870_();
      } else {
         if (this.f_19853_.f_46443_) {
            this.trailParticles();
         }

         this.handleEntityHoming();
         this.handleCursorHoming();
         this.handleHitDetection();
         this.travel();
         this.deltaMovementOld = this.m_20184_();
         this.rotateWithMotion();
      }
   }

   protected void rotateWithMotion() {
      Vec3 motion = this.m_20184_();
      double speed = motion.m_165924_();
      this.m_146922_((float)(Mth.m_14136_(motion.f_82479_, motion.f_82481_) * 180.0F / (float)Math.PI));
      this.m_146926_((float)(Mth.m_14136_(motion.f_82480_, speed) * 180.0F / (float)Math.PI));
      if (this.f_19860_ == 0.0F && this.f_19859_ == 0.0F) {
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
      } else {
         this.f_19860_ = enforceRotationContinuity(this.f_19860_, this.m_146909_());
         this.f_19859_ = enforceRotationContinuity(this.f_19859_, this.m_146908_());
      }
   }

   protected static float enforceRotationContinuity(float currentRotation, float targetRotation) {
      while (targetRotation - currentRotation < -180.0F) {
         currentRotation -= 360.0F;
      }

      while (targetRotation - currentRotation >= 180.0F) {
         currentRotation += 360.0F;
      }

      return currentRotation;
   }

   public void handleHitDetection() {
      HitResult hitresult = ProjectileUtil.m_278158_(this, this::m_5603_);
      if (hitresult instanceof EntityHitResult entityHitResult) {
         hitresult = new EntityHitResult(
            entityHitResult.m_82443_(),
            entityHitResult.m_82443_().m_20191_().m_82371_(this.m_20182_(), this.m_20182_().m_82549_(this.m_20184_())).orElse(this.m_20182_())
         );
      }

      if (hitresult.m_6662_() != Type.MISS && !MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(this, hitresult))) {
         this.m_6532_(hitresult);
      }
   }

   public void travel() {
      this.m_146884_(this.m_20182_().m_82549_(this.m_20184_()));
      Vec3 motion = this.m_20184_();
      float xRot = -((float)(Mth.m_14136_(motion.m_165924_(), motion.f_82480_) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.m_14136_(motion.f_82481_, motion.f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      this.m_146926_(Mth.m_14177_(xRot));
      this.m_146922_(Mth.m_14177_(yRot));
      if (!this.m_20068_()) {
         Vec3 vec34 = this.m_20184_();
         this.m_20334_(vec34.f_82479_, vec34.f_82480_ - this.getDefaultGravity(), vec34.f_82481_);
      }
   }

   public void stopEntityHoming() {
      this.homingTargetUUID = null;
      this.cachedHomingTarget = null;
   }

   protected void handleEntityHoming() {
      if (this.homingTargetUUID != null) {
         Entity target = this.getHomingTarget();
         if (target == null) {
            this.homingTargetUUID = null;
         } else if (target.m_213877_()) {
            this.stopEntityHoming();
         } else {
            Vec3 wantedPos = target.m_20191_().m_82399_().m_82549_(target.m_20184_());
            Vec3 newMotion = this.homeTowards(wantedPos, 0.22F);
            if (newMotion.m_82526_(wantedPos.m_82546_(this.m_20182_())) < -0.25 && this.f_19797_ > 10) {
               this.stopEntityHoming();
            }
         }
      }
   }

   protected void handleCursorHoming() {
      boolean cursorHoming = this.isCursorHoming();
      if (cursorHoming) {
         float maxRange = 48.0F;
         Entity owner = this.m_19749_();
         if (owner != null && !(this.m_20182_().m_82557_(owner.m_20182_()) > maxRange * maxRange)) {
            Vec3 start = owner.m_146892_();
            Vec3 end = start.m_82549_(owner.m_20156_().m_82490_(maxRange));
            HitResult hitresult = Utils.raycastForEntity(
               this.f_19853_, owner, start, end, true, 0.5F, entity -> Utils.canHitWithRaycast(entity) && !DamageSources.isFriendlyFireBetween(entity, owner)
            );
            Vec3 target = hitresult instanceof EntityHitResult entityHit ? entityHit.m_82443_().m_20191_().m_82399_() : hitresult.m_82450_();
            this.homeTowards(target, 0.18F);
         } else {
            this.setCursorHoming(false);
         }
      }
   }

   protected Vec3 homeTowards(Vec3 target, float strength) {
      double speed = this.m_20184_().m_82553_();
      Vec3 currentMotion = this.m_20184_().m_82541_();
      Vec3 wantedMotion = target.m_82546_(this.m_20182_()).m_82541_();
      Vec3 newMotion = Utils.slerp(strength, currentMotion, wantedMotion).m_82490_(speed);
      this.m_20256_(newMotion);
      return newMotion;
   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      if (this.canRicochet()) {
         this.doRicochet(hitresult);
      }

      if (!this.f_19853_.f_46443_) {
         Vec3 vec = hitresult.m_82450_();
         this.impactParticles(vec.f_82479_, vec.f_82480_, vec.f_82481_);
         Optional<Supplier<SoundEvent>> soundOpt = this.getImpactSound();
         if (soundOpt.isPresent()) {
            Object sound = soundOpt.get();
            if (sound instanceof Supplier<?> goodsound) {
               this.doImpactSound((Supplier<SoundEvent>)goodsound);
            } else if (sound instanceof SoundEvent badsound) {
               IronsSpellbooks.LOGGER
                  .warn(
                     "Warning: Projectile {} has not implemented forward-compatible AbstractMagicProjectile#getImpactSound()",
                     this.getClass().getCanonicalName()
                  );
               this.doImpactSound(() -> badsound);
            }
         }
      }
   }

   public boolean m_142391_() {
      return super.m_142391_() && !Objects.equals(this.m_146911_(), RemovalReason.UNLOADED_TO_CHUNK);
   }

   protected void doImpactSound(Supplier<SoundEvent> sound) {
      this.f_19853_
         .m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), sound.get(), SoundSource.NEUTRAL, 2.0F, 0.9F + Utils.random.m_188501_() * 0.2F);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_CURSOR_HOMING, false);
      this.f_19804_.m_135372_(DATA_RICOCHET, false);
      this.f_19804_.m_135372_(DATA_PIERCE_LEVEL, 0);
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.impactParticles(this.m_20185_(), this.m_20186_(), this.m_20189_());
      this.m_146870_();
   }

   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128350_("Damage", this.getDamage());
      if (this.explosionRadius != 0.0F) {
         tag.m_128350_("ExplosionRadius", this.explosionRadius);
      }

      if (this.getPierceLevel() != 0) {
         tag.m_128405_("PierceLevel", this.getPierceLevel());
      }

      if (this.homingTargetUUID != null) {
         tag.m_128362_("homingTarget", this.homingTargetUUID);
      }

      if (this.canRicochet()) {
         tag.m_128379_("ricochet", true);
      }

      tag.m_128405_("Age", this.f_19797_);
   }

   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.damage = tag.m_128457_("Damage");
      if (tag.m_128441_("ExplosionRadius")) {
         this.explosionRadius = tag.m_128457_("ExplosionRadius");
      }

      if (tag.m_128441_("PierceLevel")) {
         this.setPierceLevel(tag.m_128451_("PierceLevel"));
      }

      if (tag.m_128425_("homingTarget", 11)) {
         this.homingTargetUUID = tag.m_128342_("homingTarget");
      }

      if (tag.m_128441_("ricochet")) {
         this.setCanRicochet(tag.m_128471_("ricochet"));
      }

      this.f_19797_ = tag.m_128451_("Age");
   }

   protected void m_5790_(EntityHitResult pResult) {
      super.m_5790_(pResult);
      if (!this.shouldPierceShields() && (pResult.m_82443_() instanceof ShieldPart || pResult.m_82443_() instanceof AbstractShieldEntity)) {
         this.m_8060_(new BlockHitResult(pResult.m_82443_().m_20182_(), Direction.m_122364_(this.m_146908_()), pResult.m_82443_().m_20183_(), false));
      }
   }

   public void discardHelper(HitResult hitresult) {
      if (hitresult.m_6662_() == Type.ENTITY) {
         this.pierceOrDiscard();
      } else {
         this.m_146870_();
      }
   }

   public void pierceOrDiscard() {
      int p = this.getPierceLevel();
      if (p > 0) {
         this.setPierceLevel(p - 1);
      } else if (p == 0) {
         this.m_146870_();
      }
   }

   public void doRicochet(HitResult hitResult) {
      if (hitResult instanceof EntityHitResult entityHitResult) {
         Vec3 deltaMovement = this.m_20184_();
         Vec3 vec = deltaMovement.m_82541_();
         Entity owner = this.m_19749_();
         Entity hit = entityHitResult.m_82443_();
         List<Entity> potentialTargets = this.f_19853_
            .m_6249_(
               this,
               this.m_20191_().m_82400_(3.0).m_82369_(deltaMovement.m_82490_(12.0)),
               entity -> entity != hit
                  && (owner == null || !Utils.shouldHealEntity(owner, entity) || entity.getClass() == hit.getClass())
                  && entity.m_20191_().m_82399_().m_82546_(this.m_20182_()).m_82541_().m_82526_(vec) > 0.6
                  && Utils.hasLineOfSight(this.f_19853_, this, entity, false)
            );
         if (potentialTargets.isEmpty()) {
            return;
         }

         Entity target = potentialTargets.get(this.m_19879_() % potentialTargets.size());
         this.m_20256_(target.m_20191_().m_82399_().m_82546_(this.m_20182_()).m_82541_().m_82490_(deltaMovement.m_82553_()));
      }
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   public float getExplosionRadius() {
      return this.explosionRadius;
   }

   public void setExplosionRadius(float explosionRadius) {
      this.explosionRadius = explosionRadius;
   }

   public int getPierceLevel() {
      return (Integer)this.f_19804_.m_135370_(DATA_PIERCE_LEVEL);
   }

   public void setPierceLevel(int pierceLevel) {
      this.f_19804_.m_135381_(DATA_PIERCE_LEVEL, pierceLevel);
   }

   public void setInfinitePiercing() {
      this.setPierceLevel(-1);
   }

   @Nullable
   public Entity getHomingTarget() {
      if (this.cachedHomingTarget != null && !this.cachedHomingTarget.m_213877_()) {
         return this.cachedHomingTarget;
      } else if (this.homingTargetUUID != null && this.f_19853_ instanceof ServerLevel) {
         this.cachedHomingTarget = ((ServerLevel)this.f_19853_).m_8791_(this.homingTargetUUID);
         return this.cachedHomingTarget;
      } else {
         return null;
      }
   }

   public void setHomingTarget(LivingEntity entity) {
      this.homingTargetUUID = entity.m_20148_();
      this.cachedHomingTarget = entity;
      this.setCursorHoming(false);
   }

   public boolean isCursorHoming() {
      return (Boolean)this.f_19804_.m_135370_(DATA_CURSOR_HOMING);
   }

   public void setCursorHoming(boolean cursorHoming) {
      this.f_19804_.m_135381_(DATA_CURSOR_HOMING, cursorHoming);
      if (cursorHoming) {
         this.stopEntityHoming();
      }
   }

   public boolean canRicochet() {
      return (Boolean)this.f_19804_.m_135370_(DATA_RICOCHET);
   }

   public void setCanRicochet(boolean ricochet) {
      this.f_19804_.m_135381_(DATA_RICOCHET, ricochet);
   }

   public boolean m_6060_() {
      return false;
   }

   protected boolean shouldPierceShields() {
      return false;
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      Entity owner = this.m_19749_();
      buffer.writeInt(owner == null ? 0 : owner.m_19879_());
      Entity homingTarget = this.getHomingTarget();
      buffer.writeInt(homingTarget == null ? 0 : homingTarget.m_19879_());
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      Entity owner = this.f_19853_.m_6815_(additionalData.readInt());
      if (owner != null) {
         this.m_5602_(owner);
      }

      Entity homingTarget = this.f_19853_.m_6815_(additionalData.readInt());
      if (homingTarget != null) {
         this.cachedHomingTarget = homingTarget;
         this.homingTargetUUID = homingTarget.m_20148_();
      }
   }
}
