package io.redspace.ironsspellbooks.item.armor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;

@Deprecated(forRemoval = true)
public interface UpgradeType {
   Map<ResourceLocation, UpgradeType> UPGRADE_REGISTRY = new HashMap<>();

   static void registerUpgrade(UpgradeType upgrade) {
      UPGRADE_REGISTRY.put(upgrade.getId(), upgrade);
   }

   static Optional<UpgradeType> getUpgrade(ResourceLocation key) {
      UpgradeType upgradeType = UPGRADE_REGISTRY.get(key);
      return upgradeType == null ? Optional.empty() : Optional.of(upgradeType);
   }

   Holder<Attribute> getAttribute();

   Operation getOperation();

   float getAmountPerUpgrade();

   ResourceLocation getId();

   Optional<Holder<Item>> getContainerItem();
}
