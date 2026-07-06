package io.redspace.ironsspellbooks.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import java.io.File;
import java.io.FileWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class CreateRecipeCompatGenerator {
   private static final String EMPTY_FORMAT = "{\n  \"conditions\": [\n    {\n      \"type\": \"forge:mod_loaded\",\n      \"modid\": \"create\"\n    }\n  ],\n  \"type\": \"create:emptying\",\n  \"ingredients\": [\n    %s\n  ],\n  \"results\": [\n    {\n      \"item\": \"%s\"\n    },\n    {\n      \"fluid\": \"%s\",\n      \"amount\": %s\n    }\n  ]\n}\n";
   private static final String FILL_FORMAT = "{\n  \"conditions\": [\n    {\n      \"type\": \"forge:mod_loaded\",\n      \"modid\": \"create\"\n    }\n  ],\n  \"type\": \"create:filling\",\n  \"ingredients\": [\n    %s,\n    {\n      \"fluid\": \"%s\",\n      \"amount\": %s\n    }\n  ],\n  \"results\": [\n    {\n      \"item\": \"%s\"\n    }\n  ]\n}\n";

   public static int run(CommandContext<CommandSourceStack> context) {
      RecipeManager recipeManager = Minecraft.m_91087_().f_91073_.m_7465_();
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      new File("create_compat").mkdir();
      recipeManager.m_44013_((RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get())
         .stream()
         .forEach(
            recipe -> {
               if (((ResourceKey)recipe.result().m_220173_().m_203543_().get()).m_135782_().m_135827_().equals("irons_spellbooks")) {
                  String stringJson = String.format(
                     "{\n  \"conditions\": [\n    {\n      \"type\": \"forge:mod_loaded\",\n      \"modid\": \"create\"\n    }\n  ],\n  \"type\": \"create:filling\",\n  \"ingredients\": [\n    %s,\n    {\n      \"fluid\": \"%s\",\n      \"amount\": %s\n    }\n  ],\n  \"results\": [\n    {\n      \"item\": \"%s\"\n    }\n  ]\n}\n",
                     recipe.input().m_43942_().toString(),
                     ((ResourceKey)recipe.fluid().getFluid().m_205069_().m_203543_().get()).m_135782_().toString(),
                     recipe.fluid().getAmount(),
                     ((ResourceKey)recipe.result().m_220173_().m_203543_().get()).m_135782_().toString()
                  );
                  String outputFilepath = String.format("create_compat/create_fill_%s.json", recipe.m_6423_().m_135815_().split("/", 2)[1].split("_", 2)[1]);
                  File file = new File(outputFilepath);

                  try (FileWriter writer = new FileWriter(file)) {
                     writer.write(stringJson);
                  } catch (Exception e) {
                     IronsSpellbooks.LOGGER.debug("Failed to generate recipe \"{}\": {}", outputFilepath, e.getMessage());
                  }
               }
            }
         );
      recipeManager.m_44013_((RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get())
         .stream()
         .forEach(
            recipe -> {
               if (((ResourceKey)recipe.result().getFluid().m_205069_().m_203543_().get()).m_135782_().m_135827_().equals("irons_spellbooks")) {
                  String stringJson = String.format(
                     "{\n  \"conditions\": [\n    {\n      \"type\": \"forge:mod_loaded\",\n      \"modid\": \"create\"\n    }\n  ],\n  \"type\": \"create:emptying\",\n  \"ingredients\": [\n    %s\n  ],\n  \"results\": [\n    {\n      \"item\": \"%s\"\n    },\n    {\n      \"fluid\": \"%s\",\n      \"amount\": %s\n    }\n  ]\n}\n",
                     recipe.input().m_43942_().toString(),
                     ((ResourceKey)recipe.returned().m_220173_().m_203543_().get()).m_135782_().toString(),
                     ((ResourceKey)recipe.result().getFluid().m_205069_().m_203543_().get()).m_135782_().toString(),
                     recipe.result().getAmount()
                  );
                  new File("create_compat").mkdir();
                  String outputFilepath = String.format("create_compat/create_empty_%s.json", recipe.m_6423_().m_135815_().split("/", 2)[1].split("_", 2)[1]);
                  File file = new File(outputFilepath);

                  try (FileWriter writer = new FileWriter(file)) {
                     writer.write(stringJson);
                  } catch (Exception e) {
                     IronsSpellbooks.LOGGER.debug("Failed to generate recipe \"{}\": {}", outputFilepath, e.getMessage());
                  }
               }
            }
         );
      return 1;
   }
}
