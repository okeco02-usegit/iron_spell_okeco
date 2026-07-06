package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.ImmolateEffect;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.InfernalSorcererArmorModel;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class InfernalSorcererArmorItem extends ImbuableChestplateArmorItem {
   public static final int COOLDOWN_TICKS = 20;

   public InfernalSorcererArmorItem(Type type, Properties settings) {
      super(
         ExtendedArmorMaterials.INFERNAL_SORCERER,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADDITION),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.MULTIPLY_BASE)
      );
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pLevel, tooltipComponents, pIsAdvanced);
      tooltipComponents.add(
         Component.m_237110_(
               "tooltip.irons_spellbooks.passive_ability_no_cooldown",
               new Object[]{
                  Component.m_237113_(Utils.timeFromTicks(Utils.applyCooldownReduction(20, MinecraftInstanceHelper.getPlayer()), 1))
                     .m_130940_(ChatFormatting.AQUA)
               }
            )
            .m_130940_(ChatFormatting.DARK_PURPLE)
      );
      tooltipComponents.add(Component.m_237113_(" ").m_7220_(Component.m_237115_(this.m_5524_() + ".desc")).m_130940_(ChatFormatting.LIGHT_PURPLE));
      tooltipComponents.add(
         Component.m_237113_(" ")
            .m_7220_(
               Component.m_237110_(
                  this.m_5524_() + ".immolate.desc",
                  new Object[]{
                     Component.m_237113_(Utils.stringTruncation(ImmolateEffect.damageFor(MinecraftInstanceHelper.getPlayer()), 1))
                        .m_130940_(ChatFormatting.RED)
                  }
               )
            )
            .m_130940_(ChatFormatting.LIGHT_PURPLE)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new InfernalSorcererArmorModel());
   }
}
