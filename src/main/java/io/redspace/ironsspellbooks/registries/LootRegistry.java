package io.redspace.ironsspellbooks.registries;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.loot.AppendLootModifier;
import io.redspace.ironsspellbooks.loot.FurledMapLootFunction;
import io.redspace.ironsspellbooks.loot.RandomizeRingEnhancementFunction;
import io.redspace.ironsspellbooks.loot.RandomizeSpellFunction;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class LootRegistry {
   public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registries.f_257015_, "irons_spellbooks");
   public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "irons_spellbooks"
   );
   public static final RegistryObject<LootItemFunctionType> RANDOMIZE_SPELL_FUNCTION = LOOT_FUNCTIONS.register(
      "randomize_spell", () -> new LootItemFunctionType(new RandomizeSpellFunction.Serializer())
   );
   public static final RegistryObject<LootItemFunctionType> RANDOMIZE_SPELL_RING_FUNCTION = LOOT_FUNCTIONS.register(
      "randomize_ring_enhancement", () -> new LootItemFunctionType(new RandomizeRingEnhancementFunction.Serializer())
   );
   public static final RegistryObject<LootItemFunctionType> SET_FURLED_MAP_FUNCTION = LOOT_FUNCTIONS.register(
      "set_furled_map", () -> new LootItemFunctionType(new FurledMapLootFunction.Serializer())
   );
   public static final Supplier<Codec<? extends IGlobalLootModifier>> APPEND_LOOT_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
      "append_loot", AppendLootModifier.CODEC
   );

   public static void register(IEventBus eventBus) {
      LOOT_FUNCTIONS.register(eventBus);
      LOOT_MODIFIER_SERIALIZERS.register(eventBus);
   }
}
