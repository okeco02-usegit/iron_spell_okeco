package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;

public class KeeperEntity extends AbstractSpellCastingMob implements Enemy, IAnimatedAttacker, IEntityAdditionalSpawnData {
   private static final EntityDataAccessor<Boolean> DATA_IS_SUMMONED = SynchedEntityData.m_135353_(KeeperEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_IS_RESTORED = SynchedEntityData.m_135353_(KeeperEntity.class, EntityDataSerializers.f_135035_);
   public static final int RISE_ANIM_TIME = 25;
   public int riseAnimTick;
   public int destroyBlockDelay;
   private final AnimationController<KeeperEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);
   RawAnimation animationToPlay = null;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      buffer.writeInt(this.riseAnimTick);
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      this.riseAnimTick = additionalData.readInt();
      if (this.riseAnimTick > 0) {
         this.animationToPlay = RawAnimation.begin().thenPlay("keeper_kneeling_rise");
      }

      float y = this.m_146908_();
      this.f_20883_ = y;
      this.f_20884_ = y;
      this.f_20885_ = y;
      this.f_20886_ = y;
      this.f_19859_ = y;
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_IS_SUMMONED, false);
      this.f_19804_.m_135372_(DATA_IS_RESTORED, false);
   }

   public void triggerRise() {
      this.riseAnimTick = 25;
   }

   public KeeperEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
      this.f_21365_ = this.createLookControl();
      this.f_21342_ = this.createMoveControl();
   }

   public boolean isSummoned() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_SUMMONED);
   }

   public void setIsSummoned() {
      this.f_19804_.m_135381_(DATA_IS_SUMMONED, true);
   }

   public boolean isRestored() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_RESTORED);
   }

   public void setIsRestored() {
      this.f_19804_.m_135381_(DATA_IS_RESTORED, true);
   }

   protected boolean m_6125_() {
      return super.m_6125_() && !this.isSummoned();
   }

   public boolean m_6149_() {
      return super.m_6149_() && !this.isSummoned();
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.riseAnimTick > 0) {
         this.riseAnimTick--;
         if (!this.f_19853_.f_46443_) {
            Vec3 vec3 = this.m_20191_().m_82399_();
            MagicManager.spawnParticles(
               this.f_19853_,
               (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
               vec3.f_82479_,
               vec3.f_82480_,
               vec3.f_82481_,
               5,
               0.2,
               0.2,
               0.2,
               0.05,
               false
            );
         }
      }
   }

   public boolean isRising() {
      return this.riseAnimTick > 0;
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.isRising();
   }

   public KeeperEntity(Level pLevel) {
      this((EntityType<? extends AbstractSpellCastingMob>)EntityRegistry.KEEPER.get(), pLevel);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(4, new KeeperAnimatedWarlockAttackGoal(this, 1.0, 10, 30));
      this.f_21345_.m_25352_(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Mob.class, true, entity -> !entity.m_6095_().m_204039_(ModTags.INFERNAL_ALLIES)));
   }

   protected BodyRotationControl m_7560_() {
      return new BodyRotationControl(this);
   }

   @Override
   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float m_24956_(float pFrom, float pTo, float pMaxDelta) {
            return super.m_24956_(pFrom, pTo, pMaxDelta * 2.5F);
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

   protected SoundEvent m_7515_() {
      return (SoundEvent)SoundRegistry.KEEPER_IDLE.get();
   }

   public void m_8032_() {
      this.m_5496_(this.m_7515_(), 1.0F, Mth.m_216287_(this.m_217043_(), 5, 10) * 0.1F);
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return (SoundEvent)SoundRegistry.KEEPER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)SoundRegistry.KEEPER_DEATH.get();
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      return pSpawnData;
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(
         EquipmentSlot.MAINHAND,
         new ItemStack(this.isRestored() ? (ItemLike)ItemRegistry.LEGIONNAIRE_FLAMBERGE.get() : (ItemLike)ItemRegistry.KEEPER_FLAMBERGE.get())
      );
   }

   public static Builder prepareAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 25.0)
         .m_22268_(Attributes.f_22278_, 0.8)
         .m_22268_(Attributes.f_22282_, 2.0)
         .m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 3.5)
         .m_22268_(Attributes.f_22279_, 0.19);
   }

   public boolean m_5825_() {
      return true;
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource.m_7640_() instanceof Projectile projectile) {
         pAmount *= 0.75F;
      }

      if (this.f_19797_ < 10 && pSource.m_276093_(DamageTypes.f_268612_)) {
         Utils.doMobBreakSuffocatingBlocks(this);
      }

      return super.m_6469_(pSource, pAmount);
   }

   protected void m_7355_(BlockPos pPos, BlockState pState) {
      this.m_5496_((SoundEvent)SoundRegistry.KEEPER_STEP.get(), 0.25F, 1.0F);
   }

   protected float m_6059_() {
      return this.f_19788_ + 0.8F;
   }

   public boolean m_6673_(DamageSource pSource) {
      return super.m_6673_(pSource) || pSource.m_269533_(DamageTypeTags.f_268549_);
   }

   @Override
   public void playAnimation(String animationId) {
      try {
         KeeperEntity.AttackType attackType = KeeperEntity.AttackType.valueOf(animationId);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   private PlayState predicate(AnimationState<KeeperEntity> animationEvent) {
      AnimationController<KeeperEntity> controller = animationEvent.getController();
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

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      if (this.isSummoned()) {
         pCompound.m_128379_("summoned", true);
      }

      if (this.isRestored()) {
         pCompound.m_128379_("restored", true);
      }
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      if (pCompound.m_128471_("summoned")) {
         this.setIsSummoned();
      }

      if (pCompound.m_128471_("restored")) {
         this.setIsRestored();
      }
   }

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || pEntity.m_6095_().m_204039_(ModTags.INFERNAL_ALLIES);
   }

   public enum AttackType {
      Double_Slash(43, "sword_double_slash", 13, 29),
      Single_Upward(26, "sword_single_upward", 13),
      Single_Horizontal(28, "sword_single_horizontal", 12),
      Single_Horizontal_Fast(24, "sword_single_horizontal_fast", 12),
      Single_Stab(21, "sword_stab", 11),
      Lunge(76, "sword_lunge", 56, 57, 58, 59, 60, 61, 62, 63, 64);

      public final AttackAnimationData data;

      AttackType(int lengthInTicks, String animationId, int... attackTimestamps) {
         this.data = new AttackAnimationData(lengthInTicks, animationId, attackTimestamps);
      }
   }
}
