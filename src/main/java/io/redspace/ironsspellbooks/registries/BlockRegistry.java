package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.TrialSpawnerBlock;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.trial_spawner.TrialSpawnerBlockEntity;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.VaultBlock;
import io.redspace.ironsspellbooks.api.backwards_compat.blocks.vault.data.VaultBlockEntity;
import io.redspace.ironsspellbooks.block.ArmorPileBlock;
import io.redspace.ironsspellbooks.block.BloodCauldronBlock;
import io.redspace.ironsspellbooks.block.BookStackBlock;
import io.redspace.ironsspellbooks.block.BrazierBlock;
import io.redspace.ironsspellbooks.block.FireflyJar;
import io.redspace.ironsspellbooks.block.VoidstoneBlock;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronBlock;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import io.redspace.ironsspellbooks.block.arcane_anvil.ArcaneAnvilBlock;
import io.redspace.ironsspellbooks.block.ice_spider_egg.IceSpiderEggBlock;
import io.redspace.ironsspellbooks.block.inscription_table.InscriptionTableBlock;
import io.redspace.ironsspellbooks.block.pedestal.PedestalBlock;
import io.redspace.ironsspellbooks.block.pedestal.PedestalTile;
import io.redspace.ironsspellbooks.block.portal_frame.PocketDimensionPortalFrameBlock;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlock;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeBlock;
import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeTile;
import java.util.Collection;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
   private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.f_256747_, "irons_spellbooks");
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.f_256922_, "irons_spellbooks");
   public static final RegistryObject<Block> INSCRIPTION_TABLE_BLOCK = BLOCKS.register("inscription_table", InscriptionTableBlock::new);
   public static final RegistryObject<Block> SCROLL_FORGE_BLOCK = BLOCKS.register("scroll_forge", ScrollForgeBlock::new);
   public static final RegistryObject<Block> PEDESTAL_BLOCK = BLOCKS.register("pedestal", PedestalBlock::new);
   public static final RegistryObject<Block> BLOOD_CAULDRON_BLOCK = BLOCKS.register("blood_cauldron", BloodCauldronBlock::new);
   public static final RegistryObject<Block> ARCANE_ANVIL_BLOCK = BLOCKS.register("arcane_anvil", ArcaneAnvilBlock::new);
   public static final RegistryObject<Block> ARMOR_PILE_BLOCK = BLOCKS.register("armor_pile", ArmorPileBlock::new);
   public static final RegistryObject<Block> ALCHEMIST_CAULDRON = BLOCKS.register("alchemist_cauldron", AlchemistCauldronBlock::new);
   public static final RegistryObject<Block> FIREFLY_JAR = BLOCKS.register("firefly_jar", FireflyJar::new);
   public static final RegistryObject<Block> PORTAL_FRAME = BLOCKS.register("portal_frame", () -> new PortalFrameBlock());
   public static final RegistryObject<Block> BRAZIER_FIRE = BLOCKS.register("brazier", () -> new BrazierBlock(false));
   public static final RegistryObject<Block> BRAZIER_SOUL = BLOCKS.register("brazier_soul", () -> new BrazierBlock(true));
   public static final RegistryObject<Block> CINDEROUS_KEYSTONE = BLOCKS.register(
      "cinderous_soul_rune",
      () -> new Block(
         Properties.m_284310_().m_60953_(state -> 15).m_278166_(PushReaction.BLOCK).m_60918_(SoundType.f_154677_).m_222994_().m_60913_(40.0F, 1200.0F)
      )
   );
   public static final RegistryObject<Block> MITHRIL_ORE = BLOCKS.register(
      "mithril_ore",
      () -> new Block(Properties.m_284310_().m_60953_(state -> 9).m_284268_(DyeColor.GRAY).m_60999_().m_60913_(20.0F, 1200.0F).m_60918_(SoundType.f_56726_))
   );
   public static final RegistryObject<Block> MITHRIL_ORE_DEEPSLATE = BLOCKS.register(
      "deepslate_mithril_ore",
      () -> new Block(Properties.m_284310_().m_60953_(state -> 9).m_284268_(DyeColor.GRAY).m_60999_().m_60913_(20.0F, 1200.0F).m_60918_(SoundType.f_56726_))
   );
   public static final RegistryObject<Block> ICE_SPIDER_EGG = BLOCKS.register(
      "ice_spider_egg", () -> new IceSpiderEggBlock(Properties.m_284310_().m_284268_(DyeColor.GRAY).m_60913_(3.0F, 1.0F).m_60955_())
   );
   public static final RegistryObject<Block> BOOK_STACK = BLOCKS.register("book_stack", BookStackBlock::new);
   public static final RegistryObject<Block> WISEWOOD_PLANKS = BLOCKS.register("wisewood_planks", () -> new Block(Properties.m_60926_(Blocks.f_50705_)));
   public static final RegistryObject<Block> WISEWOOD_BOOKSHELF = BLOCKS.register("wisewood_bookshelf", () -> new Block(Properties.m_60926_(Blocks.f_50078_)));
   public static final RegistryObject<Block> GRIMY_TILES = BLOCKS.register("grimy_tiles", () -> new Block(Properties.m_60926_(Blocks.f_152550_)));
   public static final RegistryObject<Block> NETHER_BRICK_PILLAR = BLOCKS.register(
      "nether_brick_pillar", () -> new RotatedPillarBlock(Properties.m_60926_(Blocks.f_50197_))
   );
   public static final RegistryObject<Block> VOIDSTONE = BLOCKS.register("voidstone", VoidstoneBlock::new);
   public static final RegistryObject<Block> POCKET_PORTAL_FRAME = BLOCKS.register("pocket_dimension_portal_frame", PocketDimensionPortalFrameBlock::new);
   public static final RegistryObject<Block> ARCANE_DEBRIS = BLOCKS.register(
      "arcane_debris", () -> new Block(Properties.m_284310_().m_284268_(DyeColor.WHITE).m_60999_().m_60913_(20.0F, 1200.0F).m_60918_(SoundType.f_56726_))
   );
   public static final RegistryObject<BlockEntityType<ScrollForgeTile>> SCROLL_FORGE_TILE = BLOCK_ENTITIES.register(
      "scroll_forge", () -> Builder.m_155273_(ScrollForgeTile::new, new Block[]{(Block)SCROLL_FORGE_BLOCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<PedestalTile>> PEDESTAL_TILE = BLOCK_ENTITIES.register(
      "pedestal", () -> Builder.m_155273_(PedestalTile::new, new Block[]{(Block)PEDESTAL_BLOCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<AlchemistCauldronTile>> ALCHEMIST_CAULDRON_TILE = BLOCK_ENTITIES.register(
      "alchemist_cauldron", () -> Builder.m_155273_(AlchemistCauldronTile::new, new Block[]{(Block)ALCHEMIST_CAULDRON.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<PortalFrameBlockEntity>> PORTAL_FRAME_BLOCK_ENTITY = BLOCK_ENTITIES.register(
      "portal_frame",
      () -> Builder.m_155273_(PortalFrameBlockEntity::new, new Block[]{(Block)PORTAL_FRAME.get(), (Block)POCKET_PORTAL_FRAME.get()}).m_58966_(null)
   );
   public static final RegistryObject<Block> TRIAL_SPAWNER = BLOCKS.register("trial_spawner", TrialSpawnerBlock::new);
   public static final RegistryObject<BlockEntityType<TrialSpawnerBlockEntity>> TRIAL_SPAWNER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
      "trial_spawner", () -> Builder.m_155273_(TrialSpawnerBlockEntity::new, new Block[]{(Block)TRIAL_SPAWNER.get()}).m_58966_(null)
   );
   public static final RegistryObject<Block> VAULT = BLOCKS.register("vault", VaultBlock::new);
   public static final RegistryObject<BlockEntityType<VaultBlockEntity>> VAULT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
      "vault", () -> Builder.m_155273_(VaultBlockEntity::new, new Block[]{(Block)VAULT.get()}).m_58966_(null)
   );

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
      BLOCK_ENTITIES.register(eventBus);
   }

   public static Collection<RegistryObject<Block>> blocks() {
      return BLOCKS.getEntries();
   }
}
