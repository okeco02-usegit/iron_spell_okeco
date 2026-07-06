package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.render.AffinityRingRenderer;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class AffinityRing extends CurioBaseItem {
   public AffinityRing(Properties properties) {
      super(properties);
   }

   public void m_7373_(ItemStack pStack, Level context, List<Component> tooltip, TooltipFlag pIsAdvanced) {
      AffinityData affinity = AffinityData.getAffinityData(pStack);
      if (affinity != AffinityData.NONE && !affinity.affinityData().isEmpty()) {
         tooltip.add(Component.m_237119_());
         tooltip.add(Component.m_237115_("curios.modifiers.ring").m_130940_(ChatFormatting.GOLD));
         tooltip.addAll(affinity.getDescriptionComponent());
      } else {
         tooltip.add(
            Component.m_237115_("tooltip.irons_spellbooks.empty_affinity_ring").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC})
         );
      }
   }

   public Component m_7626_(ItemStack pStack) {
      return Component.m_237110_(this.m_5671_(pStack), new Object[]{AffinityData.getAffinityData(pStack).getNameForItem()});
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new AffinityRing.ClientExtension());
   }

   public static class ClientExtension implements IClientItemExtensions {
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
         return new AffinityRingRenderer(Minecraft.m_91087_().m_91291_(), Minecraft.m_91087_().m_167973_());
      }
   }
}
