package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.FallingBlockParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpectralHammer extends LivingEntity implements GeoEntity {
   private final int ticksToLive = 30;
   private final int doDamageTick = 13;
   private final int doAnimateTick = 20;
   private int depth = 0;
   private int radius = 0;
   private boolean didDamage = false;
   private boolean didAnimate = false;
   private int ticksAlive = 0;
   private boolean playSwingAnimation = true;
   private BlockHitResult blockHitResult;
   private float damageAmount;
   private Player owner;
   private final RawAnimation animationBuilder = RawAnimation.begin().thenPlay("hammer_swing");
   private final AnimationController animationController = new AnimationController(this, "controller", 0, this::predicate);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SpectralHammer(EntityType<? extends SpectralHammer> entityType, Level level) {
      super(entityType, level);
      this.m_20242_(true);
      this.m_20331_(true);
   }

   public SpectralHammer(Level levelIn, LivingEntity owner, BlockHitResult blockHitResult, int depth, int radius) {
      this((EntityType<? extends SpectralHammer>)EntityRegistry.SPECTRAL_HAMMER.get(), levelIn);
      if (owner instanceof Player player) {
         this.owner = player;
      }

      this.blockHitResult = blockHitResult;
      this.depth = depth;
      this.radius = radius;
      int xRot = blockHitResult.m_82434_().m_122434_().m_122478_() ? 90 : 0;
      float yRot = owner.m_146908_();
      float yHeadRot = owner.m_6080_();
      this.m_146922_(yRot);
      this.m_146926_(xRot);
      this.m_5618_(yRot);
      this.m_5616_(yHeadRot);
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return false;
   }

   public void m_8119_() {
      if (++this.ticksAlive >= 30) {
         this.m_146870_();
      }

      if (this.ticksAlive >= 20 && !this.didAnimate) {
         this.didAnimate = true;
      }

      if (this.ticksAlive == 11 && !this.didDamage) {
         Vec3 location = this.m_20182_();
         this.f_19853_
            .m_6263_(
               null,
               location.f_82479_,
               location.f_82480_,
               location.f_82481_,
               (SoundEvent)SoundRegistry.FORCE_IMPACT.get(),
               SoundSource.NEUTRAL,
               2.0F,
               this.f_19796_.m_216332_(6, 8) * 0.1F
            );
         this.f_19853_
            .m_6263_(
               null,
               location.f_82479_,
               location.f_82480_,
               location.f_82481_,
               SoundEvents.f_12600_,
               SoundSource.NEUTRAL,
               1.0F,
               this.f_19796_.m_216332_(6, 8) * 0.1F
            );
      }

      if (this.ticksAlive >= 13 && !this.didDamage) {
         if (this.blockHitResult != null && this.blockHitResult.m_6662_() != Type.MISS) {
            BlockPos blockPos = this.blockHitResult.m_82425_();
            BlockState blockState = this.f_19853_.m_8055_(blockPos);
            if (blockState.m_204336_(ModTags.SPECTRAL_HAMMER_MINEABLE)) {
               SpectralHammer.BlockCollectorHelper blockCollector = this.getBlockCollector(
                  blockPos, this.blockHitResult.m_82434_(), this.radius, this.depth, new HashSet<>(), new HashSet<>()
               );
               this.collectBlocks(blockPos, blockCollector);
               if (!blockCollector.blocksToRemove.isEmpty()) {
                  RandomSource random = Utils.random;
                  AtomicInteger count = new AtomicInteger();
                  int maxPossibleStacks = this.radius * 2 * (1 + this.radius * 2) * (this.depth + 1);
                  SimpleContainer drops = new SimpleContainer(maxPossibleStacks);
                  blockCollector.blocksToRemove
                     .forEach(
                        pos -> {
                           int distance = blockCollector.origin.m_123333_(pos);
                           float missChance = random.m_188501_() * 20.0F;
                           float pct = distance * distance / (100.0F * this.radius);
                           BlockState blockstate = this.f_19853_.m_8055_(pos);
                           BreakEvent event = new BreakEvent(this.f_19853_, pos, blockstate, this.owner);
                           MinecraftForge.EVENT_BUS.post(event);
                           if (!event.isCanceled()) {
                              boolean spawnFallingBlock = missChance < pct;
                              if (spawnFallingBlock && !this.f_19853_.f_46443_) {
                                 MagicManager.spawnParticles(
                                    this.f_19853_,
                                    new FallingBlockParticleOption(blockstate),
                                    pos.m_123341_(),
                                    pos.m_123342_(),
                                    pos.m_123343_(),
                                    1,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    true
                                 );
                              }

                              if (count.incrementAndGet() % 5 == 0 && !spawnFallingBlock) {
                                 this.f_19853_.m_46961_(pos, false);
                              } else {
                                 this.f_19853_.m_7471_(pos, false);
                              }

                              dropResources(blockstate, this.f_19853_, pos).forEach(drops::m_19173_);
                           }
                        }
                     );
                  Containers.m_19002_(this.f_19853_, this.m_20183_(), drops);
               }
            }
         }

         this.didDamage = true;
      }

      super.m_8119_();
   }

   public static List<ItemStack> dropResources(BlockState pState, Level pLevel, BlockPos pos) {
      List<ItemStack> drops = new ArrayList<>();
      if (pLevel instanceof ServerLevel) {
         drops = Block.m_49869_(pState, (ServerLevel)pLevel, pos, null);
         pState.m_222967_((ServerLevel)pLevel, pos, ItemStack.f_41583_, true);
      }

      return drops;
   }

   private void collectBlocks(BlockPos blockPos, SpectralHammer.BlockCollectorHelper bch) {
      Stack<BlockPos> stack = new Stack<>();
      stack.push(blockPos);

      while (!stack.isEmpty()) {
         BlockPos currentPos = stack.pop();
         if (!bch.blocksChecked.contains(currentPos) && !bch.blocksToRemove.contains(currentPos)) {
            if (bch.isValidBlockToCollect(this.f_19853_, currentPos)) {
               bch.blocksToRemove.add(currentPos);
               BlockPos tmpPos = currentPos.m_7494_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }

               tmpPos = currentPos.m_7495_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }

               tmpPos = currentPos.m_122012_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }

               tmpPos = currentPos.m_122019_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }

               tmpPos = currentPos.m_122029_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }

               tmpPos = currentPos.m_122024_();
               if (!bch.blocksChecked.contains(tmpPos) && !bch.blocksToRemove.contains(tmpPos)) {
                  stack.push(tmpPos);
               }
            } else {
               bch.blocksChecked.add(currentPos);
            }
         }
      }
   }

   private SpectralHammer.BlockCollectorHelper getBlockCollector(
      BlockPos origin, Direction direction, int radius, int depth, Set<BlockPos> blocksToRemove, Set<BlockPos> blocksChecked
   ) {
      int minX = origin.m_123341_() - radius;
      int maxX = origin.m_123341_() + radius;
      int minY = origin.m_123342_() - radius;
      int maxY = origin.m_123342_() + radius;
      int minZ = origin.m_123343_() - radius;
      int maxZ = origin.m_123343_() + radius;
      switch (direction) {
         case WEST:
            minX = origin.m_123341_();
            maxX = origin.m_123341_() + depth;
            break;
         case EAST:
            minX = origin.m_123341_() - depth;
            maxX = origin.m_123341_();
            break;
         case SOUTH:
            minZ = origin.m_123343_() - depth;
            maxZ = origin.m_123343_();
            break;
         case NORTH:
            minZ = origin.m_123343_();
            maxZ = origin.m_123343_() + depth;
            break;
         case UP:
            minY = origin.m_123342_() - depth;
            maxY = origin.m_123342_();
            break;
         case DOWN:
            minY = origin.m_123342_();
            maxY = origin.m_123342_() + depth;
      }

      return new SpectralHammer.BlockCollectorHelper(origin, direction, radius, depth, minX, maxX, minY, maxY, minZ, maxZ, blocksToRemove, blocksChecked);
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_20147_() {
      return true;
   }

   protected float m_6431_(Pose pPose, EntityDimensions pDimensions) {
      return pDimensions.f_20378_ * 0.6F;
   }

   public boolean m_20068_() {
      return true;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_();
   }

   public Iterable<ItemStack> m_6168_() {
      return Collections.singleton(ItemStack.f_41583_);
   }

   public ItemStack m_6844_(EquipmentSlot pSlot) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public HumanoidArm m_5737_() {
      return HumanoidArm.LEFT;
   }

   private PlayState predicate(AnimationState event) {
      if (event.getController().getAnimationState() == State.STOPPED && this.playSwingAnimation) {
         event.getController().setAnimation(this.animationBuilder);
         this.playSwingAnimation = false;
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{this.animationController});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   private record BlockCollectorHelper(
      BlockPos origin,
      Direction originVector,
      int radius,
      int depth,
      int minX,
      int maxX,
      int minY,
      int maxY,
      int minZ,
      int maxZ,
      Set<BlockPos> blocksToRemove,
      Set<BlockPos> blocksChecked
   ) {
      public boolean isValidBlockToCollect(Level level, BlockPos bp) {
         return level.m_8055_(bp).m_204336_(ModTags.SPECTRAL_HAMMER_MINEABLE)
            && bp.m_123341_() >= this.minX
            && bp.m_123341_() <= this.maxX
            && bp.m_123342_() >= this.minY
            && bp.m_123342_() <= this.maxY
            && bp.m_123343_() >= this.minZ
            && bp.m_123343_() <= this.maxZ;
      }
   }
}
