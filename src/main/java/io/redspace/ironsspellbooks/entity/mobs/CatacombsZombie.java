package io.redspace.ironsspellbooks.entity.mobs;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CatacombsZombie extends Zombie {
   public CatacombsZombie(EntityType<? extends Zombie> pEntityType, Level pLevel) {
      super(EntityType.f_20501_, pLevel);
      if (this.f_19796_.m_188501_() < 0.2F) {
         switch (this.f_19796_.m_216332_(1, 4)) {
            case 1:
               this.m_7292_(new MobEffectInstance(MobEffects.f_19609_, Integer.MAX_VALUE));
               break;
            case 2:
               this.m_7292_(new MobEffectInstance(MobEffects.f_19596_, Integer.MAX_VALUE, 1));
               break;
            case 3:
               this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, Integer.MAX_VALUE, 1));
               break;
            case 4:
               this.m_7292_(new MobEffectInstance(MobEffects.f_19600_, Integer.MAX_VALUE));
         }
      }
   }

   protected void m_213945_(RandomSource random, DifficultyInstance pDifficulty) {
      super.m_213945_(random, pDifficulty);
      Item[] leather = new Item[]{Items.f_42463_, Items.f_42462_, Items.f_42408_, Items.f_42407_};
      Item[] chain = new Item[]{Items.f_42467_, Items.f_42466_, Items.f_42465_, Items.f_42464_};
      Item[] iron = new Item[]{Items.f_42471_, Items.f_42470_, Items.f_42469_, Items.f_42468_};
      float power = random.m_188501_();
      ItemStack[] equipment = new ItemStack[4];

      for (int i = 0; i < 4; i++) {
         if (random.m_188501_() > 0.6F) {
            equipment[i] = ItemStack.f_41583_;
         } else {
            float stray = (random.m_188501_() - 0.5F) / 3.0F;
            if (power + stray > 0.85) {
               equipment[i] = new ItemStack(iron[i]);
            } else if (power + stray > 0.45) {
               equipment[i] = new ItemStack(chain[i]);
            } else {
               equipment[i] = new ItemStack(leather[i]);
            }
         }
      }

      this.m_8061_(EquipmentSlot.FEET, equipment[0]);
      this.m_8061_(EquipmentSlot.LEGS, equipment[1]);
      this.m_8061_(EquipmentSlot.CHEST, equipment[2]);
      this.m_8061_(EquipmentSlot.HEAD, equipment[3]);
      if (random.m_188501_() < 0.01F) {
         this.m_8061_(EquipmentSlot.HEAD, new ItemStack(Items.f_42669_));
      }

      this.m_21409_(EquipmentSlot.FEET, 0.0F);
      this.m_21409_(EquipmentSlot.LEGS, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
   }
}
