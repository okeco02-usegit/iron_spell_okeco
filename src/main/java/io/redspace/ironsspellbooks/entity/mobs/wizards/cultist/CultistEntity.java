package io.redspace.ironsspellbooks.entity.mobs.wizards.cultist;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;

public class CultistEntity extends NeutralWizard implements Enemy, IAnimatedAttacker {
   RawAnimation animationToPlay = null;
   private final AnimationController<CultistEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);

   public CultistEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
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
            return CultistEntity.this.m_5448_() == null;
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

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.DEVOUR_SPELL.get(), 3, 6, 100, 250, 1));
      this.f_21345_
         .m_25352_(
            3,
            new GenericAnimatedWarlockAttackGoal<>(this, 1.25, 50, 75)
               .setMoveset(
                  List.of(
                     new AttackAnimationData(9, "simple_sword_upward_swipe", 5),
                     new AttackAnimationData(8, "simple_sword_lunge_stab", 6),
                     new AttackAnimationData(10, "simple_sword_stab_alternate", 8),
                     new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe", 8)
                  )
               )
               .setComboChance(0.4F)
               .setMeleeAttackInverval(10, 30)
               .setMeleeMovespeedModifier(1.5F)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get()),
                  List.of(),
                  List.of()
               )
               .setDrinksPotions()
         );
      this.f_21345_.m_25352_(4, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isHostileTowards));
      this.f_21346_.m_25352_(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
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
   public boolean shouldSheathSword() {
      return true;
   }

   @Override
   public void playAnimation(String animationId) {
      try {
         this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   private PlayState predicate(AnimationState<CultistEntity> animationEvent) {
      AnimationController<CultistEntity> controller = animationEvent.getController();
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

   @Override
   public boolean guardsBlocks() {
      return false;
   }

   @Override
   public boolean isHostileTowards(LivingEntity pTarget) {
      return super.isHostileTowards(pTarget) || pTarget.m_21133_((Attribute)AttributeRegistry.BLOOD_SPELL_POWER.get()) < 1.15;
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new NotIdioticNavigation(this, pLevel);
   }
}
