package io.redspace.ironsspellbooks.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class ExtendedArmorItem extends ArmorItem implements GeoItem {
   private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
      UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
      UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
      UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
      UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
   };
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ExtendedArmorItem(IronsExtendedArmorMaterial material, Type type, Properties properties, AttributeContainer... attributes) {
      super(material, type, properties);
      this.defaultModifiers = platformHandleDefaultModifiers(material, type);
   }

   private static Multimap<Attribute, AttributeModifier> platformHandleDefaultModifiers(IronsExtendedArmorMaterial material, Type type) {
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      float defense = material.m_7366_(type);
      float toughness = material.m_6651_();
      float knockbackResistance = material.m_6649_();
      UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[type.m_266308_().m_20749_()];
      builder.put(Attributes.f_22284_, new AttributeModifier(uuid, "Armor modifier", defense, Operation.ADDITION));
      builder.put(Attributes.f_22285_, new AttributeModifier(uuid, "Armor toughness", toughness, Operation.ADDITION));
      if (knockbackResistance > 0.0F) {
         builder.put(Attributes.f_22278_, new AttributeModifier(uuid, "Armor knockback resistance", knockbackResistance, Operation.ADDITION));
      }

      for (Entry<Attribute, AttributeModifier> modifierEntry : material.getAdditionalAttributes().entrySet()) {
         AttributeModifier atr = modifierEntry.getValue();
         atr = new AttributeModifier(uuid, atr.m_22214_(), atr.m_22218_(), atr.m_22217_());
         builder.put(modifierEntry.getKey(), atr);
      }

      return builder.build();
   }

   public static AttributeContainer[] schoolAttributes(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 125.0, Operation.ADDITION),
         new AttributeContainer(school, 0.1, Operation.MULTIPLY_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.MULTIPLY_BASE)
      };
   }

   public static AttributeContainer[] withManaAttribute(int mana) {
      return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADDITION)};
   }

   public static AttributeContainer[] withManaAndSpellPowerAttribute(int mana, double spellPower) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADDITION),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.MULTIPLY_BASE)
      };
   }

   public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot pEquipmentSlot) {
      return (Multimap<Attribute, AttributeModifier>)(pEquipmentSlot == this.f_265916_.m_266308_() ? this.defaultModifiers : ImmutableMultimap.of());
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "controller", 20, this::predicate)});
   }

   private PlayState predicate(AnimationState<ExtendedArmorItem> extendedArmorItemAnimationState) {
      extendedArmorItemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private GeoArmorRenderer<?> renderer;

         @NotNull
         public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            if (this.renderer == null) {
               this.renderer = ExtendedArmorItem.this.supplyRenderer();
            }

            this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
            return this.renderer;
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   public abstract GeoArmorRenderer<?> supplyRenderer();
}
