package io.redspace.ironsspellbooks.api.spells;

import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.capabilities.magic.SpellContainer;
import java.util.List;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public interface ISpellContainer {
   String NBT = "irons_spellbooks:spell_container";
   String LEGACY_NBT = "ISB_Spells";

   @NotNull
   SpellSlot[] getAllSpells();

   @NotNull
   List<SpellSlot> getActiveSpells();

   int getMaxSpellCount();

   int getActiveSpellCount();

   int getNextAvailableIndex();

   boolean mustEquip();

   boolean isImproved();

   boolean isSpellWheel();

   @NotNull
   SpellData getSpellAtIndex(int var1);

   int getIndexForSpell(AbstractSpell var1);

   boolean isEmpty();

   ISpellContainerMutable mutableCopy();

   static boolean isSpellContainer(ItemStack itemStack) {
      return itemStack != null && !itemStack.m_41619_() && CodecHelper.hasWithLegacy(itemStack, "irons_spellbooks:spell_container", "ISB_Spells");
   }

   static ISpellContainer create(int maxSpells, boolean addsToSpellWheel, boolean mustBeEquipped) {
      return new SpellContainer(maxSpells, addsToSpellWheel, mustBeEquipped);
   }

   static ISpellContainer createScrollContainer(AbstractSpell spell, int spellLevel, ItemStack itemStack) {
      ISpellContainerMutable spellContainer = create(1, false, false).mutableCopy();
      spellContainer.addSpellAtIndex(spell, spellLevel, 0, true);
      ISpellContainer i = spellContainer.toImmutable();
      set(itemStack, i);
      return i;
   }

   static ISpellContainer createImbuedContainer(AbstractSpell spell, int spellLevel, ItemStack itemStack) {
      ISpellContainerMutable spellContainer = create(1, true, itemStack.m_41720_() instanceof ArmorItem || itemStack.m_41720_() instanceof ICurioItem)
         .mutableCopy();
      spellContainer.addSpellAtIndex(spell, spellLevel, 0, true);
      ISpellContainer i = spellContainer.toImmutable();
      set(itemStack, i);
      return i;
   }

   static ISpellContainer get(ItemStack itemStack) {
      return CodecHelper.getOrElseWithLegacy(
         itemStack, "irons_spellbooks:spell_container", SpellContainer.CODEC, null, "ISB_Spells", SpellContainer.LEGACY_CODEC
      );
   }

   static ISpellContainer getOrCreate(ItemStack itemStack) {
      return isSpellContainer(itemStack) ? get(itemStack) : new SpellContainer(1, true, false);
   }

   static void set(ItemStack stack, ISpellContainer container) {
      CodecHelper.set(stack, "irons_spellbooks:spell_container", SpellContainer.CODEC, container);
   }

   static void remove(ItemStack stack) {
      stack.m_41749_("irons_spellbooks:spell_container");
   }

   @Deprecated(forRemoval = true)
   boolean addSpell(AbstractSpell var1, int var2, boolean var3, ItemStack var4);

   @Deprecated(forRemoval = true)
   void save(ItemStack var1);
}
