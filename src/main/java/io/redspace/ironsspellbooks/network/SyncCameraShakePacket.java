package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public record SyncCameraShakePacket(CameraShakeData data, boolean remove) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<SyncCameraShakePacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "sync_camera_shake")
   );

   public SyncCameraShakePacket(FriendlyByteBuf buf) {
      this(CameraShakeData.deserializeFromBuffer(buf), buf.readBoolean());
   }

   public void write(FriendlyByteBuf buf) {
      this.data.serializeToBuffer(buf);
      buf.writeBoolean(this.remove);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         if (this.remove) {
            CameraShakeManager.removeClientCameraShake(this.data);
         } else {
            CameraShakeManager.addClientCameraShake(this.data);
         }
      });
      return true;
   }
}
