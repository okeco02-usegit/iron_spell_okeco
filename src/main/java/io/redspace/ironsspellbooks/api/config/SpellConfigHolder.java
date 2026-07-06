package io.redspace.ironsspellbooks.api.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class SpellConfigHolder {
   private final Map<SpellConfigParameter<?>, Object> defaultConfig = new HashMap<>();
   private final Map<SpellConfigParameter<?>, Object> config = new HashMap<>();

   public <T> void set(SpellConfigParameter<T> paramtype, T parameter) {
      this.config.put(paramtype, parameter);
   }

   public <T> void setDefaultValue(SpellConfigParameter<T> paramtype, T parameter) {
      this.defaultConfig.put(paramtype, parameter);
   }

   public <T> T get(SpellConfigParameter<T> paramtype) {
      return (T)(this.config.containsKey(paramtype) ? this.config.get(paramtype) : this.defaultConfig.getOrDefault(paramtype, paramtype.defaultValue().get()));
   }

   public <T> Optional<T> getDefaultValue(SpellConfigParameter<T> paramtype) {
      return Optional.ofNullable((T)this.defaultConfig.get(paramtype));
   }

   public <T> boolean isDefault(SpellConfigParameter<T> parameter) {
      return !this.config.containsKey(parameter);
   }

   public <T> JsonObject toJson(Gson gson) {
      JsonObject json = new JsonObject();

      for (Entry<SpellConfigParameter<?>, Object> entry : this.config.entrySet()) {
         SpellConfigParameter<T> param = (SpellConfigParameter<T>)entry.getKey();
         T value = (T)entry.getValue();
         Codec<T> codec = param.datatype();
         DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, value);
         json.add(param.key().toString(), gson.toJsonTree(result.getOrThrow(false, IronsSpellbooks.LOGGER::error)));
      }

      return json;
   }
}
