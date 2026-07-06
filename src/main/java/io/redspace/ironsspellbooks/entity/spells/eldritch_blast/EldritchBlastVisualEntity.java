package io.redspace.ironsspellbooks.entity.spells.eldritch_blast;

import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class EldritchBlastVisualEntity extends Entity implements IEntityAdditionalSpawnData {
   public static final int lifetime = 8;
   public float distance;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public EldritchBlastVisualEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void m_8097_() {
   }

   public EldritchBlastVisualEntity(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
      super((EntityType)EntityRegistry.ELDRITCH_BLAST_VISUAL_ENTITY.get(), level);
      this.m_146884_(start);
      this.distance = (float)start.m_82554_(end);
      this.m_19915_(owner.m_146908_(), owner.m_146909_());
   }

   public void m_8119_() {
      if (this.f_19797_ == 1) {
         if (this.f_19853_.f_46443_) {
            Vec3 forward = this.m_20156_();

            for (float i = 1.0F; i < this.distance; i += 0.5F) {
               Vec3 pos = this.m_20182_().m_82549_(forward.m_82490_(i));
               this.f_19853_.m_6493_(ParticleTypes.f_123762_, false, pos.f_82479_, pos.f_82480_ + 0.5, pos.f_82481_, 0.0, 0.0, 0.0);
            }
         }
      } else if (this.f_19797_ > 8) {
         this.m_146870_();
      }
   }

   public boolean m_6000_(double pX, double pY, double pZ) {
      return true;
   }

   public boolean m_142391_() {
      return false;
   }

   protected void m_7378_(CompoundTag pCompound) {
   }

   protected void m_7380_(CompoundTag pCompound) {
   }

   public void writeSpawnData(FriendlyByteBuf buffer) {
      buffer.writeInt((int)(this.distance * 10.0F));
   }

   public void readSpawnData(FriendlyByteBuf additionalData) {
      this.distance = additionalData.readInt() / 10.0F;
   }
}
