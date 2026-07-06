package io.redspace.ironsspellbooks.entity.spells.root;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RootEntity extends LivingEntity implements GeoEntity, PreventDismount, AntiMagicSusceptible {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int duration;
   private boolean playSound = true;
   private LivingEntity target;
   private boolean played = false;
   private final RawAnimation ANIMATION = RawAnimation.begin().thenPlay("emerge");
   private final AnimationController controller = new AnimationController(this, "root_controller", 0, this::animationPredicate);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public float m_6134_() {
      return this.target == null ? 1.0F : this.target.m_6134_();
   }

   public RootEntity(EntityType<? extends RootEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public RootEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends RootEntity>)EntityRegistry.ROOT.get(), level);
      this.setOwner(owner);
   }

   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(LivingEntity target) {
      this.target = target;
   }

   public boolean m_7337_(@NotNull Entity pEntity) {
      return false;
   }

   public boolean m_5829_() {
      return false;
   }

   protected void m_7324_(@NotNull Entity pEntity) {
   }

   public void m_7334_(@NotNull Entity pEntity) {
   }

   protected void m_6138_() {
   }

   public boolean m_275843_() {
      return false;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public double m_6048_() {
      return 0.0;
   }

   public boolean shouldRiderFaceForward(@NotNull Player player) {
      return false;
   }

   public EntityDimensions m_6972_(Pose pPose) {
      Entity rooted = this.m_146895_();
      return rooted != null ? EntityDimensions.m_20398_(rooted.m_20205_() * 1.25F, 0.75F) : super.m_6972_(pPose);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.playSound) {
         this.m_6210_();
         this.m_5496_((SoundEvent)SoundRegistry.ROOT_EMERGE.get(), 2.0F, 1.0F);
         this.playSound = false;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ > this.duration || this.target != null && this.target.m_21224_() || !this.m_20160_()) {
            this.removeRoot();
         }
      } else if (this.f_19797_ < 20) {
         this.clientDiggingParticles(this);
      }
   }

   protected void clientDiggingParticles(LivingEntity livingEntity) {
      RandomSource randomsource = livingEntity.m_217043_();
      BlockState blockstate = livingEntity.m_20075_();
      if (blockstate.m_60799_() != RenderShape.INVISIBLE) {
         for (int i = 0; i < 15; i++) {
            double d0 = livingEntity.m_20185_() + Mth.m_216283_(randomsource, -0.5F, 0.5F);
            double d1 = livingEntity.m_20186_();
            double d2 = livingEntity.m_20189_() + Mth.m_216283_(randomsource, -0.5F, 0.5F);
            livingEntity.m_9236_().m_7106_(new BlockParticleOption(ParticleTypes.f_123794_, blockstate), d0, d1, d2, 0.0, 0.0, 0.0);
         }
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.m_20148_();
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.m_9236_() instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   public void removeRoot() {
      if (this.m_9236_().f_46443_) {
         for (int i = 0; i < 5; i++) {
            this.m_9236_()
               .m_7106_(
                  ParticleHelper.ROOT_FOG,
                  this.m_20185_() + Utils.getRandomScaled(0.1F),
                  this.m_20186_() + Utils.getRandomScaled(0.1F),
                  this.m_20189_() + Utils.getRandomScaled(0.1F),
                  Utils.getRandomScaled(2.0),
                  -this.f_19796_.m_188501_() * 0.5F,
                  Utils.getRandomScaled(2.0)
               );
         }
      }

      this.m_20153_();
      this.m_146870_();
   }

   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("Age", this.f_19797_);
      if (this.ownerUUID != null) {
         pCompound.m_128362_("Owner", this.ownerUUID);
      }

      pCompound.m_128405_("Duration", this.duration);
   }

   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.f_19797_ = pCompound.m_128451_("Age");
      if (pCompound.m_128403_("Owner")) {
         this.ownerUUID = pCompound.m_128342_("Owner");
      }

      this.duration = pCompound.m_128451_("Duration");
   }

   public boolean m_20367_(Entity pEntity) {
      return true;
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.removeRoot();
   }

   public HumanoidArm m_5737_() {
      return HumanoidArm.RIGHT;
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_21275_(DamageSource pDamageSource) {
      return true;
   }

   public boolean m_20152_() {
      return false;
   }

   public void m_147240_(double pStrength, double pX, double pZ) {
   }

   public void m_19956_(Entity passenger, MoveFunction p_19958_) {
      int x = (int)(this.m_20185_() - passenger.m_20185_());
      int y = (int)(this.m_20186_() - passenger.m_20186_());
      int z = (int)(this.m_20189_() - passenger.m_20189_());
      x *= x;
      y *= y;
      z *= z;
      if (x + y + z > 25) {
         this.removeRoot();
      } else {
         passenger.m_6034_(this.m_20185_(), this.m_20186_(), this.m_20189_());
      }
   }

   protected boolean m_6107_() {
      return true;
   }

   public boolean m_5801_() {
      return false;
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource.m_269533_(DamageTypeTags.f_268738_)) {
         this.removeRoot();
         return true;
      } else {
         return false;
      }
   }

   public Iterable<ItemStack> m_6168_() {
      return Collections.singleton(ItemStack.f_41583_);
   }

   public ItemStack m_6844_(EquipmentSlot pSlot) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot pSlot, ItemStack pStack) {
   }

   private PlayState animationPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      if (!this.played && controller.getAnimationState() == State.STOPPED) {
         controller.forceAnimationReset();
         controller.setAnimation(this.ANIMATION);
         this.played = true;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{this.controller});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
