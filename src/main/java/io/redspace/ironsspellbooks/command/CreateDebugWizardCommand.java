package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizard;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

public class CreateDebugWizardCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.create_debug_wizard.failed")
   );
   private static final SimpleCommandExceptionType ERROR_FAILED_MAX_LEVEL = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.create_debug_wizard.failed_max_level")
   );

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("createDebugWizard").requires(commandSourceStack -> commandSourceStack.m_6761_(2)))
            .then(
               Commands.m_82129_("spell", SpellArgument.spellArgument())
                  .then(
                     Commands.m_82129_("spellLevel", IntegerArgumentType.integer(1))
                        .then(
                           Commands.m_82129_("targetsPlayer", BoolArgumentType.bool())
                              .then(
                                 Commands.m_82129_("cancelAfterTicks", IntegerArgumentType.integer(0))
                                    .executes(
                                       ctx -> createDebugWizard(
                                          (CommandSourceStack)ctx.getSource(),
                                          (String)ctx.getArgument("spell", String.class),
                                          IntegerArgumentType.getInteger(ctx, "spellLevel"),
                                          BoolArgumentType.getBool(ctx, "targetsPlayer"),
                                          IntegerArgumentType.getInteger(ctx, "cancelAfterTicks")
                                       )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int createDebugWizard(CommandSourceStack source, String spellId, int spellLevel, boolean targetsPlayer, int cancelAfterTicks) throws CommandSyntaxException {
      if (!spellId.contains(":")) {
         spellId = "irons_spellbooks:" + spellId;
      }

      AbstractSpell spell = SpellRegistry.getSpell(spellId);
      if (spellLevel > spell.getMaxLevel()) {
         throw new SimpleCommandExceptionType(
               Component.m_237110_("commands.irons_spellbooks.create_spell.failed_max_level", new Object[]{spell.getSpellName(), spell.getMaxLevel()})
            )
            .create();
      }

      ServerPlayer serverPlayer = source.m_230896_();
      if (serverPlayer != null) {
         DebugWizard debugWizard = new DebugWizard(
            (EntityType<? extends AbstractSpellCastingMob>)EntityRegistry.DEBUG_WIZARD.get(),
            serverPlayer.f_19853_,
            spell,
            spellLevel,
            targetsPlayer,
            cancelAfterTicks
         );
         debugWizard.m_146884_(serverPlayer.m_20182_());
         if (serverPlayer.f_19853_.m_7967_(debugWizard)) {
            return 1;
         }
      }

      throw ERROR_FAILED.create();
   }
}
