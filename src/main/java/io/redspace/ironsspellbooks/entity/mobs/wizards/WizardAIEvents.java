package io.redspace.ironsspellbooks.entity.mobs.wizards;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class WizardAIEvents {
   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if (event.getState().m_204336_(ModTags.GUARDED_BY_WIZARDS)) {
         angerNearbyWizards(event.getPlayer(), 3, false, true);
      }
   }

   @SubscribeEvent
   public static void onBlockUsed(RightClickBlock event) {
      BlockState blockstate = event.getLevel().m_8055_(event.getHitVec().m_82425_());
      if (blockstate.m_204336_(ModTags.GUARDED_BY_WIZARDS)
         && !(
            event.getLevel().m_7702_(event.getPos()) instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity
               && randomizableContainerBlockEntity.f_59605_ == null
         )) {
         angerNearbyWizards(event.getEntity(), 1, false, true);
      }
   }

   public static void angerNearbyWizards(Player player, int angerLevel, boolean requireLineOfSight, boolean blockRelated) {
      if (!player.m_150110_().f_35937_) {
         List<NeutralWizard> list = player.f_19853_.m_45976_(NeutralWizard.class, player.m_20191_().m_82400_(16.0));
         list.stream()
            .filter(neutralWizard -> (neutralWizard.guardsBlocks() || !blockRelated) && (!requireLineOfSight || BehaviorUtils.m_22667_(neutralWizard, player)))
            .forEach(neutralWizard -> {
               neutralWizard.increaseAngerLevel(player, angerLevel, true);
               neutralWizard.m_6925_(player.m_20148_());
               if (blockRelated && player instanceof ServerPlayer serverPlayer) {
                  Advancement advancement = serverPlayer.m_284548_().m_7654_().m_129889_().m_136041_(IronsSpellbooks.id("irons_spellbooks/steal_from_wizard"));
                  if (advancement != null) {
                     serverPlayer.m_8960_().m_135988_(advancement, "anger_wizard");
                  }
               }
            });
      }
   }
}
