package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericProtectOwnerTargetGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonedVex extends Vex implements IMagicSummon {
   public SummonedVex(EntityType<? extends Vex> pEntityType, Level pLevel) {
      super((EntityType)EntityRegistry.SUMMONED_VEX.get(), pLevel);
      this.f_21364_ = 0;
   }

   @Deprecated(forRemoval = true)
   public SummonedVex(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Vex>)EntityRegistry.SUMMONED_VEX.get(), pLevel);
      this.setSummoner(owner);
   }

   public void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(4, new SummonedVex.VexChargeAttackGoal());
      this.f_21345_.m_25352_(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.65F, 35.0F, 10.0F, true, 50.0F));
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.f_21345_.m_25352_(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.f_21345_.m_25352_(16, new SummonedVex.VexRandomMoveGoal());
      this.f_21346_.m_25352_(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.f_21346_.m_25352_(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers());
      this.f_21346_.m_25352_(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   public boolean m_6935_(Player pPlayer) {
      return !this.m_7307_(pPlayer);
   }

   public boolean m_7327_(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, ((AbstractSpell)SpellRegistry.SUMMON_VEX_SPELL.get()).getDamageSource(this, this.getSummoner()));
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.m_6469_(pSource, pAmount);
   }

   @Deprecated(forRemoval = true)
   public void setSummoner(@Nullable LivingEntity owner) {
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

   public boolean m_7307_(Entity pEntity) {
      return super.m_7307_(pEntity) || this.isAlliedHelper(pEntity);
   }

   @Override
   public void onUnSummon() {
      if (!this.m_9236_().f_46443_) {
         MagicManager.spawnParticles(this.m_9236_(), ParticleTypes.f_123759_, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.m_142467_(RemovalReason.DISCARDED);
      }
   }

   protected boolean m_8028_() {
      return false;
   }

   class VexChargeAttackGoal extends Goal {
      public VexChargeAttackGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         LivingEntity livingentity = SummonedVex.this.m_5448_();
         return livingentity != null
               && livingentity.m_6084_()
               && !SummonedVex.this.m_21566_().m_24995_()
               && SummonedVex.this.f_19796_.m_188503_(m_186073_(7)) == 0
            ? SummonedVex.this.m_20280_(livingentity) > 4.0
            : false;
      }

      public boolean m_8045_() {
         return SummonedVex.this.m_21566_().m_24995_()
            && SummonedVex.this.m_34028_()
            && SummonedVex.this.m_5448_() != null
            && SummonedVex.this.m_5448_().m_6084_();
      }

      public void m_8056_() {
         LivingEntity livingentity = SummonedVex.this.m_5448_();
         if (livingentity != null) {
            Vec3 vec3 = livingentity.m_146892_();
            SummonedVex.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 1.0);
         }

         SummonedVex.this.m_34042_(true);
         SummonedVex.this.m_5496_(SoundEvents.f_12500_, 1.0F, 1.0F);
      }

      public void m_8041_() {
         SummonedVex.this.m_34042_(false);
      }

      public boolean m_183429_() {
         return true;
      }

      public void m_8037_() {
         LivingEntity livingentity = SummonedVex.this.m_5448_();
         if (livingentity != null) {
            if (SummonedVex.this.m_20191_().m_82381_(livingentity.m_20191_())) {
               SummonedVex.this.m_7327_(livingentity);
               SummonedVex.this.m_34042_(false);
            } else {
               double d0 = SummonedVex.this.m_20280_(livingentity);
               if (d0 < 9.0) {
                  Vec3 vec3 = livingentity.m_146892_();
                  SummonedVex.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 1.0);
               }
            }
         }
      }
   }

   class VexRandomMoveGoal extends Goal {
      public VexRandomMoveGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         return !SummonedVex.this.m_21566_().m_24995_() && SummonedVex.this.f_19796_.m_188503_(m_186073_(7)) == 0;
      }

      public boolean m_8045_() {
         return false;
      }

      public void m_8037_() {
         BlockPos blockpos = SummonedVex.this.m_34027_();
         if (blockpos == null) {
            blockpos = SummonedVex.this.m_20183_();
         }

         for (int i = 0; i < 3; i++) {
            BlockPos blockpos1 = blockpos.m_7918_(
               SummonedVex.this.f_19796_.m_188503_(15) - 7, SummonedVex.this.f_19796_.m_188503_(11) - 5, SummonedVex.this.f_19796_.m_188503_(15) - 7
            );
            if (SummonedVex.this.m_9236_().m_46859_(blockpos1)) {
               SummonedVex.this.f_21342_.m_6849_(blockpos1.m_123341_() + 0.5, blockpos1.m_123342_() + 0.5, blockpos1.m_123343_() + 0.5, 0.25);
               if (SummonedVex.this.m_5448_() == null) {
                  SummonedVex.this.m_21563_().m_24950_(blockpos1.m_123341_() + 0.5, blockpos1.m_123342_() + 0.5, blockpos1.m_123343_() + 0.5, 180.0F, 20.0F);
               }
               break;
            }
         }
      }
   }
}
