package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.small_magic_arrow.SmallMagicArrow;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ArrowVolleyEntity extends AbstractMagicProjectile {
   int rows;
   int arrowsPerRow;
   int delay = 5;

   public ArrowVolleyEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_20242_(true);
      this.f_19794_ = true;
   }

   @Override
   public void m_8119_() {
      if (!this.f_19853_.f_46443_) {
         if (this.f_19797_ % this.delay == 0) {
            int arrows = this.arrowsPerRow;
            float speed = 0.85F;
            Vec3 motion = Vec3.m_82498_(this.m_146909_() - this.f_19797_ / 5.0F * 7.0F, this.m_146908_()).m_82541_().m_82490_(speed);
            Vec3 orth = new Vec3(
               -Mth.m_14089_(-this.m_146908_() * (float) (Math.PI / 180.0) - (float) Math.PI),
               0.0,
               Mth.m_14031_(-this.m_146908_() * (float) (Math.PI / 180.0) - (float) Math.PI)
            );

            for (int i = 0; i < arrows; i++) {
               float distance = (i - arrows * 0.5F) * 0.7F;
               SmallMagicArrow arrow = new SmallMagicArrow(this.f_19853_, this.m_19749_());
               arrow.setDamage(this.getDamage());
               Vec3 spawn = this.m_20182_().m_82549_(orth.m_82490_(distance));
               arrow.m_146884_(spawn);
               arrow.shoot(motion.m_82549_(Utils.getRandomVec3(0.04F)));
               arrow.m_5602_(this.m_19749_());
               this.f_19853_.m_7967_(arrow);
               MagicManager.spawnParticles(
                  this.f_19853_, ParticleTypes.f_123815_, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, 2, 0.1, 0.1, 0.1, 0.05, false
               );
            }

            this.f_19853_
               .m_6263_(
                  null,
                  this.m_20182_().f_82479_,
                  this.m_20182_().f_82480_,
                  this.m_20182_().f_82481_,
                  SoundEvents.f_11932_,
                  SoundSource.NEUTRAL,
                  3.0F,
                  1.1F + Utils.random.m_188501_() * 0.3F
               );
            this.f_19853_
               .m_6263_(
                  null,
                  this.m_20182_().f_82479_,
                  this.m_20182_().f_82480_,
                  this.m_20182_().f_82481_,
                  (SoundEvent)SoundRegistry.BOW_SHOOT.get(),
                  SoundSource.NEUTRAL,
                  2.0F,
                  Utils.random.m_216332_(16, 20) * 0.1F
               );
         } else if (this.f_19797_ > this.rows * this.delay) {
            this.m_146870_();
         }
      }
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("rows", this.rows);
      tag.m_128405_("arrowsPerRow", this.arrowsPerRow);
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.rows = tag.m_128451_("rows");
      this.arrowsPerRow = tag.m_128451_("arrowsPerRow");
   }

   public void setRows(int rows) {
      this.rows = rows;
   }

   public void setArrowsPerRow(int arrowsPerRow) {
      this.arrowsPerRow = arrowsPerRow;
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
}
