package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ClearCooldownCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> command = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("clearCooldowns")
                     .requires(p -> p.m_6761_(2)))
                  .executes(context -> clearCooldowns((CommandSourceStack)context.getSource(), null)))
               .then(Commands.m_82127_("all").executes(context -> clearCooldowns((CommandSourceStack)context.getSource(), null))))
            .then(
               Commands.m_82127_("player")
                  .then(
                     Commands.m_82129_("targets", EntityArgument.m_91470_())
                        .executes(context -> clearCooldowns((CommandSourceStack)context.getSource(), EntityArgument.m_91477_(context, "targets")))
                  )
            )
      );
   }

   private static int clearCooldowns(CommandSourceStack source, @Nullable Collection<ServerPlayer> targets) {
      if (targets != null && !targets.isEmpty()) {
         targets.forEach(serverPlayer -> {
            MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
            magicData.getPlayerCooldowns().clearCooldowns();
            magicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
         });
         if (!targets.isEmpty()) {
            source.m_288197_(() -> Component.m_237115_("commands.clearCooldown.success"), true);
         }

         return targets.size();
      } else {
         source.m_81377_().m_129785_().forEach(level -> level.m_8795_(player -> true).forEach(serverPlayer -> {
            MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
            magicData.getPlayerCooldowns().clearCooldowns();
            magicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
         }));
         source.m_288197_(() -> Component.m_237115_("commands.clearCooldown.success"), true);
         return 1;
      }
   }
}
