package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ClearRecastsCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> command = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("clearRecasts").requires(p -> p.m_6761_(2)))
               .then(Commands.m_82127_("all").executes(context -> clearRecast((CommandSourceStack)context.getSource(), null))))
            .then(
               Commands.m_82127_("player")
                  .then(
                     Commands.m_82129_("targets", EntityArgument.m_91470_())
                        .executes(context -> clearRecast((CommandSourceStack)context.getSource(), EntityArgument.m_91477_(context, "targets")))
                  )
            )
      );
   }

   private static int clearRecast(CommandSourceStack source, @Nullable Collection<ServerPlayer> targets) {
      if (targets != null && !targets.isEmpty()) {
         targets.forEach(ClearRecastsCommand::removeRecastForPlayer);
         if (!targets.isEmpty()) {
            source.m_288197_(() -> Component.m_237115_("commands.clearRecast.success"), true);
         }

         return targets.size();
      } else {
         source.m_81377_().m_129785_().forEach(level -> level.m_8795_(player -> true).forEach(ClearRecastsCommand::removeRecastForPlayer));
         source.m_288197_(() -> Component.m_237115_("commands.clearRecast.success"), true);
         return 1;
      }
   }

   private static void removeRecastForPlayer(ServerPlayer serverPlayer) {
      MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
      PlayerRecasts playerRecasts = magicData.getPlayerRecasts();
      playerRecasts.getAllRecasts().forEach(recastInstance -> playerRecasts.removeRecast(recastInstance, RecastResult.COMMAND));
   }
}
