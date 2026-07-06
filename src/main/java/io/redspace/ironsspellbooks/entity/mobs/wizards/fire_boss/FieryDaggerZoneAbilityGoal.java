package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class FieryDaggerZoneAbilityGoal extends AnimatedActionGoal<FireBossEntity> {
   public FieryDaggerZoneAbilityGoal(FireBossEntity mob) {
      super(mob);
   }

   @Override
   protected boolean canStartAction() {
      return this.mob.m_20096_() && this.mob.m_5448_() != null && this.mob.m_20280_(this.mob.m_5448_()) > 36.0;
   }

   @Override
   protected int getActionTimestamp() {
      return 1;
   }

   @Override
   protected int getActionDuration() {
      return 5;
   }

   @Override
   protected int getCooldown() {
      return Utils.random.m_216332_(50, 90);
   }

   @Override
   protected String getAnimationId() {
      return "instant_slash";
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
         this.mob.m_5496_((SoundEvent)SoundRegistry.FIERY_DAGGER_THROW.get(), 2.0F, Utils.random.m_216332_(80, 110) * 0.01F);
         Vec3 start = this.mob.m_146892_();
         Vec3 targetPos = target.m_20182_();
         Vec3 deltaAim = targetPos.m_82546_(start);

         for (int i = 0; i < 3; i++) {
            Vec3 aim = start.m_82549_(deltaAim.m_82524_((float) (Math.PI / 4) * (i - 1)));
            int delay = Utils.random.m_216332_(10, 40);
            FieryDaggerEntity dagger = new FieryDaggerEntity(this.mob.f_19853_);
            dagger.m_5602_(this.mob);
            dagger.m_146884_(start);
            dagger.delay = delay;
            dagger.setDamage((float)(this.mob.m_21133_(Attributes.f_22281_) * 0.75));
            dagger.setExplosionRadius(4.0F + Utils.random.m_188501_() * 2.0F);
            dagger.m_20242_(false);
            Vec3 horizontal = aim.m_82546_(start).m_82542_(1.0, 0.0, 1.0);
            double horizontalSpeed = 1.0F * Mth.m_14089_((float) (Math.PI / 4)) + 0.5;
            double distance = horizontal.m_82553_();
            double ticks = distance / horizontalSpeed;
            double y1 = aim.f_82480_ - start.f_82480_;
            double g = 0.05;
            double verticalSpeed = (y1 + 0.5 * g * ticks * ticks) / ticks;
            Vec3 trajectory = horizontal.m_82541_().m_82490_(horizontalSpeed).m_82520_(0.0, verticalSpeed, 0.0);
            dagger.m_20256_(trajectory);
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
