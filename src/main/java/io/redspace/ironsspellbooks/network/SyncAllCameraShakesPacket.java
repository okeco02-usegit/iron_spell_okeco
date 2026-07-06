package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncAllCameraShakesPacket implements CustomPacketPayload {
   ArrayList<CameraShakeData> cameraShakeData;

   public SyncAllCameraShakesPacket(ArrayList<CameraShakeData> cameraShakeData) {
      this.cameraShakeData = cameraShakeData;
   }

   public SyncAllCameraShakesPacket(FriendlyByteBuf buf) {
      this.cameraShakeData = new ArrayList<>();
      int i = buf.readInt();

      for (int j = 0; j < i; j++) {
         this.cameraShakeData.add(CameraShakeData.deserializeFromBuffer(buf));
      }
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.cameraShakeData.size());

      for (CameraShakeData data : this.cameraShakeData) {
         data.serializeToBuffer(buf);
      }
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         CameraShakeManager.cameraShakeData.clear();
         CameraShakeManager.cameraShakeData.addAll(this.cameraShakeData);
      });
      return true;
   }
}
