package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericProtectOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SummonedWeaponEntity extends AbstractSpellCastingMob implements IMagicSummon, IAnimatedAttacker {
   GenericAnimatedWarlockAttackGoal<? extends SummonedWeaponEntity> attackGoal;
   protected LivingEntity cachedSummoner;
   protected UUID summonerUUID;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final AnimationController<SummonedWeaponEntity> meleeController = new AnimationController(this, "animations", 0, this::predicate);
   RawAnimation animationToPlay = null;

   @Override
   public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
   }

   public abstract GenericAnimatedWarlockAttackGoal<? extends SummonedWeaponEntity> makeAttackGoal();

   public SummonedWeaponEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21342_ = new FlyingMoveControl(this, 20, true);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      this.m_20242_(true);
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   public boolean canDrownInFluidType(FluidType type) {
      return false;
   }

   public boolean m_5825_() {
      return true;
   }

   protected void m_8099_() {
      this.attackGoal = this.makeAttackGoal();
      this.f_21345_.m_25352_(1, this.attackGoal.setMeleeBias(1.0F, 1.0F));
      this.f_21345_.m_25352_(3, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0, 9.0F, 4.0F, true, 20.0F));
      this.f_21345_.m_25352_(5, new WaterAvoidingRandomFlyingGoal(this, 0.75));
      this.f_21346_.m_25352_(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers());
      this.f_21346_.m_25352_(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   protected PathNavigation m_6037_(Level pLevel) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
      flyingpathnavigation.m_26440_(false);
      flyingpathnavigation.m_7008_(true);
      flyingpathnavigation.m_26443_(true);
      return flyingpathnavigation;
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      if (this.f_19797_ % 8 == 0) {
         Entity owner = this.getSummoner();
         LivingEntity target = this.m_5448_();
         Entity trackEntity = (Entity)(target == null ? owner : target);
         double targetY = trackEntity == null
            ? Utils.moveToRelativeGroundLevel(this.f_19853_, this.m_20182_(), 3).f_82480_ + 1.0
            : trackEntity.m_20186_() + 1.0;
         double f = targetY - this.m_20186_();
         double force = Mth.m_14008_(f * 0.05, -0.15, 0.15);
         this.m_20256_(this.m_20184_().m_82520_(0.0, force, 0.0));
      }

      if (this.f_19797_ % 80 == 0) {
         this.m_5634_(1.0F);
      }
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
   }

   public boolean m_7327_(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, ((AbstractSpell)SpellRegistry.SUMMON_SWORDS.get()).getDamageSource(this, this.getSummoner()));
   }

   protected void m_7840_(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
   }

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || this.isAlliedHelper(pEntity);
   }

   @Deprecated(forRemoval = true)
   public void setSummoner(@javax.annotation.Nullable LivingEntity owner) {
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

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return !pSource.m_269533_(DamageTypeTags.f_268738_) && this.shouldIgnoreDamage(pSource) ? false : super.m_6469_(pSource, pAmount);
   }

   @Override
   public void onUnSummon() {
      if (!this.f_19853_.f_46443_) {
         MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123759_, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.m_142467_(RemovalReason.DISCARDED);
      }
   }

   @Override
   public double getBoneResetTime() {
      return 3.0;
   }

   @Override
   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
   }

   private PlayState predicate(AnimationState<SummonedWeaponEntity> animationEvent) {
      AnimationController<SummonedWeaponEntity> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   @Override
   public void m_7378_(CompoundTag compoundTag) {
      super.m_7378_(compoundTag);
      this.summonerUUID = OwnerHelper.deserializeOwner(compoundTag);
   }

   @Override
   public void m_7380_(CompoundTag compoundTag) {
      super.m_7380_(compoundTag);
      OwnerHelper.serializeOwner(compoundTag, this.summonerUUID);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 5.0)
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22280_, 1.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_(Attributes.f_22279_, 0.2);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController[]{this.meleeController});
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
