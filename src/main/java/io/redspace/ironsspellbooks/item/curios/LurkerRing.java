package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class LurkerRing extends PassiveAbilityCurio {
   public static final int COOLDOWN_IN_TICKS = 300;
   public static final float MULTIPLIER = 1.5F;

   public LurkerRing() {
      super(new Properties().m_41487_(1), Curios.RING_SLOT);
   }

   @Override
   public Component getDescription(ItemStack stack) {
      return Component.m_237113_(" ").m_7220_(Component.m_237110_(this.m_5524_() + ".desc", new Object[]{50})).m_130948_(this.descriptionStyle);
   }

   @Override
   protected int getCooldownTicks() {
      return 300;
   }

   @SubscribeEvent
   public static void handleAbility(LivingDamageEvent event) {
      LurkerRing RING = (LurkerRing)ItemRegistry.LURKER_RING.get();
      if (event.getSource().m_7639_() instanceof ServerPlayer attackingPlayer
         && attackingPlayer.m_20145_()
         && RING.isEquippedBy(attackingPlayer)
         && RING.tryProcCooldown(attackingPlayer)) {
         event.setAmount(event.getAmount() * 1.5F);
      }
   }
}
