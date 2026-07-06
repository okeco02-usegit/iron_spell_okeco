package io.redspace.ironsspellbooks.item.armor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.backwards_compat.CodecHelper;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record UpgradeOrbType(Holder<Attribute> attribute, double amount, Operation operation, Optional<ItemStack> containerItem) {
   private static final Codec<ItemStack> ITEM_OR_ITEMSTACK_CODEC = CodecHelper.withAlternative(
      ItemStack.f_41582_, BuiltInRegistries.f_257033_.m_206110_().xmap(ItemStack::new, ItemStack::m_220173_)
   );
   public static final Codec<UpgradeOrbType> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            BuiltInRegistries.f_256951_.m_206110_().fieldOf("attribute").forGetter(UpgradeOrbType::attribute),
            Codec.DOUBLE.fieldOf("amount").forGetter(UpgradeOrbType::amount),
            Codec.STRING.xmap(Operation::valueOf, Enum::name).fieldOf("operation").forGetter(UpgradeOrbType::operation),
            ITEM_OR_ITEMSTACK_CODEC.optionalFieldOf("containerItem").forGetter(UpgradeOrbType::containerItem)
         )
         .apply(builder, UpgradeOrbType::new)
   );

   public UpgradeOrbType(Holder<Attribute> attribute, double amount, Operation operation, Supplier<Item> container) {
      this(attribute, amount, operation, Optional.of(new ItemStack((ItemLike)container.get())));
   }

   public UpgradeOrbType(Supplier<Attribute> attribute, double amount, Operation operation, Supplier<Item> container) {
      this(BuiltInRegistries.f_256951_.m_263177_(attribute.get()), amount, operation, Optional.of(new ItemStack((ItemLike)container.get())));
   }

   public UpgradeOrbType(Supplier<Attribute> attribute, double amount, Operation operation) {
      this(BuiltInRegistries.f_256951_.m_263177_(attribute.get()), amount, operation, Optional.empty());
   }
}
