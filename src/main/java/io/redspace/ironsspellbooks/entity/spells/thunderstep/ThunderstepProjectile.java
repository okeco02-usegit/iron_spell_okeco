package io.redspace.ironsspellbooks.entity.spells.thunderstep;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderstepProjectile extends AbstractMagicProjectile {
   protected boolean inGround;

   public ThunderstepProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public ThunderstepProjectile(Level levelIn, Entity shooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.THUNDERSTEP_PROJECTILE.get(), levelIn);
      this.m_5602_(shooter);
   }

   @Override
   public void m_8119_() {
      if (!this.inGround) {
         super.m_8119_();
      } else {
         this.deltaMovementOld = this.m_20184_();
         if (this.f_19797_ > 300) {
            this.m_146870_();
            return;
         }

         if (this.shouldFall()) {
            this.inGround = false;
            this.m_20256_(this.m_20184_().m_82541_().m_82490_(0.05F));
         }
      }
   }

   protected void m_8060_(BlockHitResult pResult) {
      super.m_8060_(pResult);
      Vec3 vec3 = pResult.m_82450_().m_82492_(this.m_20185_(), this.m_20186_(), this.m_20189_());
      this.m_20256_(vec3);
      Vec3 vec31 = vec3.m_82541_().m_82490_(0.05F);
      this.m_20343_(this.m_20185_() - vec31.f_82479_, this.m_20186_() - vec31.f_82480_, this.m_20189_() - vec31.f_82481_);
      this.inGround = true;
   }

   @Override
   protected boolean m_5603_(Entity pTarget) {
      return false;
   }

   private boolean shouldFall() {
      return this.inGround && this.f_19853_.m_45772_(new AABB(this.m_20182_(), this.m_20182_()).m_82400_(0.06));
   }

   @Override
   public void trailParticles() {
   }

   @Override
   public void impactParticles(double x, double y, double z) {
   }

   @Override
   public float getSpeed() {
      return 1.0F;
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
