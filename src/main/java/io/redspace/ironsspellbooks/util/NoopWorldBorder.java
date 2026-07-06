package io.redspace.ironsspellbooks.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import org.jetbrains.annotations.NotNull;

public class NoopWorldBorder extends WorldBorder {
   public boolean m_187562_(double x, double z, double offset) {
      return true;
   }

   @NotNull
   public BlockPos m_187569_(double x, double y, double z) {
      return BlockPos.m_274561_(x, y, z);
   }

   public double m_61941_(double x, double z) {
      return 5.999997E7F;
   }

   public void m_61917_(double size) {
   }

   public void m_61949_(double x, double z) {
   }
}
