package io.redspace.ironsspellbooks.api.item.weapons;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class MagicSwordItem extends ExtendedSwordItem implements IPresetSpellContainer {
   List<SpellData> spellData = null;
   SpellDataRegistryHolder[] spellDataRegistryHolders;

   public <T extends Tier & IronsWeaponTier> MagicSwordItem(T pTier, Properties pProperties, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(pTier, pProperties);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   @Deprecated(forRemoval = true)
   public MagicSwordItem(
      Tier tier,
      double attackDamage,
      double attackSpeed,
      SpellDataRegistryHolder[] spellDataRegistryHolders,
      Map<Attribute, AttributeModifier> additionalAttributes,
      Properties properties
   ) {
      super(tier, attackDamage, attackSpeed, additionalAttributes, properties);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   public List<SpellData> getSpells() {
      if (this.spellData == null) {
         this.spellData = Arrays.stream(this.spellDataRegistryHolders).map(SpellDataRegistryHolder::getSpellData).toList();
         this.spellDataRegistryHolders = null;
      }

      return this.spellData;
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            List<SpellData> spells = this.getSpells();
            ISpellContainerMutable spellContainer = ISpellContainer.create(spells.size(), true, false).mutableCopy();
            spells.forEach(spellData -> spellContainer.addSpell(spellData.getSpell(), spellData.getLevel(), true));
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
         }
      }
   }
}
