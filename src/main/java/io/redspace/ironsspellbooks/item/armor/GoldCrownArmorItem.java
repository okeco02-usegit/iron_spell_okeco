package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.GoldCrownModel;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GoldCrownArmorItem extends ExtendedArmorItem {
   public GoldCrownArmorItem(Type slot, Properties settings) {
      super(
         ExtendedArmorMaterials.DEV,
         slot,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 9900.0, Operation.ADDITION),
         new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.9, Operation.MULTIPLY_BASE)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new GoldCrownModel());
   }
}
