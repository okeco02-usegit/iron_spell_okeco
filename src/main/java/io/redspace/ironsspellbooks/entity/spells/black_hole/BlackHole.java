package io.redspace.ironsspellbooks.entity.spells.black_hole;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.Tags.EntityTypes;

public class BlackHole extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.m_135353_(BlackHole.class, EntityDataSerializers.f_135029_);
   List<Entity> trackingEntities = new ArrayList<>();
   private float damage;
   private int duration = 600;
   private static final int loopSoundDurationInTicks = 40;

   public BlackHole(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public BlackHole(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Projectile>)EntityRegistry.BLACK_HOLE.get(), pLevel);
      this.m_5602_(owner);
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
   }

   public void m_6210_() {
      double d0 = this.m_20185_();
      double d1 = this.m_20186_();
      double d2 = this.m_20189_();
      super.m_6210_();
      this.m_6034_(d0, d1, d2);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_RADIUS, 5.0F);
   }

   public void m_7350_(EntityDataAccessor<?> pKey) {
      if (DATA_RADIUS.equals(pKey)) {
         this.m_6210_();
         if (this.getRadius() < 0.1F) {
            this.m_146870_();
         }
      }

      super.m_7350_(pKey);
   }

   public void setRadius(float pRadius) {
      if (!this.m_9236_().f_46443_) {
         this.m_20088_().m_135381_(DATA_RADIUS, Math.min(pRadius, 48.0F));
      }
   }

   public float getRadius() {
      return (Float)this.m_20088_().m_135370_(DATA_RADIUS);
   }

   protected void m_7380_(CompoundTag pCompound) {
      pCompound.m_128350_("Radius", this.getRadius());
      pCompound.m_128405_("Age", this.f_19797_);
      pCompound.m_128350_("Damage", this.getDamage());
      pCompound.m_128405_("Duration", this.duration);
      super.m_7380_(pCompound);
   }

   protected void m_7378_(CompoundTag pCompound) {
      this.f_19797_ = pCompound.m_128451_("Age");
      this.damage = pCompound.m_128457_("Damage");
      this.duration = pCompound.m_128451_("Duration");
      if (this.damage == 0.0F) {
         this.damage = 1.0F;
      }

      if (pCompound.m_128451_("Radius") > 0) {
         this.setRadius(pCompound.m_128457_("Radius"));
      }

      super.m_7378_(pCompound);
   }

   public void m_8119_() {
      super.m_8119_();
      int update = Math.max((int)(this.getRadius() / 2.0F), 2);
      if (this.f_19797_ % update == 0) {
         this.updateTrackingEntities();
      }

      AABB bb = this.m_20191_();
      float radius = (float)bb.m_82362_();
      boolean hitTick = this.f_19797_ % 10 == 0;
      Vec3 center = bb.m_82399_();

      for (Entity entity : this.trackingEntities) {
         if (entity != this.m_19749_() && !DamageSources.isFriendlyFireBetween(this.m_19749_(), entity) && !entity.m_5833_()) {
            float distance = (float)center.m_82554_(entity.m_20182_());
            if (!(distance > radius)) {
               float f = 1.0F - distance / radius;
               float scale = f * f * f * f * 0.25F;
               float resistance = entity instanceof LivingEntity livingEntity
                  ? Mth.m_14036_(1.0F - (float)livingEntity.m_21133_(Attributes.f_22278_), 0.3F, 1.0F)
                  : 1.0F;
               float bossResistance = entity.m_6095_().m_204039_(EntityTypes.BOSSES) ? 0.5F : 1.0F;
               Vec3 diff = center.m_82546_(entity.m_20182_()).m_82490_(scale * resistance * bossResistance);
               entity.m_5997_(diff.f_82479_, diff.f_82480_, diff.f_82481_);
               double dmgRadius = Math.min(2.0, radius / 5.0);
               if (hitTick && distance < dmgRadius * dmgRadius && this.m_5603_(entity)) {
                  DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.BLACK_HOLE_SPELL.get()).getDamageSource(this, this.m_19749_()));
               }

               entity.f_19789_ = 0.0F;
            }
         }
      }

      if (!this.f_19853_.f_46443_ && (Boolean)ServerConfigs.SPELL_GREIFING.get()) {
         int tries = 0;

         BlockHitResult blockHit;
         do {
            Vec3 dir = Utils.getRandomVec3(1.0).m_82541_();
            Vec3 pick = dir.m_82490_(radius * 1.25);
            blockHit = Utils.raycastForBlock(this.f_19853_, center, center.m_82549_(pick), Fluid.NONE);
            if (blockHit.m_6662_() != Type.MISS) {
               BlockPos blockpos = blockHit.m_82425_();
               if (this.f_19853_.m_7702_(blockpos) == null) {
                  BlockState state = this.f_19853_.m_8055_(blockpos);
                  this.f_19853_.m_46597_(blockpos, Blocks.f_50016_.m_49966_());
                  Vec3 spawn = blockpos.m_252807_().m_82546_(dir.m_82490_(1.5));
                  FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(this.f_19853_, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, state);
                  fallingBlockEntity.m_20256_(dir.m_82490_(-0.1));
                  this.f_19853_.m_7967_(fallingBlockEntity);
               }
            }
         } while (blockHit.m_6662_() == Type.MISS && tries++ < 3);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ > this.duration) {
            this.m_146870_();
            this.m_5496_((SoundEvent)SoundRegistry.BLACK_HOLE_CAST.get(), this.getRadius() / 2.0F, 1.0F);
            MagicManager.spawnParticles(
               this.m_9236_(),
               ParticleHelper.UNSTABLE_ENDER,
               this.m_20185_(),
               this.m_20186_() + this.getRadius(),
               this.m_20189_(),
               200,
               1.0,
               1.0,
               1.0,
               1.0,
               true
            );

            for (Entity entity : this.trackingEntities) {
               if (entity.m_20238_(center) < 9.0) {
                  entity.m_20256_(entity.m_20184_().m_82549_(entity.m_20182_().m_82546_(center).m_82541_().m_82490_(0.5)));
                  entity.f_19864_ = true;
               }
            }
         } else if ((this.f_19797_ - 1) % 40 == 0 && (this.duration < 40 || this.f_19797_ + 40 < this.duration)) {
            this.m_5496_((SoundEvent)SoundRegistry.BLACK_HOLE_LOOP.get(), this.getRadius() / 3.0F, 0.9F + Utils.random.m_188501_() * 0.2F);
         }
      }
   }

   private void updateTrackingEntities() {
      this.trackingEntities = this.m_9236_().m_45933_(this, this.m_20191_().m_82400_(1.0));
   }

   public boolean m_6051_() {
      return false;
   }
}
