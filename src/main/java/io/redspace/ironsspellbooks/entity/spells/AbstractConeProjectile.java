package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public abstract class AbstractConeProjectile extends Projectile implements NoKnockbackProjectile {
   protected static final int FAILSAFE_EXPIRE_TIME = 400;
   protected int age;
   protected float damage;
   protected boolean dealDamageActive = true;
   protected final ConePart[] subEntities;

   public AbstractConeProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level, LivingEntity entity) {
      this(entityType, level);
      this.m_5602_(entity);
   }

   public AbstractConeProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
      this.f_19794_ = true;
      this.f_19850_ = false;
      this.subEntities = new ConePart[]{
         new ConePart(this, "part1", 1.0F, 1.0F),
         new ConePart(this, "part2", 2.5F, 1.5F),
         new ConePart(this, "part3", 3.5F, 2.0F),
         new ConePart(this, "part4", 4.5F, 3.0F)
      };
   }

   public boolean m_6060_() {
      return false;
   }

   public abstract void spawnParticles();

   public boolean m_142391_() {
      return false;
   }

   protected abstract void m_5790_(EntityHitResult var1);

   public boolean isMultipartEntity() {
      return true;
   }

   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   public void m_20234_(int id) {
      super.m_20234_(id);

      for (int i = 0; i < this.subEntities.length; i++) {
         this.subEntities[i].m_20234_(id + i + 1);
      }
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   protected void m_8097_() {
   }

   protected static Vec3 rayTrace(Entity owner) {
      float f = owner.m_146909_();
      float f1 = owner.m_146908_();
      float f2 = Mth.m_14089_(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = Mth.m_14031_(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -Mth.m_14089_(-f * (float) (Math.PI / 180.0));
      float f5 = Mth.m_14031_(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      return new Vec3(f6, f5, f7);
   }

   public void m_8119_() {
      super.m_8119_();
      if (++this.age > 400) {
         this.m_146870_();
      }

      Entity owner = this.m_19749_();
      if (owner != null) {
         Vec3 rayTraceVector = rayTrace(owner);
         Vec3 ownerEyePos = owner.m_20299_(1.0F).m_82492_(0.0, 0.8, 0.0);
         this.m_146884_(ownerEyePos);
         this.m_146926_(owner.m_146909_());
         this.m_146922_(owner.m_146908_());
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
         double scale = 1.0;

         for (int i = 0; i < this.subEntities.length; i++) {
            ConePart subEntity = this.subEntities[i];
            double distance = 1.0 + i * scale * subEntity.m_6972_(null).f_20377_ / 2.0;
            Vec3 newVector = ownerEyePos.m_82549_(rayTraceVector.m_82542_(distance, distance, distance));
            subEntity.m_146884_(newVector);
            subEntity.m_20256_(newVector);
            Vec3 vec3 = new Vec3(subEntity.m_20185_(), subEntity.m_20186_(), subEntity.m_20189_());
            subEntity.f_19854_ = vec3.f_82479_;
            subEntity.f_19855_ = vec3.f_82480_;
            subEntity.f_19856_ = vec3.f_82481_;
            subEntity.f_19790_ = vec3.f_82479_;
            subEntity.f_19791_ = vec3.f_82480_;
            subEntity.f_19792_ = vec3.f_82481_;
         }
      }

      if (!this.f_19853_.f_46443_) {
         if (this.dealDamageActive) {
            for (Entity entity : this.getSubEntityCollisions()) {
               this.m_5790_(new EntityHitResult(entity));
            }

            this.dealDamageActive = false;
         }
      } else {
         this.spawnParticles();
      }
   }

   public void setDealDamageActive() {
      this.dealDamageActive = true;
   }

   protected Set<Entity> getSubEntityCollisions() {
      List<Entity> collisions = new ArrayList<>();

      for (Entity conepart : this.subEntities) {
         collisions.addAll(this.m_9236_().m_45933_(conepart, conepart.m_20191_()));
      }

      return collisions.stream()
         .filter(target -> target != this.m_19749_() && target instanceof LivingEntity && Utils.hasLineOfSight(this.f_19853_, this, target, true))
         .collect(Collectors.toSet());
   }

   protected void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128350_("Damage", this.damage);
   }

   protected void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.damage = pCompound.m_128457_("Damage");
   }
}
