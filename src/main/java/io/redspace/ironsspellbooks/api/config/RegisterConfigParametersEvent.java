package io.redspace.ironsspellbooks.api.config;

import java.util.function.Consumer;
import net.minecraftforge.eventbus.api.Event;

public class RegisterConfigParametersEvent extends Event {
   private final Consumer<SpellConfigParameter<?>> registrar;

   public RegisterConfigParametersEvent(Consumer<SpellConfigParameter<?>> registrar) {
      this.registrar = registrar;
   }

   public void register(SpellConfigParameter<?> parameterType) {
      this.registrar.accept(parameterType);
   }
}
