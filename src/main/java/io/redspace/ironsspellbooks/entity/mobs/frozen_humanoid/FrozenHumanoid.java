package io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Collections;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class FrozenHumanoid extends LivingEntity implements IEntityAdditionalSpawnData {
   protected static final EntityDataAccessor<Float> DATA_ATTACK_TIME = SynchedEntityData.m_135353_(FrozenHumanoid.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.m_135353_(FrozenHumanoid.class, EntityDataSerializers.f_135035_);
   float walkAnimSpeed;
   float walkAnimPos;
   private float shatterProjectileDamage;
   private int deathTimer = -1;
   private UUID summonerUUID;
   private LivingEntity cachedSummoner;
   @Nullable
   EntityType<?> entityToCopy;
   private HumanoidArm mainArm = HumanoidArm.RIGHT;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      LivingEntity owner = this.getSummoner();
      buffer.writeInt(owner == null ? -1 : owner.m_19879_());
      if (this.entityToCopy == null) {
         buffer.writeBoolean(false);
      } else {
         buffer.writeBoolean(true);
         buffer.m_130085_(BuiltInRegistries.f_256780_.m_7981_(this.entityToCopy));
      }
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      if (this.f_19853_.m_6815_(additionalData.readInt()) instanceof LivingEntity livingEntity) {
         this.setSummoner(livingEntity);
      }

      if (additionalData.readBoolean()) {
         this.setEntityTypeToCopy((EntityType<?>)BuiltInRegistries.f_256780_.m_7745_(additionalData.m_130281_()));
      }
   }

   public FrozenHumanoid(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_267362_ = new FrozenHumanoid.FakeWalkAnimationState();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_ATTACK_TIME, 0.0F);
      this.f_19804_.m_135372_(DATA_IS_BABY, false);
   }

   protected static void copyEntityVisualProperties(LivingEntity baseEntity, LivingEntity entityToCopy) {
      baseEntity.m_7678_(entityToCopy.m_20185_(), entityToCopy.m_20186_(), entityToCopy.m_20189_(), entityToCopy.m_146908_(), entityToCopy.m_146909_());
      baseEntity.m_5618_(entityToCopy.f_20883_);
      baseEntity.f_20884_ = baseEntity.f_20883_;
      baseEntity.m_5616_(entityToCopy.m_6080_());
      baseEntity.f_20886_ = baseEntity.f_20885_;
      baseEntity.m_20124_(entityToCopy.m_20089_());
      if (baseEntity instanceof FrozenHumanoid frozenHumanoid) {
         frozenHumanoid.mainArm = entityToCopy.m_5737_();
         frozenHumanoid.m_20088_().m_135381_(DATA_ATTACK_TIME, entityToCopy.f_20921_);
         if (entityToCopy.m_6162_()) {
            frozenHumanoid.m_20088_().m_135381_(DATA_IS_BABY, true);
         }
      } else if (baseEntity.f_19853_.f_46443_) {
         baseEntity.f_267362_ = entityToCopy.f_267362_;
         baseEntity.f_20921_ = entityToCopy.f_20921_;
         baseEntity.f_20920_ = entityToCopy.f_20921_;
         if (entityToCopy.m_6162_()) {
            if (baseEntity instanceof AgeableMob ageableMob) {
               ageableMob.m_146762_(-10);
            } else if (baseEntity instanceof Zombie zombie) {
               zombie.m_6863_(true);
            }
         }
      }

      if (entityToCopy instanceof Player player) {
         baseEntity.m_6593_(player.m_5446_());
         baseEntity.m_20340_(true);
      }
   }

   public boolean m_6162_() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_BABY);
   }

   public EntityDimensions m_6972_(Pose pose) {
      return this.entityToCopy == null ? super.m_6972_(pose) : this.entityToCopy.m_20680_();
   }

   public void setEntityTypeToCopy(@Nullable EntityType<?> entityToCopy) {
      this.entityToCopy = entityToCopy;
      this.m_6210_();
   }

   public FrozenHumanoid(Level level, LivingEntity entityToCopy) {
      this((EntityType<? extends LivingEntity>)EntityRegistry.FROZEN_HUMANOID.get(), level);
      copyEntityVisualProperties(this, entityToCopy);
      if (!(entityToCopy instanceof Player)) {
         this.setEntityTypeToCopy(entityToCopy.m_6095_());
      }

      this.f_19802_ = 1;
      this.setSummoner(entityToCopy);
   }

   public boolean m_142079_() {
      return false;
   }

   public void m_146917_(int ticksFrozen) {
   }

   public void setSummoner(@javax.annotation.Nullable LivingEntity owner) {
      if (owner != null) {
         this.summonerUUID = owner.m_20148_();
         this.cachedSummoner = owner;
      }
   }

   public LivingEntity getSummoner() {
      if (this.cachedSummoner != null && this.cachedSummoner.m_6084_()) {
         return this.cachedSummoner;
      }

      if (this.summonerUUID != null && this.m_9236_() instanceof ServerLevel) {
         if (((ServerLevel)this.m_9236_()).m_8791_(this.summonerUUID) instanceof LivingEntity livingEntity) {
            this.cachedSummoner = livingEntity;
         }

         return this.cachedSummoner;
      } else {
         return null;
      }
   }

   public float getWalkAnimSpeed() {
      return this.walkAnimSpeed;
   }

   public float getWalkAnimPos() {
      return this.walkAnimPos;
   }

   public void m_8119_() {
      if (this.f_19803_ && this.f_19853_.f_46443_ && this.cachedSummoner != null) {
         this.walkAnimSpeed = this.cachedSummoner.f_267362_.m_267731_();
         this.walkAnimPos = this.cachedSummoner.f_267362_.m_267756_();
      }

      super.m_8119_();
      if (this.deathTimer > 0) {
         this.deathTimer--;
      }

      if (this.deathTimer == 0) {
         this.m_6469_(this.m_9236_().m_269111_().m_269264_(), 100.0F);
      }
   }

   public void setDeathTimer(int timeInTicks) {
      this.deathTimer = timeInTicks;
   }

   public float getAttacktime() {
      return (Float)this.f_19804_.m_135370_(DATA_ATTACK_TIME);
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_5829_() {
      return true;
   }

   public boolean m_6087_() {
      return true;
   }

   @Nullable
   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_11983_;
   }

   @Nullable
   protected SoundEvent m_5592_() {
      return SoundEvents.f_11983_;
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (!this.m_9236_().f_46443_ && !this.m_6673_(pSource) && this.f_19802_ <= 0) {
         this.f_19802_ = 10;
         this.doPuffDamage();
         this.spawnIcicleShards(this.m_146892_(), this.shatterProjectileDamage);
         this.m_6677_(pSource);
         this.m_146870_();
         return true;
      } else {
         return false;
      }
   }

   private void doPuffDamage() {
      float damage = this.shatterProjectileDamage * 0.5F;
      AABB collider = this.m_20191_().m_82400_(2.0);
      double radius = collider.m_82362_();
      Vec3 center = collider.m_82399_();

      for (Entity entity : this.f_19853_.m_45933_(this, collider)) {
         double distanceSqr = entity.m_20238_(center);
         if (distanceSqr < radius * radius
            && entity.m_271807_()
            && !DamageSources.isFriendlyFireBetween(entity, this.getSummoner())
            && Utils.hasLineOfSight(this.f_19853_, center, entity.m_20191_().m_82399_(), true)) {
            DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.ICICLE_SPELL.get()).getDamageSource(this, this.getSummoner()));
         }
      }

      MagicManager.spawnParticles(
         this.f_19853_, ParticleHelper.SNOW_DUST, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 50, 0.2, 0.2, 0.2, 0.2, false
      );
      MagicManager.spawnParticles(
         this.f_19853_, ParticleHelper.SNOWFLAKE, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 50, 0.2, 0.2, 0.2, 0.2, false
      );
   }

   private void spawnIcicleShards(Vec3 origin, float damage) {
      int count = 8;
      int offset = 360 / count;

      for (int i = 0; i < count; i++) {
         Vec3 motion = new Vec3(0.0, 0.0, 1.0);
         motion = motion.m_82496_((float) (Math.PI / 15));
         motion = motion.m_82524_(offset * i * (float) (Math.PI / 180.0));
         IcicleProjectile shard = new IcicleProjectile(this.m_9236_(), this.getSummoner());
         shard.setDamage(damage);
         shard.m_20256_(motion);
         shard.m_20242_(false);
         Vec3 spawn = origin.m_82549_(motion.m_82542_(1.0, 0.0, 1.0).m_82541_().m_82490_(0.5));
         Vec2 angle = Utils.rotationFromDirection(motion);
         shard.m_7678_(spawn.f_82479_, spawn.f_82480_ - shard.m_20191_().m_82376_() / 2.0, spawn.f_82481_, angle.f_82471_, angle.f_82470_);
         this.m_9236_().m_7967_(shard);
      }
   }

   public void setShatterDamage(float damage) {
      this.shatterProjectileDamage = damage;
   }

   public Iterable<ItemStack> m_6168_() {
      return Collections.singleton(ItemStack.f_41583_);
   }

   public ItemStack m_6844_(EquipmentSlot pSlot) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public void m_7378_(CompoundTag compoundTag) {
      super.m_7378_(compoundTag);
      if (compoundTag.m_128403_("Summoner")) {
         this.summonerUUID = compoundTag.m_128342_("Summoner");
      }

      if (compoundTag.m_128441_("entityToCopy")) {
         try {
            this.setEntityTypeToCopy((EntityType<?>)BuiltInRegistries.f_256780_.m_7745_(ResourceLocation.parse(compoundTag.m_128461_("entityToCopy"))));
         } catch (Exception var3) {
         }
      }

      this.deathTimer = compoundTag.m_128451_("deathTimer");
   }

   public void m_7380_(CompoundTag compoundTag) {
      super.m_7380_(compoundTag);
      if (this.summonerUUID != null) {
         compoundTag.m_128362_("Summoner", this.summonerUUID);
      }

      if (this.entityToCopy != null) {
         compoundTag.m_128359_("entityToCopy", BuiltInRegistries.f_256780_.m_7981_(this.entityToCopy).toString());
      }

      compoundTag.m_128405_("deathTimer", this.deathTimer);
   }

   public HumanoidArm m_5737_() {
      return this.mainArm;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22268_(Attributes.f_22276_, 1.0)
         .m_22268_(Attributes.f_22277_, 0.0)
         .m_22268_(Attributes.f_22278_, 100.0)
         .m_22268_(Attributes.f_22279_, 0.0);
   }

   private class FakeWalkAnimationState extends WalkAnimationState {
      public float m_267756_() {
         return FrozenHumanoid.this.getWalkAnimPos();
      }

      public float m_267590_(float partialTick) {
         return FrozenHumanoid.this.getWalkAnimPos();
      }

      public float m_267731_() {
         return FrozenHumanoid.this.getWalkAnimSpeed();
      }

      public float m_267711_(float partialTick) {
         return FrozenHumanoid.this.getWalkAnimSpeed();
      }
   }
}
