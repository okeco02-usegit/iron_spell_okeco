package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExtendedFireworkRocket extends FireworkRocketEntity implements AntiMagicSusceptible {
   private final float damage;

   public ExtendedFireworkRocket(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle, float damage) {
      super(pLevel, pStack, pShooter, pX, pY, pZ, pShotAtAngle);
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   public void m_8119_() {
   }

   public void m_6686_(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
      this.m_37080_();
   }

   private void m_37080_() {
      this.f_19853_.m_7605_(this, (byte)17);
      this.m_146852_(GameEvent.f_157812_, this.m_19749_());
      this.m_37087_();
      this.m_146870_();
   }

   private void m_37087_() {
      Vec3 hitPos = this.m_20182_();
      double explosionRadius = 2.0;

      for (LivingEntity livingentity : this.f_19853_
         .m_45976_(
            LivingEntity.class,
            new AABB(hitPos.m_82492_(explosionRadius, explosionRadius, explosionRadius), hitPos.m_82520_(explosionRadius, explosionRadius, explosionRadius))
         )) {
         if (livingentity.m_6084_() && livingentity.m_6087_() && Utils.hasLineOfSight(this.f_19853_, hitPos, livingentity.m_20191_().m_82399_(), true)) {
            DamageSources.applyDamage(
               livingentity, this.getDamage(), ((AbstractSpell)SpellRegistry.FIRECRACKER_SPELL.get()).getDamageSource(this, this.m_19749_())
            );
         }
      }
   }

   @Override
   public void onAntiMagic(MagicData playerMagicData) {
      this.m_146870_();
   }
}
