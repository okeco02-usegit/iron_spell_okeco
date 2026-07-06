package io.redspace.ironsspellbooks.api.backwards_compat;

import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload extends PacketHelper {
   record Type<T extends CustomPacketPayload>(ResourceLocation id) {
   }
}
