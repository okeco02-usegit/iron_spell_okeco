package io.redspace.ironsspellbooks.entity.spells.sunbeam;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SunbeamEntity extends AoeEntity implements AntiMagicSusceptible {
   @Nullable
   LivingEntity target;
   public static final int WARMUP_TIME = 15;

   public SunbeamEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRadius((float)(this.m_20191_().m_82362_() * 0.5));
      this.m_20242_(true);
   }

   public SunbeamEntity(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.SUNBEAM.get(), level);
   }

   @Override
   public void m_8119_() {
      this.m_146867_();
      if (this.f_19797_ == 15 && !this.f_19853_.f_46443_) {
         this.checkHits();
         MagicManager.spawnParticles(
            this.f_19853_,
            ParticleHelper.EMBERS,
            this.m_20185_(),
            this.m_20186_() + 0.06,
            this.m_20189_(),
            50,
            this.getRadius() * 0.7F,
            0.2F,
            this.getRadius() * 0.7F,
            0.6F,
            true
         );
         MagicManager.spawnParticles(
            this.f_19853_,
            new BlastwaveParticleOptions(1.0F, 0.85F, 0.4F, 7.0F),
            this.m_20185_(),
            this.m_20186_() + 0.06,
            this.m_20189_(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         this.f_19853_
            .m_5594_(null, this.m_20183_(), (SoundEvent)SoundRegistry.SUNBEAM_IMPACT.get(), SoundSource.NEUTRAL, 4.5F, Utils.random.m_216332_(9, 11) * 0.1F);
      }

      if (this.f_19797_ > 15) {
         this.m_146870_();
      }
   }

   public void setTarget(LivingEntity target) {
      this.target = target;
   }

   @Override
   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   @Override
   public void applyEffect(LivingEntity target) {
      DamageSources.applyDamage(target, this.getDamage(), ((AbstractSpell)SpellRegistry.SUNBEAM_SPELL.get()).getDamageSource(this, this.m_19749_()));
   }

   @Override
   protected Vec3 getInflation() {
      return new Vec3(2.0, 2.0, 2.0);
   }

   @Override
   public float getParticleCount() {
      return 0.0F;
   }

   @Override
   public void m_6210_() {
   }

   @Override
   public void ambientParticles() {
   }

   @Override
   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   @Override
   public void onAntiMagic(MagicData magicData) {
      this.m_146870_();
   }
}
