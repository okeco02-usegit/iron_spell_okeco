package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Quaternionf;

public class IceSpiderPartEntity extends PartEntity<IceSpiderEntity> implements ICritablePartEntity {
   public final IceSpiderEntity parentMob;
   private final EntityDimensions size;
   private final Vec3 baseOffset;
   private final boolean collision;

   public IceSpiderPartEntity(IceSpiderEntity pParentMob, Vec3 offset16, float pWidth, float pHeight, boolean collision) {
      super(pParentMob);
      float inflate = 0.1F;
      this.size = EntityDimensions.m_20395_(pWidth + inflate * 2.0F, pHeight + inflate * 2.0F);
      this.parentMob = pParentMob;
      this.m_6210_();
      this.baseOffset = offset16.m_82490_(0.0625).m_82492_(0.0, inflate, 0.0);
      this.collision = collision;
   }

   public IceSpiderPartEntity(IceSpiderEntity pParentMob, Vec3 offset16, float pWidth, float pHeight) {
      this(pParentMob, offset16, pWidth, pHeight, false);
   }

   public void positionSelf(Quaternionf normal) {
      Vec3 parentPos = this.parentMob.m_20182_();
      Vec3 offset = this.baseOffset.m_82542_(1.0, this.parentMob.getCrouchHeightMultiplier(), 1.0);
      Vec3 localVec = Utils.v3d(normal.transform(Utils.v3f(this.parentMob.rotateWithBody(offset))));
      this.hardSetPos(parentPos.m_82549_(localVec.m_82490_(this.parentMob.m_6134_())));
   }

   public boolean m_5829_() {
      return this.collision;
   }

   private void hardSetPos(Vec3 newVector) {
      this.m_146884_(newVector);
      this.m_20256_(newVector);
      Vec3 vec3 = this.m_20182_();
      this.f_19854_ = vec3.f_82479_;
      this.f_19855_ = vec3.f_82480_;
      this.f_19856_ = vec3.f_82481_;
      this.f_19790_ = vec3.f_82479_;
      this.f_19791_ = vec3.f_82480_;
      this.f_19792_ = vec3.f_82481_;
   }

   protected void m_8097_() {
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return this.parentMob.hurt(this, pSource, pAmount);
   }

   protected void m_7378_(CompoundTag pCompound) {
   }

   protected void m_7380_(CompoundTag pCompound) {
   }

   public boolean m_6087_() {
      return true;
   }

   public ItemStack m_142340_() {
      return this.parentMob.m_142340_();
   }

   public boolean m_7306_(Entity pEntity) {
      return this == pEntity || this.parentMob == pEntity;
   }

   public EntityDimensions m_6972_(Pose pPose) {
      return this.size.m_20388_(this.parentMob.m_6134_());
   }

   public boolean m_142391_() {
      return false;
   }
}
