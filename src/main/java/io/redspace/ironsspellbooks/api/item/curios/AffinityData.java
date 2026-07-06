package io.redspace.ironsspellbooks.api.item.curios;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record AffinityData(Map<ResourceLocation, Integer> affinityData) {
   public static final String NBT = "irons_spellbooks:affinity_data";
   public static final String LEGACY_NBT = "ISBEnhance";
   public static final Codec<AffinityData> MULTI_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(Codec.unboundedMap(ResourceLocation.f_135803_, Codec.INT).fieldOf("bonuses").forGetter(AffinityData::affinityData))
         .apply(builder, AffinityData::new)
   );
   public static final Codec<AffinityData> CODEC = MULTI_CODEC;
   public static final Codec<AffinityData> LEGACY_CODEC = CodecHelper.createLegacyCodec(tag -> {
      String spellId = ((StringTag)tag).m_7916_();
      return new AffinityData(spellId);
   });
   public static final AffinityData NONE = new AffinityData(Map.of());

   private AffinityData(String id) {
      this(Map.of(ResourceLocation.parse(id), 1));
   }

   public AffinityData(AbstractSpell spell) {
      this(spell.getSpellId());
   }

   public static AffinityData getAffinityData(ItemStack stack) {
      return CodecHelper.getOrElseWithLegacy(stack, "irons_spellbooks:affinity_data", CODEC, NONE, "ISBEnhance", LEGACY_CODEC);
   }

   public static void setAffinityData(ItemStack stack, AbstractSpell spell) {
      set(stack, new AffinityData(spell));
   }

   public static void setAffinityData(ItemStack stack, AbstractSpell spell, int bonus) {
      set(stack, new AffinityData(Map.of(spell.getSpellResource(), bonus)));
   }

   public static void set(ItemStack stack, AffinityData data) {
      CodecHelper.set(stack, "irons_spellbooks:affinity_data", CODEC, data);
   }

   public static boolean hasAffinityData(ItemStack itemStack) {
      return CodecHelper.hasWithLegacy(itemStack, "irons_spellbooks:affinity_data", "ISBEnhance");
   }

   @Deprecated(forRemoval = true)
   public AbstractSpell getSpell() {
      return this.affinityData.keySet().stream().findFirst().map(SpellRegistry::getSpell).orElse(SpellRegistry.none());
   }

   public int getBonusFor(AbstractSpell spell) {
      return this.affinityData.getOrDefault(spell.getSpellResource(), 0);
   }

   public boolean hasBonusFor(AbstractSpell spell) {
      return this.getBonusFor(spell) != 0;
   }

   public String getNameForItem() {
      return this.getSpell() == SpellRegistry.none()
         ? Component.m_237115_("tooltip.irons_spellbooks.no_affinity").getString()
         : this.getSpell().getSchoolType().getDisplayName().getString();
   }

   public List<MutableComponent> getDescriptionComponent() {
      HashMultimap<Integer, AbstractSpell> byLevel = HashMultimap.create();
      this.affinityData.forEach((key, value) -> byLevel.put(value, SpellRegistry.getSpell(key)));
      return byLevel.keySet()
         .stream()
         .map(
            key -> {
               MutableComponent spellListComponent = Component.m_237113_("").m_130940_(ChatFormatting.YELLOW);
               List<AbstractSpell> spells = byLevel.get(key).stream().toList();

               for (int i = 0; i < spells.size(); i++) {
                  AbstractSpell spell = spells.get(i);
                  spellListComponent.m_7220_(Component.m_237115_(spell.getComponentId()).m_130948_(spell.getSchoolType().getDisplayName().m_7383_()));
                  if (i != spells.size() - 1) {
                     spellListComponent.m_130946_(", ");
                  }
               }

               return key == 1
                  ? Component.m_237110_("tooltip.irons_spellbooks.enhance_spell_level", new Object[]{spellListComponent}).m_130940_(ChatFormatting.YELLOW)
                  : Component.m_237110_("tooltip.irons_spellbooks.enhance_spell_level_plural", new Object[]{key, spellListComponent})
                     .m_130940_(ChatFormatting.YELLOW);
            }
         )
         .toList();
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof AffinityData affinityData && affinityData.affinityData.equals(this.affinityData);
   }

   @Override
   public int hashCode() {
      return this.affinityData.hashCode();
   }
}
