package io.redspace.ironsspellbooks.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;

public class SpellFilter {
   SchoolType schoolType = null;
   List<AbstractSpell> spells = new ArrayList<>();
   final boolean force;
   static final Map<SchoolType, List<AbstractSpell>> SPELLS_FOR_SCHOOL = new HashMap<>();
   static final Map<SchoolType, List<AbstractSpell>> SPELLS_FOR_SCHOOL_FORCED = new HashMap<>();

   public SpellFilter(boolean force, SchoolType schoolType) {
      this.force = force;
      this.schoolType = schoolType;
   }

   public SpellFilter(SchoolType type) {
      this(false, type);
   }

   public SpellFilter(boolean force, List<AbstractSpell> spells) {
      this.force = force;
      this.spells = spells;
   }

   public SpellFilter(List<AbstractSpell> spells) {
      this(false, spells);
   }

   public SpellFilter() {
      this.force = false;
   }

   private boolean isSpellAllowed(AbstractSpell spell) {
      return spell.isEnabled() && (this.force || spell.allowLooting());
   }

   public List<AbstractSpell> getApplicableSpells() {
      if (!this.spells.isEmpty()) {
         return this.spells.stream().filter(AbstractSpell::isEnabled).toList();
      } else if (this.schoolType != null) {
         return this.force
            ? SPELLS_FOR_SCHOOL_FORCED.computeIfAbsent(
               this.schoolType, school -> SpellRegistry.getSpellsForSchool(school).stream().filter(AbstractSpell::isEnabled).toList()
            )
            : SPELLS_FOR_SCHOOL.computeIfAbsent(
               this.schoolType, school -> SpellRegistry.getSpellsForSchool(school).stream().filter(this::isSpellAllowed).toList()
            );
      } else {
         return SpellRegistry.getEnabledSpells().stream().filter(this::isSpellAllowed).toList();
      }
   }

   public AbstractSpell getRandomSpell(RandomSource random, Predicate<AbstractSpell> filter) {
      List<AbstractSpell> spells = this.getApplicableSpells().stream().filter(filter).toList();
      return spells.isEmpty() ? SpellRegistry.none() : spells.get(random.m_188503_(spells.size()));
   }

   public AbstractSpell getRandomSpell(RandomSource randomSource) {
      return this.getRandomSpell(randomSource, spell -> spell.isEnabled() && spell != SpellRegistry.none() && spell.allowLooting());
   }

   private static SpellFilter deserializeActualObject(JsonObject json) {
      boolean force = GsonHelper.m_13900_(json, "force") && GsonHelper.m_13912_(json, "force");
      if (GsonHelper.m_13900_(json, "school")) {
         String schoolType = GsonHelper.m_13906_(json, "school");
         return new SpellFilter(force, SchoolRegistry.getSchool(ResourceLocation.parse(schoolType)));
      }

      if (GsonHelper.m_13885_(json, "spells")) {
         JsonArray spellsFromJson = GsonHelper.m_13933_(json, "spells");
         List<AbstractSpell> applicableSpellList = new ArrayList<>();

         for (JsonElement element : spellsFromJson) {
            String spellId = element.getAsString();
            AbstractSpell spell = SpellRegistry.getSpell(spellId);
            if (spell != SpellRegistry.none()) {
               applicableSpellList.add(spell);
            }
         }

         return new SpellFilter(force, applicableSpellList);
      } else {
         return new SpellFilter();
      }
   }

   public static SpellFilter deserializeSpellFilter(JsonObject json) {
      return json.has("spell_filter") ? deserializeActualObject(json.getAsJsonObject("spell_filter")) : deserializeActualObject(json);
   }

   public void serialize(JsonObject json) {
      JsonObject filter = new JsonObject();
      if (this.schoolType != null) {
         filter.addProperty("school", this.schoolType.getId().toString());
      } else if (!this.spells.isEmpty()) {
         JsonArray elements = new JsonArray();

         for (AbstractSpell spell : this.spells) {
            elements.add(spell.getSpellId());
         }

         filter.add("spells", elements);
      }

      if (!filter.isEmpty()) {
         json.add("spell_filter", filter);
      }
   }
}
