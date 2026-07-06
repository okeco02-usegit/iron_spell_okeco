package io.redspace.ironsspellbooks.item.consumables;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SimpleElixir extends DrinkableItem {
   private final Supplier<MobEffectInstance> potionEffect;
   boolean foilOverride;

   public SimpleElixir(Properties pProperties, Supplier<MobEffectInstance> potionEffect) {
      super(pProperties, SimpleElixir::applyEffect, Items.f_42590_, true);
      this.potionEffect = potionEffect;
   }

   public SimpleElixir(Properties pProperties, Supplier<MobEffectInstance> potionEffect, boolean foil) {
      this(pProperties, potionEffect);
      this.foilOverride = foil;
   }

   public MobEffectInstance getMobEffect() {
      return this.potionEffect.get();
   }

   private static void applyEffect(ItemStack itemStack, LivingEntity livingEntity) {
      if (itemStack.m_41720_() instanceof SimpleElixir elixir && elixir.potionEffect.get() != null) {
         livingEntity.m_7292_(elixir.potionEffect.get());
      }
   }

   public boolean m_5812_(ItemStack pStack) {
      return super.m_5812_(pStack) || this.foilOverride;
   }

   @Override
   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      addPotionTooltip(this.potionEffect.get(), pTooltipComponents, 1.0F);
      if (this.potionEffect.get().m_19544_() instanceof CustomDescriptionMobEffect customDescriptionMobEffect) {
         CustomDescriptionMobEffect.handleCustomPotionTooltip(pStack, pTooltipComponents, false, this.potionEffect.get(), customDescriptionMobEffect);
      }
   }

   public static void addPotionTooltip(MobEffectInstance mobeffectinstance, List<Component> pTooltips, float pDurationFactor) {
      List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
      MutableComponent mutablecomponent = Component.m_237115_(mobeffectinstance.m_19576_());
      MobEffect mobeffect = mobeffectinstance.m_19544_();
      Map<Attribute, AttributeModifier> map = mobeffect.m_19485_();
      if (!map.isEmpty()) {
         for (Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
            AttributeModifier attributemodifier = entry.getValue();
            AttributeModifier attributemodifier1 = new AttributeModifier(
               attributemodifier.m_22214_(), mobeffect.m_7048_(mobeffectinstance.m_19564_(), attributemodifier), attributemodifier.m_22217_()
            );
            list1.add(new Pair(entry.getKey(), attributemodifier1));
         }
      }

      if (mobeffectinstance.m_19564_() > 0) {
         mutablecomponent = Component.m_237110_(
            "potion.withAmplifier", new Object[]{mutablecomponent, Component.m_237115_("potion.potency." + mobeffectinstance.m_19564_())}
         );
      }

      if (mobeffectinstance.m_19557_() > 20) {
         mutablecomponent = Component.m_237110_(
            "potion.withDuration", new Object[]{mutablecomponent, MobEffectUtil.m_267641_(mobeffectinstance, pDurationFactor)}
         );
      }

      pTooltips.add(mutablecomponent.m_130940_(mobeffect.m_19483_().m_19497_()));
      if (!list1.isEmpty()) {
         pTooltips.add(CommonComponents.f_237098_);
         pTooltips.add(Component.m_237115_("potion.whenDrank").m_130940_(ChatFormatting.DARK_PURPLE));

         for (Pair<Attribute, AttributeModifier> pair : list1) {
            AttributeModifier attributemodifier2 = (AttributeModifier)pair.getSecond();
            double d0 = attributemodifier2.m_22218_();
            double d1;
            if (attributemodifier2.m_22217_() != Operation.MULTIPLY_BASE && attributemodifier2.m_22217_() != Operation.MULTIPLY_TOTAL) {
               d1 = attributemodifier2.m_22218_();
            } else {
               d1 = attributemodifier2.m_22218_() * 100.0;
            }

            if (d0 > 0.0) {
               pTooltips.add(
                  Component.m_237110_(
                        "attribute.modifier.plus." + attributemodifier2.m_22217_().m_22235_(),
                        new Object[]{ItemStack.f_41584_.format(d1), Component.m_237115_(((Attribute)pair.getFirst()).m_22087_())}
                     )
                     .m_130940_(ChatFormatting.BLUE)
               );
            } else if (d0 < 0.0) {
               d1 *= -1.0;
               pTooltips.add(
                  Component.m_237110_(
                        "attribute.modifier.take." + attributemodifier2.m_22217_().m_22235_(),
                        new Object[]{ItemStack.f_41584_.format(d1), Component.m_237115_(((Attribute)pair.getFirst()).m_22087_())}
                     )
                     .m_130940_(ChatFormatting.RED)
               );
            }
         }
      }
   }
}
