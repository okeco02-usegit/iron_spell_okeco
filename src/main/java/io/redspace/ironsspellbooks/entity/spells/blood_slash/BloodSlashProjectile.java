package io.redspace.ironsspellbooks.entity.spells.blood_slash;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class BloodSlashProjectile extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.m_135353_(BloodSlashProjectile.class, EntityDataSerializers.f_135029_);
   private static final double SPEED = 1.0;
   private static final int EXPIRE_TIME = 80;
   public final int animationSeed = Utils.random.m_188503_(9999);
   private final float maxRadius;
   public AABB oldBB;
   private int age;
   private float damage;
   public int animationTime;
   private List<Entity> victims;

   public BloodSlashProjectile(EntityType<? extends BloodSlashProjectile> entityType, Level level) {
      super(entityType, level);
      this.setRadius(0.6F);
      this.maxRadius = 3.0F;
      this.oldBB = this.m_20191_();
      this.victims = new ArrayList<>();
      this.m_20242_(true);
   }

   public BloodSlashProjectile(EntityType<? extends BloodSlashProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.m_5602_(shooter);
      this.m_146922_(shooter.m_146908_());
      this.m_146926_(shooter.m_146909_());
   }

   public BloodSlashProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends BloodSlashProjectile>)EntityRegistry.BLOOD_SLASH_PROJECTILE.get(), levelIn, shooter);
   }

   public void shoot(Vec3 rotation) {
      this.m_20256_(rotation.m_82490_(1.0));
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_RADIUS, 0.5F);
   }

   public void setRadius(float newRadius) {
      if (newRadius <= this.maxRadius && !this.f_19853_.f_46443_) {
         this.m_20088_().m_135381_(DATA_RADIUS, Mth.m_14036_(newRadius, 0.0F, this.maxRadius));
      }
   }

   public float getRadius() {
      return (Float)this.m_20088_().m_135370_(DATA_RADIUS);
   }

   public void m_6210_() {
      double d0 = this.m_20185_();
      double d1 = this.m_20186_();
      double d2 = this.m_20189_();
      super.m_6210_();
      this.m_6034_(d0, d1, d2);
   }

   public void m_8119_() {
      super.m_8119_();
      if (++this.age > 80) {
         this.m_146870_();
      } else {
         this.oldBB = this.m_20191_();
         this.setRadius(this.getRadius() + 0.12F);
         if (!this.f_19853_.f_46443_) {
            HitResult hitresult = ProjectileUtil.m_278158_(this, this::m_5603_);
            if (hitresult.m_6662_() == Type.BLOCK) {
               this.m_8060_((BlockHitResult)hitresult);
            }

            for (Entity entity : this.f_19853_
               .m_45933_(this, this.m_20191_())
               .stream()
               .filter(target -> this.m_5603_(target) && !this.victims.contains(target))
               .collect(Collectors.toSet())) {
               this.damageEntity(entity);
               MagicManager.spawnParticles(
                  this.f_19853_, ParticleHelper.BLOOD, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), 50, 0.0, 0.0, 0.0, 0.5, true
               );
               if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
                  this.m_146870_();
                  return;
               }
            }
         }

         this.m_146884_(this.m_20182_().m_82549_(this.m_20184_()));
         this.spawnParticles();
      }
   }

   public EntityDimensions m_6972_(Pose p_19721_) {
      this.m_20191_();
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, 0.5F);
   }

   public void m_7350_(EntityDataAccessor<?> p_19729_) {
      if (DATA_RADIUS.equals(p_19729_)) {
         this.m_6210_();
      }

      super.m_7350_(p_19729_);
   }

   protected void m_8060_(BlockHitResult blockHitResult) {
      super.m_8060_(blockHitResult);
      this.m_146870_();
   }

   private void damageEntity(Entity entity) {
      if (!this.victims.contains(entity)) {
         DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get()).getDamageSource(this, this.m_19749_()));
         this.victims.add(entity);
      }
   }

   public void spawnParticles() {
      if (this.f_19853_.f_46443_) {
         float width = (float)this.m_20191_().m_82362_();
         float step = 0.25F;
         float radians = (float) (Math.PI / 180.0) * this.m_146908_();
         float speed = 0.1F;

         for (int i = 0; i < width / step; i++) {
            double x = this.m_20185_();
            double y = this.m_20186_();
            double z = this.m_20189_();
            double offset = step * (i - width / step / 2.0F);
            double rotX = offset * Math.cos(radians);
            double rotZ = -offset * Math.sin(radians);
            double dx = Math.random() * speed * 2.0 - speed;
            double dy = Math.random() * speed * 2.0 - speed;
            double dz = Math.random() * speed * 2.0 - speed;
            this.f_19853_.m_6493_(ParticleHelper.BLOOD, false, x + rotX + dx, y + dy, z + rotZ + dz, dx, dy, dz);
         }
      }
   }

   protected boolean m_5603_(Entity entity) {
      return entity != this.m_19749_() && super.m_5603_(entity);
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.m_146870_();
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
