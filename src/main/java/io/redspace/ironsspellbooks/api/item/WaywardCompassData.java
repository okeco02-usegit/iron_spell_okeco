package io.redspace.ironsspellbooks.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record WaywardCompassData(BlockPos blockPos) {
   public static final Codec<WaywardCompassData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(BlockPos.f_121852_.fieldOf("catacombs_pos").forGetter(WaywardCompassData::blockPos)).apply(builder, WaywardCompassData::new)
   );

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof WaywardCompassData waywardCompassData && waywardCompassData.blockPos.equals(this.blockPos);
   }

   @Override
   public int hashCode() {
      return this.blockPos.hashCode();
   }
}
