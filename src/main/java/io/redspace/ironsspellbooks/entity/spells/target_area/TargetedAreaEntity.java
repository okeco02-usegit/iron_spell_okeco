package io.redspace.ironsspellbooks.entity.spells.target_area;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

public class TargetedAreaEntity extends Entity {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.m_135353_(TargetedAreaEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.m_135353_(TargetedAreaEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> DATA_FADING = SynchedEntityData.m_135353_(TargetedAreaEntity.class, EntityDataSerializers.f_135035_);
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   boolean hasOwner;
   boolean shouldFade;
   private int duration;

   public void setOwner(@Nullable Entity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.m_20148_();
         this.cachedOwner = pOwner;
         this.hasOwner = true;
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && this.cachedOwner.m_6084_()) {
         return this.cachedOwner;
      }

      if (this.ownerUUID != null && this.f_19853_ instanceof ServerLevel serverLevel) {
         this.cachedOwner = serverLevel.m_8791_(this.ownerUUID);
         if (serverLevel.m_8791_(this.ownerUUID) instanceof LivingEntity livingEntity) {
            this.cachedOwner = livingEntity;
         }

         return this.cachedOwner;
      } else {
         return null;
      }
   }

   public TargetedAreaEntity(EntityType<TargetedAreaEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRadius(3.0F);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public static TargetedAreaEntity createTargetAreaEntity(Level level, Vec3 center, float radius, int color) {
      TargetedAreaEntity targetedAreaEntity = new TargetedAreaEntity(level, radius, color);
      targetedAreaEntity.m_146884_(center);
      level.m_7967_(targetedAreaEntity);
      return targetedAreaEntity;
   }

   public static TargetedAreaEntity createTargetAreaEntity(Level level, Vec3 center, float radius, int color, Entity owner) {
      TargetedAreaEntity targetedAreaEntity = new TargetedAreaEntity(level, radius, color);
      targetedAreaEntity.m_146884_(center);
      targetedAreaEntity.setOwner(owner);
      level.m_7967_(targetedAreaEntity);
      return targetedAreaEntity;
   }

   public void m_8119_() {
      this.f_19803_ = false;
      Entity owner = this.getOwner();
      if (owner != null) {
         this.m_146884_(owner.m_20182_());
         this.f_19790_ = owner.f_19790_;
         this.f_19791_ = owner.f_19791_;
         this.f_19792_ = owner.f_19792_;
         this.f_19854_ = owner.f_19854_;
         this.f_19855_ = owner.f_19855_;
         this.f_19856_ = owner.f_19856_;
      }

      if (this.shouldFade && this.f_19797_ >= this.duration - 10) {
         this.f_19804_.m_135381_(DATA_FADING, true);
      }

      if (!this.f_19853_.f_46443_
         && (
            this.duration > 0 && this.f_19797_ > this.duration
               || this.duration == 0 && this.f_19797_ > 400
               || this.hasOwner && (owner == null || owner.m_213877_())
         )) {
         this.m_146870_();
      }
   }

   public TargetedAreaEntity(Level level, float radius, int color) {
      this((EntityType<TargetedAreaEntity>)EntityRegistry.TARGET_AREA_ENTITY.get(), level);
      this.setRadius(radius);
      this.setColor(color);
   }

   public EntityDimensions m_6972_(Pose pPose) {
      return EntityDimensions.m_20395_(this.getRadius() * 2.0F, 0.8F);
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   public boolean m_6060_() {
      return false;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_RADIUS, 2.0F);
      this.f_19804_.m_135372_(DATA_COLOR, 16777215);
      this.f_19804_.m_135372_(DATA_FADING, false);
   }

   public boolean isFading() {
      return (Boolean)this.f_19804_.m_135370_(DATA_FADING);
   }

   public void setRadius(float pRadius) {
      if (!this.f_19853_.f_46443_) {
         this.m_20088_().m_135381_(DATA_RADIUS, Mth.m_14036_(pRadius, 0.0F, 32.0F));
      }
   }

   public void setShouldFade(boolean shouldFade) {
      this.shouldFade = shouldFade;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public float getRadius() {
      return (Float)this.m_20088_().m_135370_(DATA_RADIUS);
   }

   public void setColor(int color) {
      if (!this.f_19853_.f_46443_) {
         this.m_20088_().m_135381_(DATA_COLOR, color);
      }
   }

   public Vector3f getColor() {
      return Utils.deconstructRGB((Integer)this.m_20088_().m_135370_(DATA_COLOR));
   }

   public int getColorRaw() {
      return (Integer)this.m_20088_().m_135370_(DATA_COLOR);
   }

   public void m_7350_(EntityDataAccessor<?> pKey) {
      if (DATA_RADIUS.equals(pKey)) {
         this.m_6210_();
         if (this.getRadius() < 0.1F) {
            this.m_146870_();
         }
      }

      super.m_7350_(pKey);
   }

   public void m_6210_() {
      double d0 = this.m_20185_();
      double d1 = this.m_20186_();
      double d2 = this.m_20189_();
      super.m_6210_();
      this.m_6034_(d0, d1, d2);
   }

   protected void m_7380_(CompoundTag tag) {
      tag.m_128350_("Radius", this.getRadius());
      tag.m_128405_("Color", this.getColorRaw());
      tag.m_128405_("Age", this.f_19797_);
      tag.m_128379_("ShouldFade", this.shouldFade);
      if (this.duration > 0) {
         tag.m_128405_("Duration", this.duration);
      }

      if (this.ownerUUID != null) {
         tag.m_128362_("Owner", this.ownerUUID);
      }
   }

   protected void m_7378_(CompoundTag tag) {
      this.setRadius(tag.m_128457_("Radius"));
      this.setColor(tag.m_128451_("Color"));
      this.f_19797_ = tag.m_128451_("Age");
      this.shouldFade = tag.m_128471_("ShouldFade");
      if (tag.m_128441_("Duration")) {
         this.duration = tag.m_128451_("Duration");
      }

      if (tag.m_128441_("Owner")) {
         this.ownerUUID = tag.m_128342_("Owner");
         this.hasOwner = true;
      }
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      Entity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.m_19879_());
   }

   public void m_141965_(ClientboundAddEntityPacket pPacket) {
      super.m_141965_(pPacket);
      Entity entity = this.f_19853_.m_6815_(pPacket.m_131509_());
      if (entity != null) {
         this.setOwner(entity);
      }
   }
}
