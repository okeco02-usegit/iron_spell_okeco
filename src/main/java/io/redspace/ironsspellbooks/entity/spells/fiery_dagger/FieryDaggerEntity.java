package io.redspace.ironsspellbooks.entity.spells.fiery_dagger;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.NBT;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FieryDaggerEntity extends AbstractMagicProjectile implements IEntityAdditionalSpawnData, GeoAnimatable {
   public int delay;
   @Nullable
   public Vec3 ownerTrack = null;
   @Nullable
   private UUID targetEntity = null;
   @Nullable
   private Entity cachedTarget = null;
   int age;
   boolean isGrounded;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   @Override
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public FieryDaggerEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
   }

   public FieryDaggerEntity(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.FIERY_DAGGER_PROJECTILE.get(), level);
   }

   public void setTarget(Entity target) {
      this.cachedTarget = target;
      this.targetEntity = target.m_20148_();
   }

   public boolean isTrackingOwner() {
      return this.ownerTrack != null;
   }

   public boolean hasTarget() {
      return this.targetEntity != null;
   }

   public boolean isSpawnDagger() {
      return this.explosionRadius > 0.0F;
   }

   private void createFireField() {
      FireField fireField = new FireField(this.f_19853_);
      fireField.m_5602_(
         this.f_19853_
            .m_45963_(
               FireBossEntity.class,
               TargetingConditions.m_148353_().m_148355_().m_26893_(),
               null,
               this.m_20185_(),
               this.m_20186_(),
               this.m_20189_(),
               this.m_20191_().m_82400_(32.0)
            )
      );
      fireField.m_146884_(Utils.moveToRelativeGroundLevel(this.f_19853_, this.m_20182_(), 3));
      fireField.setRadius(this.explosionRadius + 1.0F);
      fireField.setCircular();
      fireField.setDamage(this.getDamage() * 0.5F);
      fireField.setDuration(300);
      fireField.setDelay(this.delay + 25);
      fireField.setRadiusPerTick(-fireField.getRadius() / fireField.getDuration());
      this.f_19853_.m_7967_(fireField);
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      super.m_5790_(entityHitResult);
      entityHitResult.m_82443_()
         .m_6469_(new DamageSource(DamageSources.getHolderFromResource(this, ISSDamageTypes.FIRE_MAGIC), this, this.m_19749_()), this.getDamage());
      entityHitResult.m_82443_().f_19802_ = 0;
   }

   @Override
   protected void m_6532_(HitResult hitresult) {
      super.m_6532_(hitresult);
      if (this.isSpawnDagger() && this.f_19853_ instanceof ServerLevel) {
         this.createDaggerZone(Utils.moveToRelativeGroundLevel(this.f_19853_, hitresult.m_82450_(), 3));
      }

      this.discardHelper(hitresult);
   }

   public void createDaggerZone(Vec3 center) {
      MagicManager.spawnParticles(
         this.f_19853_,
         new BlastwaveParticleOptions(new Vector3f(1.0F, 0.6F, 0.3F), this.explosionRadius + 1.0F),
         center.f_82479_,
         center.f_82480_ + 0.15,
         center.f_82481_,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         false
      );
      this.m_5496_((SoundEvent)SoundRegistry.FIRE_CAST.get(), 2.0F, Utils.random.m_216332_(80, 110) * 0.01F);
      float spawnRadius = this.explosionRadius;
      float density = 1.0F;
      int rings = (int)(spawnRadius * density);
      float ringSpacing = 1.0F / density;

      for (int i = 1; i < rings; i++) {
         float ringRadius = ringSpacing * i;
         int daggerCount = (int)(ringRadius * (float) (Math.PI * 2));
         float angle = 360.0F / daggerCount * (float) (Math.PI / 180.0);

         for (int j = 0; j < daggerCount; j++) {
            Vec3 jitter = Utils.getRandomVec3(ringSpacing * 0.4F);
            Vec3 pos = Utils.moveToRelativeGroundLevel(
               this.f_19853_, center.m_82520_(ringRadius * Mth.m_14031_(angle * j), 0.0, ringRadius * Mth.m_14089_(angle * j)).m_82549_(jitter), 8
            );
            FieryDaggerEntity dagger = new FieryDaggerEntity(this.f_19853_);
            dagger.m_5602_(this.m_19749_());
            dagger.setDamage(this.getDamage());
            dagger.delay = this.delay + Utils.random.m_188503_(20);
            dagger.m_20334_(0.0, this.getSpeed(), 0.0);
            dagger.deltaMovementOld = dagger.m_20184_();
            dagger.m_20219_(pos);
            dagger.isGrounded = true;
            this.f_19853_.m_7967_(dagger);
         }
      }

      this.createFireField();
   }

   @Override
   public void m_8119_() {
      if (!this.isSpawnDagger() && this.age++ < this.delay) {
         Entity owner = this.m_19749_();
         float strength = 0.5F;
         if (owner != null && this.isTrackingOwner()) {
            Vec3 ownerMotion = owner.m_20182_().m_82492_(owner.f_19790_, owner.f_19791_, owner.f_19792_);
            this.m_146884_(this.m_20182_().m_82549_(ownerMotion));
         }

         Entity target = this.getTargetEntity();
         if (target != null) {
            Vec3 targetPos = target.m_20191_().m_82399_();
            Vec3 targetMotion = targetPos.m_82546_(this.m_20182_()).m_82541_().m_82490_(this.getSpeed());
            Vec3 currentMotion = this.m_20184_();
            this.deltaMovementOld = currentMotion;
            this.m_20256_(currentMotion.m_82549_(targetMotion.m_82546_(currentMotion).m_82490_(strength)));
            if (this.f_19797_ == 1) {
               this.deltaMovementOld = this.m_20184_();
            }
         }

         if (this.age == this.delay) {
            if (this.isGrounded) {
               if (Utils.random.m_188501_() < 0.25F) {
                  this.m_5496_((SoundEvent)SoundRegistry.FIERY_DAGGER_THROW.get(), 0.75F, Utils.random.m_216332_(90, 110) * 0.01F);
               }
            } else {
               this.m_5496_((SoundEvent)SoundRegistry.FIERY_DAGGER_THROW.get(), 2.0F, Utils.random.m_216332_(90, 110) * 0.01F);
            }

            List<Entity> hits = this.f_19853_.m_6249_(this, this.m_20191_().m_82400_(0.4F), this::m_5603_);
            EntityHitResult hitResult = hits.isEmpty() ? null : new EntityHitResult(hits.get(0));
            if (hitResult != null) {
               this.m_6532_(hitResult);
            }
         }

         if (this.f_19853_.f_46443_) {
            this.f_19853_.m_7106_(ParticleHelper.EMBERS, this.m_20185_(), this.m_20186_() + this.m_20206_() * 0.5F, this.m_20189_(), 0.0, 0.0, 0.0);
         }
      } else {
         super.m_8119_();
      }
   }

   @Override
   protected boolean m_5603_(Entity pTarget) {
      return !this.isSpawnDagger() && super.m_5603_(pTarget);
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123756_, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   @Override
   public void trailParticles() {
      float yHeading = -((float)(Mth.m_14136_(this.m_20184_().f_82481_, this.m_20184_().f_82479_) * 180.0F / (float)Math.PI) + 90.0F);
      float radius = 0.25F;
      int steps = 2;
      Vec3 vec = this.m_20184_();
      double x2 = this.m_20185_();
      double x1 = x2 - vec.f_82479_;
      double y2 = this.m_20186_();
      double y1 = y2 - vec.f_82480_;
      double z2 = this.m_20189_();
      double z1 = z2 - vec.f_82481_;

      for (int j = 0; j < steps; j++) {
         float offset = 1.0F / steps * j;
         double radians = (this.f_19797_ + offset) / 7.5F * 360.0F * (float) (Math.PI / 180.0);
         Vec3 swirl = new Vec3(Math.cos(radians) * radius, Math.sin(radians) * radius, 0.0).m_82524_(yHeading * (float) (Math.PI / 180.0));
         double x = Mth.m_14139_(offset, x1, x2) + swirl.f_82479_;
         double y = Mth.m_14139_(offset, y1, y2) + swirl.f_82480_ + this.m_20206_() / 2.0F;
         double z = Mth.m_14139_(offset, z1, z2) + swirl.f_82481_;
         Vec3 jitter = Vec3.f_82478_;
         this.f_19853_.m_7106_(ParticleHelper.EMBERS, x, y, z, jitter.f_82479_, jitter.f_82480_, jitter.f_82481_);
      }
   }

   @Override
   public float getSpeed() {
      return 1.25F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return this.isGrounded ? Optional.empty() : Optional.of(SoundRegistry.FIRE_IMPACT);
   }

   public Entity getTargetEntity() {
      if (this.cachedTarget != null && this.cachedTarget.m_6084_()) {
         return this.cachedTarget;
      }

      if (this.targetEntity != null && this.f_19853_ instanceof ServerLevel serverLevel) {
         this.cachedTarget = serverLevel.m_8791_(this.targetEntity);
         if (this.cachedTarget == null) {
            this.targetEntity = null;
         }

         return this.cachedTarget;
      } else {
         return null;
      }
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("delay", this.delay);
      if (this.ownerTrack != null) {
         tag.m_128365_("ownerTrack", NBT.writeVec3Pos(this.ownerTrack));
      }

      if (this.targetEntity != null) {
         tag.m_128362_("target", this.targetEntity);
      }

      tag.m_128405_("Age", this.age);
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.delay = tag.m_128451_("delay");
      if (tag.m_128403_("ownerTrack")) {
         this.ownerTrack = NBT.readVec3(tag.m_128469_("ownerTrack"));
      }

      if (tag.m_128403_("target")) {
         this.targetEntity = tag.m_128342_("target");
      }

      this.age = tag.m_128451_("Age");
   }

   @Override
   public void writeSpawnData(FriendlyByteBuf buffer) {
      buffer.writeInt(this.delay);
      buffer.writeFloat(this.explosionRadius);
      buffer.writeBoolean(this.isGrounded);
      boolean tracking = this.ownerTrack != null;
      buffer.writeBoolean(tracking);
      if (tracking) {
         buffer.writeDouble(this.ownerTrack.f_82479_);
         buffer.writeDouble(this.ownerTrack.f_82480_);
         buffer.writeDouble(this.ownerTrack.f_82481_);
      }

      boolean target = this.cachedTarget != null;
      buffer.writeBoolean(target);
      if (target) {
         buffer.writeInt(this.cachedTarget.m_19879_());
      }
   }

   @Override
   public void readSpawnData(FriendlyByteBuf buffer) {
      this.delay = buffer.readInt();
      this.explosionRadius = buffer.readFloat();
      this.isGrounded = buffer.readBoolean();
      if (buffer.readBoolean()) {
         this.ownerTrack = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      }

      if (buffer.readBoolean()) {
         this.cachedTarget = this.f_19853_.m_6815_(buffer.readInt());
         if (this.cachedTarget != null) {
            this.targetEntity = this.cachedTarget.m_20148_();
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public double getTick(Object object) {
      return this.f_19797_;
   }
}
