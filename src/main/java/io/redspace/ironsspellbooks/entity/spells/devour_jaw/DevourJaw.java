package io.redspace.ironsspellbooks.entity.spells.devour_jaw;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DevourJaw extends AoeEntity {
   LivingEntity target;
   public int vigorLevel;
   public final int waitTime = 5;
   public final int warmupTime = 13;
   public final int deathTime = 21;

   public DevourJaw(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public DevourJaw(Level level, LivingEntity owner, LivingEntity target) {
      this((EntityType<? extends Projectile>)EntityRegistry.DEVOUR_JAW.get(), level);
      this.m_5602_(owner);
      this.target = target;
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (target == this.target
         && DamageSources.applyDamage(target, this.getDamage(), ((AbstractSpell)SpellRegistry.DEVOUR_SPELL.get()).getDamageSource(this, this.m_19749_()))
         && this.m_19749_() instanceof LivingEntity livingOwner) {
         target.m_20256_(target.m_20184_().m_82520_(0.0, 0.5, 0.0));
         target.f_19864_ = true;
         if (target.m_21224_()) {
            MobEffectInstance oldVigor = livingOwner.m_21124_((MobEffect)MobEffectRegistry.VIGOR.get());
            int addition = 0;
            if (oldVigor != null) {
               addition = oldVigor.m_19564_() + 1;
            }

            livingOwner.m_7292_(
               new MobEffectInstance((MobEffect)MobEffectRegistry.VIGOR.get(), 1200, Math.min(this.vigorLevel + addition, 9), false, false, true)
            );
            livingOwner.m_5634_((this.vigorLevel + 1) * 2);
         }
      }
   }

   @Override
   public void m_8119_() {
      if (this.f_19797_ < 5) {
         if (this.target != null) {
            this.m_146884_(this.target.m_20182_());
         }
      } else if (this.f_19797_ == 5) {
         this.m_5496_((SoundEvent)SoundRegistry.DEVOUR_BITE.get(), 2.0F, 1.0F);
      } else if (this.f_19797_ == 13) {
         if (this.f_19853_.f_46443_) {
            float y = this.m_146908_();
            int countPerSide = 25;

            for (int i = -countPerSide; i < countPerSide; i++) {
               Vec3 motion = new Vec3(0.0, Math.abs(countPerSide) - i, countPerSide * 0.5F).m_82524_(y).m_82541_().m_82542_(0.4F, 0.8F, 0.4F);
               this.f_19853_
                  .m_7106_(ParticleHelper.BLOOD, this.m_20185_(), this.m_20186_() + 0.5, this.m_20189_(), motion.f_82479_, motion.f_82480_, motion.f_82481_);
            }
         } else {
            this.checkHits();
         }
      } else if (this.f_19797_ > 21) {
         this.m_146870_();
      }
   }

   @Override
   protected Vec3 getInflation() {
      return new Vec3(2.0, 2.0, 2.0);
   }

   public boolean m_142391_() {
      return false;
   }

   @Override
   public void m_6210_() {
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
   }
}
