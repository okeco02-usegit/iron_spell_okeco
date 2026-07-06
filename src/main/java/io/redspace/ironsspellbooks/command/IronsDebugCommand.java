package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.item.ChronicleItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.server.command.EnumArgument;

public class IronsDebugCommand {
   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                                       "ironsDebug"
                                    )
                                    .requires(p_138819_ -> p_138819_.m_6761_(2)))
                                 .then(
                                    Commands.m_82129_("dataType", EnumArgument.enumArgument(IronsDebugCommand.IronsDebugCommandTypes.class))
                                       .executes(
                                          commandContext -> getDataForType(
                                             (CommandSourceStack)commandContext.getSource(),
                                             (IronsDebugCommand.IronsDebugCommandTypes)commandContext.getArgument(
                                                "dataType", IronsDebugCommand.IronsDebugCommandTypes.class
                                             )
                                          )
                                       )
                                 ))
                              .then(Commands.m_82127_("spellCount").executes(commandContext -> {
                                 int i = SpellRegistry.getEnabledSpells().size();
                                 ((CommandSourceStack)commandContext.getSource()).m_288197_(() -> Component.m_237113_(String.valueOf(i)), true);
                                 return i;
                              })))
                           .then(Commands.m_82127_("items").executes(commandContext -> {
                              if (((CommandSourceStack)commandContext.getSource()).m_230896_() != null) {
                                 ServerPlayer player = ((CommandSourceStack)commandContext.getSource()).m_230896_();
                                 player.m_150109_().m_36054_(new ItemStack((ItemLike)ItemRegistry.DEV_CROWN.get()));
                                 player.m_150109_().m_36054_(new ItemStack((ItemLike)ItemRegistry.NETHERITE_SPELL_BOOK.get()));
                                 player.m_150109_().m_36054_(new ItemStack((ItemLike)ItemRegistry.INSCRIPTION_TABLE_BLOCK_ITEM.get()));
                              }

                              return 1;
                           })))
                        .then(Commands.m_82127_("pocketDimension").then(Commands.m_82127_("clearId").executes(commandContext -> {
                           if (((CommandSourceStack)commandContext.getSource()).m_230896_() != null) {
                              ServerPlayer player = ((CommandSourceStack)commandContext.getSource()).m_230896_();
                              PocketDimensionManager.INSTANCE.remove(player.m_20148_());
                           }

                           return 1;
                        }))))
                     .then(Commands.m_82127_("rarityTest").executes(commandContext -> {
                        SpellRarity.rarityTest();
                        return 1;
                     })))
                  .then(Commands.m_82127_("claimSummon").then(Commands.m_82129_("target", EntityArgument.m_91449_()).executes(commandContext -> {
                     SummonManager.setOwner(EntityArgument.m_91452_(commandContext, "target"), ((CommandSourceStack)commandContext.getSource()).m_81374_());
                     return 1;
                  }))))
               .then(Commands.m_82127_("generateCreateRecipeCompat").executes(CreateRecipeCompatGenerator::run)))
            .then(Commands.m_82127_("clear_chronicle_cache").executes(cmd -> {
               ((ChronicleItem)ItemRegistry.THE_CHRONICLE.get()).clearCache();
               return 1;
            }))
      );
   }

   public static int getDataForType(CommandSourceStack source, IronsDebugCommand.IronsDebugCommandTypes ironsDebugCommandTypes) {
      switch (ironsDebugCommandTypes) {
         case RECASTING:
            getReacstingData(source);
         default:
            return 1;
      }
   }

   public static void getReacstingData(CommandSourceStack source) {
      ServerPlayer serverPlayer = source.m_230896_();
      MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
      writeResults(source, magicData.getPlayerRecasts().toString());
   }

   private static void writeResults(CommandSourceStack source, String results) {
      try {
         File file = new File("irons_debug.txt");
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));
         writer.write(results);
         writer.close();
         Component component = Component.m_237113_(file.getName())
            .m_130940_(ChatFormatting.UNDERLINE)
            .m_130938_(style -> style.m_131142_(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
         source.m_288197_(() -> Component.m_237110_("commands.irons_spellbooks.irons_debug_command.success", new Object[]{component}), true);
      } catch (Exception var5) {
      }
   }

   public enum IronsDebugCommandTypes {
      RECASTING;
   }
}
