package io.redspace.ironsspellbooks.datagen;

import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DamageTypeTagGenerator extends TagsProvider<DamageType> {
   public static final TagKey<DamageType> BYPASS_EVASION = create("bypass_evasion");
   public static final TagKey<DamageType> LONG_CAST_IGNORE = create("long_cast_ignore");
   public static final TagKey<DamageType> FIRE_MAGIC = create("fire_magic");
   public static final TagKey<DamageType> ICE_MAGIC = create("ice_magic");
   public static final TagKey<DamageType> LIGHTNING_MAGIC = create("lightning_magic");
   public static final TagKey<DamageType> HOLY_MAGIC = create("holy_magic");
   public static final TagKey<DamageType> ENDER_MAGIC = create("ender_magic");
   public static final TagKey<DamageType> BLOOD_MAGIC = create("blood_magic");
   public static final TagKey<DamageType> EVOCATION_MAGIC = create("evocation_magic");
   public static final TagKey<DamageType> ELDRITCH_MAGIC = create("eldritch_magic");
   public static final TagKey<DamageType> NATURE_MAGIC = create("nature_magic");

   public DamageTypeTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.f_268580_, lookupProvider, "irons_spellbooks", existingFileHelper);
   }

   private static TagKey<DamageType> create(String name) {
      return TagKey.m_203882_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
   }

   protected void m_6577_(@NotNull Provider provider) {
      this.m_206424_(FIRE_MAGIC).m_255204_(ISSDamageTypes.FIRE_MAGIC);
      this.m_206424_(ICE_MAGIC).m_255204_(ISSDamageTypes.ICE_MAGIC);
      this.m_206424_(LIGHTNING_MAGIC).m_255204_(ISSDamageTypes.LIGHTNING_MAGIC);
      this.m_206424_(HOLY_MAGIC).m_255204_(ISSDamageTypes.HOLY_MAGIC);
      this.m_206424_(ENDER_MAGIC).m_255204_(ISSDamageTypes.ENDER_MAGIC);
      this.m_206424_(BLOOD_MAGIC).m_255204_(ISSDamageTypes.BLOOD_MAGIC);
      this.m_206424_(EVOCATION_MAGIC).m_255204_(ISSDamageTypes.EVOCATION_MAGIC);
      this.m_206424_(ELDRITCH_MAGIC).m_255204_(ISSDamageTypes.ELDRITCH_MAGIC);
      this.m_206424_(NATURE_MAGIC).m_255204_(ISSDamageTypes.NATURE_MAGIC);
      this.m_206424_(BYPASS_EVASION)
         .m_211101_(
            new ResourceKey[]{
               DamageTypes.f_268468_,
               DamageTypes.f_268493_,
               DamageTypes.f_268444_,
               DamageTypes.f_268441_,
               DamageTypes.f_268722_,
               DamageTypes.f_268669_,
               DamageTypes.f_286973_,
               DamageTypes.f_268724_,
               DamageTypes.f_268752_,
               DamageTypes.f_268612_,
               ISSDamageTypes.CAULDRON,
               ISSDamageTypes.HEARTSTOP
            }
         );
      this.m_206424_(LONG_CAST_IGNORE)
         .m_211101_(
            new ResourceKey[]{
               DamageTypes.f_268444_,
               DamageTypes.f_268441_,
               DamageTypes.f_268468_,
               DamageTypes.f_268493_,
               ISSDamageTypes.HEARTSTOP,
               DamageTypes.f_268722_,
               DamageTypes.f_268671_
            }
         );
   }
}
