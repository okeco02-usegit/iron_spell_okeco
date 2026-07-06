package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.AnimatedActionGoal;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class SummonedClaymoreEntity extends SummonedWeaponEntity {
   private static final EntityDataAccessor<Boolean> DATA_IS_TAUNTING = SynchedEntityData.m_135353_(
      SummonedClaymoreEntity.class, EntityDataSerializers.f_135035_
   );

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22276_, 40.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22280_, 1.0)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_(Attributes.f_22279_, 0.2);
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_IS_TAUNTING, false);
   }

   public boolean isTaunting() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_TAUNTING);
   }

   public void setTaunting(boolean taunting) {
      this.f_19804_.m_135381_(DATA_IS_TAUNTING, taunting);
   }

   public SummonedClaymoreEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SummonedClaymoreEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.SUMMONED_CLAYMORE.get(), level);
      this.setSummoner(owner);
   }

   @Override
   public GenericAnimatedWarlockAttackGoal<? extends SummonedWeaponEntity> makeAttackGoal() {
      return new GenericAnimatedWarlockAttackGoal<>(this, 1.5, 20, 40)
         .setMoveset(
            List.of(
               AttackAnimationData.builder("summoned_sword_pommel_strike")
                  .length(24)
                  .attacks(new AttackKeyframe(12, new Vec3(0.0, 0.0, 0.45F), new Vec3(0.0, 0.0, 1.0)))
                  .build(),
               AttackAnimationData.builder("summoned_sword_basic_downswing")
                  .length(45)
                  .attacks(new AttackKeyframe(25, new Vec3(0.0, -0.2, 0.15F), new Vec3(0.0, 0.0, 1.0)))
                  .area(0.7F)
                  .build()
            )
         );
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      if (this.isTaunting()) {
         this.f_20902_ = 0.0F;
         this.f_20900_ = 0.0F;
         MagicManager.spawnParticles(
            this.f_19853_, ParticleHelper.UNSTABLE_ENDER, this.m_20185_(), this.m_20186_(), this.m_20189_(), 3, 0.1, 0.1, 0.1, 0.2, false
         );
      }
   }

   public boolean m_6094_() {
      return super.m_6094_() && !this.isTaunting();
   }

   @Override
   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new SummonedClaymoreEntity.ClaymoreTauntGoal(this));
      super.m_8099_();
   }

   public void m_6478_(MoverType pType, Vec3 pPos) {
      if (!this.isTaunting()) {
         super.m_6478_(pType, pPos);
      }
   }

   @Override
   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (this.isTaunting()) {
         pAmount *= 0.2F;
      }

      return super.m_6469_(pSource, pAmount);
   }

   public static class ClaymoreTauntGoal extends AnimatedActionGoal<SummonedClaymoreEntity> {
      List<Entity> targets = null;

      public ClaymoreTauntGoal(SummonedClaymoreEntity mob) {
         super(mob);
      }

      @Override
      protected boolean canStartAction() {
         LivingEntity target = this.mob.m_5448_();
         if (target == null) {
            return false;
         } else {
            List<Entity> entities = this.mob
               .f_19853_
               .m_6249_(
                  this.mob,
                  this.mob.m_20191_().m_82377_(12.0, 6.0, 12.0),
                  entity -> entity.getClass().isAssignableFrom(target.getClass()) || entity.m_7307_(target)
               );
            if (entities.size() > 2) {
               this.targets = entities;
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      protected int getActionTimestamp() {
         return 20;
      }

      @Override
      protected int getActionDuration() {
         return 120;
      }

      @Override
      protected int getCooldown() {
         return 100;
      }

      @Override
      protected String getAnimationId() {
         return "claymore_taunt";
      }

      @Override
      public void m_8037_() {
         super.m_8037_();
         this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.8, 0.0, 0.8).m_82520_(0.0, -1.0, 0.0));
      }

      @Override
      protected void doAction() {
         this.mob.setTaunting(true);
         this.mob.m_5496_((SoundEvent)SoundRegistry.ECHOING_STRIKE.get(), 2.0F, 1.0F);
         MagicManager.spawnParticles(
            this.mob.f_19853_,
            new BlastwaveParticleOptions(((AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get()).getSchoolType().getTargetingColor(), 3.0F),
            this.mob.m_20185_(),
            this.mob.m_20186_(),
            this.mob.m_20189_(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         if (this.targets != null) {
            this.targets
               .forEach(
                  entity -> {
                     if (entity instanceof Mob tauntmob) {
                        MagicManager.spawnParticles(
                           this.mob.f_19853_,
                           ParticleTypes.f_123792_,
                           tauntmob.m_20185_(),
                           tauntmob.m_20188_() + (tauntmob.m_20191_().f_82292_ - tauntmob.m_20188_()) * 2.0,
                           tauntmob.m_20189_(),
                           5,
                           0.3,
                           0.3,
                           0.3,
                           0.0,
                           false
                        );
                        tauntmob.m_6710_(this.mob);
                     }
                  }
               );
         }
      }

      @Override
      public void m_8041_() {
         super.m_8041_();
         this.mob.setTaunting(false);
         this.targets = null;
      }
   }
}
