package io.redspace.ironsspellbooks.api.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.api.backwards_compat.UpgradeTypeCache;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record UpgradeData(Map<Holder<UpgradeOrbType>, Integer> upgrades, String upgradedSlot) {
   public static final String NBT = "irons_spellbooks:upgrade_data";
   public static final String LEGACY_NBT = "ISBUpgrades";
   public static final String UPGRADE_TYPE = "id";
   public static final String SLOT = "slot";
   public static final String COUNT = "count";
   public static final String UPGRADES = "upgrades";
   public static final UpgradeData NONE = new UpgradeData(ImmutableMap.of(), EquipmentSlot.MAINHAND.m_20751_());
   @Deprecated(forRemoval = true)
   private static final Codec<ObjectObjectImmutablePair<String, Integer>> ELEMENT_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(Codec.STRING.fieldOf("id").forGetter(Pair::left), Codec.INT.fieldOf("count").forGetter(Pair::right))
         .apply(builder, ObjectObjectImmutablePair::new)
   );
   public static final Codec<Holder<UpgradeOrbType>> I_LOVE_ONE_POINT_TWENTY = ResourceKey.m_195966_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY)
      .xmap(UpgradeTypeCache.CACHE::get, holder -> (ResourceKey)holder.m_203543_().get());
   public static final Codec<UpgradeData> LEGACY_CODEC = Codec.of(Encoder.error("Legacy codec should never write!"), new Decoder<UpgradeData>() {
      public <T> DataResult<com.mojang.datafixers.util.Pair<UpgradeData, T>> decode(DynamicOps<T> ops, T input) {
         try {
            ListTag inputTag = (ListTag)input;
            Map<Holder<UpgradeOrbType>, Integer> map = new HashMap<>();
            String upgradedSlot = null;

            for (Tag tag : inputTag) {
               if (tag instanceof CompoundTag compoundTag) {
                  if (upgradedSlot == null) {
                     upgradedSlot = compoundTag.m_128461_("slot");
                  }

                  ResourceLocation upgradeId = ResourceLocation.parse(compoundTag.m_128461_("id"));
                  Holder<UpgradeOrbType> holder = UpgradeTypeCache.CACHE.get(ResourceKey.m_135785_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, upgradeId));
                  int count = compoundTag.m_128451_("upgrades");
                  map.put(holder, count);
               }
            }

            return DataResult.success(com.mojang.datafixers.util.Pair.of(new UpgradeData(map, upgradedSlot), input));
         } catch (Exception e) {
            return DataResult.error(e::getMessage);
         }
      }
   });
   public static final Codec<UpgradeData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.STRING.fieldOf("slot").forGetter(UpgradeData::getUpgradedSlot),
            Codec.unboundedMap(I_LOVE_ONE_POINT_TWENTY, Codec.INT).fieldOf("upgrades").forGetter(UpgradeData::upgrades)
         )
         .apply(builder, (slot, list) -> new UpgradeData(list, slot))
   );

   public static UpgradeData getUpgradeData(ItemStack itemStack) {
      return CodecHelper.getOrElseWithLegacy(itemStack, "irons_spellbooks:upgrade_data", CODEC, NONE, "ISBUpgrades", LEGACY_CODEC);
   }

   public static boolean hasUpgradeData(ItemStack stack) {
      return CodecHelper.hasWithLegacy(stack, "irons_spellbooks:upgrade_data", "ISBUpgrades");
   }

   public static void removeUpgradeData(ItemStack itemstack) {
      itemstack.m_41784_().m_128473_("irons_spellbooks:upgrade_data");
   }

   public UpgradeData addUpgrade(ItemStack stack, Holder<UpgradeOrbType> upgradeType, String slot) {
      if (this == NONE) {
         Builder<Holder<UpgradeOrbType>, Integer> map = ImmutableMap.builder();
         map.put(upgradeType, 1);
         UpgradeData upgrade = new UpgradeData(map.build(), slot);
         set(stack, upgrade);
         return upgrade;
      }

      Builder<Holder<UpgradeOrbType>, Integer> map = ImmutableMap.builder();
      if (this.upgrades.containsKey(upgradeType)) {
         map.put(upgradeType, this.upgrades.get(upgradeType) + 1);
         map.putAll(this.upgrades.entrySet().stream().filter(entry -> entry.getKey() != upgradeType).toList());
      } else {
         map.put(upgradeType, 1);
         map.putAll(this.upgrades);
      }

      UpgradeData upgrade = new UpgradeData(map.build(), this.upgradedSlot);
      set(stack, upgrade);
      return upgrade;
   }

   public static void set(ItemStack stack, UpgradeData data) {
      CodecHelper.set(stack, "irons_spellbooks:upgrade_data", CODEC, data);
   }

   public int getTotalUpgrades() {
      int count = 0;

      for (Entry<Holder<UpgradeOrbType>, Integer> upgradeInstance : this.upgrades.entrySet()) {
         count += upgradeInstance.getValue();
      }

      return count;
   }

   public String getUpgradedSlot() {
      return this.upgradedSlot;
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj
         || obj instanceof UpgradeData upgradeData && this.upgradedSlot.equals(upgradeData.upgradedSlot) && this.upgrades.equals(upgradeData.upgrades);
   }

   @Override
   public int hashCode() {
      return this.upgradedSlot.hashCode() * 31 + this.upgrades.hashCode();
   }
}
