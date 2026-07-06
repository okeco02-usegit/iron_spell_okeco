package io.redspace.ironsspellbooks.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import io.redspace.ironsspellbooks.item.FurledMapItem;
import io.redspace.ironsspellbooks.registries.LootRegistry;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FurledMapLootFunction extends LootItemConditionalFunction {
   private final String destination;
   private final String translation;
   private final Optional<String> dimension;

   public Optional<String> getDimension() {
      return this.dimension;
   }

   public String getDestination() {
      return this.destination;
   }

   public String getTranslation() {
      return this.translation;
   }

   protected FurledMapLootFunction(LootItemCondition[] lootConditions, String destination, String translation, Optional<String> dimension) {
      super(lootConditions);
      this.destination = destination;
      this.translation = translation;
      this.dimension = dimension;
   }

   protected ItemStack m_7372_(ItemStack itemStack, LootContext lootContext) {
      if (itemStack.m_41720_() instanceof FurledMapItem) {
         return this.dimension.isPresent()
            ? FurledMapItem.of(
               ResourceLocation.parse(this.destination),
               ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.parse(this.dimension.get())),
               Component.m_237115_(this.translation)
            )
            : FurledMapItem.of(ResourceLocation.parse(this.destination), FurledMapItem.OVERWORLD, Component.m_237115_(this.translation));
      } else {
         return itemStack;
      }
   }

   public LootItemFunctionType m_7162_() {
      return (LootItemFunctionType)LootRegistry.SET_FURLED_MAP_FUNCTION.get();
   }

   public static class Serializer extends net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Serializer<FurledMapLootFunction> {
      public void serialize(JsonObject json, FurledMapLootFunction scrollFunction, JsonSerializationContext jsonDeserializationContext) {
         super.m_6170_(json, scrollFunction, jsonDeserializationContext);
         json.addProperty("destination", scrollFunction.destination);
         json.addProperty("translation", scrollFunction.translation);
      }

      public FurledMapLootFunction deserialize(JsonObject json, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
         if (!GsonHelper.m_13900_(json, "destination")) {
            throw new JsonSyntaxException("set_furled_map missing key: destination!");
         }

         if (!GsonHelper.m_13900_(json, "description_translation")) {
            throw new JsonSyntaxException("set_furled_map missing key: translation!");
         }

         Optional<String> dimension = Optional.empty();
         if (GsonHelper.m_13900_(json, "dimension")) {
            dimension = Optional.of(GsonHelper.m_13906_(json, "dimension"));
         }

         return new FurledMapLootFunction(
            lootConditions, GsonHelper.m_13906_(json, "destination"), GsonHelper.m_13906_(json, "description_translation"), dimension
         );
      }
   }
}
