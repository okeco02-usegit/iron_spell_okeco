package io.redspace.ironsspellbooks.datagen;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.MOD)
public class DataGenerators {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = event.getGenerator().getPackOutput();
      ExistingFileHelper helper = event.getExistingFileHelper();
      CompletableFuture<Provider> provider = event.getLookupProvider();
      DatapackBuiltinEntriesProvider datapackProvider = new RegistryDataGenerator(output, provider);
      CompletableFuture<Provider> lookupProvider = datapackProvider.getRegistryProvider();
      generator.addProvider(event.includeServer(), datapackProvider);
      generator.addProvider(event.includeServer(), new DamageTypeTagGenerator(output, lookupProvider, helper));
      generator.addProvider(
         true,
         new PackMetadataGenerator(output)
            .m_247300_(
               PackMetadataSection.f_243696_,
               new PackMetadataSection(
                  Component.m_237113_("Resources for Iron's Spells N Spellbooks"),
                  DetectedVersion.f_132476_.m_264084_(PackType.CLIENT_RESOURCES),
                  Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.f_132476_::m_264084_))
               )
            )
      );
   }
}
