package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.fluids.NoopFluid;
import io.redspace.ironsspellbooks.fluids.PotionClientFluidType;
import io.redspace.ironsspellbooks.fluids.PotionFluidType;
import io.redspace.ironsspellbooks.fluids.SimpleClientFluidType;
import io.redspace.ironsspellbooks.fluids.SimpleTintedClientFluidType;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidType.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class FluidRegistry {
   private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.f_256808_, "irons_spellbooks");
   private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, "irons_spellbooks");
   public static final RegistryObject<FluidType> BLOOD_TYPE = FLUID_TYPES.register("blood", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleClientFluidType(IronsSpellbooks.id("block/blood")));
      }
   });
   public static final RegistryObject<FluidType> TIMELESS_SLURRY_TYPE = FLUID_TYPES.register("timeless_slurry", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleClientFluidType(IronsSpellbooks.id("block/timeless_slurry")));
      }
   });
   public static final RegistryObject<FluidType> COMMON_INK_TYPE = FLUID_TYPES.register("common_ink", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), -14540254));
      }
   });
   public static final RegistryObject<FluidType> UNCOMMON_INK_TYPE = FLUID_TYPES.register("uncommon_ink", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), -15580416));
      }
   });
   public static final RegistryObject<FluidType> RARE_INK_TYPE = FLUID_TYPES.register("rare_ink", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), -15779772));
      }
   });
   public static final RegistryObject<FluidType> EPIC_INK_TYPE = FLUID_TYPES.register("epic_ink", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), -5951840));
      }
   });
   public static final RegistryObject<FluidType> LEGENDARY_INK_TYPE = FLUID_TYPES.register("legendary_ink", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), -217316));
      }
   });
   public static final RegistryObject<FluidType> POTION_FLUID_TYPE = FLUID_TYPES.register("potion", () -> new PotionFluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new PotionClientFluidType(ResourceLocation.withDefaultNamespace("block/water_still")));
      }
   });
   public static final RegistryObject<FluidType> OAKSKIN_ELIXIR_TYPE = FLUID_TYPES.register(
      "oakskin_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.OAKSKIN.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> GREATER_OAKSKIN_ELIXIR_TYPE = FLUID_TYPES.register(
      "greater_oakskin_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.OAKSKIN.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> EVASION_ELIXIR_TYPE = FLUID_TYPES.register(
      "evasion_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.EVASION.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> GREATER_EVASION_ELIXIR_TYPE = FLUID_TYPES.register(
      "greater_evasion_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.EVASION.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> INVISIBILITY_ELIXIR_TYPE = FLUID_TYPES.register(
      "invisibility_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> GREATER_INVISIBILITY_ELIXIR_TYPE = FLUID_TYPES.register(
      "greater_invisibility_elixir",
      () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(
               new SimpleTintedClientFluidType(
                  ResourceLocation.withDefaultNamespace("block/water_still"), ((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get()).m_19484_()
               )
            );
         }
      }
   );
   public static final RegistryObject<FluidType> GREATER_HEALING_ELIXIR_TYPE = FLUID_TYPES.register(
      "greater_healing_elixir", () -> new FluidType(Properties.create()) {
         public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.withDefaultNamespace("block/water_still"), MobEffects.f_19601_.m_19484_()));
         }
      }
   );
   public static final RegistryObject<FluidType> ICE_VENOM_TYPE = FLUID_TYPES.register("ice_venom", () -> new FluidType(Properties.create()) {
      public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
         consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), 7584442));
      }
   });
   public static final RegistryObject<Fluid> BLOOD = registerNoop("blood", BLOOD_TYPE);
   public static final RegistryObject<Fluid> COMMON_INK = registerNoop("common_ink", COMMON_INK_TYPE);
   public static final RegistryObject<Fluid> UNCOMMON_INK = registerNoop("uncommon_ink", UNCOMMON_INK_TYPE);
   public static final RegistryObject<Fluid> RARE_INK = registerNoop("rare_ink", RARE_INK_TYPE);
   public static final RegistryObject<Fluid> EPIC_INK = registerNoop("epic_ink", EPIC_INK_TYPE);
   public static final RegistryObject<Fluid> LEGENDARY_INK = registerNoop("legendary_ink", LEGENDARY_INK_TYPE);
   public static final RegistryObject<Fluid> POTION_FLUID = registerNoop("potion", POTION_FLUID_TYPE);
   public static final RegistryObject<Fluid> OAKSKIN_ELIXIR_FLUID = registerNoop("oakskin_elixir", OAKSKIN_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> GREATER_OAKSKIN_ELIXIR_FLUID = registerNoop("greater_oakskin_elixir", GREATER_OAKSKIN_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> INVISIBILITY_ELIXIR_FLUID = registerNoop("invisibility_elixir", INVISIBILITY_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> GREATER_INVISIBILITY_ELIXIR_FLUID = registerNoop("greater_invisibility_elixir", GREATER_INVISIBILITY_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> EVASION_ELIXIR_FLUID = registerNoop("evasion_elixir", EVASION_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> GREATER_EVASION_ELIXIR_FLUID = registerNoop("greater_evasion_elixir", GREATER_EVASION_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> GREATER_HEALING_ELIXIR_FLUID = registerNoop("greater_healing_elixir", GREATER_HEALING_ELIXIR_TYPE);
   public static final RegistryObject<Fluid> TIMELESS_SLURRY_FLUID = registerNoop("timeless_slurry", TIMELESS_SLURRY_TYPE);
   public static final RegistryObject<Fluid> ICE_VENOM_FLUID = registerNoop("ice_venom", ICE_VENOM_TYPE);

   public static void register(IEventBus eventBus) {
      FLUIDS.register(eventBus);
      FLUID_TYPES.register(eventBus);
   }

   private static RegistryObject<Fluid> registerNoop(String name, Supplier<FluidType> fluidType) {
      RegistryObject<Fluid> holder = RegistryObject.create(IronsSpellbooks.id(name), ForgeRegistries.FLUIDS);
      net.minecraftforge.fluids.ForgeFlowingFluid.Properties properties = new net.minecraftforge.fluids.ForgeFlowingFluid.Properties(fluidType, holder, holder)
         .bucket(() -> Items.f_41852_);
      FLUIDS.register(name, () -> new NoopFluid(properties));
      return holder;
   }
}
