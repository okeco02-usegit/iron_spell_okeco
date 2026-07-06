package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;

public abstract class PassiveAbilityCurio extends SimpleDescriptiveCurio {
   public PassiveAbilityCurio(Properties properties, String slotIdentifier) {
      super(properties, slotIdentifier);
      this.descriptionStyle = Style.f_131099_.m_131140_(ChatFormatting.LIGHT_PURPLE);
   }

   protected abstract int getCooldownTicks();

   public boolean tryProcCooldown(Player player) {
      if (player.m_36335_().m_41519_(this)) {
         return false;
      }

      player.m_36335_().m_41524_(this, this.getCooldownTicks(player));
      return true;
   }

   public int getCooldownTicks(@Nullable LivingEntity livingEntity) {
      return Utils.applyCooldownReduction(this.getCooldownTicks(), livingEntity);
   }

   @Override
   public List<Component> getDescriptionLines(ItemStack stack) {
      return List.of(
         Component.m_237113_(" ")
            .m_7220_(
               Component.m_237110_(
                     "tooltip.irons_spellbooks.passive_ability",
                     new Object[]{
                        Component.m_237113_(Utils.timeFromTicks(this.getCooldownTicks(MinecraftInstanceHelper.getPlayer()), 1))
                           .m_130940_(ChatFormatting.LIGHT_PURPLE)
                     }
                  )
                  .m_130940_(ChatFormatting.DARK_PURPLE)
            ),
         this.getDescription(stack)
      );
   }
}
