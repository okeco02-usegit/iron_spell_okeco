package io.redspace.ironsspellbooks;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.CommandArgumentRegistry;
import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.FeatureRegistry;
import io.redspace.ironsspellbooks.registries.FluidRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.LootRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.PoiTypeRegistry;
import io.redspace.ironsspellbooks.registries.PotionRegistry;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.registries.StructureElementRegistry;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import io.redspace.ironsspellbooks.setup.ModSetup;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod("irons_spellbooks")
public class IronsSpellbooks {
   public static final String MODID = "irons_spellbooks";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static MagicManager MAGIC_MANAGER;
   public static MinecraftServer MCS;
   public static ServerLevel OVERWORLD;

   public IronsSpellbooks() {
      ModSetup.setup();
      MAGIC_MANAGER = new MagicManager();
      MagicHelper.MAGIC_MANAGER = MAGIC_MANAGER;
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      modEventBus.addListener(ModSetup::init);
      modEventBus.addListener(this::enqueueIMC);
      modEventBus.addListener(this::processIMC);
      modEventBus.addListener(UpgradeOrbTypeRegistry::registerDatapackRegistries);
      SchoolRegistry.register(modEventBus);
      SpellRegistry.register(modEventBus);
      ItemRegistry.register(modEventBus);
      AttributeRegistry.register(modEventBus);
      BlockRegistry.register(modEventBus);
      MenuRegistry.register(modEventBus);
      EntityRegistry.register(modEventBus);
      LootRegistry.register(modEventBus);
      MobEffectRegistry.register(modEventBus);
      ParticleRegistry.register(modEventBus);
      SoundRegistry.register(modEventBus);
      FeatureRegistry.register(modEventBus);
      PotionRegistry.register(modEventBus);
      CommandArgumentRegistry.register(modEventBus);
      StructureProcessorRegistry.register(modEventBus);
      StructureElementRegistry.register(modEventBus);
      CreativeTabRegistry.register(modEventBus);
      PoiTypeRegistry.register(modEventBus);
      FluidRegistry.register(modEventBus);
      RecipeRegistry.register(modEventBus);
      modEventBus.addListener(this::addPackFinders);
      MinecraftForge.EVENT_BUS.addListener(this::addServerDataListeners);
      ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfigs.SPEC, String.format("%s-client.toml", "irons_spellbooks"));
      ModLoadingContext.get().registerConfig(Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", "irons_spellbooks"));
   }

   public void addServerDataListeners(AddReloadListenerEvent event) {
      SpellConfigManager.INSTANCE = new SpellConfigManager();
      event.addListener(SpellConfigManager.INSTANCE);
   }

   public void addPackFinders(AddPackFindersEvent event) {
      LOGGER.debug("addPackFinders");

      try {
         if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            addBuiltinPack(event, "legacy_dead_king_resource_pack", Component.m_237113_("Legacy Dead King"));
         }
      } catch (IOException ex) {
         LOGGER.error(
            "Failed to load a builtin resource pack! If you are seeing this message, please report an issue to https://github.com/iron431/Irons-Spells-n-Spellbooks/issues"
         );
      }
   }

   private static void addBuiltinPack(AddPackFindersEvent event, String filename, Component displayName) throws IOException {
      filename = "builtin_resource_packs/" + filename;
      String id = "builtin/" + filename;
      Path resourcePath = ModList.get().getModFileById("irons_spellbooks").getFile().findResource(new String[]{filename});
      Pack pack = Pack.m_245429_(
         id, displayName, false, path -> new PathPackResources(path, true, resourcePath), PackType.CLIENT_RESOURCES, Position.TOP, PackSource.f_10528_
      );
      event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
   }

   private void enqueueIMC(InterModEnqueueEvent event) {
   }

   private void processIMC(InterModProcessEvent event) {
      LOGGER.info("Got IMC {}", event.getIMCStream().map(m -> m.messageSupplier().get()).collect(Collectors.toList()));
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", path);
   }
}
