package io.redspace.ironsspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class MagehunterVindicator extends Vindicator {
   public MagehunterVindicator(EntityType<? extends Vindicator> pEntityType, Level pLevel) {
      super(EntityType.f_20493_, pLevel);
   }

   protected void m_213945_(RandomSource random, DifficultyInstance pDifficulty) {
      super.m_213945_(random, pDifficulty);
      ItemStack magehunter = new ItemStack((ItemLike)ItemRegistry.MAGEHUNTER.get());
      magehunter.m_41663_(Enchantments.f_44977_, 5);
      this.m_8061_(EquipmentSlot.MAINHAND, magehunter);
   }
}
