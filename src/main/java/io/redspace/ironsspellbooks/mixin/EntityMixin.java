package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
   @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
   public void isAlliedTo(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      Entity self = (Entity)this;
      if (entity instanceof IMagicSummon summon && summon.getSummoner() != null) {
         cir.setReturnValue(self.m_7307_(summon.getSummoner()) || self.equals(summon.getSummoner()));
      }
   }

   @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
   public void isInvisibleTo(Player player, CallbackInfoReturnable<Boolean> cir) {
      if (((CurioBaseItem)ItemRegistry.INVISIBILITY_RING.get()).isEquippedBy(player)) {
         cir.setReturnValue(false);
      }
   }
}
