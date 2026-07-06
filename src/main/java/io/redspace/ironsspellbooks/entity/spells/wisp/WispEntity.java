package io.redspace.ironsspellbooks.entity.spells.wisp;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WispAttackGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.spells.holy.WispSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WispEntity extends PathfinderMob implements GeoEntity {
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   private final RawAnimation animation = RawAnimation.begin().thenPlay("animation.wisp.flying");
   private Vec3 targetSearchStart;
   private Vec3 lastTickPos;
   private float damageAmount;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WispEntity(EntityType<? extends WispEntity> entityType, Level level) {
      super(entityType, level);
      this.m_20242_(true);
   }

   public WispEntity(Level levelIn, LivingEntity owner, float damageAmount) {
      this((EntityType<? extends WispEntity>)EntityRegistry.WISP.get(), levelIn);
      this.f_21342_ = new FlyingMoveControl(this, 20, true);
      this.damageAmount = damageAmount;
      this.setOwner(owner);
      float xRot = owner.m_146909_();
      float yRot = owner.m_146908_();
      float yHeadRot = owner.m_6080_();
      this.m_146922_(yRot);
      this.m_146926_(xRot);
      this.m_5618_(yRot);
      this.m_5616_(yHeadRot);
      this.lastTickPos = this.m_20182_();
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(2, new WispAttackGoal(this, 1.0));
   }

   public static boolean isValidTarget(@Nullable Entity entity) {
      return entity instanceof LivingEntity livingEntity && livingEntity.m_6084_() && livingEntity instanceof Enemy;
   }

   @org.jetbrains.annotations.Nullable
   public LivingEntity m_5448_() {
      return super.m_5448_();
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19853_.f_46443_) {
         this.spawnParticles();
      } else {
         LivingEntity target = this.m_5448_();
         if (target != null && !target.m_213877_()) {
            if (this.m_20191_().m_82381_(target.m_20191_())) {
               DamageSources.applyDamage(target, this.damageAmount, ((AbstractSpell)SpellRegistry.WISP_SPELL.get()).getDamageSource(this, this.cachedOwner));
               this.m_5496_(WispSpell.getImpactSound(), 1.0F, 1.0F);
               Vec3 p = target.m_146892_();
               MagicManager.spawnParticles(this.f_19853_, ParticleHelper.WISP, p.f_82479_, p.f_82480_, p.f_82481_, 25, 0.0, 0.0, 0.0, 0.18, true);
               this.m_146870_();
            }
         } else if (this.f_19797_ > 10) {
            this.popAndDie();
         }
      }

      this.lastTickPos = this.m_20182_();
   }

   public void setOwner(@Nullable Entity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.m_20148_();
         this.cachedOwner = pOwner;
      }
   }

   @NotNull
   protected PathNavigation m_6037_(Level pLevel) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
         public boolean m_6342_(BlockPos blockPos) {
            return !this.f_26495_.m_8055_(blockPos.m_7495_()).m_60795_();
         }

         public void m_7638_() {
            super.m_7638_();
         }
      };
      flyingpathnavigation.m_26440_(false);
      flyingpathnavigation.m_7008_(true);
      flyingpathnavigation.m_26443_(true);
      return flyingpathnavigation;
   }

   public void m_7023_(Vec3 pTravelVector) {
      if (this.m_21515_() || this.m_6109_()) {
         if (this.m_20069_()) {
            this.m_19920_(0.02F, pTravelVector);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            this.m_20256_(this.m_20184_().m_82490_(0.8F));
         } else if (this.m_20077_()) {
            this.m_19920_(0.02F, pTravelVector);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            this.m_20256_(this.m_20184_().m_82490_(0.5));
         } else {
            this.m_19920_(this.m_6113_(), pTravelVector);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            this.m_20256_(this.m_20184_().m_82490_(0.91F));
         }
      }

      this.m_267651_(false);
   }

   public boolean m_20068_() {
      return true;
   }

   public void m_6710_(@org.jetbrains.annotations.Nullable LivingEntity target) {
      super.m_6710_(target);
   }

   protected void m_8024_() {
      if (this.cachedOwner == null || !this.cachedOwner.m_6084_()) {
         this.m_146870_();
      }
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.animation);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "controller", 0, this::predicate)});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22280_, 0.2)
         .m_22268_(Attributes.f_22279_, 0.2);
   }

   public Iterable<ItemStack> m_6168_() {
      return Collections.singleton(ItemStack.f_41583_);
   }

   public ItemStack m_6844_(EquipmentSlot pSlot) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (!this.f_19853_.f_46443_) {
         this.popAndDie();
      }

      return true;
   }

   private void popAndDie() {
      this.m_5496_(SoundEvents.f_12411_, 1.0F, 1.0F);
      ((ServerLevel)this.f_19853_).m_8767_(ParticleTypes.f_123797_, this.m_20185_(), this.m_20186_(), this.m_20189_(), 15, 0.2, 0.2, 0.2, 0.0);
      this.m_146870_();
   }

   public HumanoidArm m_5737_() {
      return HumanoidArm.LEFT;
   }

   public void spawnParticles() {
   }
}
