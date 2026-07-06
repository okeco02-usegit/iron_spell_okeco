package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.command.SpellArgument;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class CommandArgumentRegistry {
   private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registries.f_256982_, "irons_spellbooks");
   private static final Supplier<SingletonArgumentInfo<SpellArgument>> SPELL_COMMAND_ARGUMENT_TYPE = ARGUMENT_TYPES.register(
      "spell",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(SpellArgument.class, SingletonArgumentInfo.m_235451_(SpellArgument::spellArgument))
   );

   public static void register(IEventBus modEventBus) {
      ARGUMENT_TYPES.register(modEventBus);
   }
}
