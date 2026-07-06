package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.render.CinderousRarity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BetrayerSignetRingItem extends PassiveAbilityCurio {
   public static final int COOLDOWN_IN_TICKS = 100;

   public BetrayerSignetRingItem() {
      super(new Properties().m_41487_(1).m_41497_(CinderousRarity.CINDEROUS_RARITY).m_41486_(), Curios.RING_SLOT);
      this.showHeader = false;
   }

   @Override
   protected int getCooldownTicks() {
      return 100;
   }

   @SubscribeEvent
   public static void handleAbility(LivingDamageEvent event) {
      BetrayerSignetRingItem RING = (BetrayerSignetRingItem)ItemRegistry.SIGNET_OF_THE_BETRAYER.get();
      if (event.getSource().m_7639_() instanceof ServerPlayer attackingPlayer && RING.isEquippedBy(attackingPlayer)) {
         LivingEntity victim = event.getEntity();
         double victimMaxMana = victim.m_21133_((Attribute)AttributeRegistry.MAX_MANA.get());
         double victimBaseMana = victim.m_21172_((Attribute)AttributeRegistry.MAX_MANA.get());
         if (victimMaxMana > victimBaseMana && RING.tryProcCooldown(attackingPlayer)) {
            double manaAboveBase = victimMaxMana - victimBaseMana;
            double conversionRatioPer100 = 0.1;
            double totalExtraDamagePercent = 0.0;

            while (manaAboveBase > 0.0 && conversionRatioPer100 > 0.0) {
               double step = Mth.m_14008_(manaAboveBase, 0.0, 100.0) * 0.01;
               totalExtraDamagePercent += step * conversionRatioPer100;
               manaAboveBase -= 100.0;
               conversionRatioPer100 -= 0.01;
            }

            event.setAmount((float)(event.getAmount() * Math.max(1.0, 1.0 + totalExtraDamagePercent)));
         }
      }
   }
}
