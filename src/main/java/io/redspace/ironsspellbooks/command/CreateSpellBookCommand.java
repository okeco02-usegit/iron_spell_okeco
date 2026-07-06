package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class CreateSpellBookCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.create_spell_book.failed")
   );

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("createSpellBook").requires(p_138819_ -> p_138819_.m_6761_(2)))
            .then(
               ((RequiredArgumentBuilder)Commands.m_82129_("slots", IntegerArgumentType.integer(1, 20))
                     .executes(
                        commandContext -> crateSpellBook(
                           (CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "slots")
                        )
                     ))
                  .then(
                     Commands.m_82127_("randomize")
                        .executes(
                           commandContext -> crateRandomSpellBook(
                              (CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "slots")
                           )
                        )
                  )
            )
      );
   }

   private static int crateSpellBook(CommandSourceStack source, int slots) throws CommandSyntaxException {
      ServerPlayer serverPlayer = source.m_230896_();
      if (serverPlayer != null) {
         ItemStack itemstack = new ItemStack((ItemLike)ItemRegistry.WIMPY_SPELL_BOOK.get());
         ISpellContainer spellContainer = ISpellContainer.create(slots, true, true);
         ISpellContainer.set(itemstack, spellContainer);
         if (serverPlayer.m_150109_().m_36054_(itemstack)) {
            return 1;
         }
      }

      throw ERROR_FAILED.create();
   }

   private static int crateRandomSpellBook(CommandSourceStack source, int slots) throws CommandSyntaxException {
      ServerPlayer serverPlayer = source.m_230896_();
      if (serverPlayer != null) {
         ItemStack itemstack = new ItemStack((ItemLike)ItemRegistry.WIMPY_SPELL_BOOK.get());
         ISpellContainerMutable spellContainer = ISpellContainer.create(slots, true, true).mutableCopy();

         AbstractSpell spell;
         for (int i = 0; i < slots; i++) {
            do {
               spell = new SpellFilter().getRandomSpell(source.m_81372_().f_46441_);
            } while (!spellContainer.addSpell(spell, source.m_81372_().f_46441_.m_216332_(1, spell.getMaxLevel()), false));
         }

         ISpellContainer.set(itemstack, spellContainer.toImmutable());
         if (serverPlayer.m_150109_().m_36054_(itemstack)) {
            return 1;
         }
      }

      throw ERROR_FAILED.create();
   }
}
