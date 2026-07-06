package io.redspace.ironsspellbooks.capabilities.magic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class SpellContainer implements ISpellContainer {
   public static final String SPELL_DATA = "data";
   public static final String MAX_SLOTS = "maxSpells";
   public static final String MUST_EQUIP = "mustEquip";
   public static final String IMPROVED = "improved";
   public static final String SPELL_WHEEL = "spellWheel";
   public static final String SLOT_INDEX = "index";
   public static final String SPELL_ID = "id";
   public static final String SPELL_LEVEL = "level";
   public static final String SPELL_LOCKED = "locked";
   SpellSlot[] slots;
   int maxSpells = 0;
   int activeSlots = 0;
   boolean spellWheel = false;
   boolean mustEquip = true;
   boolean improved = false;
   public static final Codec<SpellSlot> SPELL_SLOT_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            ResourceLocation.f_135803_.fieldOf("id").forGetter(data -> data.getSpell().getSpellResource()),
            Codec.INT.fieldOf("index").forGetter(SpellSlot::index),
            Codec.INT.fieldOf("level").forGetter(SpellSlot::getLevel),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(SpellSlot::isLocked)
         )
         .apply(builder, (id, index, lvl, lock) -> SpellSlot.of(new SpellData(id, lvl, lock), index))
   );
   public static final Codec<ISpellContainer> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.INT.fieldOf("maxSpells").forGetter(ISpellContainer::getMaxSpellCount),
            Codec.BOOL.fieldOf("spellWheel").forGetter(ISpellContainer::isSpellWheel),
            Codec.BOOL.fieldOf("mustEquip").forGetter(ISpellContainer::mustEquip),
            Codec.BOOL.optionalFieldOf("improved", false).forGetter(ISpellContainer::isImproved),
            Codec.list(SPELL_SLOT_CODEC).fieldOf("data").forGetter(ISpellContainer::getActiveSpells)
         )
         .apply(builder, (count, wheel, equip, improved, spells) -> {
            SpellContainer container = new SpellContainer(count, wheel, equip, improved);
            spells.forEach(slot -> container.slots[slot.index()] = slot);
            container.activeSlots = spells.size();
            return container;
         })
   );
   public static final Codec<ISpellContainer> LEGACY_CODEC = CodecHelper.createLegacyCodec(tag -> {
      CompoundTag nbt = (CompoundTag)tag;
      int maxSpells = nbt.m_128451_("maxSpells");
      boolean mustEquip = nbt.m_128471_("mustEquip");
      boolean spellWheel = nbt.m_128471_("spellWheel");
      SpellSlot[] slots = new SpellSlot[maxSpells];
      boolean improved = nbt.m_128471_("Improved");
      AtomicInteger activeSlots = new AtomicInteger(0);
      ListTag listTagSpells = (ListTag)nbt.m_128423_("data");
      if (listTagSpells != null && !listTagSpells.isEmpty()) {
         listTagSpells.forEach(tagSlot -> {
            CompoundTag t = (CompoundTag)tagSlot;
            String id = t.m_128461_("id");
            int level = t.m_128451_("level");
            boolean locked = t.m_128471_("locked");
            int index = t.m_128451_("index");
            if (index < slots.length) {
               slots[index] = new SpellSlot(new SpellData(SpellRegistry.getSpell(id), level, locked), index);
               activeSlots.incrementAndGet();
            }
         });
      }

      return new SpellContainer(maxSpells, spellWheel, mustEquip, improved, slots);
   });

   @Override
   public boolean equals(Object obj) {
      return obj == this
         || obj instanceof SpellContainer o
            && Arrays.equals(o.slots, this.slots)
            && this.maxSpells == o.maxSpells
            && this.activeSlots == o.activeSlots
            && this.spellWheel == o.spellWheel
            && this.mustEquip == o.mustEquip
            && this.improved == o.improved;
   }

   @Override
   public int hashCode() {
      int hash = Arrays.hashCode(this.slots);
      hash = (hash * 31 + this.maxSpells) * 31 + this.activeSlots;
      hash *= 1000;
      hash += this.spellWheel ? 100 : 0;
      hash += this.mustEquip ? 10 : 0;
      return hash + (this.improved ? 1 : 0);
   }

   public SpellContainer() {
   }

   public SpellContainer(int maxSpells, boolean spellWheel, boolean mustEquip) {
      this(maxSpells, spellWheel, mustEquip, false);
   }

   public SpellContainer(int maxSpells, boolean spellWheel, boolean mustEquip, boolean improved) {
      this.maxSpells = maxSpells;
      this.slots = new SpellSlot[this.maxSpells];
      this.spellWheel = spellWheel;
      this.mustEquip = mustEquip;
      this.improved = improved;
   }

   public SpellContainer(int maxSpells, boolean spellWheel, boolean mustEquip, boolean improved, SpellSlot[] slots) {
      this.maxSpells = maxSpells;
      this.slots = slots;
      this.spellWheel = spellWheel;
      this.mustEquip = mustEquip;
      this.improved = improved;
      this.activeSlots = Arrays.stream(slots).filter(Objects::nonNull).toList().size();
   }

   @Override
   public int getMaxSpellCount() {
      return this.maxSpells;
   }

   @Override
   public int getActiveSpellCount() {
      return this.activeSlots;
   }

   @Override
   public boolean isEmpty() {
      return this.activeSlots == 0;
   }

   @Override
   public SpellSlot[] getAllSpells() {
      SpellSlot[] result = new SpellSlot[this.maxSpells];
      if (this.maxSpells > 0) {
         System.arraycopy(this.slots, 0, result, 0, this.slots.length);
      }

      return result;
   }

   @NotNull
   @Override
   public List<SpellSlot> getActiveSpells() {
      return Arrays.stream(this.slots).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @Override
   public int getNextAvailableIndex() {
      return ArrayUtils.indexOf(this.slots, null);
   }

   @Override
   public boolean mustEquip() {
      return this.mustEquip;
   }

   @Override
   public boolean isImproved() {
      return this.improved;
   }

   @Override
   public boolean isSpellWheel() {
      return this.spellWheel;
   }

   @NotNull
   @Override
   public SpellData getSpellAtIndex(int index) {
      if (index >= 0 && index < this.maxSpells) {
         SpellSlot result = this.slots[index];
         if (result != null) {
            return this.slots[index].spellData();
         }
      }

      return SpellData.EMPTY;
   }

   @Override
   public int getIndexForSpell(AbstractSpell spell) {
      for (int i = 0; i < this.maxSpells; i++) {
         SpellSlot s = this.slots[i];
         if (s != null && s.getSpell().equals(spell)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public ISpellContainerMutable mutableCopy() {
      return new SpellContainer.Mutable(this);
   }

   @Deprecated(forRemoval = true)
   @Override
   public boolean addSpell(AbstractSpell spell, int level, boolean locked, ItemStack itemStack) {
      int index = ArrayUtils.indexOf(this.slots, null);
      if (index > -1
         && index < this.maxSpells
         && this.slots[index] == null
         && Arrays.stream(this.slots).noneMatch(s -> s != null && s.getSpell().equals(spell))) {
         this.slots[index] = new SpellSlot(new SpellData(spell, level, locked), index);
         this.activeSlots++;
         this.save(itemStack);
         return true;
      } else {
         return false;
      }
   }

   @Deprecated(forRemoval = true)
   @Override
   public void save(ItemStack stack) {
      if (stack != null) {
         ISpellContainer.set(stack, this);
      }
   }

   public class Mutable extends SpellContainer implements ISpellContainerMutable {
      public Mutable(SpellContainer spellContainer) {
         this.maxSpells = spellContainer.maxSpells;
         this.activeSlots = spellContainer.activeSlots;
         this.spellWheel = spellContainer.spellWheel;
         this.mustEquip = spellContainer.mustEquip;
         this.improved = spellContainer.improved;
         this.slots = Arrays.copyOf(spellContainer.slots, spellContainer.slots.length);
      }

      @Override
      public void setMaxSpellCount(int maxSpells) {
         this.maxSpells = maxSpells;
         this.slots = Arrays.copyOf(this.slots, maxSpells);
      }

      @Override
      public void setImproved(boolean improved) {
         this.improved = improved;
      }

      @Override
      public boolean addSpellAtIndex(AbstractSpell spell, int level, int index, boolean locked) {
         if (index > -1
            && index < this.maxSpells
            && this.slots[index] == null
            && Arrays.stream(this.slots).noneMatch(s -> s != null && s.getSpell().equals(spell))) {
            this.slots[index] = SpellSlot.of(new SpellData(spell, level, locked), index);
            this.activeSlots++;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean addSpell(AbstractSpell spell, int level, boolean locked) {
         return this.addSpellAtIndex(spell, level, this.getNextAvailableIndex(), locked);
      }

      @Override
      public boolean removeSpellAtIndex(int index) {
         if (index > -1 && index < this.maxSpells && this.slots[index] != null) {
            this.slots[index] = null;
            this.activeSlots--;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean removeSpell(AbstractSpell spell) {
         if (spell == null) {
            return false;
         }

         int i = 0;
         if (i < this.maxSpells) {
            SpellSlot spellData = this.slots[i];
            if (spellData != null && spell.equals(spellData.getSpell())) {
               return this.removeSpellAtIndex(i);
            }
         }

         return false;
      }

      @Override
      public ISpellContainer toImmutable() {
         return new SpellContainer(this.maxSpells, this.spellWheel, this.mustEquip, this.improved, this.slots);
      }
   }
}
