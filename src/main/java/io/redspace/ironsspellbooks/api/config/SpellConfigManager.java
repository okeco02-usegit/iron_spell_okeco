package io.redspace.ironsspellbooks.api.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.network.SyncJsonConfigPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;

@EventBusSubscriber
public class SpellConfigManager extends SimpleJsonResourceReloadListener {
   public static final String SUBCONFIG_FOLDER = "irons_spellbooks_spell_config";
   public static SpellConfigManager INSTANCE = new SpellConfigManager();
   private final Gson gson;
   @Nullable
   private Map<ResourceLocation, JsonElement> datapackOverride = null;
   private ImmutableMap<AbstractSpell, SpellConfigHolder> config = ImmutableMap.of();
   public static final Set<SpellConfigParameter<?>> ALL_TYPES = new HashSet<>();
   private static boolean registered = false;
   private boolean dirty = true;

   public static SpellConfigManager getInstance() {
      return INSTANCE;
   }

   public static <T> T getSpellConfigValue(AbstractSpell spell, SpellConfigParameter<T> parameterType) {
      return !INSTANCE.config.containsKey(spell) ? parameterType.defaultValue().get() : ((SpellConfigHolder)INSTANCE.config.get(spell)).get(parameterType);
   }

   public static <T> T getSpellDefaultConfigValue(AbstractSpell spell, SpellConfigParameter<T> parameterType) {
      return !INSTANCE.config.containsKey(spell)
         ? parameterType.defaultValue().get()
         : ((SpellConfigHolder)INSTANCE.config.get(spell)).getDefaultValue(parameterType).orElse(parameterType.defaultValue().get());
   }

   public SpellConfigManager() {
      super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), "irons_spellbooks_spell_config");
      this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   }

   protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
      if (!object.isEmpty()) {
         Map<ResourceLocation, JsonElement> data = new HashMap<>(object.size());

         for (Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (key.m_135815_().contains("/")) {
               String[] path = key.m_135815_().split("/");
               key = ResourceLocation.fromNamespaceAndPath(key.m_135827_(), path[path.length - 1]);
            }

            data.put(key, entry.getValue());
         }

         this.datapackOverride = data;
      }

      this.handleServerConfigUpdate();
   }

   public void handleServerConfigUpdate() {
      registerConfigParameterTypes();
      initiateDefaultFiles(this.gson);

      for (AbstractSpell spell : SpellRegistry.REGISTRY.get()) {
         spell.resetRarityWeights();
      }

      this.dirty = true;
   }

   public void handleClientSync(SyncJsonConfigPacket packet) {
      IronsSpellbooks.LOGGER.info("Handling spell config sync {} files", packet.data.size());
      this.handleServerConfigUpdate();
      this.buildConfigManager(this.toJson(packet.data));
   }

   @SubscribeEvent
   public static void onDatapackSync(OnDatapackSyncEvent event) {
      MinecraftServer server = event.getPlayerList().m_7873_();
      ServerPlayer player = event.getPlayer();
      boolean noErrors = true;
      if (INSTANCE.dirty) {
         INSTANCE.dirty = false;
         if (INSTANCE.datapackOverride != null) {
            noErrors = INSTANCE.buildConfigManager(INSTANCE.datapackOverride);
            INSTANCE.datapackOverride = null;
         } else {
            noErrors = INSTANCE.buildConfigManager(INSTANCE.toJson(getConfigFiles(resolveConfigDirectory(server))));
         }
      }

      if (INSTANCE.config != null) {
         if (player != null) {
            PacketDistributor.sendToPlayer(player, new SyncJsonConfigPacket(INSTANCE.createNetworkData()));
            if (!noErrors) {
               player.m_5661_(Component.m_237115_("commands.irons_spellbooks.config_load_errors").m_130940_(ChatFormatting.RED), false);
            }
         } else {
            PacketDistributor.sendToAllPlayers(new SyncJsonConfigPacket(INSTANCE.createNetworkData()));
            if (!noErrors) {
               for (Player p : server.m_6846_().m_11314_()) {
                  p.m_5661_(Component.m_237115_("commands.irons_spellbooks.config_load_errors").m_130940_(ChatFormatting.RED), false);
               }
            }
         }
      } else {
         IronsSpellbooks.LOGGER.warn("Failed to sync config to players, instance is null");
      }
   }

   private static File resolveConfigDirectory(MinecraftServer server) {
      return FMLPaths.CONFIGDIR.get().resolve("irons_spellbooks_spell_config").toFile();
   }

   private static byte[] readBytes(File file) {
      try (FileReader reader = new FileReader(file)) {
         StringBuilder sb = new StringBuilder();
         char[] buf = new char[4096];

         int n;
         while ((n = reader.read(buf)) != -1) {
            sb.append(buf, 0, n);
         }

         return sb.toString().replaceAll("[ \n]", "").getBytes(StandardCharsets.UTF_8);
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.error("Failed to read config file: {}", e);
         return new byte[0];
      }
   }

   private static List<File> expandAllJsonFiles(File directory) {
      List<File> files = new ArrayList<>();
      File[] sub = directory.listFiles();
      if (sub == null) {
         return List.of();
      }

      for (File file : sub) {
         if (file.getName().endsWith(".json")) {
            files.add(file);
         } else if (file.isDirectory()) {
            files.addAll(expandAllJsonFiles(file));
         }
      }

      return files;
   }

   private static Map<ResourceLocation, byte[]> getConfigFiles(File directory) {
      HashMap<ResourceLocation, byte[]> files = new HashMap<>();
      long milis = System.currentTimeMillis();
      File[] namespacedDirectories = directory.listFiles();
      if (namespacedDirectories != null) {
         for (File namespacedDir : namespacedDirectories) {
            if (namespacedDir.isDirectory()) {
               String namespace = namespacedDir.getName();

               for (File entry : expandAllJsonFiles(namespacedDir)) {
                  String spellName = entry.getName().split("\\.")[0];
                  ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(namespace, spellName);
                  if (SpellRegistry.REGISTRY.get().containsKey(spellId)) {
                     if (files.containsKey(spellId)) {
                        IronsSpellbooks.LOGGER.warn("Duplicate spell config for spell {}! Overriding config.", spellId);
                     }

                     files.put(spellId, readBytes(entry));
                  } else {
                     IronsSpellbooks.LOGGER.warn("Unknown Spell for Configuration file \"{}:{}\", will be ignored!", namespace, spellName);
                  }
               }
            } else if (namespacedDir.getName().endsWith(".json")) {
               IronsSpellbooks.LOGGER.warn("Spell Configuration file \"{}\", outside of namespaced directory, will be ignored!", namespacedDir.getName());
            }
         }
      }

      if (!files.isEmpty()) {
         IronsSpellbooks.LOGGER.info("Read {} spell config files ({} ms)", files.size(), System.currentTimeMillis() - milis);
      }

      return files;
   }

   private Map<ResourceLocation, byte[]> createNetworkData() {
      Map<ResourceLocation, byte[]> data = new HashMap<>();
      UnmodifiableIterator var2 = this.config.entrySet().iterator();

      while (var2.hasNext()) {
         Entry<AbstractSpell, SpellConfigHolder> entry = (Entry<AbstractSpell, SpellConfigHolder>)var2.next();
         JsonObject json = entry.getValue().toJson(this.gson);
         if (!json.asMap().isEmpty()) {
            data.put(entry.getKey().getSpellResource(), json.toString().getBytes(StandardCharsets.UTF_8));
         }
      }

      return data;
   }

   private static void registerConfigParameterTypes() {
      if (!registered) {
         registered = true;
         ALL_TYPES.add(SpellConfigParameter.SCHOOL);
         ALL_TYPES.add(SpellConfigParameter.MIN_RARITY);
         ALL_TYPES.add(SpellConfigParameter.MAX_LEVEL);
         ALL_TYPES.add(SpellConfigParameter.ENABLED);
         ALL_TYPES.add(SpellConfigParameter.COOLDOWN_IN_SECONDS);
         ALL_TYPES.add(SpellConfigParameter.ALLOW_CRAFTING);
         ALL_TYPES.add(SpellConfigParameter.MANA_MULTIPLIER);
         ALL_TYPES.add(SpellConfigParameter.POWER_MULTIPLIER);
         MinecraftForge.EVENT_BUS.post(new RegisterConfigParametersEvent(ALL_TYPES::add));
      }
   }

   private Map<ResourceLocation, JsonElement> toJson(Map<ResourceLocation, byte[]> filestreams) {
      Map<ResourceLocation, JsonElement> configEntries = new HashMap<>();

      for (Entry<ResourceLocation, byte[]> entry : filestreams.entrySet()) {
         ResourceLocation id = entry.getKey();
         byte[] file = entry.getValue();

         try {
            JsonObject obj = (JsonObject)this.gson.fromJson(new InputStreamReader(new ByteArrayInputStream(file)), JsonObject.class);
            configEntries.put(id, obj);
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Failed to parse config file \"{}\": {}", id, e.getMessage());
         }
      }

      return configEntries;
   }

   private boolean buildConfigManager(Map<ResourceLocation, JsonElement> configEntries) {
      boolean hasErrors = false;
      Builder<AbstractSpell, SpellConfigHolder> builder = ImmutableMap.builder();
      DynamicOps<JsonElement> registryops = JsonOps.INSTANCE;

      for (AbstractSpell spell : SpellRegistry.REGISTRY.get()) {
         SpellConfigHolder config = new SpellConfigHolder();
         DefaultConfig raw = spell.getDefaultConfig();
         config.setDefaultValue(SpellConfigParameter.SCHOOL, SchoolRegistry.getSchool(raw.schoolResource));
         config.setDefaultValue(SpellConfigParameter.MIN_RARITY, raw.minRarity);
         config.setDefaultValue(SpellConfigParameter.MAX_LEVEL, raw.maxLevel);
         config.setDefaultValue(SpellConfigParameter.ENABLED, raw.enabled);
         config.setDefaultValue(SpellConfigParameter.COOLDOWN_IN_SECONDS, raw.cooldownInSeconds);
         config.setDefaultValue(SpellConfigParameter.ALLOW_CRAFTING, raw.allowCrafting);
         ResourceLocation spellId = spell.getSpellResource();
         if (configEntries.containsKey(spellId)) {
            try {
               JsonObject json = configEntries.get(spellId).getAsJsonObject();

               for (SpellConfigParameter<?> paramType : ALL_TYPES) {
                  Optional<JsonElement> elem = resolveJsonElement(spellId, paramType, json);
                  if (elem.isPresent()) {
                     try {
                        Object decoded = ((Pair)paramType.datatype().decode(registryops, elem.get()).getOrThrow(false, IronsSpellbooks.LOGGER::error))
                           .getFirst();
                        config.set(paramType, decoded);
                     } catch (Exception e) {
                        IronsSpellbooks.LOGGER
                           .error(
                              "Parsing error loading spell config \"{}\" value for \"{}\": {}", new Object[]{spellId, paramType.key(), e.getLocalizedMessage()}
                           );
                        hasErrors = true;
                     }
                  }
               }
            } catch (IllegalStateException e) {
               IronsSpellbooks.LOGGER.error("Parsing error loading spell config {}: {}", spellId, e);
               hasErrors = true;
            }
         }

         builder.put(spell, config);
      }

      this.config = builder.build();

      for (AbstractSpell spell : SpellRegistry.REGISTRY.get()) {
         MinecraftForge.EVENT_BUS.post(new ModifyDefaultConfigValuesEvent(spell, (SpellConfigHolder)this.config.get(spell)));
      }

      return !hasErrors;
   }

   public static File getSpellConfigDir() {
      Path configDir = FMLPaths.CONFIGDIR.get();
      Path spellConfigDir = configDir.resolve("irons_spellbooks_spell_config");
      File folder = spellConfigDir.toFile();
      if (!folder.exists()) {
         folder.mkdir();
      }

      return folder;
   }

   private static File initiateDefaultFiles(Gson gson) {
      File spellConfigDir = getSpellConfigDir();
      File spellbookDir = spellConfigDir.toPath().resolve("irons_spellbooks").toFile();
      if (!spellbookDir.exists()) {
         spellbookDir.mkdir();
         createExampleConfig(gson, spellbookDir.toPath().resolve("example.txt").toFile());
      }

      return spellbookDir;
   }

   private static Optional<JsonElement> resolveJsonElement(ResourceLocation spellId, SpellConfigParameter<?> dataType, JsonObject parent) {
      if (parent.has(dataType.key().toString())) {
         return Optional.of(parent.get(dataType.key().toString()));
      }

      if (parent.has(dataType.key().m_135815_())) {
         if (!dataType.key().m_135827_().equals("irons_spellbooks")) {
            IronsSpellbooks.LOGGER
               .warn("Config for {} has ambiguous entry \"{}\", adapting to \"{}\"", new Object[]{spellId, dataType.key().m_135815_(), dataType.key()});
         }

         return Optional.of(parent.get(dataType.key().m_135815_()));
      } else {
         return Optional.empty();
      }
   }

   public static <T> Pair<Boolean, File> createExampleConfig(Gson gson, File file) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("_comment1", "Config Files must be placed in a directory labeled with their mod id, and the file name must match the spell id!");
      jsonObject.addProperty("_comment2", "For global config: /config/irons_spellbooks_spell_config/<mod_id>/<spell_id>.json");
      jsonObject.addProperty("_comment3", "For datapacks: /data/<mod_id>/irons_spellbooks_spell_config/<spell_id>.json");

      for (SpellConfigParameter<?> _param : ALL_TYPES) {
         SpellConfigParameter<T> param = (SpellConfigParameter<T>)_param;
         Codec<T> codec = param.datatype();
         DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, param.defaultValue().get());
         jsonObject.add(param.key().toString(), gson.toJsonTree(result.getOrThrow(false, IronsSpellbooks.LOGGER::error)));
      }

      try (FileWriter writer = new FileWriter(file)) {
         gson.toJson(jsonObject, writer);
         return Pair.of(true, file);
      } catch (IOException e) {
         IronsSpellbooks.LOGGER.error("Failed to write default config file {}: {}", file.getPath(), e.getMessage());
         return Pair.of(false, null);
      }
   }

   public static <T> Pair<Boolean, File> generateSpellConfigFile(Gson gson, AbstractSpell spell, boolean full, boolean override) {
      ResourceLocation resourceLocation = spell.getSpellResource();

      try {
         File spellConfigDir = getSpellConfigDir();
         File modDir = spellConfigDir.toPath().resolve(resourceLocation.m_135827_()).toFile();
         if (!modDir.exists()) {
            modDir.mkdir();
         }

         File spellConfig = modDir.toPath().resolve(resourceLocation.m_135815_() + ".json").toFile();
         if (spellConfig.exists() && !override) {
            return Pair.of(false, spellConfig);
         }

         JsonObject json = new JsonObject();
         if (full) {
            for (SpellConfigParameter<?> _param : ALL_TYPES) {
               SpellConfigParameter<T> param = (SpellConfigParameter<T>)_param;
               Codec<T> codec = param.datatype();
               DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, getSpellDefaultConfigValue(spell, param));
               json.add(param.key().toString(), gson.toJsonTree(result.getOrThrow(false, IronsSpellbooks.LOGGER::error)));
            }
         }

         try (FileWriter writer = new FileWriter(spellConfig)) {
            gson.toJson(json, writer);
         }

         return Pair.of(true, spellConfig);
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.error("Could not generate config file: {}", e.getMessage());
         return Pair.of(false, null);
      }
   }
}
