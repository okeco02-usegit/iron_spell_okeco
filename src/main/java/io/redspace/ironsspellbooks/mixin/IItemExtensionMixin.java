package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgeItemStack.class, remap = false, priority = 0)
public interface IItemExtensionMixin {
   @Shadow
   ItemStack self();

   @Overwrite(remap = false)
   default boolean canElytraFly(LivingEntity entity) {
      return this.self().m_41720_().canElytraFly(this.self(), entity) || entity.m_21023_((MobEffect)MobEffectRegistry.ANGEL_WINGS.get());
   }

   @Overwrite(remap = false)
   default boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
      return this.self().m_41720_().elytraFlightTick(this.self(), entity, flightTicks) || entity.m_21023_((MobEffect)MobEffectRegistry.ANGEL_WINGS.get());
   }
}
