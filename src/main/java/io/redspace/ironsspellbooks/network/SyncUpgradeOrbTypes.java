package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.backwards_compat.UpgradeTypeCache;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncUpgradeOrbTypes implements CustomPacketPayload {
   public final List<ResourceKey<UpgradeOrbType>> types;

   public SyncUpgradeOrbTypes(Collection<ResourceKey<UpgradeOrbType>> types) {
      this.types = new ArrayList<>();
      this.types.addAll(types);
   }

   public SyncUpgradeOrbTypes(FriendlyByteBuf pBuffer) {
      int i = pBuffer.readInt();
      this.types = new ArrayList<>();

      for (int j = 0; j < i; j++) {
         this.types.add(pBuffer.m_236801_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY));
      }
   }

   public void toBytes(FriendlyByteBuf pBuffer) {
      int i = this.types.size();
      pBuffer.writeInt(i);

      for (int j = 0; j < i; j++) {
         pBuffer.m_236858_(this.types.get(j));
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         try {
            UpgradeTypeCache.onClientLoad(Minecraft.m_91087_().f_91074_.f_19853_.m_9598_(), this.types);
         } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Failed to sync upgrade orb types: {}", e.getMessage());
         }
      });
      return true;
   }
}
