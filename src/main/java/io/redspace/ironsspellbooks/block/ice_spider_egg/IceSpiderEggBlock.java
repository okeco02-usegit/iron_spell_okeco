package io.redspace.ironsspellbooks.block.ice_spider_egg;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderEntity;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IceSpiderEggBlock extends Block {
   public static final BooleanProperty EGG_FROSTED = BooleanProperty.m_61465_("frosted");
   static final VoxelShape SHAPE = Block.m_49796_(3.0, 0.0, 2.0, 13.0, 14.0, 14.0);
   static final VoxelShape SHAPE_FROSTED = Block.m_49796_(2.0, 0.0, 1.0, 14.0, 15.0, 15.0);
   static int raycastCount = 0;

   public IceSpiderEggBlock(Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(EGG_FROSTED, false));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{EGG_FROSTED});
   }

   public void m_6240_(Level level, @NotNull Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      super.m_6240_(level, player, pos, state, blockEntity, tool);
      if (state.m_60713_((Block)BlockRegistry.ICE_SPIDER_EGG.get())) {
         boolean isFrosted = (Boolean)state.m_61143_(EGG_FROSTED);
         if (isFrosted && this.summonSpiderAround(player)) {
            IronsSpellbooks.LOGGER.debug("summonSpiderAround rcc: {}", raycastCount);
            level.m_7731_(pos, (BlockState)state.m_61124_(EGG_FROSTED, false), 2);
            level.m_220407_(GameEvent.f_157794_, pos, Context.m_223722_(state));
            level.m_46796_(2001, pos, Block.m_49956_(state));
         } else {
            level.m_46961_(pos, false);
         }
      }
   }

   @NotNull
   public VoxelShape m_5940_(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.m_61138_(EGG_FROSTED) && state.m_61143_(EGG_FROSTED) ? SHAPE_FROSTED : SHAPE;
   }

   public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
      return state.m_61138_(EGG_FROSTED) && state.m_61143_(EGG_FROSTED) ? SoundType.f_56744_ : SoundType.f_56751_;
   }

   private <T> void shuffle(T[] ary) {
      Random rand = new Random();

      for (int i = ary.length - 1; i > 0; i--) {
         int j = rand.nextInt(i + 1);
         T temp = ary[i];
         ary[i] = ary[j];
         ary[j] = temp;
      }
   }

   private boolean summonSpiderAround(Player player) {
      BlockPos center = player.m_20183_();
      Vec3 origin = player.m_20191_().m_82399_();
      Level level = player.f_19853_;
      int range = 24;
      IceSpiderEntity spider = new IceSpiderEntity(level);
      spider.m_21530_();
      Vec3[] probeDirections = new Vec3[]{
         new Vec3(1.0, 0.0, 0.0),
         new Vec3(-1.0, 0.0, 0.0),
         new Vec3(0.0, 0.0, 1.0),
         new Vec3(0.0, 0.0, -1.0),
         new Vec3(0.7, 0.0, 0.7),
         new Vec3(-0.7, 0.0, 0.7),
         new Vec3(-0.7, 0.0, -0.7),
         new Vec3(0.7, 0.0, -0.7)
      };
      this.shuffle(probeDirections);
      double stepLength = 5.0;
      raycastCount = 0;
      Vec3 farthest = origin;

      for (Vec3 initialProbe : probeDirections) {
         Vec3 initialCast = initialProbe.m_82490_(stepLength);
         BlockHitResult bhr = this.castRayTowardsEmptySpace(level, origin, origin.m_82549_(initialCast));
         Vec3 currentPosition = this.hoverAboveGround(level, bhr.m_82450_());
         int maxItr = 6;
         Vec3 bias = initialCast;

         for (int i = 0; i < maxItr; i++) {
            bhr = this.pickFarthestRayFromRadialCascade(level, currentPosition, bias, probeDirections, stepLength, 2.0);
            Vec3 node = bhr.m_82450_();
            if (bhr.m_6662_() != Type.MISS) {
               node = node.m_82546_(node.m_82546_(currentPosition).m_82541_());
            }

            node = this.hoverAboveGround(level, node);
            bias = node.m_82546_(currentPosition);
            currentPosition = node;
            if (currentPosition.m_82557_(origin) > range * range) {
               if (this.tryPlaceSpiderInWorld(spider, currentPosition, player)) {
                  return true;
               }
            } else if (currentPosition.m_82557_(origin) > farthest.m_82557_(origin) && this.tryMoveSpider(spider, currentPosition)) {
               farthest = currentPosition;
            }
         }
      }

      return this.tryPlaceSpiderInWorld(spider, farthest, player);
   }

   boolean tryMoveSpider(IceSpiderEntity spider, Vec3 pos) {
      Level level = spider.f_19853_;
      Vec3 originalPos = spider.m_20182_();
      pos = Utils.moveToRelativeGroundLevel(level, pos, 2);
      spider.m_20219_(pos);
      Vec3 adjustedPos = level.m_151418_(spider, Shapes.m_83064_(spider.m_20191_()), pos, 0.25, 0.25, 0.25).orElse(pos);
      spider.m_20219_(adjustedPos);
      AABB bb = spider.m_20191_();
      if (level.m_45772_(bb) && !level.m_46855_(bb)) {
         return true;
      }

      spider.m_20219_(originalPos);
      return false;
   }

   boolean tryPlaceSpiderInWorld(IceSpiderEntity spider, Vec3 pos, Player player) {
      Level level = spider.f_19853_;
      if (this.tryMoveSpider(spider, pos)) {
         level.m_5594_(null, spider.m_20183_(), (SoundEvent)SoundRegistry.ICE_SPIDER_HOWL.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
         spider.setEmergeFromGround();
         spider.m_6710_(player);
         spider.m_146922_(Utils.getAngle(pos.f_82479_, pos.f_82481_, player.m_20185_(), player.m_20189_()) * (180.0F / (float)Math.PI) + 90.0F);
         spider.m_5618_(spider.m_146908_());
         level.m_7967_(spider);
         return true;
      } else {
         return false;
      }
   }

   Vec3 hoverAboveGround(Level level, Vec3 vec3) {
      return Utils.moveToRelativeGroundLevel(level, vec3, 1, 12).m_82520_(0.0, 1.0, 0.0);
   }

   BlockHitResult pickFarthestRayFromRadialCascade(Level level, Vec3 origin, Vec3 bias, Vec3[] probeDirections, double stepLength, double randomness) {
      ArrayList<BlockHitResult> hits = new ArrayList<>(probeDirections.length);

      for (Vec3 dir : probeDirections) {
         hits.add(
            this.castRayTowardsEmptySpace(
               level, origin, origin.m_82549_(dir.m_82490_(stepLength)).m_82549_(bias.m_82490_(0.5)).m_82549_(Utils.getRandomVec3(randomness))
            )
         );
      }

      hits.sort(Comparator.comparingDouble(hit -> hit.m_82450_().m_82557_(origin)));
      return hits.get(hits.size() - 1);
   }

   BlockHitResult castRayTowardsEmptySpace(Level level, Vec3 start, Vec3 target) {
      raycastCount++;
      return level.m_45547_(new ClipContext(start, target, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.ANY, null));
   }
}
