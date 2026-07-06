package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.PyromancerArmorModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PyromancerArmorItem extends ImbuableChestplateArmorItem implements IArmorCapeProvider {
   public PyromancerArmorItem(Type slot, Properties settings) {
      super(ExtendedArmorMaterials.PYROMANCER, slot, settings);
   }

   @Override
   public ResourceLocation getCapeResourceLocation() {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/pyromancer_cape.png");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new PyromancerArmorModel());
   }
}
