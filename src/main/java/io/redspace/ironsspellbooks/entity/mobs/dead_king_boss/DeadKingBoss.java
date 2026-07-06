package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.api.util.MusicManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.MomentHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.ExtendedServerBossEvent;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;

public class DeadKingBoss extends AbstractSpellCastingMob implements Enemy, IAnimatedAttacker, IClientEventEntity {
   public static final byte CLIENT_STOP_TRACKING = 0;
   public static final byte CLIENT_START_TRACKING = 1;
   private static final BossbarManager.BossbarSprite BOSSBAR_SPRITE = new BossbarManager.BossbarSprite(
      IronsSpellbooks.id("boss_bars/dead_king_bossbar"), 192, 21, 3, -2
   );
   private static final AttributeModifier MANA_MODIFIER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("mana")), "mana", 2000.0, Operation.ADDITION
   );
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.m_135353_(DeadKingBoss.class, EntityDataSerializers.f_135028_);
   private int transitionAnimationTime = 139;
   private boolean isCloseToGround;
   public boolean isMeleeing;
   private int destroyBlockDelay;
   private ExtendedServerBossEvent bossEvent;
   private final RawAnimation phase_transition_animation = RawAnimation.begin().thenPlay("dead_king_die");
   private final RawAnimation melee = RawAnimation.begin().thenPlay("dead_king_melee");
   private final RawAnimation slam = RawAnimation.begin().thenPlay("dead_king_slam");
   private final AnimationController<DeadKingBoss> transitionController = new AnimationController(this, "dead_king_transition", 0, this::transitionPredicate);
   private final AnimationController<DeadKingBoss> meleeController = new AnimationController(this, "dead_king_animations", 0, this::meleePredicate);
   RawAnimation animationToPlay = null;

   @Override
   public void handleClientEvent(byte eventId) {
      switch (eventId) {
         case 0:
            MusicManager.stopEvent(this.m_20148_());
            BossbarManager.stopTracking(this.f_19820_);
            break;
         case 1:
            BossbarManager.startTracking(this.f_19820_, BOSSBAR_SPRITE);
            MusicManager.createEvent(this, new DeadKingMusicHandler(this));
      }
   }

   public DeadKingBoss(Level pLevel) {
      this((EntityType<? extends AbstractSpellCastingMob>)EntityRegistry.DEAD_KING.get(), pLevel);
      this.m_21530_();
   }

   public DeadKingBoss(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_21530_();
      this.f_21364_ = 60;
      this.f_21365_ = this.createLookControl();
      this.f_21342_ = this.createMoveControl();
      this.createBossEvent();
   }

   private DeadKingAnimatedWarlockAttackGoal getCombatGoal() {
      return (DeadKingAnimatedWarlockAttackGoal)new DeadKingAnimatedWarlockAttackGoal(this, 1.0, 55, 85)
         .setMeleeAttackInverval(0, 20)
         .setSpellQuality(0.3F, 0.5F)
         .setSpells(
            List.of(
               (AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get(),
               (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
               (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
               (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
               (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
               (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
               (AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(),
               (AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(),
               (AbstractSpell)SpellRegistry.POISON_ARROW_SPELL.get(),
               (AbstractSpell)SpellRegistry.POISON_ARROW_SPELL.get(),
               (AbstractSpell)SpellRegistry.BLIGHT_SPELL.get(),
               (AbstractSpell)SpellRegistry.ACID_ORB_SPELL.get()
            ),
            List.of((AbstractSpell)SpellRegistry.FANG_WARD_SPELL.get(), (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()),
            List.of(),
            List.of()
         )
         .setMeleeBias(0.8F, 0.8F)
         .setAllowFleeing(false);
   }

   protected void m_8099_() {
      this.setFirstPhaseGoals();
      this.f_21346_.m_25352_(1, new MomentHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(this, Villager.class, true));
      this.f_21346_.m_25352_(5, new NearestAttackableTargetGoal(this, AbstractIllager.class, true));
   }

   protected void setFirstPhaseGoals() {
      this.f_21345_.m_148105_().forEach(WrappedGoal::m_8041_);
      this.f_21345_.m_262460_(x -> true);
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(), 3, 4, 70, 140, 3));
      this.f_21345_.m_25352_(2, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(), 4, 4, 400, 600, 1));
      this.f_21345_.m_25352_(3, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(), 1, 1, 100, 180, 1));
      this.f_21345_.m_25352_(4, this.getCombatGoal().setSingleUseSpell((AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(), 20, 20, 8, 8));
      this.f_21345_.m_25352_(5, new PatrolNearLocationGoal(this, 32.0F, 0.9F));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   protected void setFinalPhaseGoals() {
      this.f_21345_.m_148105_().forEach(WrappedGoal::m_8041_);
      this.f_21345_.m_262460_(x -> true);
      this.f_21345_.m_25352_(1, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(), 5, 5, 60, 140, 4));
      this.f_21345_.m_25352_(2, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.SUMMON_VEX_SPELL.get(), 2, 4, 200, 400, 1));
      this.f_21345_.m_25352_(3, new DeadKingBoss.DeadKingBarrageGoal(this, (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(), 1, 1, 100, 180, 1));
      this.f_21345_.m_25352_(4, this.getCombatGoal().setIsFlying().setSingleUseSpell((AbstractSpell)SpellRegistry.BLAZE_STORM_SPELL.get(), 10, 30, 10, 10));
      this.f_21345_.m_25352_(5, new PatrolNearLocationGoal(this, 32.0F, 0.9F));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.hasUsedSingleAttack = false;
      this.f_21342_ = new FlyingMoveControl(this, 30, true);
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_12423_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return (SoundEvent)SoundRegistry.DEAD_KING_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)SoundRegistry.DEAD_KING_DEATH.get();
   }

   public void m_7822_(byte pId) {
      if (pId != 3) {
         super.m_7822_(pId);
      }
   }

   public float m_6100_() {
      return 1.0F;
   }

   public boolean m_6094_() {
      return !this.isPhaseTransitioning();
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      this.m_21051_((Attribute)AttributeRegistry.MAX_MANA.get()).m_22125_(MANA_MODIFIER);
      return pSpawnData;
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)ItemRegistry.BLOOD_STAFF.get()));
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
   }

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || pEntity instanceof IMagicSummon summon && summon.getSummoner() == this;
   }

   public boolean m_21222_() {
      return true;
   }

   public void m_8119_() {
      if (this.isPhase(DeadKingBoss.Phases.FinalPhase)) {
         this.m_20242_(true);
         if (this.f_19797_ % 10 == 0) {
            this.isCloseToGround = Utils.raycastForBlock(this.f_19853_, this.m_20182_(), this.m_20182_().m_82492_(0.0, 2.5, 0.0), Fluid.ANY).m_6662_()
               == Type.BLOCK;
         }

         Vec3 woosh = new Vec3(
            Mth.m_14031_(this.f_19797_ * 5 * (float) (Math.PI / 180.0)),
            (Mth.m_14089_((this.f_19797_ * 3 + 986741) * (float) (Math.PI / 180.0)) + (this.isCloseToGround ? 0.05 : -0.185)) * 0.5,
            Mth.m_14031_((this.f_19797_ * 1 + 465) * (float) (Math.PI / 180.0))
         );
         if (this.m_5448_() == null) {
            woosh = woosh.m_82490_(0.25);
         }

         this.m_20256_(this.m_20184_().m_82549_(woosh.m_82490_(0.0085F)));
         if (this.m_5912_() && this.m_5448_() != null && this.m_20280_(this.m_5448_()) > 16.0) {
            this.m_20256_(this.m_20184_().m_82549_(this.m_20156_().m_82490_(0.02)));
         }
      }

      super.m_8119_();
      if (this.f_19853_.f_46443_) {
         if (this.isPhase(DeadKingBoss.Phases.FinalPhase) && !this.m_20145_()) {
            float radius = 0.35F;

            for (int i = 0; i < 5; i++) {
               Vec3 random = this.m_20182_()
                  .m_82549_(
                     new Vec3(
                        (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius,
                        1.0F + (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius,
                        (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius
                     )
                  );
               this.f_19853_.m_7106_(ParticleTypes.f_123762_, random.f_82479_, random.f_82480_, random.f_82481_, 0.0, -0.1, 0.0);
            }
         }
      } else {
         float halfHealth = this.m_21233_() / 2.0F;
         if (this.isPhase(DeadKingBoss.Phases.FirstPhase)) {
            this.bossEvent.m_142711_((this.m_21223_() - halfHealth) / (this.m_21233_() - halfHealth));
            if (this.m_21223_() <= halfHealth) {
               this.setPhase(DeadKingBoss.Phases.Transitioning);
               if (!this.m_21224_()) {
                  this.m_21153_(halfHealth);
               }

               this.m_216990_((SoundEvent)SoundRegistry.DEAD_KING_FAKE_DEATH.get());
               this.m_20331_(true);
               this.getCombatGoal().m_8041_();
               this.cancelCast();
            }
         } else if (this.isPhase(DeadKingBoss.Phases.Transitioning)) {
            if (--this.transitionAnimationTime <= 0) {
               this.setPhase(DeadKingBoss.Phases.FinalPhase);
               MagicManager.spawnParticles(
                  this.f_19853_,
                  ParticleHelper.FIRE,
                  this.m_20182_().f_82479_,
                  this.m_20182_().f_82480_ + 2.5,
                  this.m_20182_().f_82481_,
                  80,
                  0.2,
                  0.2,
                  0.2,
                  0.25,
                  true
               );
               this.setFinalPhaseGoals();
               this.m_20242_(true);
               this.m_216990_((SoundEvent)SoundRegistry.DEAD_KING_EXPLODE.get());
               this.f_19853_
                  .m_6249_(
                     this,
                     this.m_20191_().m_82400_(5.0),
                     entity -> entity instanceof LivingEntity && entity.m_6087_() && entity.m_20238_(this.m_20182_()) < 25.0
                  )
                  .forEach(x$0 -> super.m_7327_(x$0));
               this.m_20331_(false);
            }
         } else if (this.isPhase(DeadKingBoss.Phases.FinalPhase)) {
            this.bossEvent.m_142711_(this.m_21223_() / (this.m_21233_() - halfHealth));
         }
      }

      if (this.destroyBlockDelay > 0) {
         this.destroyBlockDelay--;
      }
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public boolean isPhase(DeadKingBoss.Phases phase) {
      return phase.value == this.getPhase();
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource == this.f_19853_.m_269111_().m_269233_()) {
         return false;
      }

      if (pSource.m_276093_(DamageTypes.f_268612_) && this.destroyBlockDelay <= 0) {
         Utils.doMobBreakSuffocatingBlocks(this);
         this.destroyBlockDelay = 40;
      }

      Entity entity = pSource.m_7639_();
      if (entity != null) {
         float distance = entity.m_20270_(this);
         float damageReduction = Mth.m_144920_(1.0F, 0.5F, (distance - 8.0F) / 16.0F);
         pAmount *= damageReduction;
      }

      return super.m_6469_(pSource, pAmount);
   }

   protected boolean m_6107_() {
      return this.isPhase(DeadKingBoss.Phases.Transitioning) || super.m_6107_();
   }

   public boolean isPhaseTransitioning() {
      return this.isPhase(DeadKingBoss.Phases.Transitioning);
   }

   public void m_6457_(ServerPlayer pPlayer) {
      super.m_6457_(pPlayer);
      this.bossEvent.addPlayer(pPlayer);
      PacketDistributor.sendToPlayer(pPlayer, new EntityEventPacket(this, (byte)1));
   }

   public void m_6452_(ServerPlayer pPlayer) {
      super.m_6452_(pPlayer);
      this.bossEvent.removePlayer(pPlayer);
      PacketDistributor.sendToPlayer(pPlayer, new EntityEventPacket(this, (byte)0));
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_((Attribute)AttributeRegistry.SPELL_POWER.get(), 1.15)
         .m_22268_(Attributes.f_22284_, 15.0)
         .m_22268_((Attribute)AttributeRegistry.SPELL_RESIST.get(), 1.0)
         .m_22268_(Attributes.f_22276_, 500.0)
         .m_22268_(Attributes.f_22278_, 0.8)
         .m_22268_(Attributes.f_22282_, 0.6)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22280_, 0.155)
         .m_22268_(Attributes.f_22279_, 0.155);
   }

   public void m_6593_(@javax.annotation.Nullable Component pName) {
      super.m_6593_(pName);
      this.bossEvent.m_6456_(this.m_5446_());
   }

   private void setPhase(int phase) {
      this.f_19804_.m_135381_(PHASE, phase);
   }

   private void setPhase(DeadKingBoss.Phases phase) {
      this.setPhase(phase.value);
   }

   public int getPhase() {
      return (Integer)this.f_19804_.m_135370_(PHASE);
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("phase", this.getPhase());
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      if (this.m_8077_()) {
         this.bossEvent.m_6456_(this.m_5446_());
      }

      this.setPhase(pCompound.m_128451_("phase"));
      if (this.isPhase(DeadKingBoss.Phases.FinalPhase)) {
         this.setFinalPhaseGoals();
      }
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(PHASE, 0);
   }

   @Override
   public void playAnimation(String animationId) {
      try {
         DeadKingBoss.AttackType attackType = DeadKingBoss.AttackType.valueOf(animationId);
         this.animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
      } catch (Exception ignored) {
         IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
      }
   }

   private PlayState meleePredicate(AnimationState<DeadKingBoss> animationEvent) {
      AnimationController<DeadKingBoss> controller = animationEvent.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return this.transitionController.getAnimationState() == State.STOPPED ? PlayState.CONTINUE : PlayState.STOP;
   }

   private PlayState transitionPredicate(AnimationState animationEvent) {
      AnimationController controller = animationEvent.getController();
      if (this.isPhaseTransitioning()) {
         controller.setAnimation(this.phase_transition_animation);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{this.transitionController});
      controllerRegistrar.add(new AnimationController[]{this.meleeController});
      super.registerControllers(controllerRegistrar);
   }

   @Override
   public boolean shouldAlwaysAnimateHead() {
      return !this.isPhaseTransitioning();
   }

   @Override
   public boolean bobBodyWhileWalking() {
      return this.isPhase(DeadKingBoss.Phases.FirstPhase);
   }

   @Override
   public boolean isAnimating() {
      return this.transitionController.getAnimationState() != State.STOPPED || this.meleeController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   public boolean m_7327_(Entity pEntity) {
      this.f_19853_
         .m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)SoundRegistry.DEAD_KING_HIT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
      return super.m_7327_(pEntity);
   }

   @Override
   public boolean shouldAlwaysAnimateLegs() {
      return this.isPhase(DeadKingBoss.Phases.FirstPhase);
   }

   @Override
   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float m_24956_(float pFrom, float pTo, float pMaxDelta) {
            return super.m_24956_(pFrom, pTo, pMaxDelta * 2.5F);
         }

         protected boolean m_8106_() {
            return !DeadKingBoss.this.isCasting();
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

   public void m_20258_(CompoundTag compound) {
      super.m_20258_(compound);
      if (!this.f_19853_.f_46443_) {
         this.createBossEvent();
      }
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new NotIdioticNavigation(this, pLevel);
   }

   protected void createBossEvent() {
      this.bossEvent = (ExtendedServerBossEvent)new ExtendedServerBossEvent(this.m_20148_(), this.m_5446_(), BossBarColor.RED, BossBarOverlay.PROGRESS)
         .m_7003_(true)
         .m_7006_(true);
   }

   public enum AttackType {
      DOUBLE_SWING(51, "dead_king_double_swing", 16, 36),
      SLAM(48, "dead_king_slam", 30);

      public final AttackAnimationData data;

      AttackType(int lengthInTicks, String animationId, int... attackTimestamps) {
         this.data = new AttackAnimationData(lengthInTicks, animationId, attackTimestamps);
      }
   }

   private class DeadKingBarrageGoal extends SpellBarrageGoal {
      public DeadKingBarrageGoal(
         IMagicEntity abstractSpellCastingMob,
         AbstractSpell spell,
         int minLevel,
         int maxLevel,
         int pAttackIntervalMin,
         int pAttackIntervalMax,
         int projectileCount
      ) {
         super(abstractSpellCastingMob, spell, minLevel, maxLevel, pAttackIntervalMin, pAttackIntervalMax, projectileCount);
      }

      @Override
      public boolean m_8036_() {
         return !DeadKingBoss.this.isMeleeing && super.m_8036_();
      }
   }

   public enum Phases {
      FirstPhase(0),
      Transitioning(1),
      FinalPhase(2);

      final int value;

      Phases(int value) {
         this.value = value;
      }
   }
}
