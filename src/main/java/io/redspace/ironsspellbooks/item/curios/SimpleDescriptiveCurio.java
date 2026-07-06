package io.redspace.ironsspellbooks.item.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;

public class SimpleDescriptiveCurio extends CurioBaseItem {
   @Nullable
   final String slotIdentifier;
   Style descriptionStyle;
   boolean showHeader;

   public SimpleDescriptiveCurio(Properties properties, String slotIdentifier) {
      super(properties);
      this.slotIdentifier = slotIdentifier;
      this.showHeader = true;
      this.descriptionStyle = Style.f_131099_.m_131140_(ChatFormatting.YELLOW);
   }

   public SimpleDescriptiveCurio(Properties properties) {
      this(properties, null);
   }

   public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
      if (this.slotIdentifier != null) {
         MutableComponent title = Component.m_237115_("curios.modifiers." + this.slotIdentifier).m_130940_(ChatFormatting.GOLD);
         if (this.showHeader) {
            tooltips.add(Component.m_237119_());
            tooltips.add(title);
         }

         tooltips.addAll(this.getDescriptionLines(stack));
      }

      return super.getSlotsTooltip(tooltips, stack);
   }

   public List<Component> getDescriptionLines(ItemStack stack) {
      return List.of(this.getDescription(stack));
   }

   public Component getDescription(ItemStack stack) {
      return Component.m_237113_(" ").m_7220_(Component.m_237115_(this.m_5524_() + ".desc")).m_130948_(this.descriptionStyle);
   }
}
