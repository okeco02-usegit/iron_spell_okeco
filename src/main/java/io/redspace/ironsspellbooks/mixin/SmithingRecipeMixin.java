package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTransformRecipe.class)
public class SmithingRecipeMixin {
   @Inject(method = "assemble", at = @At("RETURN"), cancellable = true)
   public void fixSpellbookSlotCount(Container pContainer, RegistryAccess pRegistryAccess, CallbackInfoReturnable<ItemStack> cir) {
      ItemStack result = (ItemStack)cir.getReturnValue();
      ItemStack input = pContainer.m_8020_(1);
      ISpellContainer defaultResultContainer = ISpellContainer.get(result.m_41720_().m_7968_());
      ISpellContainer baseContainer = ISpellContainer.get(input);
      if (defaultResultContainer != null && baseContainer != null) {
         ISpellContainerMutable mutable = defaultResultContainer.mutableCopy();

         for (SpellSlot slot : baseContainer.getActiveSpells()) {
            mutable.addSpellAtIndex(slot.getSpell(), slot.getLevel(), slot.index(), slot.isLocked());
         }

         ISpellContainer.set(result, mutable.toImmutable());
         cir.setReturnValue(result);
      } else if (defaultResultContainer != null) {
         ISpellContainer.set(result, defaultResultContainer);
      }
   }
}
