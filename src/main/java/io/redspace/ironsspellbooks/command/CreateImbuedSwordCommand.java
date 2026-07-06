package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class CreateImbuedSwordCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
      Component.m_237115_("commands.irons_spellbooks.create_imbued_sword.failed")
   );
   private static final SuggestionProvider<CommandSourceStack> SWORD_SUGGESTIONS = (context, builder) -> {
      Set<ResourceLocation> resources = BuiltInRegistries.f_257033_
         .m_6579_()
         .stream()
         .filter(e -> e.getValue() instanceof SwordItem)
         .map(e -> ((ResourceKey)e.getKey()).m_135782_())
         .collect(Collectors.toSet());
      return SharedSuggestionProvider.m_82926_(resources, builder);
   };

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext context) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("createImbuedSword").requires(commandSourceStack -> commandSourceStack.m_6761_(2)))
            .then(
               Commands.m_82129_("item", ItemArgument.m_235279_(context))
                  .suggests(SWORD_SUGGESTIONS)
                  .then(
                     Commands.m_82129_("spell", SpellArgument.spellArgument())
                        .then(
                           Commands.m_82129_("level", IntegerArgumentType.integer(1))
                              .executes(
                                 ctx -> createImbuedSword(
                                    (CommandSourceStack)ctx.getSource(),
                                    (ItemInput)ctx.getArgument("item", ItemInput.class),
                                    (String)ctx.getArgument("spell", String.class),
                                    IntegerArgumentType.getInteger(ctx, "level")
                                 )
                              )
                        )
                  )
            )
      );
   }

   private static int createImbuedSword(CommandSourceStack source, ItemInput itemInput, String spell, int spellLevel) throws CommandSyntaxException {
      if (!spell.contains(":")) {
         spell = "irons_spellbooks:" + spell;
      }

      AbstractSpell abstractSpell = (AbstractSpell)SpellRegistry.REGISTRY.get().getValue(ResourceLocation.parse(spell));
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
         ItemStack itemstack = new ItemStack(itemInput.m_120979_());
         if (itemstack.m_41720_() instanceof SwordItem swordItem) {
            ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
            spellContainer.addSpell(abstractSpell, spellLevel, false);
            ISpellContainer.set(itemstack, spellContainer.toImmutable());
            if (serverPlayer.m_150109_().m_36054_(itemstack)) {
               return 1;
            }
         }
      }

      throw ERROR_FAILED.create();
   }
}
