package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CursedDollSpellbookItem extends SpellBook {
   public CursedDollSpellbookItem() {
      super(10);
      this.withSpellbookAttributes(
         new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.1, Operation.MULTIPLY_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.1, Operation.MULTIPLY_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADDITION)
      );
   }

   @Override
   public void m_7373_(@NotNull ItemStack itemStack, Level context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.m_7373_(itemStack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(itemStack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.irons_spellbooks.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.set(
            itemStack,
            new AffinityData(
               Map.of(
                  ((AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get()).getSpellResource(),
                  1,
                  ((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()).getSpellResource(),
                  1
               )
            )
         );
      }
   }
}
