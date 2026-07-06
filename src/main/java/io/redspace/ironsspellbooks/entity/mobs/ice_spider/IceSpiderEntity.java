package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.MomentHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.PartEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;

public class IceSpiderEntity extends AbstractSpellCastingMob implements Enemy, IAnimatedAttacker, PreventDismount {
   private static final EntityDataAccessor<Boolean> DATA_IS_CLIMBING = SynchedEntityData.m_135353_(IceSpiderEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_IS_CROUCHING = SynchedEntityData.m_135353_(IceSpiderEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Optional<UUID>> DATA_GRAPPLE_UUID = SynchedEntityData.m_135353_(
      IceSpiderEntity.class, EntityDataSerializers.f_135041_
   );
   private static final AttributeModifier CROUCH_SPEED_MODIFIER = new AttributeModifier(
      UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E"), "crouching", -0.3, Operation.MULTIPLY_TOTAL
   );
   public static final Vec3 TORSO_OFFSET = new Vec3(0.0, 18.0, 0.0);
   private static final int EMERGE_TIME = 45;
   public final Vec3[] cornerPins = new Vec3[]{Vec3.f_82478_, Vec3.f_82478_, Vec3.f_82478_, Vec3.f_82478_};
   public Vec3 normal = Vec3.f_82478_;
   public Vec3 lastNormal = Vec3.f_82478_;
   private int emergeTick;
   int crouchTick;
   public boolean wantsToLeapBack;
   public boolean wantsToCastSpells;
   IceSpiderPartEntity[] subEntities;
   IceSpiderAttackGoal attackGoal;
   @Nullable
   int grappleTime;
   @Nullable
   Entity cachedGrappleTarget = null;
   RawAnimation animationToPlay = null;
   private final AnimationController<IceSpiderEntity> meleeController = new AnimationController(this, "melee_animations", 0, this::predicate);

   public void m_141965_(ClientboundAddEntityPacket packet) {
      super.m_141965_(packet);
      float y = this.m_146908_();
      this.f_19859_ = y;
      this.f_20883_ = y;
      this.f_20884_ = y;
      this.f_20885_ = y;
      this.f_20886_ = y;
   }

   public IceSpiderEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.subEntities = new IceSpiderPartEntity[]{
         new IceSpiderPartEntity(this, TORSO_OFFSET.m_82520_(0.0, 0.0, 16.0), 1.2F, 0.8F),
         new IceSpiderPartEntity(this, TORSO_OFFSET, 0.75F, 0.75F),
         new IceSpiderPartEntity(this, TORSO_OFFSET.m_82520_(0.0, 0.0, -20.0), 1.75F, 1.5F)
      };
      this.m_20234_(f_19843_.getAndAdd(this.subEntities.length + 1) + 1);
      this.f_21342_ = this.createMoveControl();
   }

   public IceSpiderEntity(Level level) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.ICE_SPIDER.get(), level);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22276_, 50.0)
         .m_22268_(Attributes.f_22284_, 20.0)
         .m_22268_(Attributes.f_22278_, 0.6)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.5)
         .m_22268_(Attributes.f_22279_, 0.35);
   }

   public void m_8119_() {
      super.m_8119_();
      float scalar = this.m_6134_() * 4.0F;
      Vec3 worldpos = this.m_20182_();

      for (int x = 0; x < 2; x++) {
         for (int y = 0; y < 2; y++) {
            Vec3 vec = this.rotateWithBody(new Vec3((x - 0.5) * scalar, 0.0, (y - 0.5) * scalar));
            int maxStep = 2;
            int climbOffset = this.isClimbing() ? 4 * Mth.m_14205_(y - 0.5) : 0;
            this.cornerPins[x * 2 + y] = Utils.moveToRelativeGroundLevel(this.f_19853_, worldpos.m_82549_(vec), maxStep + climbOffset, maxStep - climbOffset)
               .m_82546_(worldpos);
         }
      }

      Vec3[] vx = this.cornerPins;
      Vec3 n0 = vx[1].m_82546_(vx[0]).m_82537_(vx[2].m_82546_(vx[0]));
      Vec3 n1 = vx[3].m_82546_(vx[1]).m_82537_(vx[0].m_82546_(vx[1]));
      Vec3 n2 = vx[0].m_82546_(vx[2]).m_82537_(vx[3].m_82546_(vx[2]));
      Vec3 n3 = vx[2].m_82546_(vx[3]).m_82537_(vx[1].m_82546_(vx[3]));
      Vec3 targetNormal = n0.m_82549_(n1).m_82549_(n2).m_82549_(n3).m_82541_();
      this.lastNormal = this.normal;
      this.normal = Utils.lerp(0.2F, this.normal, targetNormal);
      Quaternionf quat = Utils.rotationBetweenVectors(new Vector3f(0.0F, 1.0F, 0.0F), Utils.v3f(this.normal));

      for (IceSpiderPartEntity part : this.subEntities) {
         part.positionSelf(quat);
      }

      if (this.emergeTick > 0) {
         this.emergeTick--;
         if (!this.f_19853_.f_46443_) {
            if (this.emergeTick == 0) {
               this.m_20124_(Pose.STANDING);
            }
         } else {
            this.m_267689_(this.emergeTick / 45.0F);
         }
      }
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      this.tickGrapple();
      this.handleCrouchStatus();
      this.handleClimbingStatus();
   }

   private void handleCrouchStatus() {
      if (!this.f_19853_.f_46443_) {
         if (this.m_6047_()) {
            AABB projection = this.m_6972_(Pose.STANDING).m_20393_(this.m_20182_());
            if (this.f_19853_.m_45756_(this, projection.m_82406_(1.0E-7))) {
               this.stopCrouching();
            }
         } else if (this.f_19862_) {
            AABB projection = this.m_6972_(Pose.CROUCHING).m_20393_(this.m_20182_().m_82549_(this.m_20156_().m_82490_(0.15)));
            if (this.f_19853_.m_45756_(this, projection.m_82406_(1.0E-7))) {
               this.startCrouching();
            }
         }
      }
   }

   private void handleClimbingStatus() {
      if (!this.f_19853_.f_46443_ && !this.m_6047_()) {
         if (this.f_19863_ && !this.f_201939_) {
            AABB leftprojection = this.m_20191_().m_82406_(0.2).m_82383_(this.m_20156_().m_82490_(0.5).m_82524_((float) (-Math.PI / 2)));
            boolean strafeLeft = this.f_19853_.m_45756_(this, leftprojection);
            this.m_21566_().m_24988_(0.0F, strafeLeft ? 1.0F : -1.0F);
         } else {
            if (this.isClimbing()) {
               if (!this.f_19862_) {
                  this.setIsClimbing(false);
               }
            } else if (this.f_19862_) {
               float deflate = 0.75F;
               AABB projection = this.m_20191_().m_82406_(deflate).m_82383_(this.m_20156_().m_82490_(0.25 + deflate / 2.0F));
               if (!this.f_19853_.m_45756_(this, projection)) {
                  this.setIsClimbing(true);
               }
            }
         }
      }
   }

   public void setEmergeFromGround() {
      if (!this.f_19853_.f_46443_) {
         this.m_20124_(Pose.EMERGING);
         this.emergeTick = 45;
      }
   }

   public void setIsClimbing(boolean climbing) {
      this.f_19804_.m_135381_(DATA_IS_CLIMBING, climbing);
   }

   public boolean isClimbing() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_CLIMBING);
   }

   public void setIsCrouching(boolean climbing) {
      this.f_19804_.m_135381_(DATA_IS_CROUCHING, climbing);
   }

   public boolean m_6047_() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_CROUCHING);
   }

   public Vec3 m_20184_() {
      return this.isClimbing() ? super.m_20184_().m_82542_(1.0, 0.0, 1.0).m_82520_(0.0, 0.275F, 0.0) : super.m_20184_();
   }

   public float getCrouchHeightMultiplier(float partialTick) {
      return Mth.m_14179_(this.crouchTweenPercent(partialTick), 0.5F, 1.0F);
   }

   public void startCrouching() {
      this.m_20124_(Pose.CROUCHING);
      this.m_21051_(Attributes.f_22279_).m_22130_(CROUCH_SPEED_MODIFIER);
      this.m_21051_(Attributes.f_22279_).m_22118_(CROUCH_SPEED_MODIFIER);
      this.setIsCrouching(true);
   }

   public void stopCrouching() {
      this.m_20124_(Pose.STANDING);
      this.m_21051_(Attributes.f_22279_).m_22130_(CROUCH_SPEED_MODIFIER);
      this.setIsCrouching(false);
   }

   public float getCrouchHeightMultiplier() {
      return this.m_6047_() ? 0.5F : 1.0F;
   }

   @Override
   public void castComplete() {
      super.castComplete();
      this.wantsToCastSpells = false;
   }

   @Override
   public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
      if (this.wantsToCastSpells) {
         if (spell.getCastType() == CastType.INSTANT) {
            this.serverTriggerAnimation("attack_fang_basic");
         } else {
            this.serverTriggerAnimation("long_cast");
         }

         super.initiateCastSpell(spell, spellLevel);
      }
   }

   public float m_274421_() {
      return Math.max(1.0F, super.m_274421_() * this.m_6134_());
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_IS_CLIMBING, false);
      this.f_19804_.m_135372_(DATA_IS_CROUCHING, false);
      this.f_19804_.m_135372_(DATA_GRAPPLE_UUID, Optional.empty());
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float m_24991_(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
            double d0 = this.f_24975_ - this.f_24974_.m_20185_();
            double d1 = this.f_24977_ - this.f_24974_.m_20189_();
            return d0 * d0 + d1 * d1 < 0.5 ? pSourceAngle : super.m_24991_(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
         }
      };
   }

   protected PathNavigation m_6037_(Level level) {
      return new IceSpiderNavigation(this, level);
   }

   protected void m_7840_(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new LeapBackGoal(this));
      this.f_21345_.m_25352_(1, new PounceGrappleGoal(this));
      this.attackGoal = (IceSpiderAttackGoal)new IceSpiderAttackGoal(this, 1.1, 0, 40)
         .setMoveset(
            List.of(
               new AttackAnimationData.Builder("attack_bite").length(22).attacks(new AttackKeyframe(14, new Vec3(0.0, 0.0, 1.0))).build(),
               new AttackAnimationData.Builder("attack_fang_basic").length(20).attacks(new AttackKeyframe(12, new Vec3(0.0, 0.0, 1.0))).build(),
               new AttackAnimationData.Builder("attack_right_swipe")
                  .length(14)
                  .attacks(new AttackKeyframe(10, new Vec3(0.0, 0.1, -1.0), new Vec3(0.0, 0.0, 1.0)))
                  .build()
            )
         )
         .setMeleeBias(1.0F, 1.0F)
         .setSpells(
            List.of((AbstractSpell)SpellRegistry.SNOWBALL_SPELL.get(), (AbstractSpell)SpellRegistry.ICE_SPIKES_SPELL.get()), List.of(), List.of(), List.of()
         )
         .setSpellQuality(0.75F, 0.75F);
      this.f_21345_.m_25352_(2, this.attackGoal);
      this.f_21345_.m_25352_(3, new LookAtPlayerGoal(this, Player.class, 32.0F, 0.08F));
      this.f_21345_.m_25352_(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new MomentHurtByTargetGoal(this, IceSpiderEntity.class));
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(this, LivingEntity.class, true, livingEntity -> livingEntity instanceof Player || livingEntity instanceof IronGolem)
         );
      this.f_21346_
         .m_25352_(
            3,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               true,
               livingEntity -> livingEntity instanceof Animal || livingEntity instanceof AbstractVillager || livingEntity instanceof Raider
            )
         );
   }

   protected SoundEvent m_7975_(DamageSource damageSource) {
      return (SoundEvent)SoundRegistry.ICE_SPIDER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)SoundRegistry.ICE_SPIDER_DEATH.get();
   }

   @org.jetbrains.annotations.Nullable
   protected SoundEvent m_7515_() {
      return (SoundEvent)SoundRegistry.ICE_SPIDER_AMBIENT.get();
   }

   @Override
   protected LookControl createLookControl() {
      return super.createLookControl();
   }

   protected BodyRotationControl m_7560_() {
      return new BodyRotationControl(this) {
         public void m_24882_() {
            float rot = this.f_24875_.f_20883_;
            super.m_24882_();
            if (rot != this.f_24875_.f_20883_) {
               IceSpiderEntity.this.m_267689_(1.0F);
            }
         }
      };
   }

   public void m_7601_(BlockState state, Vec3 motionMultiplier) {
      if (!state.m_60713_(Blocks.f_50033_)) {
         super.m_7601_(state, motionMultiplier);
      }
   }

   public float crouchTweenPercent(float partialTick) {
      float tick = this.f_19797_ + partialTick - this.crouchTick;
      float tweenTime = 10.0F;
      float f;
      if (tick > tweenTime) {
         f = 1.0F;
      } else {
         f = tick / tweenTime;
      }

      if (this.m_6047_()) {
         f = 1.0F - f;
      }

      return f;
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.m_20089_().equals(Pose.EMERGING);
   }

   protected void m_7355_(BlockPos pos, BlockState block) {
      this.m_5496_(SoundEvents.f_12435_, 0.15F, 1.0F);
   }

   public boolean m_6673_(DamageSource source) {
      return super.m_6673_(source) || this.m_20089_().equals(Pose.EMERGING);
   }

   public boolean hurt(IceSpiderPartEntity bodypart, DamageSource source, float amount) {
      return this.m_6469_(source, amount);
   }

   protected void m_6475_(DamageSource damageSource, float damageAmount) {
      if (damageSource.m_7639_() != null && damageSource.m_7639_().m_20148_().equals(this.getGrappleTargetUUID())) {
         damageAmount *= 0.2F;
      }

      if (damageSource.m_7639_() instanceof IronGolem) {
         damageAmount *= 0.5F;
      }

      if (this.m_5912_() && !this.m_6047_() && !this.isGrappling() && !this.wantsToLeapBack && !damageSource.m_269014_()) {
         float f = Mth.m_14179_(Mth.m_14036_(damageAmount / 12.0F, 0.0F, 1.0F), 0.02F, 0.7F);
         if (this.f_19796_.m_188501_() < f) {
            this.wantsToCastSpells = true;
            this.wantsToLeapBack = true;
         }
      }

      super.m_6475_(damageSource, damageAmount);
   }

   public Vec3 rotateWithBody(Vec3 vec3) {
      float y = -this.f_20883_ + (float) (Math.PI / 2);
      return vec3.m_82524_(y * (float) (Math.PI / 180.0));
   }

   public boolean isMultipartEntity() {
      return true;
   }

   public void m_20234_(int id) {
      super.m_20234_(id);

      for (int i = 0; i < this.subEntities.length; i++) {
         this.subEntities[i].m_20234_(id + i + 1);
      }
   }

   @Nullable
   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   public boolean m_6087_() {
      return false;
   }

   public void m_6210_() {
      super.m_6210_();

      for (IceSpiderPartEntity part : this.subEntities) {
         part.m_6210_();
      }
   }

   @Override
   public void m_7350_(EntityDataAccessor<?> pKey) {
      super.m_7350_(pKey);
      if (pKey == DATA_IS_CROUCHING) {
         this.m_6210_();
         this.crouchTick = this.f_19797_;
      } else if (pKey == Entity.f_19806_ && this.m_20089_() == Pose.EMERGING) {
         this.playAnimation("emerge_from_ground");
         this.emergeTick = 45;
      }
   }

   public EntityDimensions m_6972_(Pose pose) {
      EntityDimensions dimensions = super.m_6972_(pose);
      if (pose == Pose.CROUCHING) {
         dimensions = dimensions.m_20390_(1.0F, 0.5F);
      }

      return dimensions;
   }

   @Nullable
   public UUID getGrappleTargetUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DATA_GRAPPLE_UUID)).orElse(null);
   }

   public boolean isGrappling() {
      return this.getGrappleTargetUUID() != null;
   }

   public void setGrappleTargetUUID(@Nullable UUID uuid) {
      this.f_19804_.m_135381_(DATA_GRAPPLE_UUID, Optional.ofNullable(uuid));
      if (uuid == null) {
         this.cachedGrappleTarget = null;
      }
   }

   protected void m_19956_(Entity passenger, MoveFunction callback) {
      if (passenger.m_20148_().equals(this.getGrappleTargetUUID())) {
         Vec3 vec = this.m_20182_().m_82549_(this.rotateWithBody(new Vec3(0.0, 0.0, this.m_6134_() * 2.0F)));
         callback.m_20372_(passenger, vec.f_82479_, vec.f_82480_, vec.f_82481_);
      } else {
         super.m_19956_(passenger, callback);
      }
   }

   public boolean shouldRiderFaceForward(Player player) {
      return false;
   }

   public boolean m_142079_() {
      return false;
   }

   public void startGrapple(Entity entity) {
      if (this.getGrappleTargetUUID() == null && !entity.m_20159_() && entity.m_20329_(this)) {
         this.grappleTime = 0;
         this.setGrappleTargetUUID(entity.m_20148_());
      }

      this.wantsToCastSpells = false;
      this.wantsToLeapBack = false;
   }

   public void tickGrapple() {
      UUID uuid = this.getGrappleTargetUUID();
      if (uuid != null) {
         if (this.f_19853_ instanceof ServerLevel serverLevel) {
            if (this.cachedGrappleTarget == null) {
               Entity entity = serverLevel.m_8791_(uuid);
               if (entity == null) {
                  this.setGrappleTargetUUID(null);
                  return;
               }

               this.cachedGrappleTarget = entity;
            }

            if (this.cachedGrappleTarget.m_213877_()) {
               this.stopGrappling();
            } else {
               if (this.grappleTime % 20 == 0) {
                  this.m_5634_(this.m_21233_() * 0.08F);
               }

               if (this.grappleTime++ > 40) {
                  Entity entity = this.cachedGrappleTarget;
                  this.stopGrappling();
                  this.entomb(entity);
               } else {
                  this.cachedGrappleTarget.m_146917_(Math.min(this.cachedGrappleTarget.m_146891_() * 3, this.cachedGrappleTarget.m_146888_() + 10));
                  this.f_20885_ = this.f_20883_;
               }
            }
         }
      }
   }

   public void stopGrappling() {
      if (this.cachedGrappleTarget != null && this.m_20365_(this.cachedGrappleTarget)) {
         this.cachedGrappleTarget.m_8127_();
      }

      this.cachedGrappleTarget = null;
      this.setGrappleTargetUUID(null);
   }

   protected void m_20351_(Entity passenger) {
      super.m_20351_(passenger);
      if (passenger.m_20148_().equals(this.getGrappleTargetUUID())) {
         this.stopGrappling();
      }
   }

   @Override
   public boolean canEntityDismount(Entity entity) {
      return !entity.m_20148_().equals(this.getGrappleTargetUUID());
   }

   public IceTombEntity entomb(Entity entity) {
      IceTombEntity iceTombEntity = new IceTombEntity(this.f_19853_, this);
      iceTombEntity.m_20219_(entity.m_20182_());
      iceTombEntity.m_20256_(entity.m_20184_().m_82549_(this.m_20156_().m_82520_(0.0, 1.0, 0.0).m_82490_(0.5)));
      iceTombEntity.setEvil();
      iceTombEntity.setLifetime(100);
      this.f_19853_.m_7967_(iceTombEntity);
      entity.m_7998_(iceTombEntity, true);
      this.m_216990_((SoundEvent)SoundRegistry.ICE_SPIDER_GRAPPLE_SPIT.get());
      return iceTombEntity;
   }

   @Nullable
   public LivingEntity m_6688_() {
      Entity entity = this.m_146895_();
      if (entity != null && entity.m_20148_().equals(this.getGrappleTargetUUID())) {
         return null;
      }

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

   public boolean m_20367_(Entity pEntity) {
      return pEntity.m_20148_().equals(this.getGrappleTargetUUID());
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      if (this.getGrappleTargetUUID() != null) {
         pCompound.m_128405_("grappleTime", this.grappleTime);
         pCompound.m_128362_("grappleTarget", this.getGrappleTargetUUID());
      }

      pCompound.m_128379_("crouching", this.m_6047_());
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      if (pCompound.m_128403_("grappleTarget")) {
         this.setGrappleTargetUUID(pCompound.m_128342_("grappleTarget"));
         this.grappleTime = pCompound.m_128451_("grappleTime");
      }

      if (pCompound.m_128471_("crouching")) {
         this.startCrouching();
      }
   }

   @Override
   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
   }

   private PlayState predicate(AnimationState<IceSpiderEntity> animationEvent) {
      AnimationController<IceSpiderEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{this.meleeController});
   }

   @Override
   public boolean isAnimating() {
      return this.meleeController.getAnimationState() == State.RUNNING;
   }
}
