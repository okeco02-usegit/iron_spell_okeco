package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;

public abstract class AbstractShieldEntity extends Entity implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_HEALTH_ID = SynchedEntityData.m_135353_(AbstractShieldEntity.class, EntityDataSerializers.f_135029_);
   public boolean hurtThisTick;

   public AbstractShieldEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public AbstractShieldEntity(Level level, float health) {
      this((EntityType<?>)EntityRegistry.SHIELD_ENTITY.get(), level);
      this.setHealth(health);
   }

   protected abstract void createShield();

   public abstract void takeDamage(DamageSource var1, float var2, @Nullable Vec3 var3);

   public void m_8119_() {
      this.hurtThisTick = false;

      for (PartEntity<?> subEntity : this.getParts()) {
         Vec3 pos = subEntity.m_20182_();
         subEntity.m_146884_(pos);
         subEntity.f_19854_ = pos.f_82479_;
         subEntity.f_19855_ = pos.f_82480_;
         subEntity.f_19856_ = pos.f_82481_;
         subEntity.f_19790_ = pos.f_82479_;
         subEntity.f_19791_ = pos.f_82480_;
         subEntity.f_19792_ = pos.f_82481_;
      }
   }

   protected void destroy() {
      this.m_6074_();
   }

   public boolean isMultipartEntity() {
      return true;
   }

   public abstract PartEntity<?>[] getParts();

   public void m_20234_(int id) {
      super.m_20234_(id);
      PartEntity<?>[] subEntities = this.getParts();

      for (int i = 0; i < subEntities.length; i++) {
         subEntities[i].m_20234_(id + i + 1);
      }
   }

   public float getHealth() {
      return (Float)this.f_19804_.m_135370_(DATA_HEALTH_ID);
   }

   public void setHealth(float pHealth) {
      this.f_19804_.m_135381_(DATA_HEALTH_ID, pHealth);
   }

   public boolean m_7337_(Entity pEntity) {
      return false;
   }

   public boolean m_5829_() {
      return false;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_HEALTH_ID, 1.0F);
   }

   protected void m_7378_(CompoundTag pCompound) {
      if (pCompound.m_128425_("Health", 99)) {
         this.setHealth(pCompound.m_128457_("Health"));
      }
   }

   protected void m_7380_(CompoundTag pCompound) {
      pCompound.m_128350_("Health", this.getHealth());
   }

   public List<VoxelShape> getVoxels() {
      List<VoxelShape> voxels = new ArrayList<>();

      for (PartEntity<?> shieldPart : this.getParts()) {
         voxels.add(Shapes.m_83064_(shieldPart.m_20191_()));
      }

      return voxels;
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.m_146870_();
   }
}
