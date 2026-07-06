package io.redspace.ironsspellbooks.block;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEvent;

public class BloodCauldronBlock extends LayeredCauldronBlock {
   public BloodCauldronBlock() {
      super(Properties.m_60926_(Blocks.f_50256_), p -> false, getInteractionMap());
   }

   public void m_7892_(BlockState blockState, Level level, BlockPos pos, Entity entity) {
      if (entity.f_19797_ % 20 == 0) {
         attemptCookEntity(blockState, level, pos, entity, () -> {
            level.m_46597_(pos, (BlockState)blockState.m_61122_(LayeredCauldronBlock.f_153514_));
            level.m_142346_(null, GameEvent.f_157769_, pos);
         });
      }

      super.m_7892_(blockState, level, pos, entity);
   }

   public static void attemptCookEntity(BlockState blockState, Level level, BlockPos pos, Entity entity, BloodCauldronBlock.CookExecution execution) {
      if (!level.f_46443_) {
         if (CampfireBlock.m_51319_(level.m_8055_(pos.m_7495_()))) {
            if (level.m_8055_(pos).m_60734_() instanceof AbstractCauldronBlock cauldron) {
               if (entity instanceof LivingEntity livingEntity
                  && livingEntity.m_20191_().m_82381_(cauldron.m_49966_().m_60820_(level, pos).m_83215_().m_82338_(pos))
                  && livingEntity.m_6469_(DamageSources.get(level, ISSDamageTypes.CAULDRON), 2.0F)
                  && !livingEntity.m_6095_().m_204039_(ModTags.CANT_PRODUCE_BLOOD)) {
                  MagicManager.spawnParticles(
                     level,
                     ParticleHelper.BLOOD,
                     entity.m_20185_(),
                     entity.m_20186_() + entity.m_20206_() / 2.0F,
                     entity.m_20189_(),
                     20,
                     0.05,
                     0.05,
                     0.05,
                     0.1,
                     false
                  );
                  if (Utils.random.m_188500_() <= 0.5 && !isCauldronFull(blockState)) {
                     execution.execute();
                  }
               }
            }
         }
      }
   }

   private static boolean isCauldronFull(BlockState blockState) {
      return !blockState.m_61138_(f_153514_) ? false : (Integer)blockState.m_61143_(LayeredCauldronBlock.f_153514_) == 3;
   }

   public static Map<Item, CauldronInteraction> getInteractionMap() {
      Object2ObjectOpenHashMap<Item, CauldronInteraction> map = CauldronInteraction.m_175617_();
      map.put(Items.f_42590_, (CauldronInteraction)(blockState, level, blockPos, player, hand, itemStack) -> {
         if (!level.f_46443_) {
            Item item = itemStack.m_41720_();
            player.m_21008_(hand, ItemUtils.m_41813_(itemStack, player, new ItemStack((ItemLike)ItemRegistry.BLOOD_VIAL.get())));
            player.m_36220_(Stats.f_12944_);
            player.m_36246_(Stats.f_12982_.m_12902_(item));
            LayeredCauldronBlock.m_153559_(blockState, level, blockPos);
            level.m_5594_(null, blockPos, SoundEvents.f_11770_, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.m_142346_(null, GameEvent.f_157816_, blockPos);
         }

         return InteractionResult.m_19078_(level.f_46443_);
      });
      return map;
   }

   @Deprecated(forRemoval = true)
   public interface CookExecution {
      void execute();
   }
}
