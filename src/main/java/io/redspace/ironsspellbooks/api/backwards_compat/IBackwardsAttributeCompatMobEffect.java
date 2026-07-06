package io.redspace.ironsspellbooks.api.backwards_compat;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public interface IBackwardsAttributeCompatMobEffect {
   default IBackwardsAttributeCompatMobEffect addAttributeModifier(Supplier<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
      return this.addAttributeModifier(attribute.get(), id, amount, operation);
   }

   default IBackwardsAttributeCompatMobEffect addAttributeModifier(Attribute attribute, ResourceLocation id, double amount, Operation operation) {
      this.cast().m_19472_(attribute, AttributeHelper.uuidFromId(id).toString(), amount, operation);
      return this;
   }

   default MobEffect cast() {
      return (MobEffect)this;
   }
}
