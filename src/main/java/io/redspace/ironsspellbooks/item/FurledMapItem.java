package io.redspace.ironsspellbooks.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderSet.Direct;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;

public class FurledMapItem extends Item {
   public static final ResourceKey<Level> OVERWORLD = ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.withDefaultNamespace("overworld"));
   public static final ResourceKey<Level> NETHER = ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.withDefaultNamespace("the_nether"));

   public FurledMapItem() {
      super(ItemPropertiesHelper.material().m_41487_(1));
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      if (level instanceof ServerLevel serverlevel) {
         level.m_6269_(null, player, SoundEvents.f_12493_, player.m_5720_(), 1.0F, 1.0F);
         ItemStack itemStack = player.m_21120_(hand);
         player.m_36335_().m_41524_(itemStack.m_41720_(), 50);
         if (FurledMapItem.FurledMapData.has(itemStack)) {
            FurledMapItem.FurledMapData furledMapData = FurledMapItem.FurledMapData.get(itemStack);
            ResourceKey<Structure> structureResourceKey = ResourceKey.m_135785_(Registries.f_256944_, furledMapData.destinationResource);
            Optional<Direct<Structure>> holder = serverlevel.m_9598_()
               .m_175515_(Registries.f_256944_)
               .m_203636_(structureResourceKey)
               .map(xva$0 -> HolderSet.m_205809_(new Holder[]{xva$0}));
            if (furledMapData.dimension().isPresent()) {
               ResourceKey<Level> dimensionRestriction = furledMapData.dimension().get();
               if (!serverlevel.m_46472_().equals(dimensionRestriction)) {
                  ((ServerPlayer)player)
                     .f_8906_
                     .m_9829_(
                        new ClientboundSetActionBarTextPacket(
                           Component.m_237115_("item.irons_spellbooks.furled_map.dimension_fail").m_130940_(ChatFormatting.RED)
                        )
                     );
                  return InteractionResultHolder.m_19100_(itemStack);
               }
            }

            if (holder.isPresent()) {
               Pair<BlockPos, Holder<Structure>> pair = serverlevel.m_7726_()
                  .m_8481_()
                  .m_223037_(serverlevel, (HolderSet)holder.get(), player.m_20183_(), 100, (Boolean)ServerConfigs.FURLED_MAPS_SKIP_CHUNKS.get());
               if (pair != null) {
                  BlockPos blockpos = (BlockPos)pair.getFirst();
                  ItemStack mapStack = MapItem.m_42886_(serverlevel, blockpos.m_123341_(), blockpos.m_123343_(), (byte)2, true, true);
                  MapItem.m_42850_(serverlevel, mapStack);
                  MapItemSavedData.m_77925_(mapStack, blockpos, "x", Type.RED_X);
                  furledMapData.descriptionOverride.ifPresent(mapStack::m_41714_);
                  replaceItem(player, mapStack, hand);
                  return InteractionResultHolder.m_19092_(itemStack, level.f_46443_);
               }
            }
         }

         replaceItem(player, new ItemStack(Items.f_42676_), hand);
      }

      return super.m_7203_(level, player, hand);
   }

   private static void replaceItem(Player player, ItemStack itemStack, InteractionHand hand) {
      boolean flag = player.m_150110_().f_35937_;
      if (!flag) {
         player.m_21008_(hand, itemStack);
      } else {
         player.m_150109_().m_36054_(itemStack);
      }
   }

   public static ItemStack of(ResourceLocation structure, MutableComponent descriptor) {
      ItemStack itemStack = new ItemStack((ItemLike)ItemRegistry.FURLED_MAP.get());
      FurledMapItem.FurledMapData.set(itemStack, new FurledMapItem.FurledMapData(structure, Optional.empty(), Optional.of(descriptor)));
      FurledMapItem.FurledMapData.setLoreHelper(
         itemStack,
         Component.m_237110_("item.irons_spellbooks.furled_map_descriptor_framing", new Object[]{descriptor})
            .m_6270_(Style.f_131099_.m_131140_(ChatFormatting.GOLD))
      );
      return itemStack;
   }

   public static ItemStack of(ResourceLocation structure, ResourceKey<Level> exclusiveDimension, MutableComponent descriptor) {
      return of(structure, exclusiveDimension, descriptor, false);
   }

   public static ItemStack of(ResourceLocation structure, ResourceKey<Level> exclusiveDimension, MutableComponent descriptor, boolean ancient) {
      ItemStack itemStack = new ItemStack(ancient ? (ItemLike)ItemRegistry.ANCIENT_FURLED_MAP.get() : (ItemLike)ItemRegistry.FURLED_MAP.get());
      FurledMapItem.FurledMapData.set(itemStack, new FurledMapItem.FurledMapData(structure, Optional.of(exclusiveDimension), Optional.of(descriptor)));
      FurledMapItem.FurledMapData.setLoreHelper(
         itemStack,
         Component.m_237110_("item.irons_spellbooks.furled_map_descriptor_framing", new Object[]{descriptor})
            .m_6270_(Style.f_131099_.m_131140_(ChatFormatting.GOLD))
      );
      return itemStack;
   }

   public record FurledMapData(ResourceLocation destinationResource, Optional<ResourceKey<Level>> dimension, Optional<Component> descriptionOverride) {
      public static final String NBT = "irons_spellbooks:furled_map_data";
      public static final String LEGACY_NBT = "furledMapData";
      public static final String FURLED_MAP_LOCATION = "destination";
      public static final String FURLED_MAP_DESCRIPTION = "description";
      public static final Codec<FurledMapItem.FurledMapData> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               ResourceLocation.f_135803_.fieldOf("destination").forGetter(FurledMapItem.FurledMapData::destinationResource),
               ResourceKey.m_195966_(Registries.f_256858_).optionalFieldOf("dimension").forGetter(FurledMapItem.FurledMapData::dimension),
               ExtraCodecs.f_252442_.optionalFieldOf("descriptionOverride").forGetter(FurledMapItem.FurledMapData::descriptionOverride)
            )
            .apply(builder, FurledMapItem.FurledMapData::new)
      );
      public static final Codec<FurledMapItem.FurledMapData> LEGACY_CODEC = CodecHelper.createLegacyCodec(tag -> {
         CompoundTag nbt = (CompoundTag)tag;
         String destination = nbt.m_128461_("destination");
         String rawDesc = nbt.m_128461_("description");
         Optional<Component> desc = Optional.empty();
         if (!rawDesc.isEmpty()) {
            desc = Optional.ofNullable(Serializer.m_130701_(rawDesc));
         }

         return new FurledMapItem.FurledMapData(ResourceLocation.parse(destination), Optional.empty(), desc);
      });

      public static boolean has(ItemStack stack) {
         return CodecHelper.hasWithLegacy(stack, "irons_spellbooks:furled_map_data", "furledMapData");
      }

      public static FurledMapItem.FurledMapData get(ItemStack stack) {
         return CodecHelper.getWithLegacy(CODEC, stack, "irons_spellbooks:furled_map_data", "furledMapData", LEGACY_CODEC);
      }

      public static void set(ItemStack stack, FurledMapItem.FurledMapData data) {
         CodecHelper.set(stack, "irons_spellbooks:furled_map_data", CODEC, data);
      }

      public static void setLoreHelper(ItemStack stack, Component line) {
         ListTag lore = new ListTag();
         lore.add(StringTag.m_129297_(Serializer.m_130703_(line)));
         stack.m_41698_("display").m_128365_("Lore", lore);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this
            || obj instanceof FurledMapItem.FurledMapData data
               && data.destinationResource.equals(this.destinationResource)
               && data.descriptionOverride.equals(this.descriptionOverride);
      }

      @Override
      public int hashCode() {
         return this.destinationResource.hashCode() + this.descriptionOverride.hashCode() * 31;
      }
   }
}
