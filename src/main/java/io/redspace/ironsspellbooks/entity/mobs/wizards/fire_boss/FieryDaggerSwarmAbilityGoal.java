package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class FieryDaggerSwarmAbilityGoal extends AnimatedActionGoal<FireBossEntity> {
   public static final int ANIM_DURATION = 25;
   public static final int ACTION_TIMESTAMP = 17;

   public FieryDaggerSwarmAbilityGoal(FireBossEntity mob) {
      super(mob);
   }

   @Override
   protected boolean canStartAction() {
      return this.mob.m_5448_() != null;
   }

   @Override
   protected int getActionTimestamp() {
      return 17;
   }

   @Override
   protected int getActionDuration() {
      return 25;
   }

   @Override
   protected int getCooldown() {
      return Utils.random.m_216332_(40, 80);
   }

   @Override
   protected String getAnimationId() {
      return "summon_fiery_daggers";
   }

   @Override
   public void m_8037_() {
      if (this.mob.m_5448_() != null) {
         this.mob.attackGoal.setTarget(this.mob.m_5448_());
         this.mob.attackGoal.doMovement(this.mob.m_20280_(this.mob.m_5448_()));
      }

      super.m_8037_();
   }

   @Override
   protected void doAction() {
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         this.mob.m_5496_((SoundEvent)SoundRegistry.FIRE_CAST.get(), 2.0F, Utils.random.m_216332_(80, 110) * 0.01F);
         Vec3 pos = this.mob.m_20182_();
         int count = 7;
         int delay = Utils.random.m_216332_(30, 70);
         float yAngle = -Utils.getAngle(target.m_20185_(), target.m_20189_(), this.mob.m_20185_(), this.mob.m_20189_()) + (float) (Math.PI / 2);

         for (int i = 0; i < count; i++) {
            Vec3 offset = new Vec3(1.5 * this.mob.m_6134_(), 0.0, 0.0)
               .m_82535_(Mth.m_14179_(i / (count - 1.0F), 0.0F, (float) -Math.PI))
               .m_82524_(yAngle)
               .m_82520_(0.0, this.mob.m_20192_(), 0.0);
            FieryDaggerEntity dagger = new FieryDaggerEntity(this.mob.f_19853_);
            dagger.m_5602_(this.mob);
            dagger.ownerTrack = offset;
            dagger.setTarget(this.mob.m_5448_());
            dagger.m_146884_(pos.m_82549_(offset.m_82524_(this.mob.m_146908_())));
            dagger.delay = delay + i * 2;
            dagger.setDamage((float)(this.mob.m_21133_(Attributes.f_22281_) * 0.75));
            this.mob.f_19853_.m_7967_(dagger);
         }
      }
   }

   @Override
   public void m_8041_() {
      super.m_8041_();
      this.mob.attackGoal.setTarget(null);
   }
}
