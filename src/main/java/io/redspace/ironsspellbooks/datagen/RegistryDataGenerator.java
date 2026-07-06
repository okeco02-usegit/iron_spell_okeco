package io.redspace.ironsspellbooks.datagen;

import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.FeatureRegistry;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class RegistryDataGenerator extends DatapackBuiltinEntriesProvider {
   private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .m_254916_(Keys.BIOME_MODIFIERS, FeatureRegistry::bootstrapBiomeModifier)
      .m_254916_(Registries.f_256911_, FeatureRegistry::bootstrapConfiguredFeature)
      .m_254916_(Registries.f_256988_, FeatureRegistry::bootstrapPlacedFeature)
      .m_254916_(Registries.f_268580_, ISSDamageTypes::bootstrap)
      .m_254916_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, UpgradeOrbTypeRegistry::bootstrap);

   public RegistryDataGenerator(PackOutput output, CompletableFuture<Provider> provider) {
      super(output, provider, BUILDER, Set.of("minecraft", "irons_spellbooks"));
   }
}
