package io.redspace.ironsspellbooks.entity.spells.ice_spike;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;

public class IceSpikeEntity extends AoeEntity {
   private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.m_135353_(IceSpikeEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> DATA_WAIT_TIME = SynchedEntityData.m_135353_(IceSpikeEntity.class, EntityDataSerializers.f_135028_);
   public static final int RISE_TIME = 6;
   public static final int REST_TIME = 40;
   public static final int LOWER_TIME = 30;
   private final List<Entity> victims = new ArrayList<>();

   public IceSpikeEntity(EntityType<? extends AoeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public IceSpikeEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends AoeEntity>)EntityRegistry.ICE_SPIKE.get(), level);
      this.m_5602_(owner);
   }

   @Override
   public void applyEffect(LivingEntity target) {
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_SIZE, 1.0F);
      this.f_19804_.m_135372_(DATA_WAIT_TIME, 10);
   }

   public float getSpikeSize() {
      return (Float)this.f_19804_.m_135370_(DATA_SIZE);
   }

   public void setSpikeSize(float f) {
      this.f_19804_.m_135381_(DATA_SIZE, f);
      this.m_6210_();
   }

   public int getWaitTime() {
      return (Integer)this.f_19804_.m_135370_(DATA_WAIT_TIME);
   }

   public void setWaitTime(int i) {
      this.f_19804_.m_135381_(DATA_WAIT_TIME, i);
   }

   public float getPositionOffset(float partialTick) {
      float f = this.f_19797_ + partialTick;
      int waitTime = this.getWaitTime();
      if (f < waitTime) {
         return -1.0F;
      }

      if (f < waitTime + 6) {
         f = (f - waitTime) / 6.0F;
         return Mth.m_14031_(f * (float) Math.PI) / (float) Math.PI + f - 1.0F;
      }

      if (f < waitTime + 6 + 40) {
         return 0.0F;
      }

      f = Mth.m_14036_((f - (waitTime + 6 + 40)) / 30.0F, 0.0F, 1.0F) + 1.0F;
      return -(Mth.m_14031_(f * (float) Math.PI) / (float) Math.PI + f - 1.0F);
   }

   @Override
   public void m_8119_() {
      this.m_6210_();
      int waitTime = this.getWaitTime();
      if (this.f_19797_ == waitTime) {
         if (!this.f_19853_.f_46443_) {
            float f = this.getSpikeSize();
            if (!this.m_20067_()) {
               this.f_19853_
                  .m_5594_(
                     null,
                     this.m_20183_(),
                     (SoundEvent)SoundRegistry.ICE_SPIKE_EMERGE.get(),
                     SoundSource.NEUTRAL,
                     1.25F * this.getSpikeSize(),
                     Mth.m_216287_(Utils.random, 6, 12) * 0.1F
                  );
            }

            MagicManager.spawnParticles(
               this.f_19853_,
               ParticleHelper.SNOWFLAKE,
               this.m_20185_(),
               this.f_19853_
                     .m_45547_(new ClipContext(this.m_20182_().m_82520_(0.0, 2.0, 0.0), this.m_20182_(), Block.COLLIDER, Fluid.NONE, null))
                     .m_82450_()
                     .m_7098_()
                  + 0.1,
               this.m_20189_(),
               (int)(10.0F * f * f),
               0.1 * f,
               0.1 * f,
               0.1F * f,
               0.12 * f,
               false
            );
            MagicManager.spawnParticles(
               this.f_19853_,
               ParticleHelper.SNOW_DUST,
               this.m_20185_(),
               this.f_19853_
                     .m_45547_(new ClipContext(this.m_20182_().m_82520_(0.0, 2.0, 0.0), this.m_20182_(), Block.COLLIDER, Fluid.NONE, null))
                     .m_82450_()
                     .m_7098_()
                  + 0.1,
               this.m_20189_(),
               (int)(15.0F * f * f),
               0.1 * f,
               0.1 * f,
               0.1F * f,
               0.08 * f,
               false
            );
         }
      } else if (this.f_19797_ > waitTime && this.f_19797_ < waitTime + 6) {
         AABB damager = this.m_20191_();
         damager.m_165893_(this.m_20186_() + damager.m_82376_() * (this.getPositionOffset(0.0F) + 1.0F));

         for (Entity entity : this.f_19853_
            .m_45933_(this, damager)
            .stream()
            .filter(target -> this.m_5603_(target) && !this.victims.contains(target))
            .collect(Collectors.toSet())) {
            if (DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.ICE_SPIKES_SPELL.get()).getDamageSource(this, this.m_19749_()))) {
               entity.m_20256_(entity.m_20184_().m_82520_(0.0, this.getSpikeSize() * 0.3, 0.0));
               entity.f_19864_ = true;
               entity.m_146917_(entity.m_146888_() + (int)(40.0F * this.getSpikeSize()));
            }

            this.victims.add(entity);
            if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
               this.m_146870_();
               return;
            }
         }
      } else if (this.f_19797_ > waitTime + 6 + 40 + 30) {
         this.m_146870_();
      }
   }

   @Override
   protected void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("waitTime", this.getWaitTime());
   }

   @Override
   protected void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.setWaitTime(pCompound.m_128451_("waitTime"));
   }

   @Override
   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getSpikeSize() * 0.4F, this.getSpikeSize() * 1.25F * (this.getPositionOffset(1.0F) + 1.0F));
   }

   public boolean m_5829_() {
      return true;
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
   }
}
