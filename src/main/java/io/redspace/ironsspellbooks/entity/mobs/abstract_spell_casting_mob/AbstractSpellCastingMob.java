package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import com.google.common.collect.Maps;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.spells.fire.BurningDashSpell;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractSpellCastingMob extends PathfinderMob implements GeoEntity, IMagicEntity {
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/abstract_casting_mob.geo.json");
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/entity/abstract_casting_mob/abstract_casting_mob.png"
   );
   public static final ResourceLocation animationInstantCast = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/casting_animations.json");
   private static final EntityDataAccessor<Boolean> DATA_CANCEL_CAST = SynchedEntityData.m_135353_(
      AbstractSpellCastingMob.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Boolean> DATA_DRINKING_POTION = SynchedEntityData.m_135353_(
      AbstractSpellCastingMob.class, EntityDataSerializers.f_135035_
   );
   private final MagicData playerMagicData = new MagicData(true);
   private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(
      UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E"), "Drinking speed penalty", -0.15, Operation.MULTIPLY_TOTAL
   );
   @Nullable
   private SpellData castingSpell;
   private final HashMap<String, AbstractSpell> spells = Maps.newHashMap();
   private int drinkTime;
   public boolean hasUsedSingleAttack;
   private boolean recreateSpell;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private AbstractSpell lastCastSpellType = SpellRegistry.none();
   private AbstractSpell instantCastSpellType = SpellRegistry.none();
   private boolean cancelCastAnimation = false;
   private boolean animatingLegs = false;
   private final AnimationController animationControllerOtherCast = new AnimationController(this, "other_casting", 0, this::otherCastingPredicate);
   private final AnimationController animationControllerInstantCast = new AnimationController(this, "instant_casting", 0, this::instantCastingPredicate);
   private final AnimationController animationControllerLongCast = new AnimationController(this, "long_casting", 0, this::longCastingPredicate);

   protected AbstractSpellCastingMob(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.playerMagicData.setSyncedData(new SyncedSpellData(this));
      this.f_19811_ = true;
      this.f_21365_ = this.createLookControl();
   }

   @Override
   public boolean getHasUsedSingleAttack() {
      return this.hasUsedSingleAttack;
   }

   @Override
   public void setHasUsedSingleAttack(boolean hasUsedSingleAttack) {
      this.hasUsedSingleAttack = hasUsedSingleAttack;
   }

   public void m_6083_() {
      super.m_6083_();
      if (this.m_20202_() instanceof PathfinderMob pathfindermob) {
         pathfindermob.f_20883_ = this.f_20883_;
      }
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected boolean m_8106_() {
            return AbstractSpellCastingMob.this.m_5448_() == null;
         }
      };
   }

   @Override
   public MagicData getMagicData() {
      return this.playerMagicData;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_CANCEL_CAST, false);
      this.f_19804_.m_135372_(DATA_DRINKING_POTION, false);
   }

   @Override
   public boolean isDrinkingPotion() {
      return (Boolean)this.f_19804_.m_135370_(DATA_DRINKING_POTION);
   }

   protected void setDrinkingPotion(boolean drinkingPotion) {
      this.f_19804_.m_135381_(DATA_DRINKING_POTION, drinkingPotion);
   }

   public boolean m_6573_(Player pPlayer) {
      return false;
   }

   @Override
   public void startDrinkingPotion() {
      if (!this.f_19853_.f_46443_) {
         this.setDrinkingPotion(true);
         this.drinkTime = 35;
         AttributeInstance attributeinstance = this.m_21051_(Attributes.f_22279_);
         attributeinstance.m_22130_(SPEED_MODIFIER_DRINKING);
         attributeinstance.m_22118_(SPEED_MODIFIER_DRINKING);
      }
   }

   private void finishDrinkingPotion() {
      this.setDrinkingPotion(false);
      this.m_5634_(Math.min(Math.max(10.0F, this.m_21233_() / 10.0F), this.m_21233_() / 4.0F));
      this.m_21051_(Attributes.f_22279_).m_22130_(SPEED_MODIFIER_DRINKING);
      if (!this.m_20067_()) {
         this.f_19853_
            .m_6263_(
               null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_12551_, this.m_5720_(), 1.0F, 0.8F + this.f_19796_.m_188501_() * 0.4F
            );
      }
   }

   public void m_7350_(EntityDataAccessor<?> pKey) {
      super.m_7350_(pKey);
      if (this.f_19853_.f_46443_) {
         if (pKey.m_135015_() == DATA_CANCEL_CAST.m_135015_()) {
            this.cancelCast();
         }
      }
   }

   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      this.playerMagicData.getSyncedData().saveNBTData(pCompound, this.f_19853_.m_9598_());
      pCompound.m_128379_("usedSpecial", this.hasUsedSingleAttack);
   }

   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      SyncedSpellData syncedSpellData = new SyncedSpellData(this);
      syncedSpellData.loadNBTData(pCompound, this.f_19853_.m_9598_());
      if (syncedSpellData.isCasting()) {
         this.recreateSpell = true;
      }

      this.playerMagicData.setSyncedData(syncedSpellData);
      this.hasUsedSingleAttack = pCompound.m_128471_("usedSpecial");
   }

   @Override
   public void cancelCast() {
      if (this.isCasting()) {
         if (this.f_19853_.f_46443_) {
            this.cancelCastAnimation = true;
         } else {
            this.f_19804_.m_135381_(DATA_CANCEL_CAST, !(Boolean)this.f_19804_.m_135370_(DATA_CANCEL_CAST));
         }

         this.castComplete();
      }
   }

   @Override
   public void castComplete() {
      if (!this.f_19853_.f_46443_) {
         if (this.castingSpell != null) {
            this.castingSpell.getSpell().onServerCastComplete(this.f_19853_, this.castingSpell.getLevel(), this, this.playerMagicData, false);
         }
      } else {
         this.playerMagicData.resetCastingState();
      }

      this.castingSpell = null;
   }

   @Override
   public void setSyncedSpellData(SyncedSpellData syncedSpellData) {
      if (this.f_19853_.f_46443_) {
         boolean isCasting = this.playerMagicData.isCasting();
         this.playerMagicData.setSyncedData(syncedSpellData);
         this.castingSpell = this.playerMagicData.getCastingSpell();
         if (this.castingSpell != null) {
            if (!this.playerMagicData.isCasting() && isCasting) {
               this.castComplete();
            } else if (this.playerMagicData.isCasting() && !isCasting) {
               AbstractSpell spell = this.playerMagicData.getCastingSpell().getSpell();
               this.initiateCastSpell(spell, this.playerMagicData.getCastingSpellLevel());
               if (this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
                  this.instantCastSpellType = this.castingSpell.getSpell();
                  this.castingSpell
                     .getSpell()
                     .onClientPreCast(this.f_19853_, this.castingSpell.getLevel(), this, InteractionHand.MAIN_HAND, this.playerMagicData);
                  this.castComplete();
               }
            }
         }
      }
   }

   protected void m_8024_() {
      super.m_8024_();
      if (this.recreateSpell) {
         this.recreateSpell = false;
         SyncedSpellData syncedSpellData = this.playerMagicData.getSyncedData();
         AbstractSpell spell = SpellRegistry.getSpell(syncedSpellData.getCastingSpellId());
         this.initiateCastSpell(spell, syncedSpellData.getCastingSpellLevel());
      }

      if (this.isDrinkingPotion()) {
         if (this.drinkTime-- <= 0) {
            this.finishDrinkingPotion();
         } else if (this.drinkTime % 4 == 0 && !this.m_20067_()) {
            this.f_19853_
               .m_6263_(
                  null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11911_, this.m_5720_(), 1.0F, Utils.random.m_188501_() * 0.1F + 0.9F
               );
         }
      }

      if (this.castingSpell != null) {
         this.playerMagicData.handleCastDuration();
         if (this.playerMagicData.isCasting()) {
            this.castingSpell.getSpell().onServerCastTick(this.f_19853_, this.castingSpell.getLevel(), this, this.playerMagicData);
         }

         this.forceLookAtTarget(this.m_5448_());
         if (this.playerMagicData.getCastDurationRemaining() <= 0) {
            if (this.castingSpell.getSpell().getCastType() == CastType.LONG || this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
               this.castingSpell.getSpell().onCast(this.f_19853_, this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
            }

            this.castComplete();
         } else if (this.castingSpell.getSpell().getCastType() == CastType.CONTINUOUS && (this.playerMagicData.getCastDurationRemaining() + 1) % 10 == 0) {
            this.castingSpell.getSpell().onCast(this.f_19853_, this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
         }
      }
   }

   @Override
   public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
      if (spell == SpellRegistry.none()) {
         this.castingSpell = null;
      } else {
         if (this.f_19853_.f_46443_) {
            this.cancelCastAnimation = false;
         }

         this.castingSpell = new SpellData(spell, spellLevel);
         if (this.m_5448_() != null) {
            this.forceLookAtTarget(this.m_5448_());
         }

         if (!this.f_19853_.f_46443_ && !this.castingSpell.getSpell().checkPreCastConditions(this.f_19853_, spellLevel, this, this.playerMagicData)) {
            this.castingSpell = null;
         } else {
            if (spell == SpellRegistry.TELEPORT_SPELL.get() || spell == SpellRegistry.FROST_STEP_SPELL.get()) {
               this.setTeleportLocationBehindTarget(10);
            } else if (spell == SpellRegistry.BLOOD_STEP_SPELL.get()) {
               this.setTeleportLocationBehindTarget(3);
            } else if (spell == SpellRegistry.BURNING_DASH_SPELL.get()) {
               this.setBurningDashDirectionData();
            }

            this.playerMagicData
               .initiateCast(
                  this.castingSpell.getSpell(),
                  this.castingSpell.getLevel(),
                  this.castingSpell.getSpell().getEffectiveCastTime(this.castingSpell.getLevel(), this),
                  CastSource.MOB,
                  SpellSelectionManager.MAINHAND
               );
            if (!this.f_19853_.f_46443_) {
               this.castingSpell.getSpell().onServerPreCast(this.f_19853_, this.castingSpell.getLevel(), this, this.playerMagicData);
            }
         }
      }
   }

   @Override
   public void notifyDangerousProjectile(Projectile projectile) {
   }

   @Override
   public boolean isCasting() {
      return this.playerMagicData.isCasting();
   }

   @Override
   public boolean setTeleportLocationBehindTarget(int distance) {
      LivingEntity target = this.m_5448_();
      boolean valid = false;
      if (target != null) {
         Vec3 rotation = target.m_20154_().m_82541_().m_82490_(-distance);
         Vec3 pos = target.m_20182_();
         Vec3 teleportPos = rotation.m_82549_(pos);

         for (int i = 0; i < 24; i++) {
            Vec3 randomness = Utils.getRandomVec3(0.15F * i).m_82542_(1.0, 0.0, 1.0);
            Vec3 var10 = Utils.moveToRelativeGroundLevel(
               this.f_19853_,
               target.m_20182_()
                  .m_82546_(new Vec3(0.0, 0.0, (float)distance / (i / 7 + 1)).m_82524_(-(target.m_146908_() + i * 45) * (float) (Math.PI / 180.0)))
                  .m_82549_(randomness),
               5
            );
            teleportPos = new Vec3(var10.f_82479_, var10.f_82480_ + 0.1F, var10.f_82481_);
            AABB reposBB = this.m_20191_().m_82383_(teleportPos.m_82546_(this.m_20182_()));
            if (!this.f_19853_.m_186437_(this, reposBB.m_82400_(-0.05F))) {
               valid = true;
               break;
            }
         }

         if (valid) {
            this.playerMagicData.setAdditionalCastData(new TeleportSpell.TeleportData(teleportPos));
         } else {
            this.playerMagicData.setAdditionalCastData(new TeleportSpell.TeleportData(this.m_20182_()));
         }
      } else {
         this.playerMagicData.setAdditionalCastData(new TeleportSpell.TeleportData(this.m_20182_()));
      }

      return valid;
   }

   @Override
   public void setBurningDashDirectionData() {
      this.playerMagicData.setAdditionalCastData(new BurningDashSpell.BurningDashDirectionOverrideCastData());
   }

   private void forceLookAtTarget(LivingEntity target) {
      if (target != null) {
         double d0 = target.m_20185_() - this.m_20185_();
         double d2 = target.m_20189_() - this.m_20189_();
         double d1 = target.m_20188_() - this.m_20188_();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
         float f1 = (float)(-(Mth.m_14136_(d1, d3) * 180.0F / (float)Math.PI));
         this.m_146926_(f1 % 360.0F);
         this.m_146922_(f % 360.0F);
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void triggerAnim(@org.jetbrains.annotations.Nullable String controllerName, String animName) {
      super.triggerAnim(controllerName, animName);
   }

   public double getBoneResetTime() {
      return 5.0;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{this.animationControllerOtherCast});
      controllerRegistrar.add(new AnimationController[]{this.animationControllerInstantCast});
      controllerRegistrar.add(new AnimationController[]{this.animationControllerLongCast});
   }

   private PlayState instantCastingPredicate(AnimationState event) {
      if (this.cancelCastAnimation) {
         return PlayState.STOP;
      }

      AnimationController controller = event.getController();
      if (this.instantCastSpellType != SpellRegistry.none() && controller.getAnimationState() == State.STOPPED) {
         this.setStartAnimationFromSpell(controller, this.instantCastSpellType);
         this.instantCastSpellType = SpellRegistry.none();
      }

      return PlayState.CONTINUE;
   }

   private PlayState longCastingPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      if (!this.cancelCastAnimation
         && (
            controller.getAnimationState() != State.STOPPED
               || this.isCasting() && this.castingSpell != null && this.castingSpell.getSpell().getCastType() == CastType.LONG
         )) {
         if (this.isCasting()) {
            if (controller.getAnimationState() == State.STOPPED) {
               this.setStartAnimationFromSpell(controller, this.castingSpell.getSpell());
            }
         } else if (this.lastCastSpellType.getCastType() == CastType.LONG) {
            this.setFinishAnimationFromSpell(controller, this.lastCastSpellType);
         }

         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState otherCastingPredicate(AnimationState event) {
      if (this.cancelCastAnimation) {
         return PlayState.STOP;
      }

      AnimationController controller = event.getController();
      if (this.isCasting() && this.castingSpell != null && controller.getAnimationState() == State.STOPPED) {
         if (this.castingSpell.getSpell().getCastType() == CastType.CONTINUOUS) {
            this.setStartAnimationFromSpell(controller, this.castingSpell.getSpell());
         }

         return PlayState.CONTINUE;
      } else {
         return this.isCasting() ? PlayState.CONTINUE : PlayState.STOP;
      }
   }

   private void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
      spell.getCastStartAnimation().getForMob().ifPresentOrElse(animationBuilder -> {
         controller.forceAnimationReset();
         controller.setAnimation(animationBuilder);
         this.lastCastSpellType = spell;
         this.cancelCastAnimation = false;
         this.animatingLegs = spell.getCastStartAnimation().animatesLegs;
      }, () -> this.cancelCastAnimation = true);
   }

   private void setFinishAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
      if (spell.getCastFinishAnimation().isPass) {
         this.cancelCastAnimation = false;
      } else {
         spell.getCastFinishAnimation().getForMob().ifPresentOrElse(animationBuilder -> {
            controller.forceAnimationReset();
            controller.setAnimation(animationBuilder);
            this.lastCastSpellType = SpellRegistry.none();
            this.cancelCastAnimation = false;
         }, () -> this.cancelCastAnimation = true);
      }
   }

   public boolean isAnimating() {
      return this.isCasting()
         || this.animationControllerLongCast.getAnimationState() == State.RUNNING
         || this.animationControllerOtherCast.getAnimationState() == State.RUNNING
         || this.animationControllerInstantCast.getAnimationState() == State.RUNNING;
   }

   public boolean shouldBeExtraAnimated() {
      return true;
   }

   public boolean shouldAlwaysAnimateHead() {
      return true;
   }

   public boolean shouldAlwaysAnimateLegs() {
      return !this.animatingLegs;
   }

   public boolean shouldPointArmsWhileCasting() {
      return true;
   }

   public boolean bobBodyWhileWalking() {
      return true;
   }

   public boolean shouldSheathSword() {
      return false;
   }
}
