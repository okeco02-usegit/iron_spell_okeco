package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class PounceGrappleGoal extends AnimatedActionGoal<IceSpiderEntity> {
   private static final AttributeModifier TELEGRAPH_SPEED_MODIFIER = new AttributeModifier(
      AttributeHelper.uuidFromId(IronsSpellbooks.id("pouncing")), "pouncing", -0.2, Operation.MULTIPLY_TOTAL
   );
   private static final int DAMAGER_START = 30;
   private static final int DAMAGER_END = 35;

   public PounceGrappleGoal(IceSpiderEntity mob) {
      super(mob);
   }

   @Override
   protected boolean canStartAction() {
      return !this.mob.m_6047_()
         && this.mob.m_5912_()
         && this.mob.m_5448_() != null
         && (Utils.random.m_188501_() < 0.05 || this.mob.m_20280_(this.mob.m_5448_()) > 25.0);
   }

   @Override
   protected int getActionTimestamp() {
      return 20;
   }

   @Override
   protected int getActionDuration() {
      return 40;
   }

   @Override
   protected int getCooldown() {
      return this.mob.m_217043_().m_216332_(3, 8) * 20;
   }

   @Override
   protected String getAnimationId() {
      return "attack_grapple_pounce";
   }

   @Override
   public void m_8037_() {
      super.m_8037_();
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         this.mob.attackGoal.setTarget(this.mob.m_5448_());
         this.mob.attackGoal.doMovement(this.mob.m_20280_(this.mob.m_5448_()));
         if (this.abilityTimer == 30) {
            this.mob.m_216990_((SoundEvent)SoundRegistry.ICE_SPIDER_BITE.get());
         }

         if (this.abilityTimer >= 30 && this.abilityTimer <= 35) {
            double meleeRange = this.mob.m_21133_((Attribute)ForgeMod.ENTITY_REACH.get()) * this.mob.m_6134_();
            if (target.m_20280_(this.mob) <= meleeRange * meleeRange && Utils.hasLineOfSight(this.mob.f_19853_, this.mob, target, true)) {
               if (this.mob.m_7327_(target)) {
                  if (target.m_21254_() && target instanceof Player player) {
                     player.m_36384_(true);
                  } else {
                     this.mob.startGrapple(target);
                     this.mob.m_216990_((SoundEvent)SoundRegistry.ICE_SPIDER_GRAPPLE_LATCH.get());
                  }
               }

               this.m_8041_();
            }
         }
      }
   }

   public boolean m_6767_() {
      return false;
   }

   @Override
   protected void doAction() {
      Vec3 leapVector = new Vec3(0.0, 0.5, 1.5);
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         Vec3 power = Utils.lerp(Mth.m_14036_(this.mob.m_20270_(target) / 18.0F, 0.0F, 1.0F), new Vec3(0.125, 0.25, 0.125), new Vec3(3.0, 1.2, 3.0));
         Vec3 lunge = leapVector.m_82542_(power.f_82479_, power.f_82480_, power.f_82481_)
            .m_82524_(-Utils.getAngle(this.mob.m_20185_(), this.mob.m_20189_(), target.m_20185_(), target.m_20189_()) - (float) (Math.PI / 2));
         this.mob.m_20256_(this.mob.m_20184_().m_82549_(lunge));
         this.mob.m_21051_(Attributes.f_22279_).m_22130_(TELEGRAPH_SPEED_MODIFIER);
         this.mob.m_5496_((SoundEvent)SoundRegistry.ICE_SPIDER_SWING.get(), 3.0F, Utils.random.m_216332_(13, 16) * 0.1F);
         this.mob.m_5496_((SoundEvent)SoundRegistry.ICE_SPIDER_AMBIENT.get(), 3.0F, Utils.random.m_216332_(14, 20) * 0.1F);
      }
   }

   @Override
   public void m_8056_() {
      super.m_8056_();
      this.mob.m_21051_(Attributes.f_22279_).m_22130_(TELEGRAPH_SPEED_MODIFIER);
      this.mob.m_21051_(Attributes.f_22279_).m_22118_(TELEGRAPH_SPEED_MODIFIER);
   }
}
