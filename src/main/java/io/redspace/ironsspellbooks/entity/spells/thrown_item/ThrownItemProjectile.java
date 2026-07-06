package io.redspace.ironsspellbooks.entity.spells.thrown_item;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownItemProjectile extends AbstractMagicProjectile {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.m_135353_(ThrownItemProjectile.class, EntityDataSerializers.f_135033_);
   private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.m_135353_(ThrownItemProjectile.class, EntityDataSerializers.f_135029_);

   public float getScale() {
      return (Float)this.f_19804_.m_135370_(DATA_SCALE);
   }

   public void setScale(float scale) {
      this.f_19804_.m_135381_(DATA_SCALE, scale);
   }

   public ThrownItemProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public ThrownItemProjectile(Level level, ItemStack itemStack) {
      this((EntityType<? extends Projectile>)EntityRegistry.THROWN_ITEM.get(), level);
      this.setThrownItem(itemStack);
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_ITEM, ItemStack.f_41583_);
      this.f_19804_.m_135372_(DATA_SCALE, 1.0F);
   }

   public ItemStack getThrownItem() {
      return (ItemStack)this.f_19804_.m_135370_(DATA_ITEM);
   }

   public void setThrownItem(ItemStack stack) {
      this.f_19804_.m_135381_(DATA_ITEM, stack);
   }

   @Override
   protected void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      ItemStack item = this.getThrownItem();
      if (!item.m_41619_()) {
         tag.m_128365_("item", item.m_41739_(new CompoundTag()));
      }
   }

   @Override
   protected void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128441_("item")) {
         this.setThrownItem(ItemStack.m_41712_(tag.m_128469_("item")));
      }
   }

   @Override
   public void trailParticles() {
   }

   @Override
   protected void m_5790_(EntityHitResult pResult) {
      super.m_5790_(pResult);
      ItemStack item = this.getThrownItem();
      double damage = this.getDamage();
      Entity target = pResult.m_82443_();
      SpellDamageSource damageSource = ((AbstractSpell)SpellRegistry.THROW_SPELL.get()).getDamageSource(this, this.m_19749_());
      if (DamageSources.applyDamage(target, (float)damage, damageSource) && !item.m_41619_() && this.f_19853_ instanceof ServerLevel var7) {
         ;
      }

      this.m_146870_();
   }

   protected void m_8060_(BlockHitResult result) {
      super.m_8060_(result);
      this.m_146870_();
   }

   @Override
   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.f_19853_, ParticleTypes.f_123797_, x, y, z, 25, 0.1, 0.1, 0.1, 0.5, true);
   }

   @Override
   public float getSpeed() {
      return 1.5F;
   }

   @Override
   public Optional<Supplier<SoundEvent>> getImpactSound() {
      return Optional.of(BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_12515_));
   }
}
