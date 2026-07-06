package io.redspace.ironsspellbooks.registries;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PoiTypeRegistry {
   private static final DeferredRegister<PoiType> POIS = DeferredRegister.create(Registries.f_256805_, "irons_spellbooks");
   public static final RegistryObject<PoiType> CINDEROUS_KEYSTONE_POI = POIS.register(
      "cinderous_soul_rune", () -> new PoiType(getBlockStates((Block)BlockRegistry.CINDEROUS_KEYSTONE.get()), 1, 1)
   );

   public static void register(IEventBus eventBus) {
      POIS.register(eventBus);
   }

   private static Set<BlockState> getBlockStates(Block pBlock) {
      return ImmutableSet.copyOf(pBlock.m_49965_().m_61056_());
   }
}
