package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.FogManager;
import io.redspace.ironsspellbooks.api.util.MusicManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.goals.MomentHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.spells.FireEruptionAoe;
import io.redspace.ironsspellbooks.entity.spells.fireball.MagicFireball;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
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

public class FireBossEntity extends AbstractSpellCastingMob implements Enemy, IAnimatedAttacker, IEntityAdditionalSpawnData, IClientEventEntity {
   public static final byte CLIENT_STOP_TRACKING = 0;
   public static final byte CLIENT_START_TRACKING = 1;
   public static final byte PROC_HALF_HEALTH_TIMER = 2;
   public static final byte STOP_HALF_HEALTH_TIMER = 3;
   public static final byte START_MUSIC = 4;
   public static final byte STOP_MUSIC = 5;
   public static final byte PROC_SPECTRAL_DAGGER = 6;
   public static final float DEFAULT_SCALE = 1.75F;
   public static final float SOUL_MODE_SCALE = 2.0125F;
   public static final int PROC_DESPAWN_SECONDS = 60;
   public static final int UNLOADED_DESPAWN_LIMIT_SECONDS = 300;
   private static final BossbarManager.BossbarSprite BOSSBAR_SPRITE = new BossbarManager.BossbarSprite(
      IronsSpellbooks.id("boss_bars/tyros_bossbar"), 192, 18, 3, -1
   );
   private static final EntityDataAccessor<Boolean> DATA_SOUL_MODE = SynchedEntityData.m_135353_(FireBossEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_IS_DESPAWNING = SynchedEntityData.m_135353_(FireBossEntity.class, EntityDataSerializers.f_135035_);
   private static final AttributeModifier SOUL_SPEED_MODIFIER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("soul_mode")), "soul_mode", 0.05, Operation.MULTIPLY_TOTAL
   );
   private static final AttributeModifier SOUL_SCALE_MODIFIER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("soul_mode")), "soul_mode", 0.15, Operation.MULTIPLY_TOTAL
   );
   private static final AttributeModifier MANA_MODIFIER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("mana")), "mana", 10000.0, Operation.ADDITION
   );
   private int despawnAggroDelay;
   private int destroyBlockDelay;
   private int stuckDetectorDelay;
   private int stuckDetector;
   private Vec3 lastStuckPos = Vec3.f_82478_;
   private int playerScale;
   private boolean canAnimateOver;
   private boolean stopHeadAnimation;
   public float isAnimatingDampener;
   private ExtendedServerBossEvent bossEvent;
   FireBossAttackGoal attackGoal;
   int stanceBreakCounter;
   int stanceBreakTimer;
   static final int STANCE_BREAK_ANIM_TIME = 180;
   static final int STANCE_BREAK_BEGIN_SLAMS_TIMESTAMP = 130;
   static final int STANCE_BREAK_COUNT = 2;
   int spawnTimer;
   private static final int SPAWN_ANIM_TIME = 175;
   private static final int SPAWN_DELAY = 40;
   boolean hasPerformedHalfHealthAttack;
   protected int halfHealthTimer;
   protected float halfHealthDamageAccumulated;
   protected static final int HALF_HEALTH_ANIM_DURATION = 235;
   protected static final int HALF_HEALTH_JUMP_TIMESTAMP = 11;
   protected static final int HALF_HEALTH_CAST_TIMESTAMP = 230;
   int daggerTime;
   int parryCooldown;
   boolean clientDaggerParticles;
   SimpleContainer deathLoot = null;
   RawAnimation animationToPlay = null;
   private final AnimationController<FireBossEntity> meleeController = new AnimationController(this, "melee_animations", 0, this::predicate);

   @Override
   public void handleClientEvent(byte eventId) {
      switch (eventId) {
         case 0:
            FogManager.stopEvent(this.f_19820_);
            MusicManager.stopEvent(this.f_19820_);
            BossbarManager.stopTracking(this.f_19820_);
            break;
         case 1:
            FogManager.createEvent(this, new FogManager.FogEvent(Optional.empty(), true));
            if (!this.isSpawning()) {
               MusicManager.createEvent(this, new FireBossMusicHandler(true));
            }

            BossbarManager.startTracking(this.f_19820_, BOSSBAR_SPRITE);
            break;
         case 2:
            this.halfHealthTimer = 235;
            break;
         case 3:
            this.halfHealthTimer = 0;
            this.playAnimation("idle");
            break;
         case 4:
            MusicManager.createEvent(this, new FireBossMusicHandler(true));
            break;
         case 5:
            MusicManager.stopEvent(this.f_19820_);
            break;
         case 6:
            this.procSpectralDagger();
      }
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      buffer.writeInt(this.spawnTimer);
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      this.spawnTimer = additionalData.readInt();
      float y = this.m_146908_();
      this.f_20883_ = y;
      this.f_20884_ = y;
      this.f_20885_ = y;
      this.f_20886_ = y;
      this.f_19859_ = y;
   }

   public FireBossEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
      this.f_21365_ = this.createLookControl();
      this.f_21342_ = this.createMoveControl();
      this.createBossEvent();
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_SOUL_MODE, false);
      this.f_19804_.m_135372_(DATA_IS_DESPAWNING, false);
   }

   @Override
   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float m_24956_(float pFrom, float pTo, float pMaxDelta) {
            return super.m_24956_(pFrom, pTo, pMaxDelta * 2.5F);
         }

         protected boolean m_8106_() {
            return FireBossEntity.this.m_5448_() == null;
         }
      };
   }

   protected MoveControl createMoveControl() {
      return new FireBossMoveControl(this);
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

   public FireBossMoveControl getMoveControl() {
      return (FireBossMoveControl)super.m_21566_();
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.attackGoal = (FireBossAttackGoal)new FireBossAttackGoal(this, 1.5, 50, 75)
         .setMoveset(
            List.of(
               AttackAnimationData.builder("scythe_dagger_double_horizontal")
                  .length(60)
                  .attacks(
                     new FireBossAttackKeyframe(15, new Vec3(0.0, 0.0, 0.25), new FireBossAttackKeyframe.SwingData(false, true)),
                     new InvokeDaggerKeyframe(35),
                     new FireBossAttackKeyframe(36, new Vec3(0.0, 0.0, 0.75), new FireBossAttackKeyframe.SwingData(false, false)),
                     new AttackKeyframe(42, new Vec3(0.0, 0.0, 0.0))
                  )
                  .build(),
               AttackAnimationData.builder("scythe_backpedal")
                  .length(40)
                  .rangeMultiplier(2.0F)
                  .attacks(new FireBossAttackKeyframe(20, new Vec3(0.0, 0.3, -2.0), new FireBossAttackKeyframe.SwingData(false, true)))
                  .build(),
               AttackAnimationData.builder("scythe_sideslash_downslash_sideslash")
                  .length(62)
                  .rangeMultiplier(2.0F)
                  .attacks(
                     new FireBossAttackKeyframe(18, new Vec3(0.0, 0.0, 0.45), new FireBossAttackKeyframe.SwingData(false, true)),
                     new FireBossAttackKeyframe(30, new Vec3(0.0, 0.0, 0.45), new FireBossAttackKeyframe.SwingData(false, false)),
                     new FireBossAttackKeyframe(50, new Vec3(0.0, 0.1, 1.25), new Vec3(0.0, 0.3, 0.8), new FireBossAttackKeyframe.SwingData(false, false))
                  )
                  .build(),
               AttackAnimationData.builder("scythe_jump_combo")
                  .length(45)
                  .cancellable()
                  .rangeMultiplier(3.0F)
                  .attacks(
                     new FireBossAttackKeyframe(20, new Vec3(0.0, 1.0, 0.0), new Vec3(0.0, 1.15, 0.1), new FireBossAttackKeyframe.SwingData(true, false)),
                     new FireBossAttackKeyframe(35, new Vec3(0.0, 0.0, -0.2), new Vec3(0.0, 0.0, 0.5), new FireBossAttackKeyframe.SwingData(false, false))
                  )
                  .build(),
               AttackAnimationData.builder("scythe_downslash_sideslash")
                  .length(60)
                  .attacks(
                     new FireBossAttackKeyframe(22, new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, -0.2, 0.0), new FireBossAttackKeyframe.SwingData(true, true)),
                     new FireBossAttackKeyframe(40, new Vec3(0.0, 0.1, 0.8), new FireBossAttackKeyframe.SwingData(false, false))
                  )
                  .build(),
               AttackAnimationData.builder("scythe_horizontal_slash_spin")
                  .length(45)
                  .area(0.25F)
                  .rangeMultiplier(3.0F)
                  .attacks(
                     new FireBossAttackKeyframe(14, new Vec3(0.0, 0.1, 1.25), new Vec3(0.0, 0.1, 0.8), new FireBossAttackKeyframe.SwingData(false, true)),
                     new FireBossAttackKeyframe(30, new Vec3(0.0, 0.1, 1.85), new Vec3(0.0, 0.3, 0.8), new FireBossAttackKeyframe.SwingData(false, false))
                  )
                  .build()
            )
         )
         .setComboChance(1.0F)
         .setMeleeAttackInverval(10, 30)
         .setMeleeBias(1.0F, 1.0F)
         .setSpells(
            List.of(
               (AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get(),
               (AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get(),
               (AbstractSpell)SpellRegistry.SCORCH_SPELL.get()
            ),
            List.of(),
            List.of(),
            List.of()
         );
      this.f_21345_.m_25352_(2, new FieryDaggerSwarmAbilityGoal(this));
      this.f_21345_.m_25352_(2, new FieryDaggerZoneAbilityGoal(this));
      this.f_21345_.m_25352_(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.RAISE_HELL_SPELL.get(), 5, 5, 80, 240, 1));
      this.f_21345_.m_25352_(3, this.attackGoal);
      this.f_21345_.m_25352_(4, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21346_.m_25352_(1, new MomentHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Pig.class, true));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, DeadKingBoss.class, true));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public void triggerHalfHealthAttack() {
      this.hasPerformedHalfHealthAttack = true;
      this.halfHealthTimer = 235;
      this.castComplete();
      this.attackGoal.stopMeleeAction();
      this.attackGoal.fireballcooldown = 200;
      this.serverTriggerEvent((byte)2);
      this.serverTriggerAnimation("fire_boss_half_health_attack");
      this.m_5496_((SoundEvent)SoundRegistry.BOSS_STANCE_BREAK.get(), 5.0F, 2.0F);
   }

   public void stopHalfHealthAttack() {
      this.halfHealthTimer = 0;
      this.m_20242_(false);
      this.serverTriggerEvent((byte)3);
   }

   public boolean isHalfHealthAttacking() {
      return this.halfHealthTimer > 0;
   }

   public void triggerSpawnAnim() {
      this.spawnTimer = 215;
   }

   public void triggerStanceBreak() {
      this.stanceBreakCounter++;
      this.stanceBreakTimer = 180;
      this.castComplete();
      this.attackGoal.stopMeleeAction();
      this.stopHalfHealthAttack();
      this.serverTriggerAnimation("fire_boss_break_stance");
      this.m_5496_((SoundEvent)SoundRegistry.BOSS_STANCE_BREAK.get(), 3.0F, 1.0F);
      Vec3 vec3 = this.m_20191_().m_82399_();
      MagicManager.spawnParticles(
         this.f_19853_,
         (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
         vec3.f_82479_,
         vec3.f_82480_,
         vec3.f_82481_,
         25,
         0.2,
         0.2,
         0.2,
         0.12,
         false
      );
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return (SoundEvent)SoundRegistry.FIRE_BOSS_HURT.get();
   }

   public boolean isStanceBroken() {
      return this.stanceBreakTimer > 0;
   }

   public boolean isSpawning() {
      return this.spawnTimer > 0;
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.isStanceBroken() || this.isSpawning() || this.isHalfHealthAttacking();
   }

   public boolean m_6673_(DamageSource pSource) {
      return this.isSpawning() || this.isDespawning() || super.m_6673_(pSource);
   }

   public boolean m_8023_() {
      return true;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      this.m_21559_(false);
      this.m_21051_((Attribute)AttributeRegistry.MAX_MANA.get()).m_22125_(MANA_MODIFIER);
      this.playerScale = pLevel.m_6907_().stream().filter(player -> this.m_20280_(player) < 3600.0 && !player.m_5833_() && !player.m_7500_()).toList().size();
      int extraPlayers = Math.max(0, this.playerScale - 1);
      double extraHealthPercent = extraPlayers * 0.4 + extraPlayers * extraPlayers * 0.1;
      double extraHealth = (Double)ServerConfigs.TYROS_ADDITIONAL_HEALTH.get();
      double extraDamage = (Double)ServerConfigs.TYROS_ADDITIONAL_ATTACK_DAMAGE.get();
      double extraPower = (Double)ServerConfigs.TYROS_ADDITIONAL_SPELL_POWER.get();
      if (extraHealth != 0.0) {
         this.m_21051_(Attributes.f_22276_)
            .m_22125_(new AttributeModifier(AttributeHelper.uuidFromId(IronsSpellbooks.id("config")), "config", extraHealth, Operation.ADDITION));
      }

      if (extraHealthPercent != 0.0) {
         this.m_21051_(Attributes.f_22276_)
            .m_22125_(
               new AttributeModifier(
                  AttributeHelper.uuidFromId(IronsSpellbooks.id("player_scale")), "player_scale", extraHealthPercent, Operation.MULTIPLY_TOTAL
               )
            );
      }

      if (extraDamage != 0.0) {
         this.m_21051_(Attributes.f_22281_)
            .m_22125_(new AttributeModifier(AttributeHelper.uuidFromId(IronsSpellbooks.id("config")), "config", extraDamage, Operation.ADDITION));
      }

      if (extraPower != 0.0) {
         this.m_21051_((Attribute)AttributeRegistry.SPELL_POWER.get())
            .m_22125_(new AttributeModifier(AttributeHelper.uuidFromId(IronsSpellbooks.id("config")), "config", extraPower, Operation.ADDITION));
      }

      this.m_21153_(this.m_21233_());
      return pSpawnData;
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(
         EquipmentSlot.MAINHAND, new ItemStack(this.isSoulMode() ? (ItemLike)ItemRegistry.HELLRAZOR.get() : (ItemLike)ItemRegistry.DECREPIT_SCYTHE.get())
      );
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
   }

   public void procSpectralDagger() {
      if (!this.f_19853_.f_46443_) {
         this.serverTriggerEvent((byte)6);
      } else {
         this.clientDaggerParticles = true;
      }

      this.daggerTime = 15;
   }

   public boolean spectralDaggerActive() {
      return this.daggerTime > 0;
   }

   public void m_8119_() {
      super.m_8119_();
      float maxHealth = this.m_21233_();
      float currentHealth = this.m_21223_();
      this.bossEvent.m_142711_(currentHealth / maxHealth);
      if (this.daggerTime > 0) {
         this.daggerTime--;
      }

      if (this.parryCooldown > 0) {
         this.parryCooldown--;
      }

      if (this.isSpawning()) {
         this.spawnTimer--;
         this.handleSpawnSequence();
         if (this.spawnTimer == 0 && !this.f_19853_.f_46443_) {
            this.spawnKnight(true);
            this.spawnKnight(false);
         }
      } else if (this.isDespawning()) {
         this.f_20919_++;
         if (!this.f_19853_.f_46443_) {
            this.deathParticles();
            if (this.m_5448_() != null) {
               this.setDespawning(false);
            }

            if (this.f_20919_ > 160) {
               this.doForcedDespawned();
            }
         }
      } else if (this.f_20919_ > 0 && !this.m_21224_()) {
         this.f_20919_ = Math.max(0, this.f_20919_ - 3);
      } else if (this.isHalfHealthAttacking()) {
         this.halfHealthTimer--;
         if (!this.f_19853_.f_46443_) {
            this.handleHalfHealthSequence();
         }
      }

      if (!this.f_19853_.f_46443_) {
         if (this.isStanceBroken()) {
            this.stanceBreakTimer--;
            this.handleStanceBreakSequence();
         }

         if (this.isSoulMode() && !this.f_20890_) {
            this.soulParticles();
         }
      }

      if (this.destroyBlockDelay > 0) {
         this.destroyBlockDelay--;
      }

      if (this.stuckDetectorDelay > 0) {
         this.stuckDetectorDelay--;
      }
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      float maxHealth = this.m_21233_();
      float currentHealth = this.m_21223_();
      if (this.stanceBreakCounter == 0) {
         if (currentHealth < maxHealth * 0.75F) {
            this.triggerStanceBreak();
            return;
         }
      } else if (this.stanceBreakCounter == 1 && currentHealth < maxHealth * 0.333F) {
         this.triggerStanceBreak();
         return;
      }

      if (!this.hasPerformedHalfHealthAttack && currentHealth < maxHealth * 0.5F) {
         this.triggerHalfHealthAttack();
      }

      if (this.f_19797_ > 400 && !this.isDespawning() && this.m_5448_() == null && this.f_19797_ - this.m_21213_() > 200) {
         if (this.f_19797_ % 20 == 0) {
            this.m_5634_(5.0F);
         }

         if (this.despawnAggroDelay++ > 1200) {
            this.setDespawning(true);
            this.f_19853_.m_5594_(null, this.m_20183_(), (SoundEvent)SoundRegistry.FIRE_BOSS_ACCENT.get(), SoundSource.HOSTILE, 4.0F, 0.75F);
         }
      }

      if (this.m_5912_() && this.f_19797_ % 240 == 0) {
         int knightCount = this.f_19853_.m_45976_(KeeperEntity.class, this.m_20191_().m_82377_(50.0, 20.0, 50.0)).size();
         if (knightCount < 2 + Math.max(this.playerScale - 1, 0) / 2) {
            this.spawnKnight(this.f_19796_.m_188499_());
         }
      }
   }

   public boolean m_7327_(Entity entity) {
      if (super.m_7327_(entity)) {
         entity.m_20254_(2);
         return true;
      } else {
         return false;
      }
   }

   private void handleHalfHealthSequence() {
      if (!this.f_19853_.f_46443_) {
         this.f_21346_.m_25373_();
         if (this.m_5448_() != null) {
            this.f_21365_.m_148051_(this.m_5448_());
         }

         this.f_21365_.m_8128_();
         if (this.halfHealthDamageAccumulated > this.m_21233_() * 0.1F) {
            PacketDistributor.sendToPlayersTrackingEntity(this, new FieryExplosionParticlesPacket(this.m_20191_().m_82399_(), 10.0F));
            this.m_21153_(Math.max(10.0F, Math.min(this.m_21223_(), this.m_21233_() * 0.33F - 1.0F)));
            this.stopHalfHealthAttack();
         } else {
            int tick = 235 - this.halfHealthTimer;
            this.m_20256_(this.m_20184_().m_82542_(0.1, 1.0, 0.1));
            if (tick == 11) {
               this.m_20334_(0.0, 0.75, 0.0);
            } else if (tick > 11 && tick < 230) {
               if (tick == 31) {
                  this.m_20242_(true);
               }

               if (tick % 5 == 0) {
                  int targetHeight = 8;
                  double groundY = Utils.raycastForBlock(this.f_19853_, this.m_20182_(), this.m_20182_().m_82492_(0.0, targetHeight + 1, 0.0), Fluid.NONE)
                     .m_82450_()
                     .f_82480_;
                  this.m_5997_(0.0, this.m_20186_() - groundY > targetHeight ? -0.02 : 0.02, 0.0);
               }

               Vec3 vec3 = this.m_20182_().m_82520_(0.0, this.m_20191_().m_82376_() * 1.25, 0.0);
               MagicManager.spawnParticles(
                  this.f_19853_, ParticleHelper.FIRE_EMITTER, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 1, 0.1, 0.1, 0.1, 0.03, true
               );
               if (tick % 10 == 0) {
                  float pitch = Mth.m_14179_(tick / 235.0F, 0.5F, 1.8F);
                  this.m_5496_((SoundEvent)SoundRegistry.SCORCH_PREPARE.get(), 2.0F + pitch, pitch);
               }
            } else if (tick == 230) {
               this.m_20242_(false);
               MagicFireball fireball = new MagicFireball(this.f_19853_, this);
               fireball.setDamage((float)(this.m_21133_(Attributes.f_22281_) * 12.0));
               fireball.setExplosionRadius(30.0F);
               Vec3 origin = this.m_20182_().m_82492_(0.0, fireball.m_20206_() / 2.0F, 0.0).m_82520_(0.0, this.m_20191_().m_82376_() * 1.25, 0.0);
               Vec3 trajectory = this.m_5448_() == null ? this.m_20156_() : this.m_5448_().m_20182_().m_82546_(origin).m_82541_();
               fireball.m_146884_(origin);
               fireball.shoot(trajectory);
               this.f_19853_.m_7967_(fireball);
               this.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_FIREBALL.get(), 4.0F, 1.0F);
            }
         }
      }
   }

   private void handleStanceBreakSequence() {
      int tick = 180 - this.stanceBreakTimer;
      if (this.stanceBreakCounter == 2) {
         if (tick == 80) {
            this.setSoulMode(true);
            Vec3 vec3 = this.m_20191_().m_82399_();
            MagicManager.spawnParticles(this.f_19853_, ParticleHelper.FIRE, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 120, 0.3, 0.3, 0.3, 0.3, true);
            AttributeInstance speed = this.m_21051_(Attributes.f_22279_);
            speed.m_22130_(SOUL_SPEED_MODIFIER);
            speed.m_22125_(SOUL_SPEED_MODIFIER);
            this.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_TRANSITION_SOUL.get(), 3.0F, 1.0F);
            if (this.m_6844_(EquipmentSlot.MAINHAND).m_150930_((Item)ItemRegistry.DECREPIT_SCYTHE.get())) {
               this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistry.HELLRAZOR.get(), 1, this.m_6844_(EquipmentSlot.MAINHAND).m_41783_()));
            }
         } else if (tick < 80) {
            double f = Mth.m_14139_(tick / 80.0F, 0.2, 0.4);
            Vec3 vec3 = this.m_20191_().m_82399_();
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.FIRE, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 12 + (int)(f * 10.0), f, f, f, 0.02, true
            );
         }
      }

      if (tick >= 130) {
         if (tick == 130) {
            this.createEruptionEntity(8.0F, (float)this.m_21133_(Attributes.f_22281_));
            this.m_5496_((SoundEvent)SoundRegistry.FIRE_ERUPTION_SLAM.get(), 2.0F, 1.2F);
         } else if (tick == 155) {
            this.createEruptionEntity(11.0F, (float)this.m_21133_(Attributes.f_22281_) * 2.0F);
            this.m_5496_((SoundEvent)SoundRegistry.FIRE_ERUPTION_SLAM.get(), 3.0F, 1.0F);
         } else if (tick == 180) {
            this.createEruptionEntity(15.0F, (float)this.m_21133_(Attributes.f_22281_) * 3.0F);
            this.m_5496_((SoundEvent)SoundRegistry.FIRE_ERUPTION_SLAM.get(), 4.0F, 0.9F);
         }
      }
   }

   private void handleSpawnSequence() {
      int animProgress = 215 - this.spawnTimer;
      float walkProgress = this.getSpawnWalkPercent(0.0F);
      float worldZOffset = Mth.m_14179_(walkProgress, -3.75F * this.m_6134_(), 0.0F);
      Vec3 position = this.m_20182_().m_82549_(new Vec3(0.0, 0.0, worldZOffset).m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0)));
      if (!this.f_19853_.f_46443_ && animProgress == 65) {
         this.serverTriggerEvent((byte)4);
      }

      if (animProgress == 40) {
         if (!this.f_19853_.f_46443_) {
            MagicManager.spawnParticles(
               this.f_19853_,
               ParticleTypes.f_123777_,
               position.f_82479_,
               position.f_82480_ + 1.2,
               position.f_82481_,
               (int)(165.0F * this.m_6134_()),
               0.4 * this.m_6134_(),
               1.0 * this.m_6134_(),
               0.4 * this.m_6134_(),
               0.01,
               true
            );
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.FOG_CAMPFIRE_SMOKE, position.f_82479_, position.f_82480_ + 0.1, position.f_82481_, 6, 0.6, 0.1, 0.6, 0.05, true
            );
            MagicManager.spawnParticles(
               this.f_19853_,
               new BlastwaveParticleOptions(1.0F, 0.6F, 0.3F, 8.0F),
               position.f_82479_,
               position.f_82480_,
               position.f_82481_,
               0,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            this.serverTriggerAnimation("fire_boss_spawn");
         }

         this.f_19853_
            .m_6263_(
               null,
               position.f_82479_,
               position.f_82480_,
               position.f_82481_,
               (SoundEvent)SoundRegistry.SOULCALLER_TOLL_SUCCESS.get(),
               SoundSource.PLAYERS,
               5.0F,
               0.75F
            );
      }

      if (animProgress == 60
         || animProgress == 80
         || animProgress == 100
         || animProgress == 120
         || animProgress == 140
         || animProgress == 154
         || animProgress == 168) {
         this.f_19853_
            .m_6263_(null, position.f_82479_, position.f_82480_, position.f_82481_, (SoundEvent)SoundRegistry.KEEPER_STEP.get(), this.m_5720_(), 0.4F, 1.0F);
      }

      if (animProgress == 155) {
         this.f_19853_
            .m_6263_(
               null,
               position.f_82479_,
               position.f_82480_,
               position.f_82481_,
               (SoundEvent)SoundRegistry.FIRE_BOSS_SUMMON_SCYTHE.get(),
               this.m_5720_(),
               3.0F,
               1.0F
            );
      }
   }

   protected float getSpawnWalkPercent(float partialTick) {
      return Mth.m_14036_((175 - this.spawnTimer + partialTick) / 175.0F, 0.0F, 1.0F);
   }

   private void doForcedDespawned() {
      this.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_ACCENT.get(), 5.0F, 1.0F);
      Vec3 vec3 = this.m_20191_().m_82399_();
      MagicManager.spawnParticles(
         this.f_19853_,
         (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
         vec3.f_82479_,
         vec3.f_82480_,
         vec3.f_82481_,
         25,
         0.2,
         0.2,
         0.2,
         0.12,
         false
      );
      this.killNearbySummonedKnights();
      this.m_142687_(RemovalReason.DISCARDED);
      IronsSpellbooks.LOGGER.info("{} despawned due to inactivity", this);
   }

   public void spawnKnight(boolean left) {
      if (this.f_19853_ instanceof ServerLevel serverLevel) {
         KeeperEntity knight = new KeeperEntity(this.f_19853_);
         float angle = (left ? -90 : 90) * (float) (Math.PI / 180.0);
         Vec3 offset = this.m_20156_().m_82542_(3.0, 0.0, 3.0).m_82490_(this.m_6134_()).m_82524_(angle);
         Vec3 spawn = Utils.moveToRelativeGroundLevel(
            this.f_19853_, Utils.raycastForBlock(this.f_19853_, this.m_146892_(), this.m_20182_().m_82549_(offset), Fluid.NONE).m_82450_(), 4
         );
         knight.m_20219_(spawn.m_82520_(0.0, 0.1, 0.0));
         knight.triggerRise();
         knight.m_146922_(this.m_146908_());
         knight.setIsSummoned();
         if (this.isSoulMode()) {
            knight.setIsRestored();
         }

         knight.m_6518_(serverLevel, this.f_19853_.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         this.f_19853_.m_7967_(knight);
         this.f_19853_
            .m_6263_(null, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, (SoundEvent)SoundRegistry.FIRE_BOSS_ACCENT.get(), this.m_5720_(), 2.0F, 0.9F);
      }
   }

   public void soulParticles() {
      Vec3 vec3 = this.m_20191_().m_82399_();
      MagicManager.spawnParticles(this.f_19853_, ParticleHelper.FIRE, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 2, 0.2, 0.6, 0.2, 0.01, true);
   }

   private void createEruptionEntity(float radius, float damage) {
      Vec3 forward = this.m_20156_().m_82542_(1.0, 0.0, 1.0).m_82541_().m_82490_(3.0);
      Vec3 pos = Utils.moveToRelativeGroundLevel(this.f_19853_, this.m_20182_().m_82549_(forward).m_82520_(0.0, 1.0, 0.0), 4);
      FireEruptionAoe aoe = new FireEruptionAoe(this.f_19853_, radius);
      aoe.m_5602_(this);
      aoe.setDamage(damage);
      aoe.m_20219_(pos);
      this.f_19853_.m_7967_(aoe);
      CameraShakeManager.addCameraShake(new CameraShakeData(this.f_19853_, 20 + (int)radius, pos, radius * 2.0F + 5.0F));
   }

   public void m_6074_() {
      if (!this.m_21224_() && !this.isSpawning()) {
         super.m_6074_();
      } else {
         this.m_146870_();
      }
   }

   public void m_6667_(DamageSource pDamageSource) {
      super.m_6667_(pDamageSource);
      if (this.m_21224_() && !this.f_19853_.f_46443_) {
         this.stanceBreakTimer = 0;
         this.castComplete();
         this.attackGoal.m_8041_();
         this.serverTriggerAnimation("fire_boss_death");
         this.serverTriggerEvent((byte)5);
         this.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_DEATH.get(), 5.0F, 1.0F);
         Vec3 vec3 = this.m_20191_().m_82399_();
         MagicManager.spawnParticles(
            this.f_19853_,
            (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
            vec3.f_82479_,
            vec3.f_82480_,
            vec3.f_82481_,
            25,
            0.2,
            0.2,
            0.2,
            0.12,
            false
         );
         this.killNearbySummonedKnights();
      }
   }

   private void killNearbySummonedKnights() {
      this.f_19853_
         .m_45976_(KeeperEntity.class, this.m_20191_().m_82377_(50.0, 20.0, 50.0))
         .stream()
         .filter(KeeperEntity::isSummoned)
         .forEach(LivingEntity::m_6074_);
   }

   protected void m_6668_(DamageSource pDamageSource) {
      this.m_5907_();
      this.m_21226_();
      boolean playerDeath = this.f_20889_ > 0;
      this.m_7472_(pDamageSource, ForgeHooks.getLootingLevel(this, pDamageSource.m_7639_(), pDamageSource), playerDeath);
      ResourceLocation resourcekey = this.m_5743_();
      LootTable mainLoot = this.f_19853_.m_7654_().m_278653_().m_278676_(resourcekey);
      LootTable lootPerPlayer = this.f_19853_.m_7654_().m_278653_().m_278676_(resourcekey.m_266382_("_per_player"));
      Builder lootparams$builder = new Builder((ServerLevel)this.m_9236_())
         .m_287286_(LootContextParams.f_81455_, this)
         .m_287286_(LootContextParams.f_81460_, this.m_20182_())
         .m_287286_(LootContextParams.f_81457_, pDamageSource)
         .m_287289_(LootContextParams.f_81456_, this.f_20888_);
      if (playerDeath && this.f_20888_ != null) {
         lootparams$builder = lootparams$builder.m_287286_(LootContextParams.f_81456_, this.f_20888_).m_287239_(this.f_20888_.m_36336_());
      }

      LootParams lootparams = lootparams$builder.m_287235_(LootContextParamSets.f_81415_);
      ObjectArrayList<ItemStack> objectarraylist = new ObjectArrayList();
      mainLoot.m_287276_(lootparams, this.m_287233_(), objectarraylist::add);

      for (int i = 0; i < this.playerScale; i++) {
         lootPerPlayer.m_287276_(lootparams, this.m_287233_(), objectarraylist::add);
      }

      this.deathLoot = new SimpleContainer(objectarraylist.size());
      objectarraylist.forEach(this.deathLoot::m_19173_);
   }

   protected void m_6153_() {
      this.f_20919_++;
      if (!this.f_19853_.f_46443_) {
         float scale = this.m_6134_();
         Vec3 vec3 = this.m_20182_();
         this.deathParticles();
         if (this.f_20919_ >= 160 && !this.m_9236_().m_5776_() && !this.m_213877_()) {
            if (this.deathLoot != null) {
               this.deathLoot.m_19195_().forEach(this::m_19983_);
            }

            this.m_142687_(RemovalReason.KILLED);
            MagicManager.spawnParticles(
               this.f_19853_,
               (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
               vec3.f_82479_,
               vec3.f_82480_ + 1.0,
               vec3.f_82481_,
               50,
               0.3,
               0.3,
               0.3,
               0.2 * scale,
               true
            );
            this.m_5496_((SoundEvent)SoundRegistry.FIRE_BOSS_ACCENT.get(), 4.0F, 0.9F);
         }
      }
   }

   private void deathParticles() {
      float scale = this.m_6134_();
      Vec3 vec3 = this.m_20182_();
      int particles = (int)Mth.m_14179_(Mth.m_14036_((this.f_20919_ - 20) / 60.0F, 0.0F, 1.0F), 0.0F, 5.0F * scale);
      float range = Mth.m_14179_(Mth.m_14036_((this.f_20919_ - 20) / 80.0F, 0.0F, 1.0F), 0.0F, 0.4F * scale);
      if (particles > 0) {
         MagicManager.spawnParticles(
            this.f_19853_,
            (ParticleOptions)ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(),
            vec3.f_82479_,
            vec3.f_82480_ + 1.0,
            vec3.f_82481_,
            particles,
            range,
            range,
            range,
            100.0,
            false
         );
      }
   }

   public void m_267651_(boolean pIncludeHeight) {
      super.m_267651_(false);
   }

   protected void m_267689_(float f) {
      super.m_267689_(f * (!this.m_20096_() ? 0.5F : (this.isSoulMode() ? 0.7F : 0.9F)));
   }

   @Override
   public boolean bobBodyWhileWalking() {
      return !this.isAnimating();
   }

   protected void m_7355_(BlockPos pPos, BlockState pState) {
      this.m_5496_((SoundEvent)SoundRegistry.KEEPER_STEP.get(), 0.25F, 0.9F);
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_((Attribute)AttributeRegistry.SPELL_POWER.get(), 1.25)
         .m_22268_(Attributes.f_22284_, 15.0)
         .m_22268_((Attribute)AttributeRegistry.SPELL_RESIST.get(), 1.25)
         .m_22268_((Attribute)AttributeRegistry.FIRE_MAGIC_RESIST.get(), 1.5)
         .m_22268_(Attributes.f_22276_, 1000.0)
         .m_22268_(Attributes.f_22278_, 0.8)
         .m_22268_(Attributes.f_22282_, 0.6)
         .m_22268_(Attributes.f_22277_, 48.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_GRAVITY.get(), 0.03)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 3.0)
         .m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0)
         .m_22268_(Attributes.f_22279_, 0.21);
   }

   public void m_7334_(Entity pEntity) {
      if (!this.isSpawning()) {
         super.m_7334_(pEntity);
      }
   }

   public void m_147240_(double pStrength, double pX, double pZ) {
      if (!this.isStanceBroken()) {
         super.m_147240_(pStrength, pX, pZ);
      }
   }

   public boolean m_6094_() {
      return super.m_6094_() && !this.m_6107_();
   }

   @Override
   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
      this.canAnimateOver = animationId.equals("fire_boss_spawn") || animationId.equals("summon_fiery_daggers");
      this.stopHeadAnimation = animationId.equals("fire_boss_break_stance") || animationId.equals("fire_boss_death");
   }

   @Override
   public boolean shouldAlwaysAnimateHead() {
      return !this.stopHeadAnimation;
   }

   private PlayState predicate(AnimationState<FireBossEntity> animationEvent) {
      AnimationController<FireBossEntity> controller = animationEvent.getController();
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
      return this.meleeController.getAnimationState() == State.RUNNING && !this.canAnimateOver || super.isAnimating();
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (this.f_19853_.f_46443_) {
         return false;
      }

      boolean canParry = this.m_5912_()
         && this.parryCooldown <= 0
         && !this.m_6107_()
         && !this.attackGoal.isActing()
         && pSource.m_7639_() != null
         && pSource.m_7270_() != null
         && pSource.m_7270_().m_82546_(this.m_20182_()).m_82541_().m_82526_(this.m_20156_()) >= 0.35
         && !pSource.m_269533_(DamageTypeTags.f_268738_);
      if (canParry && this.f_19796_.m_188501_() < 0.5) {
         this.serverTriggerAnimation("offhand_parry");
         this.procSpectralDagger();
         this.parryCooldown = 100;
         this.m_216990_((SoundEvent)SoundRegistry.FIRE_DAGGER_PARRY.get());
         return false;
      }

      if (this.isStanceBroken()) {
         pAmount *= 0.6F;
      }

      if (this.isSoulMode()) {
         pAmount *= 0.5F;
      }

      if (this.isHalfHealthAttacking()) {
         pAmount *= 0.8F;
      }

      float limit = this.m_21233_() * 0.025F;
      if (pAmount > limit) {
         pAmount = limit + (pAmount - limit) * 0.3F;
      }

      if (pSource.m_276093_(DamageTypes.f_268612_) && this.destroyBlockDelay <= 0) {
         Utils.doMobBreakSuffocatingBlocks(this);
         this.destroyBlockDelay = 40;
      }

      return super.m_6469_(pSource, pAmount);
   }

   protected void m_6475_(DamageSource damageSource, float damageAmount) {
      super.m_6475_(damageSource, damageAmount);
      if (this.isHalfHealthAttacking()) {
         this.halfHealthDamageAccumulated += damageAmount;
      }

      Vec3 oldStuckPos = this.lastStuckPos;
      this.lastStuckPos = this.m_20182_();
      if (this.stuckDetectorDelay <= 0) {
         if (oldStuckPos.m_82557_(this.lastStuckPos) < 9.0 && !this.m_6107_()) {
            this.stuckDetectorDelay = 20;
            if (this.f_19862_) {
               this.stuckDetector++;
            }
         } else {
            this.stuckDetector = 0;
         }
      }

      if (this.stuckDetector >= 3 && this.destroyBlockDelay <= 0) {
         Utils.doMobBreakSuffocatingBlocks(this, this.m_20156_().m_82490_(1.5));
         this.stuckDetector = 0;
         this.destroyBlockDelay = 40;
      }
   }

   public boolean isSoulMode() {
      return (Boolean)this.f_19804_.m_135370_(DATA_SOUL_MODE);
   }

   public void setSoulMode(boolean soulMode) {
      boolean wasSoulMode = this.isSoulMode();
      this.f_19804_.m_135381_(DATA_SOUL_MODE, soulMode);
      if (!this.f_19853_.f_46443_ && !wasSoulMode && soulMode) {
         this.m_6210_();
      }
   }

   public EntityDimensions m_6972_(Pose pPose) {
      return this.isSoulMode() ? super.m_6972_(pPose).m_20388_(1.15F) : super.m_6972_(pPose);
   }

   public boolean isDespawning() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_DESPAWNING);
   }

   public void setDespawning(boolean despawning) {
      this.f_19804_.m_135381_(DATA_IS_DESPAWNING, despawning);
      if (!despawning) {
         this.despawnAggroDelay = 0;
      }
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("stanceBreakCount", this.stanceBreakCounter);
      pCompound.m_128405_("playerScale", this.playerScale);
      if (this.stanceBreakTimer > 0) {
         pCompound.m_128405_("stanceBreakTime", this.stanceBreakTimer);
      }

      pCompound.m_128379_("soulMode", this.isSoulMode());
      if (this.deathLoot != null) {
         pCompound.m_128365_("deathLootItems", this.deathLoot.m_7927_());
      }

      pCompound.m_128356_("unloadedGametime", this.f_19853_.m_46467_());
      pCompound.m_128405_("halfHealthTimer", this.halfHealthTimer);
      pCompound.m_128350_("halfHealthDamage", this.halfHealthDamageAccumulated);
      pCompound.m_128379_("halfHealthAttack", this.hasPerformedHalfHealthAttack);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.stanceBreakCounter = pCompound.m_128451_("stanceBreakCount");
      this.playerScale = pCompound.m_128451_("playerScale");
      if (this.m_8077_()) {
         this.bossEvent.m_6456_(this.m_5446_());
      }

      int stanceTime = pCompound.m_128451_("stanceBreakTime");
      if (stanceTime > 0) {
         this.stanceBreakTimer = stanceTime;
         if (this.f_19853_.f_46443_) {
            this.animationToPlay = RawAnimation.begin().thenPlay("fire_boss_break_stance");
         }
      }

      this.setSoulMode(pCompound.m_128471_("soulMode"));
      if (pCompound.m_128425_("deathLootItems", 9)) {
         ListTag tag = pCompound.m_128437_("deathLootItems", 10);
         this.deathLoot = new SimpleContainer(tag.size());
         this.deathLoot.m_7797_(tag);
      }

      this.halfHealthTimer = pCompound.m_128451_("halfHealthTimer");
      this.halfHealthDamageAccumulated = pCompound.m_128457_("halfHealthDamage");
      this.hasPerformedHalfHealthAttack = pCompound.m_128471_("halfHealthAttack");
   }

   public void m_20258_(CompoundTag pCompound) {
      if (pCompound.m_128425_("unloadedGametime", 99)) {
         long unloadTimestamp = pCompound.m_128454_("unloadedGametime");
         long delta = this.f_19853_.m_46467_() - unloadTimestamp;
         if (delta > 6000L) {
            this.m_142467_(RemovalReason.DISCARDED);
            IronsSpellbooks.LOGGER.info("Refusing to load {}, elapsed time {} greater than limit {}", new Object[]{this, delta, 6000});
            return;
         }
      }

      super.m_20258_(pCompound);
      if (!this.f_19853_.f_46443_) {
         this.createBossEvent();
      }
   }

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || pEntity.m_6095_().m_204039_(ModTags.INFERNAL_ALLIES);
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new NotIdioticNavigation(this, pLevel);
   }

   protected void createBossEvent() {
      this.bossEvent = (ExtendedServerBossEvent)new ExtendedServerBossEvent(
            this.m_20148_(), this.m_5446_().m_6881_().m_130940_(ChatFormatting.RED), BossBarColor.RED, BossBarOverlay.PROGRESS
         )
         .m_7006_(true);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public float m_6134_() {
      return this.isSoulMode() ? 2.0125F : 1.75F;
   }
}
