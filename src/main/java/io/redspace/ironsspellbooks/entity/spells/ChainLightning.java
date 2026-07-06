package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChainLightning extends AbstractMagicProjectile {
   List<Entity> allVictims;
   List<Entity> lastVictims;
   Entity initialVictim;
   public int maxConnections = 4;
   public int maxConnectionsPerWave = 3;
   public float range = 3.0F;
   private static final Supplier<AbstractSpell> SPELL = SpellRegistry.CHAIN_LIGHTNING_SPELL;
   int hits;

   public ChainLightning(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.allVictims = new ArrayList<>();
      this.lastVictims = new ArrayList<>();
      this.m_20242_(true);
      this.f_19794_ = true;
   }

   public ChainLightning(Level level, Entity owner, Entity initialVictim) {
      this((EntityType<? extends Projectile>)EntityRegistry.CHAIN_LIGHTNING.get(), level);
      this.m_5602_(owner);
      this.m_146884_(initialVictim.m_20182_());
      this.initialVictim = initialVictim;
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      int f = this.f_19797_ - 1;
      if (!this.f_19853_.f_46443_ && f % 6 == 0) {
         if (f == 0 && !this.allVictims.contains(this.initialVictim)) {
            this.doHurt(this.initialVictim);
            if (this.m_19749_() != null) {
               Vec3 start = this.m_19749_().m_20182_().m_82520_(0.0, this.m_19749_().m_20206_() / 2.0F, 0.0);
               Vec3 dest = this.initialVictim.m_20182_().m_82520_(0.0, this.initialVictim.m_20206_() / 2.0F, 0.0);
               ((ServerLevel)this.f_19853_).m_8767_(new ZapParticleOption(dest), start.f_82479_, start.f_82480_, start.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
            }
         } else {
            int j = this.lastVictims.size();
            AtomicInteger zapsThisWave = new AtomicInteger();

            for (int i = 0; i < j; i++) {
               Entity entity = this.lastVictims.get(i);
               List<Entity> entities = this.f_19853_.m_6249_(entity, entity.m_20191_().m_82400_(this.range), this::m_5603_);
               entities.sort(Comparator.comparingDouble(o -> o.m_20280_(entity)));
               entities.forEach(
                  victim -> {
                     if (zapsThisWave.get() < this.maxConnectionsPerWave
                        && this.hits < this.maxConnections
                        && victim.m_20280_(entity) < this.range * this.range
                        && Utils.hasLineOfSight(this.f_19853_, entity.m_146892_(), victim.m_146892_(), true)) {
                        this.doHurt(victim);
                        victim.m_5496_((SoundEvent)SoundRegistry.CHAIN_LIGHTNING_CHAIN.get(), 2.0F, 1.0F);
                        zapsThisWave.getAndIncrement();
                        Vec3 start = new Vec3(entity.f_19790_, entity.f_19791_, entity.f_19792_).m_82520_(0.0, entity.m_20206_() / 2.0F, 0.0);
                        Vec3 dest = victim.m_20182_().m_82520_(0.0, victim.m_20206_() / 2.0F, 0.0);
                        ((ServerLevel)this.f_19853_)
                           .m_8767_(new ZapParticleOption(dest), start.f_82479_, start.f_82480_, start.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
                     }
                  }
               );
            }

            this.lastVictims.removeAll(this.allVictims);
            if (this.lastVictims.isEmpty()) {
               this.m_146870_();
            }
         }

         this.allVictims.addAll(this.lastVictims);
      }
   }

   public void doHurt(Entity victim) {
      this.hits++;
      DamageSources.applyDamage(victim, this.damage, SPELL.get().getDamageSource(this, this.m_19749_()));
      MagicManager.spawnParticles(
         this.f_19853_,
         ParticleHelper.ELECTRICITY,
         victim.m_20185_(),
         victim.m_20186_() + victim.m_20206_() / 2.0F,
         victim.m_20189_(),
         10,
         victim.m_20205_() / 3.0F,
         victim.m_20206_() / 3.0F,
         victim.m_20205_() / 3.0F,
         0.1,
         false
      );
      this.lastVictims.add(victim);
   }

   public boolean hasAlreadyZapped(Entity entity) {
      return this.allVictims.contains(entity) || this.lastVictims.contains(entity);
   }

   @Override
   protected boolean m_5603_(Entity target) {
      return target instanceof LivingEntity
         && !DamageSources.isFriendlyFireBetween(target, this.m_19749_())
         && target != this.m_19749_()
         && !this.hasAlreadyZapped(target)
         && super.m_5603_(target);
   }

   @Override
   public void trailParticles() {
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return 0.0F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   @Override
   public boolean m_142391_() {
      return false;
   }
}
