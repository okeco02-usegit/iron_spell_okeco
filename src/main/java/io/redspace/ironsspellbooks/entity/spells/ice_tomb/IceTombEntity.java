package io.redspace.ironsspellbooks.entity.spells.ice_tomb;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.ice_spider.ICritablePartEntity;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class IceTombEntity extends Entity implements PreventDismount, AntiMagicSusceptible, ICritablePartEntity {
   @Nullable
   private Entity cachedOwner;
   @Nullable
   private UUID ownerUUID;
   private boolean evil;
   private float health = 1.0F;
   private int lifetime = -1;
   private float healing;

   public IceTombEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public IceTombEntity(Level level, Entity owner) {
      super((EntityType)EntityRegistry.ICE_TOMB.get(), level);
      this.setOwner(owner);
   }

   public boolean m_7313_(Entity entity) {
      return this.m_20365_(entity);
   }

   public void setEvil() {
      this.evil = true;
   }

   public void setOwner(@Nullable Entity owner) {
      if (owner != null) {
         this.ownerUUID = owner.m_20148_();
         this.cachedOwner = owner;
      }
   }

   public void setLifetime(int lifetime) {
      this.lifetime = lifetime;
   }

   public void setHealing(float healing) {
      this.healing = healing;
   }

   public boolean m_20367_(Entity pEntity) {
      return this.evil;
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.m_213877_()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.f_19853_ instanceof ServerLevel serverlevel) {
         this.cachedOwner = serverlevel.m_8791_(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.evil) {
         this.m_20197_().forEach(this::doNegativeEffects);
      } else if (this.f_19797_ % 20 == 0) {
         this.m_20197_().forEach(this::doPositiveEffects);
      }

      if (!this.m_20068_()) {
         this.m_20256_(this.m_20184_().m_82520_(0.0, -0.08, 0.0));
      }

      this.m_6478_(MoverType.SELF, this.m_20184_());
      if (this.m_20096_()) {
         this.m_20256_(this.m_20184_().m_82490_(0.7));
      } else {
         this.m_20256_(this.m_20184_().m_82542_(0.95, 1.0, 0.95));
      }

      if (this.lifetime >= 0 && this.f_19797_ > this.lifetime) {
         this.destroyTomb();
      }
   }

   public void doPositiveEffects(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         MinecraftForge.EVENT_BUS.post(new SpellHealEvent(livingEntity, livingEntity, this.healing, (SchoolType)SchoolRegistry.ICE.get()));
         livingEntity.m_5634_(this.healing);
      }
   }

   public void doNegativeEffects(Entity entity) {
      entity.m_146917_(Math.min(entity.m_146891_() * 3, entity.m_146888_() + 10));
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (!this.f_19853_.f_46443_ && this.health > 0.0F) {
         if (DamageSources.isFriendlyFireBetween(source.m_7639_(), this.m_146895_())) {
            return false;
         }

         if (!this.m_6673_(source) && (source.m_7639_() == null || !this.m_20365_(source.m_7639_()))) {
            this.health -= amount;
            if (this.health <= 0.0F) {
               this.die(source, amount);
            }

            return true;
         }
      }

      return super.m_6469_(source, amount);
   }

   public void die(DamageSource damageSource, float amount) {
      List<Entity> entities = this.m_20197_();
      this.destroyTomb();
      if (this.evil) {
         entities.forEach(entity -> entity.m_6469_(damageSource, amount * 2.0F));
      }
   }

   public void m_6074_() {
      this.destroyTomb();
   }

   protected void m_8097_() {
   }

   @Override
   public boolean canEntityDismount(Entity entity) {
      return entity.m_20148_().equals(this.ownerUUID);
   }

   protected void m_20348_(Entity passenger) {
      super.m_20348_(passenger);
      this.m_6210_();
   }

   protected void m_20351_(Entity passenger) {
      super.m_20351_(passenger);
      this.destroyTomb();
   }

   public void destroyTomb() {
      if (!this.f_19853_.f_46443_) {
         this.m_20153_();
         this.m_5496_(SoundEvents.f_11983_, 2.0F, 1.0F);
         MagicManager.spawnParticles(
            this.f_19853_, ParticleHelper.SNOW_DUST, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 50, 0.2, 0.2, 0.2, 0.2, false
         );
         MagicManager.spawnParticles(
            this.f_19853_, ParticleHelper.SNOWFLAKE, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 50, 0.2, 0.2, 0.2, 0.2, false
         );
         this.m_146870_();
      }
   }

   public double m_6048_() {
      return 0.0;
   }

   public void m_19956_(Entity passenger, MoveFunction p_19958_) {
      passenger.m_6034_(this.m_20185_(), this.m_20186_(), this.m_20189_());
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   protected void m_7380_(CompoundTag compound) {
      if (this.ownerUUID != null) {
         compound.m_128362_("Owner", this.ownerUUID);
      }

      compound.m_128405_("age", this.f_19797_);
      compound.m_128405_("lifetime", this.lifetime);
      compound.m_128379_("evil", this.evil);
      compound.m_128350_("health", this.health);
      compound.m_128350_("healing", this.healing);
   }

   public boolean m_275843_() {
      return false;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   protected void m_7378_(CompoundTag compound) {
      if (compound.m_128403_("Owner")) {
         this.ownerUUID = compound.m_128342_("Owner");
         this.cachedOwner = null;
      }

      this.f_19797_ = compound.m_128451_("age");
      this.lifetime = compound.m_128451_("lifetime");
      this.evil = compound.m_128471_("evil");
      this.health = compound.m_128457_("health");
      this.healing = compound.m_128457_("healing");
   }

   public void m_6210_() {
      double d0 = this.m_20185_();
      double d1 = this.m_20186_();
      double d2 = this.m_20189_();
      super.m_6210_();
      this.m_6034_(d0, d1, d2);
   }

   public EntityDimensions m_6972_(Pose pPose) {
      List<Entity> passengers = this.m_20197_();
      float hScale = 1.0F;
      float vScale = 1.0F;
      if (!passengers.isEmpty() && passengers.get(0) instanceof LivingEntity livingEntity) {
         hScale = livingEntity.m_20205_() + 0.4F;
         vScale = (livingEntity.m_20206_() + 0.2F) / 2.0F;
         vScale = (vScale + hScale) * 0.5F;
      }

      return super.m_6972_(pPose).m_20390_(hScale * 0.9F, vScale * 0.9F);
   }

   public boolean m_7337_(@NotNull Entity pEntity) {
      return true;
   }

   public boolean m_5829_() {
      return true;
   }

   public boolean m_6087_() {
      return true;
   }

   public void m_7334_(@NotNull Entity pEntity) {
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.destroyTomb();
   }
}
