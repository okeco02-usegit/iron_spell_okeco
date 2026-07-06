package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericProtectOwnerTargetGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.PolarBear.PolarBearMeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class SummonedPolarBear extends PolarBear implements IMagicSummon {
   public SummonedPolarBear(EntityType<? extends PolarBear> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 0;
   }

   @Deprecated(forRemoval = true)
   public SummonedPolarBear(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends PolarBear>)EntityRegistry.SUMMONED_POLAR_BEAR.get(), pLevel);
      this.setSummoner(owner);
   }

   public float m_274421_() {
      return 1.0F;
   }

   public void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new PolarBearMeleeAttackGoal(this));
      this.f_21345_.m_25352_(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 15.0F, 5.0F, false, 25.0F));
      this.f_21345_.m_25352_(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.f_21345_.m_25352_(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.f_21346_.m_25352_(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers());
      this.f_21346_.m_25352_(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   public InteractionResult m_6071_(Player pPlayer, InteractionHand pHand) {
      if (this.m_20160_()) {
         return super.m_6071_(pPlayer, pHand);
      }

      if (pPlayer == this.getSummoner()) {
         this.doPlayerRide(pPlayer);
      }

      return InteractionResult.m_19078_(this.f_19853_.f_46443_);
   }

   protected void doPlayerRide(Player pPlayer) {
      this.m_29567_(false);
      if (!this.f_19853_.f_46443_) {
         pPlayer.m_146922_(this.m_146908_());
         pPlayer.m_146926_(this.m_146909_());
         pPlayer.m_20329_(this);
      }
   }

   protected void m_8024_() {
      super.m_8024_();
      if (this.f_19797_ % 80 == 0) {
         this.m_5634_(1.0F);
      }
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
   }

   public void m_7380_(CompoundTag compoundTag) {
      super.m_7380_(compoundTag);
   }

   public boolean m_7327_(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, ((AbstractSpell)SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get()).getDamageSource(this, this.getSummoner()));
   }

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || this.isAlliedHelper(pEntity);
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

   public static Builder m_29560_() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 20.0)
         .m_22268_(Attributes.f_22279_, 0.3)
         .m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0)
         .m_22268_(Attributes.f_22281_, 6.0);
   }

   @Nullable
   public LivingEntity m_6688_() {
      Entity entity = this.m_146895_();
      if (entity instanceof Mob) {
         return (Mob)entity;
      }

      entity = this.m_146895_();
      return entity instanceof Player ? (Player)entity : null;
   }

   protected void m_274498_(Player player, Vec3 p_275242_) {
      super.m_274498_(player, p_275242_);
      this.f_19859_ = this.m_146908_();
      this.m_146922_(player.m_146908_());
      this.m_146926_(player.m_146909_());
      this.m_19915_(this.m_146908_(), this.m_146909_());
      this.f_20883_ = this.f_19859_;
      this.f_20885_ = this.m_146908_();
   }

   protected Vec3 m_274312_(Player player, Vec3 p_275300_) {
      float f = player.f_20900_ * 0.5F;
      float f1 = player.f_20902_;
      if (f1 <= 0.0F) {
         f1 *= 0.25F;
      }

      if (this.m_20069_()) {
         f *= 0.3F;
         f1 *= 0.3F;
      }

      return new Vec3(f, 0.0, f1);
   }

   protected float m_245547_(Player p_278336_) {
      return (float)this.m_21133_(Attributes.f_22279_) * 0.8F;
   }
}
