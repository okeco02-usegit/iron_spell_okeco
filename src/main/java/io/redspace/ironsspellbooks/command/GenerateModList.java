package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraftforge.fml.ModList;

public class GenerateModList {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.generate_mod_list.failed")
   );

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      if (pDispatcher.getRoot().getChild("modlist") == null) {
         pDispatcher.register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("modlist").requires(p_138819_ -> p_138819_.m_6761_(2)))
               .executes(commandContext -> generateModList((CommandSourceStack)commandContext.getSource()))
         );
      } else {
         IronsSpellbooks.LOGGER.debug("modlist already loaded.. skipping");
      }
   }

   private static int generateModList(CommandSourceStack source) throws CommandSyntaxException {
      StringBuilder sb = new StringBuilder();
      sb.append("mod_id");
      sb.append(",");
      sb.append("mod_name");
      sb.append(",");
      sb.append("mod_version");
      sb.append(",");
      sb.append("mod_file");
      sb.append(",");
      sb.append("mod_url");
      sb.append(",");
      sb.append("display_url");
      sb.append(",");
      sb.append("issue_tracker_url");
      sb.append("\n");
      ModList.get().getMods().forEach(iModInfo -> {
         sb.append(iModInfo.getModId());
         sb.append(",");
         sb.append(iModInfo.getDisplayName());
         sb.append(",");
         sb.append(iModInfo.getVersion());
         sb.append(",");
         sb.append(iModInfo.getOwningFile().getFile().getFileName());
         sb.append(",");
         iModInfo.getModURL().ifPresent(sb::append);
         sb.append(",");
         iModInfo.getConfig().getConfigElement(new String[]{"displayURL"}).ifPresent(sb::append);
         sb.append(",");
         iModInfo.getOwningFile().getConfig().getConfigElement(new String[]{"issueTrackerURL"}).ifPresent(sb::append);
         sb.append("\n");
      });

      try {
         File file = new File("modlist.txt");
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));
         writer.write(sb.toString());
         writer.close();
         Component component = Component.m_237113_(file.getName())
            .m_130940_(ChatFormatting.UNDERLINE)
            .m_130938_(style -> style.m_131142_(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
         source.m_288197_(() -> Component.m_237110_("commands.irons_spellbooks.generate_mod_list.success", new Object[]{component}), true);
         return 1;
      } catch (Exception e) {
         IronsSpellbooks.LOGGER.info(e.getMessage());
         throw ERROR_FAILED.create();
      }
   }
}
