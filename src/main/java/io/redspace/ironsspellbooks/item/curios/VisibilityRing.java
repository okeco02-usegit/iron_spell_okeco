package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class VisibilityRing extends SimpleDescriptiveCurio {
   public VisibilityRing() {
      super(ItemPropertiesHelper.equipment().m_41487_(1), Curios.RING_SLOT);
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      super.curioTick(slotContext, stack);
      slotContext.entity().m_21195_(MobEffects.f_19610_);
      slotContext.entity().m_21195_(MobEffects.f_216964_);
   }
}
