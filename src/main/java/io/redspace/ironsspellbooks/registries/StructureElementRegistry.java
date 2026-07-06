package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.worldgen.IndividualTerrainStructurePoolElement;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class StructureElementRegistry {
   public static final DeferredRegister<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_DEFERRED_REGISTER = DeferredRegister.create(
      Registries.f_257024_, "irons_spellbooks"
   );
   public static final Supplier<StructurePoolElementType<IndividualTerrainStructurePoolElement>> INDIVIDUAL_TERRAIN_ELEMENT = STRUCTURE_POOL_ELEMENT_DEFERRED_REGISTER.register(
      "individual_terrain_element", () -> () -> IndividualTerrainStructurePoolElement.CODEC
   );

   public static void register(IEventBus eventBus) {
      STRUCTURE_POOL_ELEMENT_DEFERRED_REGISTER.register(eventBus);
   }
}
