package io.redspace.ironsspellbooks.api.entity;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import net.minecraft.world.entity.projectile.Projectile;

public interface NoopMagicEntity extends IMagicEntity {
   @Override
   default void setSyncedSpellData(SyncedSpellData syncedSpellData) {
   }

   @Override
   default boolean isCasting() {
      return false;
   }

   @Override
   default void initiateCastSpell(AbstractSpell spell, int spellLevel) {
   }

   @Override
   default void cancelCast() {
   }

   @Override
   default void castComplete() {
   }

   @Override
   default void notifyDangerousProjectile(Projectile projectile) {
   }

   @Override
   default boolean setTeleportLocationBehindTarget(int distance) {
      return false;
   }

   @Override
   default void setBurningDashDirectionData() {
   }

   @Override
   default boolean isDrinkingPotion() {
      return false;
   }

   @Override
   default boolean getHasUsedSingleAttack() {
      return false;
   }

   @Override
   default void setHasUsedSingleAttack(boolean bool) {
   }

   @Override
   default void startDrinkingPotion() {
   }
}
