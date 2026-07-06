package io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;

public record TrialSpawnerConfig(
   int spawnRange,
   float totalMobs,
   float simultaneousMobs,
   float totalMobsAddedPerPlayer,
   float simultaneousMobsAddedPerPlayer,
   int ticksBetweenSpawn,
   SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition,
   SimpleWeightedRandomList<ResourceLocation> lootTablesToEject,
   ResourceLocation itemsToDropWhenOminous
) {
   public static final TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(
      4,
      6.0F,
      2.0F,
      2.0F,
      1.0F,
      40,
      SimpleWeightedRandomList.m_185864_(),
      SimpleWeightedRandomList.m_146263_().m_146270_(),
      ResourceLocation.withDefaultNamespace("empty")
   );
   public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(
      p_338041_ -> p_338041_.group(
            Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
            Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
            Codec.floatRange(0.0F, Float.MAX_VALUE)
               .optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
               .forGetter(TrialSpawnerConfig::simultaneousMobs),
            Codec.floatRange(0.0F, Float.MAX_VALUE)
               .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
               .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
            Codec.floatRange(0.0F, Float.MAX_VALUE)
               .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
               .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
            Codec.intRange(0, Integer.MAX_VALUE)
               .optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn)
               .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
            SpawnData.f_186560_
               .optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.m_185864_())
               .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
            SimpleWeightedRandomList.m_185860_(ResourceLocation.f_135803_)
               .optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
               .forGetter(TrialSpawnerConfig::lootTablesToEject),
            ResourceLocation.f_135803_
               .optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous)
               .forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
         )
         .apply(p_338041_, TrialSpawnerConfig::new)
   );

   public int calculateTargetTotalMobs(int players) {
      return (int)Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * players);
   }

   public int calculateTargetSimultaneousMobs(int players) {
      return (int)Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * players);
   }

   public long ticksBetweenItemSpawners() {
      return 160L;
   }
}
