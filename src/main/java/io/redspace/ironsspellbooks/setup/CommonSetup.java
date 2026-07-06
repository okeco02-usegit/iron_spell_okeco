package io.redspace.ironsspellbooks.setup;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import io.redspace.ironsspellbooks.entity.mobs.SummonedVex;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizard;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderEntity;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.necromancer.NecromancerEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.alchemist.ApothecaristEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.archevoker.ArchevokerEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cryomancer.CryomancerEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cultist.CultistEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand.CursedArmorStandEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.pyromancer.PyromancerEntity;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.entity.spells.spectral_hammer.SpectralHammer;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreEntity;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedRapierEntity;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordEntity;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import io.redspace.ironsspellbooks.entity.spells.wisp.WispEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent.Loading;
import net.minecraftforge.fml.event.config.ModConfigEvent.Reloading;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.MOD)
public class CommonSetup {
   @SubscribeEvent
   public static void onModConfigLoadingEvent(Loading event) {
      if (event.getConfig().getType() == Type.SERVER) {
         SpellRegistry.onConfigReload();
         ServerConfigs.onConfigReload();
      } else if (event.getConfig().getType() == Type.CLIENT) {
         ClientConfigs.onConfigReload();
      }
   }

   @SubscribeEvent
   public static void onModConfigReloadingEvent(Reloading event) {
      if (event.getConfig().getType() == Type.SERVER) {
         SpellRegistry.onConfigReload();
         ServerConfigs.onConfigReload();
      } else if (event.getConfig().getType() == Type.CLIENT) {
         ClientConfigs.onConfigReload();
      }
   }

   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)EntityRegistry.DEBUG_WIZARD.get(), DebugWizard.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.PYROMANCER.get(), PyromancerEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.NECROMANCER.get(), NecromancerEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SPECTRAL_STEED.get(), SummonedHorse.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.WISP.get(), WispEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SPECTRAL_HAMMER.get(), SpectralHammer.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_VEX.get(), SummonedVex.m_34040_().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_ZOMBIE.get(), SummonedZombie.m_34328_().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_SKELETON.get(), SummonedSkeleton.m_32166_().m_22265_());
      event.put((EntityType)EntityRegistry.FROZEN_HUMANOID.get(), FrozenHumanoid.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_POLAR_BEAR.get(), PolarBear.m_29560_().m_22265_());
      event.put((EntityType)EntityRegistry.DEAD_KING.get(), DeadKingBoss.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.DEAD_KING_CORPSE.get(), DeadKingBoss.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.CATACOMBS_ZOMBIE.get(), Zombie.m_34328_().m_22265_());
      event.put((EntityType)EntityRegistry.MAGEHUNTER_VINDICATOR.get(), Vindicator.m_34104_().m_22265_());
      event.put((EntityType)EntityRegistry.ARCHEVOKER.get(), ArchevokerEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.PRIEST.get(), PriestEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.KEEPER.get(), KeeperEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SCULK_TENTACLE.get(), VoidTentacle.m_21183_().m_22265_());
      event.put((EntityType)EntityRegistry.CRYOMANCER.get(), CryomancerEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.ROOT.get(), RootEntity.m_21183_().m_22265_());
      event.put((EntityType)EntityRegistry.FIREFLY_SWARM.get(), WispEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.APOTHECARIST.get(), ApothecaristEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.CULTIST.get(), CultistEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.FIRE_BOSS.get(), FireBossEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.CURSED_ARMOR_STAND.get(), CursedArmorStandEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_SWORD.get(), SummonedSwordEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_CLAYMORE.get(), SummonedClaymoreEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.SUMMONED_RAPIER.get(), SummonedRapierEntity.prepareAttributes().m_22265_());
      event.put((EntityType)EntityRegistry.ICE_SPIDER.get(), IceSpiderEntity.prepareAttributes().m_22265_());
   }

   @SubscribeEvent
   public static void spawnPlacements(SpawnPlacementRegisterEvent event) {
      event.register(
         (EntityType)EntityRegistry.NECROMANCER.get(),
         net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (type, serverLevelAccessor, spawnType, blockPos, random) -> Utils.checkMonsterSpawnRules(serverLevelAccessor, spawnType, blockPos, random),
         Operation.OR
      );
   }
}
