package io.redspace.ironsspellbooks.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import java.io.File;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class IronsSpellbooksCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> command = (LiteralArgumentBuilder<CommandSourceStack>)Commands.m_82127_("ironsSpellbooks")
         .requires(p -> p.m_6761_(3));
      registerSummonCommandChain(command);
      registerUpgradeChain(command);
      registerInscriptionTableCommand(command);
      registerCameraShakeCommand(command);
      registerConfigCommands(command);
      dispatcher.register(command);
   }

   public static void registerSummonCommandChain(LiteralArgumentBuilder<CommandSourceStack> command) {
      command.then(
         Commands.m_82127_("summons")
            .then(
               Commands.m_82129_("target", EntityArgument.m_91460_())
                  .then(
                     Commands.m_82127_("setOwner").then(Commands.m_82129_("owner", EntityArgument.m_91449_()).executes(IronsSpellbooksCommand::summonSetOwner))
                  )
            )
      );
   }

   public static void registerUpgradeChain(LiteralArgumentBuilder<CommandSourceStack> command) {
      command.then(
         Commands.m_82127_("upgrade")
            .then(
               ((RequiredArgumentBuilder)Commands.m_82129_("type", ResourceKeyArgument.m_212386_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY))
                     .executes(IronsSpellbooksCommand::upgradeHeldItem))
                  .then(Commands.m_82129_("amount", IntegerArgumentType.integer(1)).executes(IronsSpellbooksCommand::upgradeHeldItem))
            )
      );
   }

   public static void registerInscriptionTableCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
      command.then(
         Commands.m_82127_("it")
            .executes(
               source -> ((CommandSourceStack)source.getSource())
                  .m_230896_()
                  .m_5893_(
                     new SimpleMenuProvider(
                        (i, inventory, player) -> new InscriptionTableMenu(i, inventory, ContainerLevelAccess.f_39287_),
                        Component.m_237115_("block.irons_spellbooks.inscription_table")
                     )
                  )
                  .orElse(0)
            )
      );
   }

   public static void registerCameraShakeCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
      command.then(
         Commands.m_82127_("camera_shake")
            .then(
               Commands.m_82129_("pos", Vec3Argument.m_120841_())
                  .then(
                     Commands.m_82129_("radius", DoubleArgumentType.doubleArg(0.0))
                        .then(Commands.m_82129_("ticks", IntegerArgumentType.integer(0)).executes(IronsSpellbooksCommand::createCameraShake))
                  )
            )
      );
   }

   private static int upgradeHeldItem(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
      int amount = 1;

      try {
         amount = IntegerArgumentType.getInteger(commandSourceStackCommandContext, "amount");
      } catch (Exception var6) {
      }

      ItemStack stack = ((CommandSourceStack)commandSourceStackCommandContext.getSource()).m_230896_().m_21205_();
      if (stack.m_41619_()) {
         throw new RuntimeException("empty item");
      }

      ResourceKey resourcekey = (ResourceKey)commandSourceStackCommandContext.getArgument("type", ResourceKey.class);
      String slot = UpgradeUtils.getRelevantEquipmentSlot(stack);

      for (int i = 0; i < amount; i++) {
         UpgradeData.set(
            stack,
            UpgradeData.getUpgradeData(stack)
               .addUpgrade(
                  stack,
                  (Holder<UpgradeOrbType>)UpgradeOrbTypeRegistry.upgradeTypeRegistry(
                        ((CommandSourceStack)commandSourceStackCommandContext.getSource()).m_5894_()
                     )
                     .m_203636_(resourcekey)
                     .get(),
                  slot
               )
         );
      }

      return amount;
   }

   private static int summonSetOwner(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
      Entity owner = EntityArgument.m_91452_(source, "owner");
      Collection<? extends Entity> targets = EntityArgument.m_91461_(source, "target");

      for (Entity entity : targets) {
         SummonManager.setOwner(entity, owner);
      }

      ((CommandSourceStack)source.getSource())
         .m_288197_(() -> Component.m_237113_(String.format("Set %s as owner for %s entities", owner.m_7755_().getString(), targets.size())), true);
      return targets.size();
   }

   private static int createCameraShake(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
      Vec3 pos = Vec3Argument.m_120844_(source, "pos");
      double radius = DoubleArgumentType.getDouble(source, "radius");
      int ticks = IntegerArgumentType.getInteger(source, "ticks");
      CameraShakeManager.addCameraShake(new CameraShakeData(((CommandSourceStack)source.getSource()).m_81372_(), ticks, pos, (float)radius));
      return ticks;
   }

   public static void registerConfigCommands(LiteralArgumentBuilder<CommandSourceStack> command) {
      command.then(Commands.m_82127_("convert_legacy_config").executes(LegacyConfigConverter::runCommand));
      command.then(
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("config")
                  .then(Commands.m_82127_("regenerate_example").executes(IronsSpellbooksCommand::regenerateExampleSpellConfigFile)))
               .then(
                  Commands.m_82127_("generate_file")
                     .then(
                        ((RequiredArgumentBuilder)Commands.m_82129_("spell", SpellArgument.spellArgument())
                              .then(
                                 ((LiteralArgumentBuilder)Commands.m_82127_("full").executes(c -> generateSpellConfigFile(c, true, false)))
                                    .then(Commands.m_82127_("override").executes(c -> generateSpellConfigFile(c, true, true)))
                              ))
                           .then(
                              ((LiteralArgumentBuilder)Commands.m_82127_("skeleton").executes(c -> generateSpellConfigFile(c, false, false)))
                                 .then(Commands.m_82127_("override").executes(c -> generateSpellConfigFile(c, false, true)))
                           )
                     )
               ))
            .then(Commands.m_82127_("list").executes(c -> {
               SpellConfigManager.ALL_TYPES.forEach(param -> ((CommandSourceStack)c.getSource()).m_243053_(Component.m_237113_(param.key().toString())));
               return 1;
            }))
      );
   }

   private static int regenerateExampleSpellConfigFile(CommandContext<CommandSourceStack> context) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      Pair<Boolean, File> result = SpellConfigManager.createExampleConfig(
         gson, SpellConfigManager.getSpellConfigDir().toPath().resolve("irons_spellbooks").resolve("example.txt").toFile()
      );
      if ((Boolean)result.getFirst()) {
         ((CommandSourceStack)context.getSource())
            .m_288197_(
               () -> Component.m_237110_(
                  "commands.irons_spellbooks.generic.create_file",
                  new Object[]{
                     Component.m_237113_(((File)result.getSecond()).getName())
                        .m_130948_(Style.f_131099_.m_131162_(true).m_131142_(new ClickEvent(Action.OPEN_FILE, ((File)result.getSecond()).getPath())))
                  }
               ),
               true
            );
         return 1;
      } else {
         ((CommandSourceStack)context.getSource()).m_81352_(Component.m_237115_("command.failed"));
         return 0;
      }
   }

   private static int generateSpellConfigFile(CommandContext<CommandSourceStack> context, boolean full, boolean override) {
      String spellid = (String)context.getArgument("spell", String.class);
      if (!spellid.contains(":")) {
         spellid = "irons_spellbooks:" + spellid;
      }

      CommandSourceStack source = (CommandSourceStack)context.getSource();
      AbstractSpell spell = SpellRegistry.getSpell(spellid);
      if (spell == SpellRegistry.none()) {
         source.m_81352_(Component.m_237110_("commands.irons_spellbooks.generic.unknown_spell", new Object[]{spellid}));
         return 0;
      } else {
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         Pair<Boolean, File> result = SpellConfigManager.generateSpellConfigFile(gson, spell, full, override);
         if ((Boolean)result.getFirst()) {
            source.m_288197_(
               () -> Component.m_237110_(
                  "commands.irons_spellbooks.generic.create_file",
                  new Object[]{
                     Component.m_237113_(((File)result.getSecond()).getName())
                        .m_130948_(Style.f_131099_.m_131162_(true).m_131142_(new ClickEvent(Action.OPEN_FILE, ((File)result.getSecond()).getPath())))
                  }
               ),
               true
            );
            return 1;
         } else if (result.getSecond() != null) {
            source.m_81352_(
               Component.m_237110_(
                  "commands.irons_spellbooks.config.cant_override",
                  new Object[]{Component.m_237113_(((File)result.getSecond()).getName()).m_130940_(ChatFormatting.UNDERLINE)}
               )
            );
            return 0;
         } else {
            source.m_81352_(Component.m_237115_("command.failed"));
            return 0;
         }
      }
   }
}
