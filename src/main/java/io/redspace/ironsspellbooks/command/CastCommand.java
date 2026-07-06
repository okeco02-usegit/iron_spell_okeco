package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CastCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> command = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("cast").requires(p -> p.m_6761_(2)))
            .then(
               Commands.m_82129_("casters", EntityArgument.m_91460_())
                  .then(
                     ((RequiredArgumentBuilder)Commands.m_82129_("spell", SpellArgument.spellArgument())
                           .executes(
                              context -> castSpell(
                                 (CommandSourceStack)context.getSource(),
                                 EntityArgument.m_91461_(context, "casters"),
                                 (String)context.getArgument("spell", String.class)
                              )
                           ))
                        .then(
                           Commands.m_82129_("level", IntegerArgumentType.integer(1))
                              .executes(
                                 context -> castSpell(
                                    (CommandSourceStack)context.getSource(),
                                    EntityArgument.m_91461_(context, "casters"),
                                    (String)context.getArgument("spell", String.class),
                                    IntegerArgumentType.getInteger(context, "level")
                                 )
                              )
                        )
                  )
            )
      );
   }

   private static int castSpell(CommandSourceStack source, Collection<? extends Entity> targets, String spellId) {
      return castSpell(source, targets, spellId, 1);
   }

   private static int castSpell(CommandSourceStack source, Collection<? extends Entity> targets, String spellId, int spellLevel) {
      if (!spellId.contains(":")) {
         spellId = "irons_spellbooks:" + spellId;
      }

      AbstractSpell spell = SpellRegistry.getSpell(spellId);

      for (Entity target : targets) {
         if (target instanceof ServerPlayer serverPlayer) {
            spell.attemptInitiateCast(ItemStack.f_41583_, spellLevel, source.m_81372_(), serverPlayer, CastSource.COMMAND, false, "command");
         } else if (target instanceof IMagicEntity castingMob) {
            castingMob.initiateCastSpell(spell, spellLevel);
         } else if (target instanceof LivingEntity livingEntity) {
            MagicData magicData = MagicData.getPlayerMagicData(livingEntity);
            if (!spell.checkPreCastConditions(source.m_81372_(), spellLevel, livingEntity, magicData)) {
               return 0;
            }

            spell.onCast(source.m_81372_(), spellLevel, livingEntity, CastSource.COMMAND, magicData);
            spell.onServerCastComplete(source.m_81372_(), spellLevel, livingEntity, magicData, false);
         }
      }

      return 1;
   }
}
