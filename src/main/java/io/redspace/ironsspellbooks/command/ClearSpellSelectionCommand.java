package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.gui.overlays.SpellSelection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ClearSpellSelectionCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> command = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("clearSpellSelection").requires(p -> p.m_6761_(2)))
            .executes(context -> clearCooldowns((CommandSourceStack)context.getSource()))
      );
   }

   private static int clearCooldowns(CommandSourceStack source) {
      MagicData.getPlayerMagicData(source.m_230896_()).getSyncedData().setSpellSelection(new SpellSelection());
      source.m_288197_(() -> Component.m_237113_(String.format("Spell selection cleared for %s", source.m_230896_().toString())), true);
      return 1;
   }
}
