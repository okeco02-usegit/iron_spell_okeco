package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.backwards_compat.IBackwardsCompatDefaultNbtItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FurledMapCraftableItem extends FurledMapItem implements IBackwardsCompatDefaultNbtItem {
   final FurledMapItem.FurledMapData mapData;
   final boolean ancient;

   public FurledMapCraftableItem(boolean ancient, FurledMapItem.FurledMapData mapData) {
      this.ancient = ancient;
      this.mapData = mapData;
   }

   public String m_5524_() {
      return this.ancient ? ((Item)ItemRegistry.ANCIENT_FURLED_MAP.get()).m_5524_() : ((Item)ItemRegistry.FURLED_MAP.get()).m_5524_();
   }

   @Override
   public void setupItem(ItemStack stack) {
      FurledMapItem.FurledMapData.set(stack, this.mapData);
      this.mapData.descriptionOverride().ifPresent(desc -> FurledMapItem.FurledMapData.setLoreHelper(stack, desc));
   }
}
