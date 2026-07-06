package io.redspace.ironsspellbooks.entity.spells.thrown_spear;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownSpear extends AbstractArrow {
   private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.m_135353_(ThrownSpear.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.m_135353_(ThrownSpear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> ID_CHANNELED = SynchedEntityData.m_135353_(ThrownSpear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<ItemStack> ID_ITEM = SynchedEntityData.m_135353_(ThrownSpear.class, EntityDataSerializers.f_135033_);
   private boolean dealtDamage;
   public int clientSideReturnTridentTickCount;

   public ThrownSpear(EntityType<? extends AbstractArrow> entityType, Level level) {
      super(entityType, level);
   }

   public ThrownSpear(Level level, ItemStack spearitem, double damage) {
      this((EntityType<? extends AbstractArrow>)EntityRegistry.THROWN_SPEAR.get(), level);
      this.m_36781_(damage);
      this.setWeaponItem(spearitem);
   }

   public boolean isChanneled() {
      return (Boolean)this.f_19804_.m_135370_(ID_CHANNELED);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(ID_LOYALTY, (byte)0);
      this.f_19804_.m_135372_(ID_FOIL, false);
      this.f_19804_.m_135372_(ID_ITEM, ItemStack.f_41583_);
      this.f_19804_.m_135372_(ID_CHANNELED, false);
   }

   public void setWeaponItem(ItemStack itemStack) {
      this.f_19804_.m_135381_(ID_ITEM, itemStack);
      this.f_19804_.m_135381_(ID_LOYALTY, this.getLoyaltyFromItem(itemStack));
      this.f_19804_.m_135381_(ID_FOIL, itemStack.m_41790_());
      this.f_19804_.m_135381_(ID_CHANNELED, Utils.getEnchantmentLevel(this.f_19853_, itemStack, Enchantments.f_44958_) > 0);
   }

   protected ItemStack m_7941_() {
      return this.getWeaponItem();
   }

   public ItemStack getWeaponItem() {
      return (ItemStack)this.f_19804_.m_135370_(ID_ITEM);
   }

   public void m_8119_() {
      if (this.f_36704_ > 4) {
         this.dealtDamage = true;
      }

      Entity entity = this.m_19749_();
      int loyalty = (Byte)this.f_19804_.m_135370_(ID_LOYALTY);
      if (loyalty > 0 && (this.dealtDamage || this.m_36797_()) && entity != null) {
         this.m_36790_(true);
         Vec3 vec3 = entity.m_146892_().m_82546_(this.m_20182_());
         this.m_20343_(this.m_20185_(), this.m_20186_() + vec3.f_82480_ * 0.015 * loyalty, this.m_20189_());
         if (this.m_9236_().f_46443_) {
            this.f_19791_ = this.m_20186_();
         }

         double d0 = 0.07 * loyalty;
         this.m_20256_(this.m_20184_().m_82490_(0.95).m_82549_(vec3.m_82541_().m_82490_(d0)));
         if (this.clientSideReturnTridentTickCount == 0) {
            this.m_20256_(Vec3.f_82478_);
            this.m_5496_(SoundEvents.f_12516_, 10.0F, 1.0F);
         }

         Player player = this.f_19853_.m_46003_(entity.m_20148_());
         if (player != null && player.m_20280_(this) < Mth.m_14008_(this.m_20184_().m_82556_() * 3.0, 4.0, 25.0)) {
            this.m_6123_(player);
         }

         this.clientSideReturnTridentTickCount++;
      }

      super.m_8119_();
   }

   public boolean isFoil() {
      return (Boolean)this.f_19804_.m_135370_(ID_FOIL);
   }

   @Nullable
   protected EntityHitResult m_6351_(Vec3 startVec, Vec3 endVec) {
      return this.dealtDamage ? null : super.m_6351_(startVec, endVec);
   }

   protected void m_6532_(HitResult result) {
      if (this.isChanneled() && !this.f_19853_.f_46443_ && !this.dealtDamage) {
         this.m_5496_((SoundEvent)SoundRegistry.SPEAR_CHANNELING_STRIKE.get(), 6.0F, 0.9F + Utils.random.m_188503_(20) * 0.01F);
         MagicManager.spawnParticles(this.f_19853_, ParticleHelper.ELECTRICITY, this.m_20185_(), this.m_20186_(), this.m_20189_(), 75, 0.1, 0.1, 0.1, 2.0, true);
         MagicManager.spawnParticles(
            this.f_19853_, ParticleHelper.ELECTRICITY, this.m_20185_(), this.m_20186_(), this.m_20189_(), 75, 0.1, 0.1, 0.1, 0.5, false
         );
      }

      super.m_6532_(result);
      this.m_36740_(SoundEvents.f_12515_);
   }

   protected void m_5790_(EntityHitResult result) {
      Entity victim = result.m_82443_();
      float f = (float)this.m_36789_();
      Entity owner = this.m_19749_();
      boolean channeled = this.isChanneled();
      DamageSource damagesource = channeled
         ? this.m_269291_().m_268998_(ISSDamageTypes.LIGHTNING_MAGIC, this, (Entity)(owner == null ? this : owner))
         : this.m_269291_().m_269525_(this, (Entity)(owner == null ? this : owner));
      if (victim instanceof LivingEntity livingentity) {
         f += EnchantmentHelper.m_44833_(this.getWeaponItem(), livingentity.m_6336_());
      }

      if (channeled && owner instanceof LivingEntity livingOwner) {
         f *= (float)livingOwner.m_21133_((Attribute)AttributeRegistry.LIGHTNING_SPELL_POWER.get());
      }

      this.dealtDamage = true;
      if (victim.m_6469_(damagesource, f)) {
         if (victim.m_6095_() == EntityType.f_20566_) {
            return;
         }

         if (victim instanceof LivingEntity livingentity1 && owner instanceof LivingEntity entity1) {
            EnchantmentHelper.m_44823_(livingentity1, entity1);
            EnchantmentHelper.m_44896_(entity1, livingentity1);
         }

         if (victim instanceof LivingEntity livingentity) {
            this.m_7761_(livingentity);
         }
      }

      this.m_20256_(this.m_20184_().m_82542_(-0.03, -0.1, -0.03));
      this.m_5496_(SoundEvents.f_12514_, 1.0F, 0.7F);
   }

   protected boolean m_142470_(Player player) {
      if (!this.m_213877_() && this.m_19749_() != null && this.m_150171_(player)) {
         int loyalty = (Byte)this.f_19804_.m_135370_(ID_LOYALTY);
         if (player.m_150110_().f_35937_ && this.f_36705_ == Pickup.CREATIVE_ONLY
            || !player.m_150110_().f_35937_ && this.f_36705_ == Pickup.ALLOWED
            || this.f_36705_ != Pickup.DISALLOWED && loyalty > 0) {
            player.m_36335_().m_41527_(this.m_7941_().m_41720_());
            if (loyalty > 0) {
               this.m_216990_((SoundEvent)SoundRegistry.SPEAR_RETURN.get());
            }

            return true;
         }
      }

      return false;
   }

   protected SoundEvent m_7239_() {
      return SoundEvents.f_12515_;
   }

   public void m_6123_(Player entity) {
      if (this.m_150171_(entity) || this.m_19749_() == null) {
         super.m_6123_(entity);
      }
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.dealtDamage = compound.m_128471_("DealtDamage");
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("DealtDamage", this.dealtDamage);
      compound.m_128365_("item", this.getWeaponItem().m_41739_(new CompoundTag()));
      compound.m_128473_("weapon");
   }

   private byte getLoyaltyFromItem(ItemStack stack) {
      return this.m_9236_() instanceof ServerLevel serverlevel ? (byte)Mth.m_14045_(EnchantmentHelper.m_44928_(stack), 0, 127) : 0;
   }

   public void m_6901_() {
      int i = (Byte)this.f_19804_.m_135370_(ID_LOYALTY);
      if (this.f_36705_ != Pickup.ALLOWED || i <= 0) {
         super.m_6901_();
      }
   }

   protected float m_6882_() {
      return 0.99F;
   }

   public boolean m_6000_(double x, double y, double z) {
      return true;
   }
}
