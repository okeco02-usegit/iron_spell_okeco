package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ManaCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> command = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("mana")
                     .requires(p -> p.m_6761_(2)))
                  .then(
                     Commands.m_82127_("set")
                        .then(
                           Commands.m_82129_("targets", EntityArgument.m_91470_())
                              .then(
                                 Commands.m_82129_("amount", IntegerArgumentType.integer())
                                    .executes(
                                       context -> changeMana(
                                          (CommandSourceStack)context.getSource(),
                                          EntityArgument.m_91477_(context, "targets"),
                                          IntegerArgumentType.getInteger(context, "amount"),
                                          true
                                       )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.m_82127_("add")
                     .then(
                        Commands.m_82129_("targets", EntityArgument.m_91470_())
                           .then(
                              Commands.m_82129_("amount", IntegerArgumentType.integer())
                                 .executes(
                                    context -> changeMana(
                                       (CommandSourceStack)context.getSource(),
                                       EntityArgument.m_91477_(context, "targets"),
                                       IntegerArgumentType.getInteger(context, "amount"),
                                       false
                                    )
                                 )
                           )
                     )
               ))
            .then(
               Commands.m_82127_("get")
                  .then(
                     Commands.m_82129_("targets", EntityArgument.m_91466_())
                        .executes(context -> getMana((CommandSourceStack)context.getSource(), EntityArgument.m_91474_(context, "targets")))
                  )
            )
      );
   }

   private static int changeMana(CommandSourceStack source, Collection<ServerPlayer> targets, int amount, boolean set) {
      targets.forEach(serverPlayer -> {
         MagicData pmg = MagicData.getPlayerMagicData(serverPlayer);
         float base = set ? 0.0F : pmg.getMana();
         pmg.setMana(amount + base);
         PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(pmg));
      });
      String s = set ? "set" : "add";
      if (targets.size() == 1) {
         source.m_288197_(() -> Component.m_237110_("commands.mana." + s + ".success.single", new Object[]{amount, targets.iterator().next().m_5446_()}), true);
      } else {
         source.m_288197_(() -> Component.m_237110_("commands.mana." + s + ".success.multiple", new Object[]{amount, targets.size()}), true);
      }

      return targets.size();
   }

   private static int getMana(CommandSourceStack source, ServerPlayer serverPlayer) {
      MagicData pmg = MagicData.getPlayerMagicData(serverPlayer);
      int mana = (int)pmg.getMana();
      source.m_288197_(() -> Component.m_237110_("commands.mana.get.success", new Object[]{serverPlayer.m_5446_(), mana}), true);
      return mana;
   }
}
