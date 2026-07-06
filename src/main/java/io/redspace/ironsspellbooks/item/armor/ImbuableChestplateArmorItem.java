package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public abstract class ImbuableChestplateArmorItem extends ExtendedArmorItem implements IPresetSpellContainer {
   public ImbuableChestplateArmorItem(IronsExtendedArmorMaterial pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
      super(pMaterial, pType, pProperties, attributes);
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.m_41720_() instanceof ArmorItem armorItem && armorItem.m_266204_() == Type.CHESTPLATE && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, true);
            ISpellContainer.set(itemStack, spellContainer);
         }
      }
   }
}
