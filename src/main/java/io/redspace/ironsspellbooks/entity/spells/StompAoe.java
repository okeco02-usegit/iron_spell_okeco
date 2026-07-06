package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class StompAoe extends AbstractMagicProjectile {
   int step;
   int maxSteps;

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

   public StompAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19794_ = true;
      this.m_20242_(true);
      this.maxSteps = 5;
   }

   public StompAoe(Level level, int steps, float yRot) {
      this((EntityType<? extends Projectile>)EntityRegistry.STOMP_AOE.get(), level);
      this.maxSteps = steps;
      this.m_146922_(yRot);
   }

   @Override
   public void m_8119_() {
      if (!this.f_19853_.f_46443_) {
         if (this.f_19797_ % 1 == 0) {
            this.checkHits();
         }

         if (this.step > this.maxSteps) {
            this.m_146870_();
         }
      }
   }

   protected void checkHits() {
      if (!this.f_19853_.f_46443_) {
         this.step++;
         int width = Math.max(this.step / 2, 2);
         float angle = this.m_146908_() * (float) (Math.PI / 180.0);
         Vec3 forward = new Vec3(Mth.m_14031_(-angle), 0.0, Mth.m_14089_(-angle));
         Vec3 orth = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_);
         Vec3 center = this.m_20182_().m_82549_(forward.m_82490_(this.step));
         Vec3 leftBound = Utils.moveToRelativeGroundLevel(this.f_19853_, center.m_82546_(orth.m_82490_(width)), 2).m_82520_(0.0, 0.75, 0.0);
         Vec3 rightBound = Utils.moveToRelativeGroundLevel(this.f_19853_, center.m_82549_(orth.m_82490_(width)), 2).m_82520_(0.0, 0.75, 0.0);
         this.f_19853_
            .m_45933_(this, new AABB(leftBound.m_82520_(0.0, -1.0, 0.0), rightBound.m_82520_(0.0, 1.0, 0.0)))
            .forEach(
               entity -> {
                  if (this.m_5603_(entity)
                     && Utils.checkEntityIntersecting(entity, leftBound, rightBound, 1.0F).m_6662_() != Type.MISS
                     && DamageSources.applyDamage(
                        entity, this.getDamage(), ((AbstractSpell)SpellRegistry.STOMP_SPELL.get()).getDamageSource(this, this.m_19749_())
                     )
                     && entity instanceof LivingEntity livingEntity) {
                     livingEntity.m_147240_(this.explosionRadius * -0.35F, forward.f_82479_, forward.f_82481_);
                  }
               }
            );

         for (int i = 0; i < this.step; i++) {
            Vec3 pos = leftBound.m_82549_(rightBound.m_82546_(leftBound).m_82490_((i + 0.5) / this.step));
            BlockPos blockPos = BlockPos.m_274446_(Utils.moveToRelativeGroundLevel(this.f_19853_, pos, 2)).m_7495_();
            float impulseStrength = Utils.random.m_188501_() * 0.15F + 0.3F;
            Utils.createTremorBlock(this.f_19853_, blockPos, impulseStrength);
         }
      }
   }

   @Override
   protected void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("stompStep", this.step);
      pCompound.m_128405_("maxSteps", this.maxSteps);
   }

   @Override
   protected void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.step = pCompound.m_128451_("stompStep");
      this.maxSteps = pCompound.m_128451_("maxSteps");
   }
}
