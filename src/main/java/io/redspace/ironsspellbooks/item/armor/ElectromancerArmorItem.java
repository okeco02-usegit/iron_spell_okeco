package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.ElectromancerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ElectromancerArmorItem extends ImbuableChestplateArmorItem implements IArmorCapeProvider {
   public ElectromancerArmorItem(Type slot, Properties settings) {
      super(ExtendedArmorMaterials.ELECTROMANCER, slot, settings);
   }

   @Override
   public ResourceLocation getCapeResourceLocation() {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/models/armor/electromancer_cape.png");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new ElectromancerArmorModel());
   }
}
