package io.redspace.ironsspellbooks.util;

import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class UpgradeUtils {
   public static final Map<EquipmentSlot, UUID> UPGRADE_UUIDS_BY_SLOT = Map.of(
      EquipmentSlot.HEAD,
      UUID.fromString("f6c19678-1c70-4d41-ad19-cd84d8610242"),
      EquipmentSlot.CHEST,
      UUID.fromString("8d02c916-b0eb-4d17-8414-329b4bd38ae7"),
      EquipmentSlot.LEGS,
      UUID.fromString("3739c748-98d4-4a2d-9c25-3b4dec74823d"),
      EquipmentSlot.FEET,
      UUID.fromString("41cede88-7881-42dd-aac3-d6ab4b56b1f2"),
      EquipmentSlot.MAINHAND,
      UUID.fromString("c3865ad7-1f35-46d4-8b4b-a6b934a1a896"),
      EquipmentSlot.OFFHAND,
      UUID.fromString("c508430e-7497-42a9-9a9c-1a324dccca54")
   );

   public static String getRelevantEquipmentSlot(ItemStack itemStack) {
      if (itemStack.m_41720_() instanceof var curioItem) {
         Set<String> tags = CuriosApi.getCuriosHelper().getCurioTags((Item)curioItem);
         Optional<String> slot = tags.stream().findFirst();
         if (slot.isPresent()) {
            return slot.get();
         }
      } else if (itemStack.m_41720_() instanceof ArmorItem armorItem) {
         return armorItem.m_40402_().m_20751_();
      }

      return EquipmentSlot.MAINHAND.m_20751_();
   }

   public static UUID UUIDForSlot(EquipmentSlot slot) {
      return UPGRADE_UUIDS_BY_SLOT.get(slot);
   }

   public static void handleAttributeEvent(
      Multimap<Attribute, AttributeModifier> modifiers,
      UpgradeData upgradeData,
      BiConsumer<Attribute, AttributeModifier> addCallback,
      BiConsumer<Attribute, AttributeModifier> removeCallback,
      Optional<UUID> uuidOverride
   ) {
      Map<Holder<UpgradeOrbType>, Integer> upgrades = upgradeData.upgrades();

      for (Entry<Holder<UpgradeOrbType>, Integer> entry : upgrades.entrySet()) {
         UpgradeOrbType upgradeType = (UpgradeOrbType)entry.getKey().get();
         int count = entry.getValue();
         double baseAmount = collectAndRemovePreexistingAttribute(
            modifiers, (Attribute)upgradeType.attribute().m_203334_(), upgradeType.operation(), removeCallback
         );
         UUID uuid;
         if (uuidOverride.isPresent()) {
            uuid = uuidOverride.get();
         } else {
            try {
               uuid = UUIDForSlot(EquipmentSlot.m_20747_(upgradeData.getUpgradedSlot()));
            } catch (IllegalArgumentException e) {
               IronsSpellbooks.LOGGER.warn("Invalid UpgradeData NBT: {}", e.toString());
               return;
            }
         }

         addCallback.accept(
            (Attribute)upgradeType.attribute().get(),
            new AttributeModifier(uuid, "upgrade", baseAmount + upgradeType.amount() * count, upgradeType.operation())
         );
      }
   }

   public static double collectAndRemovePreexistingAttribute(
      Multimap<Attribute, AttributeModifier> modifiers, Attribute key, Operation operationToMatch, BiConsumer<Attribute, AttributeModifier> removeCallback
   ) {
      if (modifiers.containsKey(key)) {
         for (AttributeModifier modifier : modifiers.get(key)) {
            if (modifier.m_22217_().equals(operationToMatch)) {
               removeCallback.accept(key, modifier);
               return modifier.m_22218_();
            }
         }
      }

      return 0.0;
   }
}
