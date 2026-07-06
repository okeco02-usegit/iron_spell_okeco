package io.redspace.ironsspellbooks.data;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class IronsDataStorage extends SavedData {
   public static IronsDataStorage INSTANCE;

   public static void init(DimensionDataStorage dimensionDataStorage) {
      if (dimensionDataStorage != null) {
         INSTANCE = (IronsDataStorage)dimensionDataStorage.m_164861_(IronsDataStorage::load, IronsDataStorage::new, "irons_spellbooks_data");
      }
   }

   @NotNull
   public CompoundTag m_7176_(@NotNull CompoundTag pCompoundTag) {
      CompoundTag tag = new CompoundTag();
      tag.m_128365_("GuidingBoltManager", GuidingBoltManager.INSTANCE.serializeNBT());
      tag.m_128365_("PortalManager", PortalManager.INSTANCE.serializeNBT());
      tag.m_128365_("PocketDimensionIdManager", PocketDimensionManager.INSTANCE.serializeNBT());
      tag.m_128365_("SummonManager", SummonManager.INSTANCE.serializeNBT());
      return tag;
   }

   public static IronsDataStorage load(CompoundTag tag) {
      if (tag.m_128425_("GuidingBoltManager", 10)) {
         GuidingBoltManager.INSTANCE.deserializeNBT(tag.m_128469_("GuidingBoltManager"));
      }

      if (tag.m_128425_("PortalManager", 10)) {
         PortalManager.INSTANCE.deserializeNBT(tag.m_128469_("PortalManager"));
      }

      if (tag.m_128425_("PocketDimensionIdManager", 10)) {
         PocketDimensionManager.INSTANCE.deserializeNBT(tag.m_128469_("PocketDimensionIdManager"));
      }

      if (tag.m_128425_("SummonManager", 10)) {
         SummonManager.INSTANCE.deserializeNBT(tag.m_128469_("SummonManager"));
      }

      return new IronsDataStorage();
   }
}
