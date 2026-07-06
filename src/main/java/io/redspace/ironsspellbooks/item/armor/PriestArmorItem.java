package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorModel;
import io.redspace.ironsspellbooks.entity.armor.priest.PriestArmorRenderer;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PriestArmorItem extends ImbuableChestplateArmorItem {
   public PriestArmorItem(Type slot, Properties settings) {
      super(ExtendedArmorMaterials.PRIEST, slot, settings);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new PriestArmorRenderer(new PriestArmorModel());
   }
}
