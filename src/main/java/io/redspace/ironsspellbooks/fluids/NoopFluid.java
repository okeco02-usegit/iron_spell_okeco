package io.redspace.ironsspellbooks.fluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;

public class NoopFluid extends ForgeFlowingFluid {
   public NoopFluid(Properties properties) {
      super(properties);
   }

   public Item m_6859_() {
      return Items.f_41852_;
   }

   protected BlockState m_5804_(FluidState state) {
      return Blocks.f_50016_.m_49966_();
   }

   public boolean m_7444_(FluidState p_207193_1_) {
      return true;
   }

   public int m_7430_(FluidState p_207192_1_) {
      return 0;
   }
}
