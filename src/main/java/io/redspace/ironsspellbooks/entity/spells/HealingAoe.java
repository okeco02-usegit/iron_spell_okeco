package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class HealingAoe extends AoeEntity implements AntiMagicSusceptible {
   public HealingAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public HealingAoe(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.HEALING_AOE.get(), level);
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (this.m_19749_() instanceof LivingEntity owner && Utils.shouldHealEntity(owner, target)) {
         float healAmount = this.getDamage();
         MinecraftForge.EVENT_BUS.post(new SpellHealEvent((LivingEntity)this.m_19749_(), target, healAmount, (SchoolType)SchoolRegistry.HOLY.get()));
         target.m_5634_(healAmount);
      }
   }

   @Override
   public void ambientParticles() {
      if (this.f_19853_.f_46443_) {
         int color = PotionUtils.m_43559_(Potion.m_43489_("healing"));
         double d0 = (color >> 16 & 0xFF) / 255.0;
         double d1 = (color >> 8 & 0xFF) / 255.0;
         double d2 = (color >> 0 & 0xFF) / 255.0;
         float f = this.getParticleCount();
         f = Mth.m_14036_(f * this.getRadius(), f / 4.0F, f * 10.0F);

         for (int i = 0; i < f; i++) {
            if (f - i < 1.0F && this.f_19796_.m_188501_() > f - i) {
               return;
            }

            float r = this.getRadius();
            Vec3 pos;
            if (this.isCircular()) {
               float distance = (1.0F - this.f_19796_.m_188501_() * this.f_19796_.m_188501_()) * r;
               pos = new Vec3(0.0, 0.0, distance).m_82524_(this.f_19796_.m_188501_() * 360.0F);
            } else {
               pos = new Vec3(Utils.getRandomScaled(r * 0.85F), 0.2F, Utils.getRandomScaled(r * 0.85F));
            }

            this.f_19853_
               .m_7106_(
                  ParticleTypes.f_123811_,
                  this.m_20185_() + pos.f_82479_,
                  this.m_20186_() + pos.f_82480_ + this.particleYOffset(),
                  this.m_20189_() + pos.f_82481_,
                  d0,
                  d1,
                  d2
               );
         }
      }
   }

   @Override
   protected boolean m_5603_(Entity pTarget) {
      return !pTarget.m_5833_() && pTarget.m_6084_() && pTarget.m_6087_();
   }

   @Override
   public float getParticleCount() {
      return 0.35F;
   }

   @Override
   protected float getParticleSpeedModifier() {
      return 0.0F;
   }

   @Override
   protected Vec3 getInflation() {
      return new Vec3(0.0, 1.0, 0.0);
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
