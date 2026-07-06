package io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.NBT;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;

public class CursedArmorStandEntity extends AbstractSpellCastingMob implements IAnimatedAttacker, NeutralMob {
   public static final int JIGGLE_TIME = 15;
   private static final EntityDataAccessor<Boolean> DATA_FROZEN = SynchedEntityData.m_135353_(CursedArmorStandEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<String> DATA_POSE = SynchedEntityData.m_135353_(CursedArmorStandEntity.class, EntityDataSerializers.f_135030_);
   @Nullable
   Vec3 spawn = null;
   float originalYRot = 0.0F;
   int bootJiggle;
   int legJiggle;
   int chestJiggle;
   int helmetJiggle;
   int interactionAnger;
   RawAnimation animationToPlay = null;
   private final AnimationController<CursedArmorStandEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.m_145020_(20, 39);
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_FROZEN, true);
      this.f_19804_.m_135372_(DATA_POSE, "DEFAULT");
   }

   public boolean isArmorStandFrozen() {
      return (Boolean)this.f_19804_.m_135370_(DATA_FROZEN);
   }

   public CursedArmorStandEntity.Pose getArmorstandPose() {
      return CursedArmorStandEntity.Pose.valueOf((String)this.f_19804_.m_135370_(DATA_POSE));
   }

   public void setArmorstandPose(CursedArmorStandEntity.Pose pose) {
      this.f_19804_.m_135381_(DATA_POSE, pose.name());
   }

   public void setArmorStandFrozen(boolean frozen) {
      boolean wasFrozen = this.isArmorStandFrozen();
      this.f_19804_.m_135381_(DATA_FROZEN, frozen);
      if (frozen) {
         this.m_5616_(this.originalYRot);
         this.m_5618_(this.originalYRot);
         this.m_146922_(this.originalYRot);
      } else if (wasFrozen && !this.f_19853_.f_46443_) {
         MagicManager.spawnParticles(
            this.f_19853_, ParticleTypes.f_123792_, this.m_20185_(), this.m_20186_() + 1.25, this.m_20189_(), 15, 0.3, 0.2, 0.3, 0.0, false
         );
      }
   }

   public InteractionResult m_7111_(Player pPlayer, Vec3 pVector, InteractionHand pHand) {
      if (this.isArmorStandFrozen()) {
         if (pPlayer.f_19853_.f_46443_) {
            this.handleInteraction(pVector, slot -> {
               switch (slot) {
                  case HEAD:
                     this.helmetJiggle = 15;
                     break;
                  case CHEST:
                     this.chestJiggle = 15;
                     break;
                  case LEGS:
                     this.legJiggle = 15;
                     break;
                  case FEET:
                     this.bootJiggle = 15;
               }
            });
         } else {
            AtomicReference<SoundEvent> sound = new AtomicReference<>(SoundEvents.f_11683_);
            this.handleInteraction(
               pVector,
               slot -> {
                  if (this.m_21033_(slot) && this.m_6844_(slot).m_41720_() instanceof ArmorItem armorItem) {
                     sound.set(armorItem.m_40401_().m_7344_());
                  }

                  if (pPlayer.m_7500_() && pPlayer.m_6047_()) {
                     ItemStack equipped = this.m_6844_(slot);
                     ItemStack playerHeld = pPlayer.m_21120_(pHand);
                     if ((equipped.m_41619_() || equipped.m_41720_() instanceof ArmorItem)
                        && (playerHeld.m_41619_() || playerHeld.m_41720_() instanceof ArmorItem armorItem && armorItem.m_40402_().equals(slot))) {
                        pPlayer.m_21008_(pHand, equipped);
                        this.m_8061_(slot, playerHeld);
                     }
                  }
               }
            );
            this.m_216990_(sound.get());
            if (this.m_6779_(pPlayer) && this.interactionAnger++ >= 2) {
               this.m_6710_(pPlayer);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.m_7111_(pPlayer, pVector, pHand);
      }
   }

   private void handleInteraction(Vec3 interactionVector, Consumer<EquipmentSlot> onInteract) {
      double d0 = interactionVector.f_82480_ / this.m_6134_();
      if (d0 >= 0.1 && d0 < 0.55) {
         onInteract.accept(EquipmentSlot.FEET);
      } else if (d0 >= 0.9 && d0 < 1.6) {
         onInteract.accept(EquipmentSlot.CHEST);
      } else if (d0 >= 0.4 && d0 < 1.2000000000000002) {
         onInteract.accept(EquipmentSlot.LEGS);
      } else if (d0 >= 1.6) {
         onInteract.accept(EquipmentSlot.HEAD);
      }
   }

   @Override
   public boolean shouldBeExtraAnimated() {
      return !this.isArmorStandFrozen();
   }

   public CursedArmorStandEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 0;
      this.f_21365_ = this.createLookControl();
      this.f_21342_ = this.createMoveControl();
   }

   @Override
   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float m_24956_(float pFrom, float pTo, float pMaxDelta) {
            return super.m_24956_(pFrom, pTo, pMaxDelta * 2.5F);
         }

         protected boolean m_8106_() {
            return CursedArmorStandEntity.this.m_5448_() == null;
         }
      };
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

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19853_.f_46443_) {
         if (this.helmetJiggle > 0) {
            this.helmetJiggle--;
         }

         if (this.chestJiggle > 0) {
            this.chestJiggle--;
         }

         if (this.legJiggle > 0) {
            this.legJiggle--;
         }

         if (this.bootJiggle > 0) {
            this.bootJiggle--;
         }
      }
   }

   protected void m_6677_(DamageSource pSource) {
      ItemStack chestplate = this.m_6844_(EquipmentSlot.CHEST);
      if (!chestplate.m_41619_() && chestplate.m_41720_() instanceof ArmorItem armorItem) {
         this.m_5496_(armorItem.m_40401_().m_7344_(), this.m_6121_(), this.m_6100_());
      }

      super.m_6677_(pSource);
   }

   @org.jetbrains.annotations.Nullable
   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_11683_;
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128350_("originalYRot", this.originalYRot);
      pCompound.m_128379_("armorStandFrozen", this.isArmorStandFrozen());
      if (this.spawn != null) {
         pCompound.m_128365_("spawnPos", NBT.writeVec3Pos(this.spawn));
      }

      pCompound.m_128359_("armorStandPose", this.getArmorstandPose().name());
      this.m_21678_(pCompound);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.originalYRot = pCompound.m_128457_("originalYRot");
      if (pCompound.m_128425_("spawnPos", 10)) {
         this.spawn = NBT.readVec3(pCompound.m_128469_("spawnPos"));
      }

      this.setArmorStandFrozen(pCompound.m_128471_("armorStandFrozen"));
      String pose = pCompound.m_128461_("armorStandPose");

      try {
         this.setArmorstandPose(CursedArmorStandEntity.Pose.valueOf(pose));
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.warn("Entity {} attempting to load invalid pose: {}", this, pose);
         this.setArmorstandPose(CursedArmorStandEntity.Pose.DEFAULT);
      }

      this.m_147285_(this.f_19853_, pCompound);
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      if (this.spawn == null) {
         this.spawn = this.m_20182_();
      }

      if (this.f_19853_ instanceof ServerLevel serverLevel) {
         this.m_21666_(serverLevel, true);
      }

      if (this.interactionAnger > 0 && this.f_19797_ % 30 == 0) {
         this.interactionAnger--;
      }
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_
         .m_25352_(
            3,
            new ArmorStandAttackGoal(this, 1.0, 50, 75)
               .setMoveset(
                  List.of(new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe", 8), new AttackAnimationData(20, "simple_sword_downstrike", 16))
               )
               .setComboChance(0.2F)
               .setMeleeAttackInverval(10, 30)
               .setMeleeMovespeedModifier(1.5F)
               .setMeleeBias(1.0F, 1.0F)
               .setSpells(List.of(), List.of(), List.of(), List.of())
         );
      this.f_21345_.m_25352_(5, new ArmorStandReturnToHomeGoal(this, 1.0));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::m_21674_));
      this.f_21346_.m_25352_(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   public boolean m_6094_() {
      return false;
   }

   protected void m_6138_() {
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.isArmorStandFrozen();
   }

   public void m_6710_(@org.jetbrains.annotations.Nullable LivingEntity pTarget) {
      if (pTarget != null) {
         this.setArmorStandFrozen(false);
      }

      super.m_6710_(pTarget);
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      this.setArmorStandFrozen(false);
      return super.m_6469_(pSource, pAmount);
   }

   @org.jetbrains.annotations.Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel,
      DifficultyInstance pDifficulty,
      MobSpawnType pReason,
      @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData,
      @org.jetbrains.annotations.Nullable CompoundTag pDataTag
   ) {
      if (pReason.equals(MobSpawnType.STRUCTURE)) {
         this.originalYRot = this.m_146908_();
         this.spawn = null;
         this.m_21530_();
      }

      super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      this.m_21559_(false);
      return pSpawnData;
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.CULTIST_HELMET.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.CULTIST_CHESTPLATE.get()));
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistry.MISERY.get()));
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 3.0)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   @Override
   public void playAnimation(String animationId) {
      try {
         this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   private PlayState predicate(AnimationState<CursedArmorStandEntity> animationEvent) {
      AnimationController<CursedArmorStandEntity> controller = animationEvent.getController();
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
      super.registerControllers(controllerRegistrar);
   }

   @Override
   public boolean isAnimating() {
      return this.meleeController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new NotIdioticNavigation(this, pLevel);
   }

   public int m_6784_() {
      return this.remainingPersistentAngerTime;
   }

   public void m_7870_(int pRemainingPersistentAngerTime) {
      this.remainingPersistentAngerTime = pRemainingPersistentAngerTime;
   }

   @org.jetbrains.annotations.Nullable
   public UUID m_6120_() {
      return this.persistentAngerTarget;
   }

   public void m_6925_(@org.jetbrains.annotations.Nullable UUID pPersistentAngerTarget) {
      this.persistentAngerTarget = pPersistentAngerTarget;
   }

   public void m_6825_() {
      this.m_7870_(PERSISTENT_ANGER_TIME.m_214085_(this.f_19796_));
   }

   public enum Pose {
      DEFAULT,
      KNEELING,
      HEROIC,
      STOIC;
   }
}
