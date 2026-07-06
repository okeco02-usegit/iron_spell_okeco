package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public class InvisibiltyRing extends SimpleDescriptiveCurio {
   public InvisibiltyRing() {
      super(ItemPropertiesHelper.equipment().m_41487_(1), Curios.RING_SLOT);
      this.descriptionStyle = Style.f_131099_.m_131140_(ChatFormatting.GRAY).m_131155_(true);
   }
}
