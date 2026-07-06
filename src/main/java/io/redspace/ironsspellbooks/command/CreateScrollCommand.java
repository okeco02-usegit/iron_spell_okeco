package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class CreateScrollCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.create_scroll.failed")
   );

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("createScroll").requires(p_138819_ -> p_138819_.m_6761_(2)))
            .then(
               Commands.m_82129_("spell", SpellArgument.spellArgument())
                  .then(
                     Commands.m_82129_("level", IntegerArgumentType.integer(1))
                        .executes(
                           commandContext -> createScroll(
                              (CommandSourceStack)commandContext.getSource(),
                              (String)commandContext.getArgument("spell", String.class),
                              IntegerArgumentType.getInteger(commandContext, "level")
                           )
                        )
                  )
            )
      );
   }

   private static int createScroll(CommandSourceStack source, String spell, int spellLevel) throws CommandSyntaxException {
      if (!spell.contains(":")) {
         spell = "irons_spellbooks:" + spell;
      }

      AbstractSpell abstractSpell = (AbstractSpell)SpellRegistry.REGISTRY.get().getValue(ResourceLocation.parse(spell));
      if (abstractSpell == null || abstractSpell == SpellRegistry.none()) {
         throw ERROR_FAILED.create();
      }

      if (spellLevel > abstractSpell.getMaxLevel()) {
         throw new SimpleCommandExceptionType(
               Component.m_237110_(
                  "commands.irons_spellbooks.create_spell.failed_max_level", new Object[]{abstractSpell.getSpellName(), abstractSpell.getMaxLevel()}
               )
            )
            .create();
      }

      ServerPlayer serverPlayer = source.m_230896_();
      if (serverPlayer != null) {
         ItemStack itemStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
         ISpellContainer.createScrollContainer(abstractSpell, spellLevel, itemStack);
         if (serverPlayer.m_150109_().m_36054_(itemStack)) {
            return 1;
         }
      }

      throw ERROR_FAILED.create();
   }
}
