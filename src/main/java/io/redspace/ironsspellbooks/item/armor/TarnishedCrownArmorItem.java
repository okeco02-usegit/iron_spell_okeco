package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.TarnishedCrownModel;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class TarnishedCrownArmorItem extends ExtendedArmorItem implements IPresetSpellContainer {
   public TarnishedCrownArmorItem(Type slot, Properties settings) {
      super(
         ExtendedArmorMaterials.TARNISHED,
         slot,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADDITION),
         new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.25, Operation.MULTIPLY_BASE),
         new AttributeContainer(() -> Attributes.f_22281_, -0.15, Operation.MULTIPLY_BASE)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public GeoArmorRenderer<?> supplyRenderer() {
      return new GenericCustomArmorRenderer(new TarnishedCrownModel());
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, true);
            ISpellContainer.set(itemStack, spellContainer);
         }
      }
   }
}
