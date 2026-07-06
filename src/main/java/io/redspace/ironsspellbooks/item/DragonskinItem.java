package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DragonskinItem extends Item {
   public DragonskinItem() {
      super(ItemPropertiesHelper.material());
   }

   public boolean hasCustomEntity(ItemStack stack) {
      return true;
   }

   public Entity createEntity(Level world, Entity entity, ItemStack itemstack) {
      if (!world.m_45976_(EnderDragon.class, entity.m_20191_().m_82400_(5.0)).isEmpty()) {
         entity.m_20242_(true);
         if (!world.f_46443_) {
            MagicManager.spawnParticles(
               world, ParticleHelper.UNSTABLE_ENDER, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), 100, 0.0, 0.0, 0.0, 2.0, true
            );
         }

         entity.m_20334_(0.0, -0.01F, 0.0);
         entity.m_146915_(true);
      }

      return null;
   }
}
