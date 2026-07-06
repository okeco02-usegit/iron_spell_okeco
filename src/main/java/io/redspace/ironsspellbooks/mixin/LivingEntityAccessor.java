package io.redspace.ironsspellbooks.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
   @Invoker("setLivingEntityFlag")
   void setLivingEntityFlagInvoker(int var1, boolean var2);
}
