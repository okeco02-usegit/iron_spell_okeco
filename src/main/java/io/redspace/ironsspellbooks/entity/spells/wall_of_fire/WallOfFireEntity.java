package io.redspace.ironsspellbooks.entity.spells.wall_of_fire;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

public class WallOfFireEntity extends AbstractShieldEntity implements IEntityAdditionalSpawnData {
   protected ShieldPart[] subEntities;
   protected List<Vec3> partPositions = new ArrayList<>();
   protected List<Vec3> anchorPoints = new ArrayList<>();
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   protected float damage;
   protected int lifetime = 240;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public WallOfFireEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.subEntities = new ShieldPart[0];
   }

   @Override
   public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {
   }

   public WallOfFireEntity(Level level, Entity owner, List<Vec3> anchors, float damage) {
      this((EntityType<?>)EntityRegistry.WALL_OF_FIRE_ENTITY.get(), level);
      this.anchorPoints = anchors;
      this.createShield();
      this.damage = damage;
      this.setOwner(owner);
   }

   @Override
   public void m_8119_() {
      if (this.anchorPoints.size() > 1 && this.subEntities.length > 1) {
         int i = 0;

         for (int subEntitiesLength = this.subEntities.length; i < subEntitiesLength; i++) {
            PartEntity<?> subEntity = this.subEntities[i];
            Vec3 pos = this.partPositions.get(i);
            subEntity.m_146884_(pos);
            subEntity.f_19854_ = pos.f_82479_;
            subEntity.f_19855_ = pos.f_82480_;
            subEntity.f_19856_ = pos.f_82481_;
            subEntity.f_19790_ = pos.f_82479_;
            subEntity.f_19791_ = pos.f_82480_;
            subEntity.f_19792_ = pos.f_82481_;
            if (this.f_19853_.f_46443_ && i < subEntitiesLength - 1) {
               int count = this.f_19796_.m_216332_(1, 2);

               for (int j = 0; j < count; j++) {
                  Vec3 offset = this.partPositions.get(i + 1).m_82546_(pos).m_82490_(Utils.random.m_188501_()).m_82549_(Utils.getRandomVec3(0.1));
                  this.f_19853_
                     .m_7106_(
                        ParticleHelper.FIRE,
                        pos.f_82479_ + offset.f_82479_,
                        pos.f_82480_ + Utils.random.m_188501_() * 0.25 + 0.1,
                        pos.f_82481_ + offset.f_82481_,
                        0.0,
                        Math.random() * 0.25 + 0.05,
                        0.0
                     );
               }
            } else {
               for (LivingEntity livingentity : this.f_19853_.m_45976_(LivingEntity.class, subEntity.m_20191_().m_82377_(0.2, 0.0, 0.2))) {
                  if (livingentity != this.getOwner()) {
                     DamageSources.applyDamage(
                        livingentity, this.damage, ((AbstractSpell)SpellRegistry.WALL_OF_FIRE_SPELL.get()).getDamageSource(this, this.getOwner())
                     );
                  }
               }
            }
         }

         if (!this.f_19853_.f_46443_ && --this.lifetime < 0) {
            this.m_146870_();
         }
      } else {
         this.m_146870_();
      }
   }

   @Override
   public void createShield() {
      float height = 3.0F;
      float step = 0.8F;
      List<ShieldPart> entitiesList = new ArrayList<>();

      for (int i = 0; i < this.anchorPoints.size() - 1; i++) {
         Vec3 start = this.anchorPoints.get(i);
         Vec3 end = this.anchorPoints.get(i + 1);
         Vec3 dirVec = end.m_82546_(start).m_82541_().m_82490_(step);
         int steps = (int)((start.m_82554_(end) + 0.5) / step);

         for (int currentStep = 0; currentStep < steps; currentStep++) {
            ShieldPart part = new ShieldPart(this, "part" + i * steps + currentStep, 0.55F, height, false);
            double x = start.f_82479_ + dirVec.f_82479_ * currentStep;
            double y = start.f_82480_ + dirVec.f_82480_ * currentStep;
            double z = start.f_82481_ + dirVec.f_82481_ * currentStep;
            double groundY = Utils.moveToRelativeGroundLevel(this.f_19853_, new Vec3(x, y, z), 4, 4).f_82480_;
            Vec3 pos = new Vec3(x, groundY, z);
            this.partPositions.add(pos);
            entitiesList.add(part);
         }
      }

      this.subEntities = entitiesList.toArray(this.subEntities);
   }

   public void setOwner(@Nullable Entity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.m_20148_();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.m_213877_()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.f_19853_ instanceof ServerLevel) {
         this.cachedOwner = ((ServerLevel)this.f_19853_).m_8791_(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   @Override
   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   @Override
   protected void m_7380_(CompoundTag compoundTag) {
      if (this.ownerUUID != null) {
         compoundTag.m_128362_("Owner", this.ownerUUID);
      }

      compoundTag.m_128405_("lifetime", this.lifetime);
      ListTag anchors = new ListTag();

      for (Vec3 vec : this.anchorPoints) {
         CompoundTag anchor = new CompoundTag();
         anchor.m_128350_("x", (float)vec.f_82479_);
         anchor.m_128350_("y", (float)vec.f_82480_);
         anchor.m_128350_("z", (float)vec.f_82481_);
         anchors.add(anchor);
      }

      compoundTag.m_128365_("Anchors", anchors);
      super.m_7380_(compoundTag);
   }

   @Override
   protected void m_7378_(CompoundTag compoundTag) {
      if (compoundTag.m_128403_("Owner")) {
         this.ownerUUID = compoundTag.m_128342_("Owner");
      }

      if (compoundTag.m_128441_("lifetime")) {
         this.lifetime = compoundTag.m_128451_("lifetime");
      }

      this.anchorPoints = new ArrayList<>();
      if (compoundTag.m_128425_("Anchors", 9)) {
         for (Tag tag : (ListTag)compoundTag.m_128423_("Anchors")) {
            if (tag instanceof CompoundTag anchor) {
               this.anchorPoints.add(new Vec3(anchor.m_128459_("x"), anchor.m_128459_("y"), anchor.m_128459_("z")));
            }
         }
      }

      super.m_7378_(compoundTag);
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      buffer.writeInt(this.anchorPoints.size());

      for (Vec3 vec : this.anchorPoints) {
         buffer.writeFloat((float)vec.f_82479_);
         buffer.writeFloat((float)vec.f_82480_);
         buffer.writeFloat((float)vec.f_82481_);
      }
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      this.anchorPoints = new ArrayList<>();
      int length = additionalData.readInt();

      for (int i = 0; i < length; i++) {
         this.anchorPoints.add(new Vec3(additionalData.readFloat(), additionalData.readFloat(), additionalData.readFloat()));
      }

      this.createShield();
   }
}
