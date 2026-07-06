package io.redspace.ironsspellbooks.api.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SpellData implements Comparable<SpellData> {
   public static final String SPELL_ID = "id";
   public static final String SPELL_LEVEL = "level";
   public static final String SPELL_LOCKED = "locked";
   public static final Codec<SpellData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            ResourceLocation.f_135803_.fieldOf("id").forGetter(data -> data.spell.getSpellResource()),
            Codec.INT.fieldOf("level").forGetter(SpellData::getLevel),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(SpellData::isLocked)
         )
         .apply(builder, SpellData::new)
   );
   public static final SpellData EMPTY = new SpellData(SpellRegistry.none(), 0, false);
   protected final AbstractSpell spell;
   protected final int spellLevel;
   protected final boolean locked;

   private SpellData() throws Exception {
      throw new Exception("Cannot create empty spell slots.");
   }

   public SpellData(AbstractSpell spell, int level, boolean locked) {
      this.spell = Objects.requireNonNull(spell);
      this.spellLevel = level;
      this.locked = locked;
   }

   public SpellData(AbstractSpell spell, int level) {
      this(spell, level, false);
   }

   public SpellData(ResourceLocation spellId, int level, boolean locked) {
      this(SpellRegistry.getSpell(spellId), level, locked);
   }

   public static void writeToBuffer(FriendlyByteBuf buf, SpellData data) {
      buf.m_130085_(data.spell.getSpellResource());
      buf.writeInt(data.spellLevel);
      buf.writeBoolean(data.locked);
   }

   public static SpellData readFromBuffer(FriendlyByteBuf buf) {
      return new SpellData(buf.m_130281_(), buf.readInt(), buf.readBoolean());
   }

   public AbstractSpell getSpell() {
      return this.spell == null ? SpellRegistry.none() : this.spell;
   }

   public int getLevel() {
      return this.spellLevel;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public boolean canRemove() {
      return !this.locked;
   }

   public SpellRarity getRarity() {
      return this.getSpell().getRarity(this.getLevel());
   }

   public Component getDisplayName() {
      return this.getSpell()
         .getDisplayName(MinecraftInstanceHelper.instance.player())
         .m_130946_(" ")
         .m_7220_(Component.m_237115_(((Item)ItemRegistry.SCROLL.get()).m_5524_()));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SpellData other) ? false : this.spell.equals(other.spell) && this.spellLevel == other.spellLevel;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.spell.hashCode() + this.spellLevel;
   }

   public int compareTo(SpellData other) {
      int i = this.spell.getSpellId().compareTo(other.spell.getSpellId());
      if (i == 0) {
         i = Integer.compare(this.spellLevel, other.spellLevel);
      }

      return i;
   }
}
