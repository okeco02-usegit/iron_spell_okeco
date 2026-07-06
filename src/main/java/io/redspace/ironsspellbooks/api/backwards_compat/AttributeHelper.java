package io.redspace.ironsspellbooks.api.backwards_compat;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class AttributeHelper {
   public static UUID uuidFromId(ResourceLocation location) {
      return UUID.nameUUIDFromBytes(location.toString().getBytes());
   }
}
