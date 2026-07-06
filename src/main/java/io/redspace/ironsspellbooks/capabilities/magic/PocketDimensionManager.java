package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.data.IronsDataStorage;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.worldgen.ClearPortalFrameDataProcessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class PocketDimensionManager implements INBTSerializable<CompoundTag> {
   public static final ResourceKey<Level> POCKET_DIMENSION = ResourceKey.m_135785_(Registries.f_256858_, IronsSpellbooks.id("pocket_dimension"));
   public static final ResourceLocation POCKET_ROOM_STRUCTURE = IronsSpellbooks.id("pocket_room");
   public static final int POCKET_SPACING = 256;
   private static final String UUID_KEY = "uuid";
   private static final String INT_ID_KEY = "pocket_id";
   private static final String ID_MAP_KEY = "ids";
   private static final String NEXT_ID_KEY = "next_id";
   public static final PocketDimensionManager INSTANCE = new PocketDimensionManager();
   private int nextId;
   private final Object2IntMap<UUID> ids = new Object2IntOpenHashMap();

   public void remove(UUID uuid) {
      this.ids.remove(uuid);
      IronsDataStorage.INSTANCE.m_77762_();
   }

   public @UnknownNullability CompoundTag serializeNBT() {
      CompoundTag compoundTag = new CompoundTag();
      ListTag entries = new ListTag();
      ObjectIterator var3 = this.ids.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<UUID> entry = (Entry<UUID>)var3.next();
         CompoundTag tagEntry = new CompoundTag();
         tagEntry.m_128362_("uuid", (UUID)entry.getKey());
         tagEntry.m_128405_("pocket_id", entry.getIntValue());
         entries.add(tagEntry);
      }

      compoundTag.m_128365_("ids", entries);
      compoundTag.m_128405_("next_id", this.nextId);
      return compoundTag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag entries = nbt.m_128437_("ids", 10);
      int nextId = nbt.m_128451_("next_id");

      for (Tag tag : entries) {
         try {
            CompoundTag compoundTag = (CompoundTag)tag;
            UUID uuid = compoundTag.m_128342_("uuid");
            int pocketId = compoundTag.m_128451_("pocket_id");
            this.ids.put(uuid, pocketId);
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Failed to parse PocketDimensionManager id entry: {}: {}", tag, e.getMessage());
         }
      }

      this.nextId = nextId;
   }

   public int idFor(UUID uuid) {
      if (!this.ids.containsKey(uuid)) {
         this.ids.put(uuid, this.nextId);
         this.nextId++;
         IronsDataStorage.INSTANCE.m_77762_();
      }

      return this.ids.getInt(uuid);
   }

   public int idFor(Player player) {
      return this.idFor(player.m_20148_());
   }

   public BlockPos structurePosForId(int pocketDimensionId) {
      return BlockPos.m_274561_(0.0, 0.0, 256 * pocketDimensionId);
   }

   public BlockPos structurePosForPlayer(Player player) {
      return this.structurePosForId(this.idFor(player));
   }

   public BlockPos findPortalForStructure(ServerLevel pocketDimension, BlockPos blockPos) {
      BlockPos defaultPos = blockPos.m_122020_(10).m_122030_(7).m_6630_(2);
      if (pocketDimension.m_8055_(defaultPos).m_60713_((Block)BlockRegistry.POCKET_PORTAL_FRAME.get())) {
         return defaultPos;
      }

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            for (int y = 0; y < 32; y++) {
               BlockPos pos = blockPos.m_122020_(x).m_122030_(z).m_6630_(y);
               if (pocketDimension.m_8055_(pos).m_60713_((Block)BlockRegistry.POCKET_PORTAL_FRAME.get())) {
                  return pos;
               }
            }
         }
      }

      return defaultPos;
   }

   public boolean maybeGeneratePocketRoom(ServerPlayer player) {
      ServerLevel serverLevel = player.m_284548_();
      BlockPos structurePos = this.structurePosForPlayer(player);
      ServerLevel pocketLevel = serverLevel.m_7654_().m_129880_(POCKET_DIMENSION);
      BlockState blockState = pocketLevel.m_8055_(structurePos);
      if (blockState.m_60795_() && !blockState.m_60713_(Blocks.f_50375_)) {
         StructureTemplateManager structureTemplateManager = pocketLevel.m_215082_();
         StructureTemplate structureTemplate = structureTemplateManager.m_230359_(POCKET_ROOM_STRUCTURE);
         StructurePlaceSettings placementSettings = new StructurePlaceSettings()
            .m_74377_(Mirror.NONE)
            .m_74379_(Rotation.NONE)
            .m_74392_(true)
            .m_74383_(new ClearPortalFrameDataProcessor());
         structureTemplate.m_230328_(pocketLevel, structurePos, structurePos, placementSettings, pocketLevel.m_213780_(), 2);
         return true;
      } else {
         return false;
      }
   }

   public void tick(Level level) {
      if (level instanceof ServerLevel serverLevel) {
         if (serverLevel.m_46472_().equals(POCKET_DIMENSION)) {
            if (serverLevel.m_46467_() % 100L == 0L) {
               serverLevel.m_6907_().forEach(player -> {
                  if (!player.m_7500_() && !player.m_5833_()) {
                     int pocketX = (int)(player.m_20185_() / 256.0) * 256;
                     int pocketZ = (int)(player.m_20189_() / 256.0) * 256;
                     if (player.m_20185_() < pocketX || player.m_20185_() > pocketX + 16 || player.m_20189_() < pocketZ || player.m_20189_() > pocketZ + 16) {
                        BlockPos blockPos = this.structurePosForPlayer(player);
                        BlockPos portalPos = this.findPortalForStructure(serverLevel, blockPos);
                        player.m_183634_();
                        player.m_20219_(Vec3.m_82539_(portalPos));
                     }
                  }
               });
            }
         }
      }
   }
}
