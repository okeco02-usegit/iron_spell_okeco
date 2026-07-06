package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.ShadowwalkerArmorModel;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ShadowwalkerArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
   public ShadowwalkerArmorItem(Type slot, Properties settings) {
      super(ExtendedArmorMaterials.SHADOWWALKER, slot, settings);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new ShadowwalkerArmorModel());
   }
}
