package io.redspace.ironsspellbooks.registries;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class FeatureRegistry {
   private static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registries.f_256911_, "irons_spellbooks");
   private static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.f_256988_, "irons_spellbooks");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ARCANE_DEBRIS_FEATURE = configuredFeatureResourceKey("ore_arcane_debris");
   public static final ResourceKey<PlacedFeature> ARCANE_DEBRIS_PLACEMENT = placedFeatureResourceKey("ore_arcane_debris");
   public static final ResourceKey<BiomeModifier> ADD_ARCANE_DEBRIS_ORE = biomeModifierResourceKey("add_arcane_debris_ore");

   public static void register(IEventBus eventBus) {
      CONFIGURED_FEATURES.register(eventBus);
      PLACED_FEATURES.register(eventBus);
   }

   public static void bootstrapConfiguredFeature(BootstapContext<ConfiguredFeature<?, ?>> context) {
      RuleTest ruleTestArcaneDebris = new TagMatchTest(BlockTags.f_144267_);
      List<TargetBlockState> arcaneDebrisList = List.of(OreConfiguration.m_161021_(ruleTestArcaneDebris, ((Block)BlockRegistry.ARCANE_DEBRIS.get()).m_49966_()));
      FeatureUtils.m_254977_(context, ARCANE_DEBRIS_FEATURE, Feature.f_65731_, new OreConfiguration(arcaneDebrisList, 3, 1.0F));
   }

   public static void bootstrapPlacedFeature(BootstapContext<PlacedFeature> context) {
      HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.m_255420_(CONFIGURED_FEATURES.getRegistryKey());
      Holder<ConfiguredFeature<?, ?>> holderArcaneDebris = holdergetter.m_255043_(ARCANE_DEBRIS_FEATURE);
      List<PlacementModifier> list = List.of(
         CountPlacement.m_191628_(7),
         InSquarePlacement.m_191715_(),
         HeightRangePlacement.m_191680_(VerticalAnchor.m_158922_(-63), VerticalAnchor.m_158922_(-38)),
         BiomeFilter.m_191561_()
      );
      PlacementUtils.m_254943_(context, ARCANE_DEBRIS_PLACEMENT, holderArcaneDebris, list);
   }

   public static void bootstrapBiomeModifier(BootstapContext<BiomeModifier> context) {
      HolderGetter<Biome> biomes = context.m_255420_(ForgeRegistries.BIOMES.getRegistryKey());
      HolderGetter<PlacedFeature> features = context.m_255420_(PLACED_FEATURES.getRegistryKey());
      context.m_255272_(
         ADD_ARCANE_DEBRIS_ORE,
         new AddFeaturesBiomeModifier(tag(biomes, BiomeTags.f_215817_), feature(features, ARCANE_DEBRIS_PLACEMENT), Decoration.UNDERGROUND_ORES)
      );
   }

   private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(String name) {
      return ResourceKey.m_135785_(CONFIGURED_FEATURES.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
   }

   private static ResourceKey<PlacedFeature> placedFeatureResourceKey(String name) {
      return ResourceKey.m_135785_(PLACED_FEATURES.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
   }

   private static ResourceKey<BiomeModifier> biomeModifierResourceKey(String name) {
      return ResourceKey.m_135785_(Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
   }

   private static HolderSet<Biome> tag(HolderGetter<Biome> holderGetter, TagKey<Biome> key) {
      return holderGetter.m_254956_(key);
   }

   private static HolderSet<PlacedFeature> feature(HolderGetter<PlacedFeature> holderGetter, ResourceKey<PlacedFeature> feature) {
      return HolderSet.m_205809_(new Holder[]{holderGetter.m_255043_(feature)});
   }
}
