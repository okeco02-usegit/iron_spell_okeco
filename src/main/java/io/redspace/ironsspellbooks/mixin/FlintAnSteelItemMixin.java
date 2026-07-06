package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.block.BrazierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public class FlintAnSteelItemMixin {
   @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
   private void useOn(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
      Player player = pContext.m_43723_();
      Level level = pContext.m_43725_();
      BlockPos blockpos = pContext.m_8083_();
      BlockState blockstate = level.m_8055_(blockpos);
      if (blockstate.m_60734_() instanceof BrazierBlock
         && blockstate.m_61138_(BlockStateProperties.f_61362_)
         && blockstate.m_61138_(BlockStateProperties.f_61443_)
         && !(Boolean)blockstate.m_61143_(BlockStateProperties.f_61362_)
         && !(Boolean)blockstate.m_61143_(BlockStateProperties.f_61443_)) {
         level.m_5594_(player, blockpos, SoundEvents.f_11942_, SoundSource.BLOCKS, 1.0F, level.m_213780_().m_188501_() * 0.4F + 0.8F);
         level.m_7731_(blockpos, (BlockState)blockstate.m_61124_(BlockStateProperties.f_61443_, true), 11);
         level.m_142346_(player, GameEvent.f_157792_, blockpos);
         if (player != null) {
            pContext.m_43722_().m_41622_(1, player, p_41303_ -> p_41303_.m_21190_(pContext.m_43724_()));
         }

         cir.setReturnValue(InteractionResult.m_19078_(level.m_5776_()));
      }
   }
}
