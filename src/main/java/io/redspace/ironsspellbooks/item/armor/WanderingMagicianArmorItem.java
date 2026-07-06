package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.WanderingMagicianModel;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WanderingMagicianArmorItem extends ExtendedArmorItem {
   public WanderingMagicianArmorItem(Type slot, Properties settings) {
      super(ExtendedArmorMaterials.WANDERING_MAGICIAN, slot, settings, withManaAttribute(25));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new WanderingMagicianModel());
   }
}
