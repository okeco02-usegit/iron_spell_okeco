package io.redspace.ironsspellbooks.api.registry;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class SchoolRegistry {
   public static final ResourceKey<Registry<SchoolType>> SCHOOL_REGISTRY_KEY = ResourceKey.m_135788_(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "schools")
   );
   private static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, "irons_spellbooks");
   public static final Supplier<IForgeRegistry<SchoolType>> REGISTRY = SCHOOLS.makeRegistry(() -> new RegistryBuilder().disableSaving().disableOverrides());
   public static final ResourceLocation FIRE_RESOURCE = IronsSpellbooks.id("fire");
   public static final ResourceLocation ICE_RESOURCE = IronsSpellbooks.id("ice");
   public static final ResourceLocation LIGHTNING_RESOURCE = IronsSpellbooks.id("lightning");
   public static final ResourceLocation HOLY_RESOURCE = IronsSpellbooks.id("holy");
   public static final ResourceLocation ENDER_RESOURCE = IronsSpellbooks.id("ender");
   public static final ResourceLocation BLOOD_RESOURCE = IronsSpellbooks.id("blood");
   public static final ResourceLocation EVOCATION_RESOURCE = IronsSpellbooks.id("evocation");
   public static final ResourceLocation NATURE_RESOURCE = IronsSpellbooks.id("nature");
   public static final ResourceLocation ELDRITCH_RESOURCE = IronsSpellbooks.id("eldritch");
   public static final RegistryObject<SchoolType> FIRE = registerSchool(
      new SchoolType(
         FIRE_RESOURCE,
         ModTags.FIRE_FOCUS,
         Component.m_237115_("school.irons_spellbooks.fire").m_130940_(ChatFormatting.GOLD),
         AttributeRegistry.FIRE_SPELL_POWER,
         AttributeRegistry.FIRE_MAGIC_RESIST,
         SoundRegistry.FIRE_CAST,
         ISSDamageTypes.FIRE_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> ICE = registerSchool(
      new SchoolType(
         ICE_RESOURCE,
         ModTags.ICE_FOCUS,
         Component.m_237115_("school.irons_spellbooks.ice").m_130948_(Style.f_131099_.m_178520_(13695487)),
         AttributeRegistry.ICE_SPELL_POWER,
         AttributeRegistry.ICE_MAGIC_RESIST,
         SoundRegistry.ICE_CAST,
         ISSDamageTypes.ICE_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> LIGHTNING = registerSchool(
      new SchoolType(
         LIGHTNING_RESOURCE,
         ModTags.LIGHTNING_FOCUS,
         Component.m_237115_("school.irons_spellbooks.lightning").m_130940_(ChatFormatting.AQUA),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         AttributeRegistry.LIGHTNING_MAGIC_RESIST,
         SoundRegistry.LIGHTNING_CAST,
         ISSDamageTypes.LIGHTNING_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> HOLY = registerSchool(
      new SchoolType(
         HOLY_RESOURCE,
         ModTags.HOLY_FOCUS,
         Component.m_237115_("school.irons_spellbooks.holy").m_130948_(Style.f_131099_.m_178520_(16775380)),
         AttributeRegistry.HOLY_SPELL_POWER,
         AttributeRegistry.HOLY_MAGIC_RESIST,
         SoundRegistry.HOLY_CAST,
         ISSDamageTypes.HOLY_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> ENDER = registerSchool(
      new SchoolType(
         ENDER_RESOURCE,
         ModTags.ENDER_FOCUS,
         Component.m_237115_("school.irons_spellbooks.ender").m_130940_(ChatFormatting.LIGHT_PURPLE),
         AttributeRegistry.ENDER_SPELL_POWER,
         AttributeRegistry.ENDER_MAGIC_RESIST,
         SoundRegistry.ENDER_CAST,
         ISSDamageTypes.ENDER_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> BLOOD = registerSchool(
      new SchoolType(
         BLOOD_RESOURCE,
         ModTags.BLOOD_FOCUS,
         Component.m_237115_("school.irons_spellbooks.blood").m_130940_(ChatFormatting.DARK_RED),
         AttributeRegistry.BLOOD_SPELL_POWER,
         AttributeRegistry.BLOOD_MAGIC_RESIST,
         SoundRegistry.BLOOD_CAST,
         ISSDamageTypes.BLOOD_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> EVOCATION = registerSchool(
      new SchoolType(
         EVOCATION_RESOURCE,
         ModTags.EVOCATION_FOCUS,
         Component.m_237115_("school.irons_spellbooks.evocation").m_130940_(ChatFormatting.WHITE),
         AttributeRegistry.EVOCATION_SPELL_POWER,
         AttributeRegistry.EVOCATION_MAGIC_RESIST,
         SoundRegistry.EVOCATION_CAST,
         ISSDamageTypes.EVOCATION_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> NATURE = registerSchool(
      new SchoolType(
         NATURE_RESOURCE,
         ModTags.NATURE_FOCUS,
         Component.m_237115_("school.irons_spellbooks.nature").m_130940_(ChatFormatting.GREEN),
         AttributeRegistry.NATURE_SPELL_POWER,
         AttributeRegistry.NATURE_MAGIC_RESIST,
         SoundRegistry.NATURE_CAST,
         ISSDamageTypes.NATURE_MAGIC
      )
   );
   public static final RegistryObject<SchoolType> ELDRITCH = registerSchool(
      new SchoolType(
         ELDRITCH_RESOURCE,
         ModTags.ELDRITCH_FOCUS,
         Component.m_237115_("school.irons_spellbooks.eldritch").m_130948_(Style.f_131099_.m_178520_(1016732)),
         AttributeRegistry.ELDRITCH_SPELL_POWER,
         AttributeRegistry.ELDRITCH_MAGIC_RESIST,
         SoundRegistry.EVOCATION_CAST,
         ISSDamageTypes.ELDRITCH_MAGIC,
         true,
         false
      )
   );

   public static void register(IEventBus eventBus) {
      SCHOOLS.register(eventBus);
   }

   private static RegistryObject<SchoolType> registerSchool(SchoolType schoolType) {
      return SCHOOLS.register(schoolType.getId().m_135815_(), () -> schoolType);
   }

   public static SchoolType getSchool(ResourceLocation resourceLocation) {
      return (SchoolType)REGISTRY.get().getValue(resourceLocation);
   }

   @Nullable
   public static SchoolType getSchoolFromFocus(ItemStack focusStack) {
      for (SchoolType school : REGISTRY.get()) {
         if (school.isFocus(focusStack)) {
            return school;
         }
      }

      return null;
   }

   public static List<SchoolType> getSchoolsFromFocus(ItemStack focusStack) {
      return REGISTRY.get().getValues().stream().filter(school -> school.isFocus(focusStack)).toList();
   }
}
