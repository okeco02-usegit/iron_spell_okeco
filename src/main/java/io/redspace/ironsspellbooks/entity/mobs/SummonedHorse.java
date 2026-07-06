package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonedHorse extends AbstractHorse implements IMagicSummon {
   public SummonedHorse(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SummonedHorse(Level pLevel) {
      this((EntityType<? extends AbstractHorse>)EntityRegistry.SPECTRAL_STEED.get(), pLevel);
   }

   @Deprecated(forRemoval = true)
   public SummonedHorse(Level pLevel, LivingEntity owner) {
      this(pLevel);
      this.m_30586_(owner.m_20148_());
      this.setSummoner(owner);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new GenericFollowOwnerGoal(this, this::getSummoner, 0.8F, 12.0F, 4.0F, false, 32.0F));
      this.f_21345_.m_25352_(3, new PanicGoal(this, 0.9F));
      this.f_21345_.m_25352_(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
   }

   public void m_213583_(Player pPlayer) {
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22276_, 15.0)
         .m_22268_(Attributes.f_22288_, 1.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22279_, 0.35);
   }

   public void m_8119_() {
      this.spawnParticles();
      super.m_8119_();
   }

   protected SoundEvent m_7515_() {
      super.m_7515_();
      return SoundEvents.f_11971_;
   }

   protected SoundEvent m_5592_() {
      super.m_5592_();
      return SoundEvents.f_11975_;
   }

   @Override
   public void onUnSummon() {
      if (!this.f_19853_.f_46443_) {
         MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123759_, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.m_142467_(RemovalReason.DISCARDED);
      }
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.m_6469_(pSource, pAmount);
   }

   @Nullable
   protected SoundEvent m_7872_() {
      return SoundEvents.f_11976_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_11978_;
   }

   protected SoundEvent m_7871_() {
      super.m_7871_();
      return SoundEvents.f_11972_;
   }

   public void spawnParticles() {
      if (this.f_19853_.f_46443_ && Utils.random.m_188501_() < 0.25F) {
         float radius = 0.75F;
         Vec3 vec = new Vec3(
            this.f_19796_.m_188501_() * 2.0F * radius - radius,
            this.f_19796_.m_188501_() * 2.0F * radius - radius,
            this.f_19796_.m_188501_() * 2.0F * radius - radius
         );
         this.f_19853_
            .m_7106_(
               ParticleTypes.f_123809_,
               this.m_20185_() + vec.f_82479_,
               this.m_20186_() + vec.f_82480_ + 1.0,
               this.m_20189_() + vec.f_82481_,
               vec.f_82479_ * 0.01F,
               0.08 + vec.f_82480_ * 0.01F,
               vec.f_82481_ * 0.01F
            );
      }
   }

   public InteractionResult m_6071_(Player pPlayer, InteractionHand pHand) {
      if (this.m_20160_()) {
         return super.m_6071_(pPlayer, pHand);
      }

      if (pPlayer == this.getSummoner()) {
         this.m_6835_(pPlayer);
      } else {
         this.m_7564_();
      }

      return InteractionResult.m_19078_(this.f_19853_.f_46443_);
   }

   @Deprecated(forRemoval = true)
   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public void m_6667_(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.m_6667_(pDamageSource);
   }

   public void onRemovedFromWorld() {
      this.onRemovedHelper(this);
      super.onRemovedFromWorld();
   }

   public void m_7378_(CompoundTag compoundTag) {
      super.m_7378_(compoundTag);
      this.m_30586_(OwnerHelper.deserializeOwner(compoundTag));
   }

   public void m_7380_(CompoundTag compoundTag) {
      super.m_7380_(compoundTag);
      OwnerHelper.serializeOwner(compoundTag, this.m_21805_());
   }

   public boolean m_6573_(Player pPlayer) {
      return false;
   }

   protected boolean m_30628_() {
      return false;
   }

   public boolean m_6254_() {
      return true;
   }

   public boolean m_30614_() {
      return true;
   }
}
