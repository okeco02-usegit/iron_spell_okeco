package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncJsonConfigPacket implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<SyncJsonConfigPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sync_config")
   );
   public final Map<ResourceLocation, byte[]> data;

   public SyncJsonConfigPacket(Map<ResourceLocation, byte[]> bytes) {
      this.data = bytes;
   }

   public SyncJsonConfigPacket(FriendlyByteBuf buf) {
      this.data = new HashMap<>();
      int size = buf.readInt();

      for (int i = 0; i < size; i++) {
         ResourceLocation id = buf.m_130281_();
         byte[] bytes = new byte[buf.readInt()];
         buf.readBytes(bytes, 0, bytes.length);
         this.data.put(id, bytes);
      }
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeInt(this.data.size());

      for (Entry<ResourceLocation, byte[]> entry : this.data.entrySet()) {
         buf.m_130085_(entry.getKey());
         buf.writeInt(entry.getValue().length);
         buf.writeBytes(entry.getValue());
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      supplier.get().enqueueWork(() -> {
         for (AbstractSpell spell : SpellRegistry.REGISTRY.get()) {
            spell.resetRarityWeights();
         }

         SpellConfigManager.INSTANCE.handleClientSync(this);
      });
      return true;
   }
}
