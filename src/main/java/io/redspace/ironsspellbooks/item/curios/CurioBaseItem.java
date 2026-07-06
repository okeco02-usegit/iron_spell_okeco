package io.redspace.ironsspellbooks.item.curios;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;

public class CurioBaseItem extends Item implements ICurioItem {
   String attributeSlot = "";
   Function<Integer, Multimap<Attribute, AttributeModifier>> attributes = null;

   public CurioBaseItem(Properties properties) {
      super(properties);
   }

   public boolean isEquippedBy(@Nullable LivingEntity entity) {
      return entity != null && CuriosApi.getCuriosInventory(entity).map(inv -> inv.findFirstCurio(this).isPresent()).orElse(false);
   }

   @NotNull
   public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
      return new SoundInfo(SoundEvents.f_11672_, 1.0F, 1.0F);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
      return slotContext.identifier().equals(this.attributeSlot)
         ? this.attributes.apply(slotContext.index())
         : super.getAttributeModifiers(slotContext, uuid, stack);
   }

   public CurioBaseItem withAttributes(String slot, AttributeContainer... attributes) {
      this.attributeSlot = slot;
      this.attributes = index -> {
         Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

         for (AttributeContainer holder : attributes) {
            String id = String.format("%s_%s", this.attributeSlot, index);
            builder.put(holder.attribute().get(), holder.createModifier(id));
         }

         return builder.build();
      };
      return this;
   }

   public CurioBaseItem withSpellbookAttributes(AttributeContainer... attributes) {
      return this.withAttributes(Curios.SPELLBOOK_SLOT, attributes);
   }
}
