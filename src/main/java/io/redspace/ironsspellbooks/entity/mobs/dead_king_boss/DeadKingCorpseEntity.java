package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class DeadKingCorpseEntity extends AbstractSpellCastingMob {
   DeadKingAmbienceSoundManager ambienceSoundManager;
   private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.m_135353_(DeadKingCorpseEntity.class, EntityDataSerializers.f_135035_);
   private int currentAnimTime;
   private final int animLength = 300;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("dead_king_rest");
   private final RawAnimation rise = RawAnimation.begin().thenPlay("dead_king_rise");

   public DeadKingCorpseEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_21530_();
   }

   public boolean m_6087_() {
      return true;
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_5829_() {
      return true;
   }

   protected boolean m_8028_() {
      return false;
   }

   public boolean m_21532_() {
      return true;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.triggered()) {
         this.currentAnimTime++;
         if (!this.m_9236_().f_46443_) {
            if (this.currentAnimTime > 300) {
               DeadKingBoss boss = new DeadKingBoss(this.m_9236_());
               boss.m_20219_(this.m_20182_().m_82520_(0.0, 1.0, 0.0));
               boss.m_6518_((ServerLevel)this.m_9236_(), this.m_9236_().m_6436_(boss.m_20097_()), MobSpawnType.TRIGGERED, null, null);
               int playerCount = Math.max(this.m_9236_().m_45976_(Player.class, boss.m_20191_().m_82400_(32.0)).size(), 1);
               UUID attributeId = AttributeHelper.uuidFromId(IronsSpellbooks.id("gank_bonus"));
               boss.m_21204_()
                  .m_22146_(Attributes.f_22276_)
                  .m_22125_(new AttributeModifier(attributeId, "gank_bonus", (playerCount - 1) * 0.5, Operation.MULTIPLY_BASE));
               boss.m_21153_(boss.m_21233_());
               boss.m_21204_()
                  .m_22146_(Attributes.f_22281_)
                  .m_22125_(new AttributeModifier(attributeId, "gank_bonus", (playerCount - 1) * 0.25, Operation.MULTIPLY_BASE));
               boss.m_21204_()
                  .m_22146_((Attribute)AttributeRegistry.SPELL_RESIST.get())
                  .m_22125_(new AttributeModifier(attributeId, "gank_bonus", (playerCount - 1) * 0.1, Operation.MULTIPLY_BASE));
               boss.m_21530_();
               this.f_19853_.m_7967_(boss);
               MagicManager.spawnParticles(
                  this.m_9236_(),
                  ParticleTypes.f_235898_,
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
               this.f_19853_
                  .m_6263_(
                     null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)SoundRegistry.DEAD_KING_SPAWN.get(), SoundSource.MASTER, 20.0F, 1.0F
                  );
               this.m_146870_();
            }
         } else {
            this.resurrectParticles();
         }
      } else if (this.f_19853_.f_46443_ && this.f_19797_ % 40 == 0) {
         MinecraftInstanceHelper.ifPlayerPresent(
            player -> {
               float yRot = this.m_146908_();
               Vec3 musicCenter = this.m_20182_()
                  .m_82520_(-15.0F * Mth.m_14031_(yRot * (float) (Math.PI / 180.0)), 0.0, 15.0F * Mth.m_14089_(yRot * (float) (Math.PI / 180.0)));
               if (musicCenter.m_82557_(player.m_20182_()) < 400.0) {
                  if (this.ambienceSoundManager == null) {
                     this.ambienceSoundManager = new DeadKingAmbienceSoundManager(musicCenter);
                  }

                  this.ambienceSoundManager.trigger();
               }
            }
         );
      }
   }

   private void resurrectParticles() {
      float f = this.currentAnimTime / 300.0F;
      float rot = this.currentAnimTime * 12 + (1.0F + f * 15.0F);
      float height = f * 4.0F + 0.4F * Mth.m_14031_(this.currentAnimTime * 30 * (float) (Math.PI / 180.0)) * f * f;
      float distance = Mth.m_14036_(Utils.smoothstep(0.0F, 1.15F, f * 3.0F), 0.0F, 1.15F);
      Vec3 pos = new Vec3(0.0, 0.0, distance).m_82524_(rot * (float) (Math.PI / 180.0)).m_82520_(0.0, height, 0.0).m_82549_(this.m_20182_());
      this.f_19853_.m_7106_(ParticleTypes.f_235898_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 0.0, 0.0, 0.0);
      float radius = 4.0F;
      if (this.f_19796_.m_188501_() < f * 1.5F) {
         Vec3 random = this.m_20182_()
            .m_82549_(
               new Vec3(
                  (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius,
                  3.5 + (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius,
                  (this.f_19796_.m_188501_() * 2.0F - 1.0F) * radius
               )
            );
         Vec3 motion = this.m_20182_().m_82546_(random).m_82490_(0.04F);
         this.f_19853_.m_7106_(ParticleTypes.f_235898_, random.f_82479_, random.f_82480_, random.f_82481_, motion.f_82479_, motion.f_82480_, motion.f_82481_);
      }
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource.m_269533_(DamageTypeTags.f_268738_)) {
         this.m_146870_();
         return true;
      }

      Player player = this.f_19853_.m_45930_(this, 8.0);
      if (player != null) {
         this.trigger();
      }

      return false;
   }

   protected InteractionResult m_6071_(Player pPlayer, InteractionHand pHand) {
      if (!this.triggered()) {
         this.trigger();
         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      } else {
         return super.m_6071_(pPlayer, pHand);
      }
   }

   private void trigger() {
      if (!this.triggered()) {
         this.f_19853_
            .m_6263_(
               null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)SoundRegistry.DEAD_KING_RESURRECT.get(), SoundSource.AMBIENT, 2.0F, 1.0F
            );
         this.f_19804_.m_135381_(TRIGGERED, true);
         if (this.ambienceSoundManager != null) {
            this.ambienceSoundManager.triggerStop();
         }
      }
   }

   public boolean triggered() {
      return (Boolean)this.f_19804_.m_135370_(TRIGGERED);
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(TRIGGERED, false);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "idle", 0, this::idlePredicate)});
   }

   private PlayState idlePredicate(AnimationState event) {
      if (this.triggered()) {
         event.getController().setAnimation(this.rise);
      } else {
         event.getController().setAnimation(this.idle);
      }

      return PlayState.CONTINUE;
   }

   @Override
   public boolean shouldBeExtraAnimated() {
      return false;
   }

   @Override
   public boolean shouldAlwaysAnimateHead() {
      return false;
   }
}
