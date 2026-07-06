package io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.spawning.PlayerDetector;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record VaultConfig(
   ResourceLocation lootTable,
   double activationRange,
   double deactivationRange,
   ItemStack keyItem,
   Optional<ResourceLocation> overrideLootTableToDisplay,
   PlayerDetector playerDetector,
   PlayerDetector.EntitySelector entitySelector
) {
   static final String TAG_NAME = "config";
   static VaultConfig DEFAULT = new VaultConfig();
   static Codec<VaultConfig> CODEC = RecordCodecBuilder.create(
      p_335305_ -> p_335305_.group(
            ResourceLocation.f_135803_.optionalFieldOf("loot_table", DEFAULT.lootTable()).forGetter(VaultConfig::lootTable),
            Codec.DOUBLE.optionalFieldOf("activation_range", DEFAULT.activationRange()).forGetter(VaultConfig::activationRange),
            Codec.DOUBLE.optionalFieldOf("deactivation_range", DEFAULT.deactivationRange()).forGetter(VaultConfig::deactivationRange),
            ItemStack.f_41582_
               .optionalFieldOf("key_item")
               .xmap(opt -> opt.orElse(ItemStack.f_41583_), stack -> stack.m_41619_() ? Optional.empty() : Optional.of(stack))
               .forGetter(VaultConfig::keyItem),
            ResourceLocation.f_135803_.optionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::overrideLootTableToDisplay)
         )
         .apply(p_335305_, VaultConfig::new)
   );

   private VaultConfig() {
      this(
         ResourceLocation.withDefaultNamespace("empty"),
         4.0,
         4.5,
         new ItemStack((ItemLike)ItemRegistry.DECREPIT_KEY.get()),
         Optional.empty(),
         PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
         PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
      );
   }

   public VaultConfig(ResourceLocation p_335999_, double p_323704_, double p_323499_, ItemStack p_323661_, Optional<ResourceLocation> p_323481_) {
      this(p_335999_, p_323704_, p_323499_, p_323661_, p_323481_, DEFAULT.playerDetector(), DEFAULT.entitySelector());
   }

   private DataResult<VaultConfig> validate() {
      return this.activationRange > this.deactivationRange
         ? DataResult.error(
            () -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")"
         )
         : DataResult.success(this);
   }
}
