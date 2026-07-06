package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.AttributeHelper;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public record AttributeContainer(Supplier<Attribute> attribute, double value, Operation operation) {
   public AttributeModifier createModifier(String slot) {
      Attribute attribute = this.attribute().get();
      String attributeName = attribute.m_22087_();
      ResourceLocation id = IronsSpellbooks.id(String.format("%s_%s_modifier", slot, attributeName));
      return new AttributeModifier(AttributeHelper.uuidFromId(id), id.toString(), this.value, this.operation);
   }
}
