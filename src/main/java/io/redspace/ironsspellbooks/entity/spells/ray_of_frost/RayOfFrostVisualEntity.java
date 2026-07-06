package io.redspace.ironsspellbooks.entity.spells.ray_of_frost;

import io.redspace.ironsspellbooks.registries.EntityRegistry;
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

public class RayOfFrostVisualEntity extends Entity implements IEntityAdditionalSpawnData {
   public static final int lifetime = 15;
   public float distance;

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public RayOfFrostVisualEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public RayOfFrostVisualEntity(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
      super((EntityType)EntityRegistry.RAY_OF_FROST_VISUAL_ENTITY.get(), level);
      this.m_146884_(start.m_82492_(0.0, 0.75, 0.0));
      this.distance = (float)start.m_82554_(end);
      this.m_19915_(owner.m_146908_(), owner.m_146909_());
   }

   public void m_8119_() {
      if (++this.f_19797_ > 15) {
         this.m_146870_();
      }
   }

   public boolean m_6000_(double pX, double pY, double pZ) {
      return true;
   }

   public boolean m_142391_() {
      return false;
   }

   protected void m_8097_() {
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
