package io.redspace.ironsspellbooks.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class ISSDamageTypes {
   public static final ResourceKey<DamageType> FIRE_MAGIC = register("fire_magic");
   public static final ResourceKey<DamageType> ICE_MAGIC = register("ice_magic");
   public static final ResourceKey<DamageType> LIGHTNING_MAGIC = register("lightning_magic");
   public static final ResourceKey<DamageType> HOLY_MAGIC = register("holy_magic");
   public static final ResourceKey<DamageType> ENDER_MAGIC = register("ender_magic");
   public static final ResourceKey<DamageType> BLOOD_MAGIC = register("blood_magic");
   public static final ResourceKey<DamageType> EVOCATION_MAGIC = register("evocation_magic");
   public static final ResourceKey<DamageType> ELDRITCH_MAGIC = register("eldritch_magic");
   public static final ResourceKey<DamageType> NATURE_MAGIC = register("nature_magic");
   public static final ResourceKey<DamageType> CAULDRON = register("blood_cauldron");
   public static final ResourceKey<DamageType> HEARTSTOP = register("heartstop");
   public static final ResourceKey<DamageType> DRAGON_BREATH_POOL = register("dragon_breath_pool");
   public static final ResourceKey<DamageType> FIRE_FIELD = register("fire_field");
   public static final ResourceKey<DamageType> POISON_CLOUD = register("poison_cloud");

   public static ResourceKey<DamageType> register(String name) {
      return ResourceKey.m_135785_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
   }

   public static void bootstrap(BootstapContext<DamageType> context) {
      context.m_255272_(FIRE_MAGIC, new DamageType(FIRE_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(ICE_MAGIC, new DamageType(ICE_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(LIGHTNING_MAGIC, new DamageType(LIGHTNING_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(HOLY_MAGIC, new DamageType(HOLY_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(ENDER_MAGIC, new DamageType(ENDER_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(BLOOD_MAGIC, new DamageType(BLOOD_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(EVOCATION_MAGIC, new DamageType(EVOCATION_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(ELDRITCH_MAGIC, new DamageType(ELDRITCH_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(NATURE_MAGIC, new DamageType(NATURE_MAGIC.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(CAULDRON, new DamageType(CAULDRON.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(HEARTSTOP, new DamageType(HEARTSTOP.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(DRAGON_BREATH_POOL, new DamageType(DRAGON_BREATH_POOL.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(FIRE_FIELD, new DamageType(FIRE_FIELD.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
      context.m_255272_(POISON_CLOUD, new DamageType(POISON_CLOUD.m_135782_().m_135815_(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
   }
}
