package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.ClothingVariantHelper;
import io.redspace.ironsspellbooks.entity.armor.DyeableArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.GenericArmorModel;
import java.util.Map;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WizardArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket, DyeableLeatherItem {
   private static final String descIdHat = "item.irons_spellbooks.wizard_helmet.hat";
   private static final String descIdHood = "item.irons_spellbooks.wizard_helmet.hood";

   public WizardArmorItem(Type type, Properties settings) {
      super(ExtendedArmorMaterials.WIZARD, type, settings);
   }

   @NotNull
   public String m_5671_(ItemStack stack) {
      if (stack != null && stack.m_41720_() instanceof ArmorItem armorItem && armorItem.m_266204_() == Type.HELMET) {
         return ClothingVariantHelper.getClothingVariantOrElse(stack, "").equals("hat")
            ? "item.irons_spellbooks.wizard_helmet.hat"
            : "item.irons_spellbooks.wizard_helmet.hood";
      } else {
         return super.m_5671_(stack);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new DyeableArmorRenderer(new GenericArmorModel("wizard").variants(Map.of("hat", IronsSpellbooks.id("geo/wizard_armor_hat.geo.json"))));
   }
}
