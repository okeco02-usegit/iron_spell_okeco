package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.IBackwardsCompatDefaultNbtItem;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.item.armor.UpgradeType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class UpgradeOrbItem extends Item implements IBackwardsCompatDefaultNbtItem {
   public static final Component TOOLTIP_HEADER = Component.m_237115_("tooltip.irons_spellbooks.upgrade_tooltip").m_130940_(ChatFormatting.GRAY);
   public final ResourceKey<UpgradeOrbType> upgradeOrbTypeResourceKey;

   public UpgradeOrbItem(Properties pProperties, ResourceKey<UpgradeOrbType> upgradeOrbTypeResourceKey) {
      super(pProperties);
      this.upgradeOrbTypeResourceKey = upgradeOrbTypeResourceKey;
   }

   @Deprecated(forRemoval = true)
   public UpgradeOrbItem(UpgradeType type, Properties properties) {
      this(properties, UpgradeOrbTypeRegistry.MANA);
      IronsSpellbooks.LOGGER
         .warn("Upgrade orb {} using legacy upgrade orb format! This is no longer valid, defaulting to mana upgrade", type.getId().toString());
   }

   public Component m_7626_(ItemStack pStack) {
      return super.m_7626_(pStack);
   }

   @Override
   public void setupItem(ItemStack stack) {
      UpgradeOrbTypeData.set(stack, new UpgradeOrbTypeData(this.upgradeOrbTypeResourceKey));
   }
}
