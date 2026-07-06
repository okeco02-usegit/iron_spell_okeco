package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningStrike extends AoeEntity {
   static final int chargeTime = 20;
   static final int vfxHeight = 15;

   public LightningStrike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRadius(3.0F);
      this.setCircular();
   }

   public LightningStrike(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.LIGHTNING_STRIKE.get(), level);
   }

   @Override
   public void m_8119_() {
      if (!this.f_19853_.f_46443_) {
         if (this.f_19797_ == 1) {
            int total = 5;
            int light = Utils.random.m_188503_(total);
            Vec3 location = this.m_20182_().m_82520_(0.0, 15.0, 0.0);
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.FOG_THUNDER_LIGHT, location.f_82479_, location.f_82480_, location.f_82481_, light, 1.0, 1.0, 1.0, 1.0, true
            );
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.FOG_THUNDER_DARK, location.f_82479_, location.f_82480_, location.f_82481_, total - light, 1.0, 1.0, 1.0, 1.0, true
            );
            MagicManager.spawnParticles(
               this.f_19853_,
               new ShockwaveParticleOptions(((SchoolType)SchoolRegistry.LIGHTNING.get()).getTargetingColor(), -1.5F, true),
               this.m_20185_(),
               this.m_20186_(),
               this.m_20189_(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
         }

         if (this.f_19797_ == 20) {
            this.checkHits();
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.ELECTRIC_SPARKS, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.2F, 0.2F, 0.2F, 0.25, true
            );
            MagicManager.spawnParticles(
               this.f_19853_, ParticleHelper.FIERY_SPARKS, this.m_20185_(), this.m_20186_(), this.m_20189_(), 5, 0.2F, 0.2F, 0.2F, 0.125, true
            );
            Vec3 bottom = this.m_20182_();
            Vec3 top = bottom.m_82520_(0.0, 15.0, 0.0);
            Vec3 middle = bottom.m_82520_(Utils.getRandomScaled(2.0), Utils.random.m_216332_(3, 12), Utils.getRandomScaled(2.0));
            MagicManager.spawnParticles(
               this.f_19853_, new ZapParticleOption(top), middle.f_82479_, middle.f_82480_, middle.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, true
            );
            MagicManager.spawnParticles(
               this.f_19853_, new ZapParticleOption(middle), this.m_20185_(), this.m_20186_(), this.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
            if (Utils.random.m_188501_() < 0.3F) {
               Vec3 split = middle.m_82520_(Utils.getRandomScaled(2.0), -Math.abs(Utils.getRandomScaled(2.0)), Utils.getRandomScaled(2.0));
               MagicManager.spawnParticles(
                  this.f_19853_, new ZapParticleOption(middle), split.f_82479_, split.f_82480_, split.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, true
               );
            }

            this.m_5496_((SoundEvent)SoundRegistry.SMALL_LIGHTNING_STRIKE.get(), 2.0F, 0.8F + this.f_19796_.m_188501_() * 0.5F);
         }

         if (this.f_19797_ > 20) {
            this.m_146870_();
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity target) {
      DamageSources.applyDamage(target, this.getDamage(), ((AbstractSpell)SpellRegistry.THUNDERSTORM_SPELL.get()).getDamageSource(this, this.m_19749_()));
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }
}
